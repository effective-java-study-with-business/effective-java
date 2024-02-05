# Item78 공유중인 가변 데이터는 동기화해 사용하라.

### 동기화 개념
- 자바에서는 멀티스레딩 환경에서 여러 스레드가 하나의 공유 자원에 안정적으로 접근하도록 하도록 처리하는 것을 스레드 동기화 라고 한다.
- 스레드에서 중요한 개념 두가지 '배타적 실행', '안정적인 통신'
- 배타적 실행: 타 스레드가 진입하지 않도록 lock을 걸어 단일 스레드가 임계영역을 점유하도록 함.
- 안정적인 통신: 동일 시점에 모든 스레드가 동일한 값을 가지도록 동기화 함.


다음의 값 참조 예제를 살펴보자.
```java
public class StopThread {
  private static boolean stopRequested;
  public static void main(String[] args) throws InterruptedException {
      new Thread(() -> {
      int i = 0;
      while(!stopRequested){
          i++;
      }
    }).start();
      
    TimeUnit.SECONDS.sleep(1);
    stopRequested = true;
  }
}
```
스레드는 영원히 끝나지 않았다!
while 문에서 참조한 stopRequested 의 값과 스레드 외부에서 바꾼 값으로 참조 값이 바뀌지 않기 때문이다.
서로 값이 동기화 되지 못한것. 통신의 문제.

동기화하지 않으면 메인 스레드가 수정한 값을 백그라운드 스레드가 동일한 값을 가지는 것을 보장할 수 없다!

그렇다면 안정적인 통신을 위해서 어떻게 해야할까?

### 첫번째 방법. synchronized
- 동기화가 필요한 메소드 또는 코드블럭에 사용이 가능.
- synchronized로 지정된 임계영역은 한 스레드가 사용할때 점유하여 lock을 걸어 다른 스레드가 접근할 수 없게 함.
- 하나의 객체만 lock 권한을 얻어 독점으로 사용한다.

```java
public class StopThread {
    private static boolean stopRequested;
  
    private static synchronized void requestStop() {
        stopRequested = true;
    }

    private static synchronized boolean stopRequested() {
        return stopRequested;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested()) {
                i++;
            }
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        requestStop();
  }
}
```

쓰기 메서드(requestStop()) 읽기 메서드(stopRequested()) 둘다 동기화 하여 동일한 값을 가지도록 할 수 있다.


### 두번째 방법. volatile

- 멀티스레드 환경에서 변수는 CPU 메모리 영역에 캐싱하는데 volatile 을 쓰면 cache를 사용하지 않고 최신의 값을 메인 메모리에 저장해 공유한다.
- 캐시없이 여러 스레드에 동일한 최신의 값을 보여주는 것을 보장한다. 통신 동기화.
- syncronized 보다 속도가 빠름.

공유 변수에 volatile 을 붙이면 완전한 동기화 처리가 된다.
```java
public class StopThread {
    private static volatile boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            int i = 0;
            while (!stopRequested) {
                i++;
            }
        }).start();
    
        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}
```

하지만 volatile 에도 단점이 있다.

volatile은 값을 읽는 원자적 연산에서만 동기화가 보장되며,
여러 스레드가 읽고 쓰기를 하는 비원자적 연산에서는 동기화가 보장되지 않는다.

```java
private static volatile int nextSerialNumber= 0;

public static int generateSerialNumber(){
    return nextSerialNumber++; 
}
```
쓰기전에 읽게되면 읽기 스레드가 generateSerialNumber 중복된 값을 가지게 된다.
이때는 syncronized를 쓰는것이 더 낫다.

### 세번째 방법. java.util.concurrent.atomic 

- 락-프리 동기화를 지원하는 클래스
- 알아서 동기화를 해주므로 편하게 사용할 수 있다.

```java
import java.util.concurrent.atomic.AtomicLong;

private static final AtomicLong nextSerialNumber = new AtomicLong();

public static long generateSerialNumber() {
    return nextSerialNumber.getAndIncrement();
}
```

- 성능 또한 뛰어나다.

### 정리
- 여러 스레드가 가변 데이터를 공유한다면 그 데이터를 읽고 쓰는 동작은 동기화 해야한다.
- 동기화 하는데 실패하면 한 스레스가 수행한 변경을 다른 스레드가 보지 못하거나, 응답 불가 상태로 빠질수 있다.
- 스레드 간 안정적 통신만이 중요하다면 volatile 키워드를 사용할 수 있다.
- 하지만 왠만하면 가변 데이터를 공유하지 않도록하자. 불변 데이터만 공유하자. (아이템 17)
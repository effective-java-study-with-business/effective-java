# Item 81 wait와 notify보다는 동시성 유틸리티를 애용하라

자바5에 java.util.concurrent 동시성 유틸리티가 도입되었다.

세가지 범주
- 실행자 프레임워크 (item 80)
- 동시성 컬렉션 concurrent collection
- 동기화 장치 synchronizer


## 1. 동시성 컬렉션 concurrent collection
- List, Queue, Map 같은 표준 컬렉션 인터페이스에 동시성을 가미해 구현한 고성능 컬렉션.
- 여러 스레드가 한번에 접근 가능하기 때문에 스레드 대기시간을 줄여준다.
- ConcurrentHashMap, CopyOnWriteArrayList, CopyOnWriteHashSet ...
- 높은 동시성에 도달하기 위해 동기화를 각자 내부에서 수행.
- 여러 기본 동작을 하나의 원자적 동작으로 묶는 '상태 의존적 수정' 메서드들이 추가됨.
- 자바8에서는 이 메서드들을 일반 컬렉션 인터페이스에도 디폴트 메서드로 추가. ex) putIfAbsent

### 동시성 컬렉션의 스레드 안정적 확보 방법
- Lock Stripping 기법. 여러개의 락을 사용하고, 락을 분할하여 동시에 여러 스레드가 접근하도록 함.
- ConcurrentHashMap : 전체 map을 여러 조각으로 나누고, 관련된 부분에만 락을 걸어 다른 조각에 여러 스레드가 접근 가능.
- CopyOnWriteArrayList : read는 synchronization 없이 여러 스레드가 읽을 수 있다. write는 전체 ArrayList를 복사하고 최신 컬렉션과 바꿈.

### ConcurrentHashMap
- 병렬 컬렉션. 동시성이 뛰어나며 속도가 빠르다.
- putIfAbsent, replace, conditional remove 연산 등이 정의된 ConcurrentMap의 하위 클래스.
- 자바8 이전은 ReentrantLock 을 상속받는 Segment를 이용해 영역을 구분하고 잠금.
- 자바8 이후 Node 배열과 CAS(Compare And Swap) 연산을 사용한 구현체로 변경.
- ConcurrentHashMap 의 해시 테이블은 volatile로 선언되어 있어 메인 메모리에 올라가 있다.

다음의 예제는 String.intern 메서드를 구현한 것이다.
String.intern 메서드는 String 객체가 존재하는 경우 해당 객체를 반환하고, 존재하지 않는 경우 해당 객체를 풀에 추가하고 해당 객체를 반환한다.
```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

public static String intern(String s) {
    String result = map.get(s);
    // get 기능에 최적화 되어있으므로 미리 확인하여 호출
    if(result == null) {
        // 없으면 추가하고 null 반환, 있으면 해당 객체 반환.
        result = map.putIfAbsent(s, s);
        if (result == null) result = s;
    }
    return result;
}
```

## 2. 동기화 장치 synchronizer
- 스레드가 다른 스레드를 기다릴수 있게 도와줘서 서로 작업을 조율할 수 있게 해준다.
- Latches, Semaphore, Barriers, Exchanger, Phaser ...

### CountDownLatch
- 하나 이상의 스레드가 또 다른 하나 이상의 스레드 작업이 끝날 때까지 기다리게 한다.
- 생성자에서 int 값을 받고 래치의 countDown 메서드를 몇번 호출해야 대기 중인 스레드를 깨우는지 결정.

어떤 동작을 동시에 시작해 모두 완료할때 까지 시간을 재는 로직.
다음 예제는 메인 스레드가 3개의 스레드를 생성하고, 3개의 스레드가 작업을 종료할 때까지 걸리는 시간은 재는 예제이다.
```java
public static void main(String[] args) {

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    try {
        long result = time(executorService, 3, () -> System.out.println("hello"));
        System.out.println("Time : " + result);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        executorService.shutdown();
    }
}

public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
    CountDownLatch ready = new CountDownLatch(concurrency);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(concurrency);

    for (int i = 0; i < concurrency; i++) {
        executor.execute(() -> {
            // ready 래치 카운트 다운. 카운트가 0이 되면 startNanos 기록.
            ready.countDown();
            try {
                // 모든 작업자 스레드가 준비될 때까지 기다린다.
                start.await();
                action.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 타이머에게 작업을 마쳤음을 알린다.
                done.countDown();
            }
        });
    }

    ready.await(); // 모든 작업자가 준비될 때까지 기다린다.
    long startNanos = System.nanoTime();
    start.countDown(); // 작업자들을 깨운다.
    done.await(); // 모든 작업자가 일을 끝마치기를 기다린다.
    return System.nanoTime() - startNanos;
}
```

```
hello
hello
hello
Time : 364956
```

실행 순서는 다음과 같다.

1) 3개의 카운트다운 래치가 사용되는데,
카운트 3을 가진 read 래치, 카운트 1을 가진 start 래치, 카운트 3을 가진 down 래치가 준비된다.


2) 실제 print 작업을 run 시키기 전 3개의 스레드에서 ready 래치로 카운트 다운을 하면,
메인 스레드에서는 세개의 스레드에서 작업을 실행하기 전까지 await 으로 작업 중지를 시켜놓는다.


4) ready 래치 카운트가 0 이되면 타이머 시작 시간을 기록하고,
메인 스레드에서 시작 시간을 기록하기 전까지 start.await 로 대기시켜 두엇던 작업 스레드를 
start.countDown() 으로 모두 실행시킨다.


5) 마지막 작업을 모두 수행한 후 걸리는 시간을 측정하기 위해 done 래치가 done.countDown() 을 각 작업 스레드 마지막에 수행하면, 
작업을 기다리고 있던 메인 스레드는 done 카운트가 0이 되면 마지막 시간을 측정해 모든 스레드가 종료된 시간을 계산해 리턴한다.

## 3. wait 과 notify
- Object 클래스에 스레드와 관련하여 있는 wait()와 notify(), notifyAll() 메서드.
- 스레드와 관련된 이 메소드들은 synchronized로 지정된 임계영역 안에서만 사용이 가능.
- wait 메서드는 반드시 객체를 잠근 동기화 영역 안에서 호출해야한다. 사용할 때는 반드시 대기 반복문(wait loop) 관용구를 사용하자.
- 반복문 밖에서는 절대로 호출하지 말 것.

wait() : 현재 스레드를 다른 스레드가 이 객체에 대한 notify() 또는 notifyAll() 메소드를 호출할때까지 대기한다.
wait(long timeout) : 현재 스레드를 다른 스레드가 이 객체에 대한 notify() 또는 notifyAll() 메소드를 호출하거나 timeout 시간동안 대기한다.
notify() : 객체에 대해 대기중인 스레드 하나를 깨운다.
notifyAll() : 객체에 대해 대기중인 모든 스레드를 깨운다.


```java
synchronized (obj) {
    while (<조건이 충족되지 않았다>)
        obj.wait(); // 
    
    // 조건이 충족되었을 때의 동작.
    obj.notify();
    }
```

## 정리
- wait, notify 대신 java.util.concurrent 를 사용하자.
- 부득이 하게 사용해야할 경우엔 wait은 while 문 안에서 호출.
- notify 보다는 notifyAll 을 사용. 응답 불가 상태에 빠지지않도록 주의.

## 참고
https://steady-coding.tistory.com/575
https://liltdevs.tistory.com/166
http://www.java2s.com/Tutorials/Java/Java_Thread/0220__Java_Synchronizers.htm

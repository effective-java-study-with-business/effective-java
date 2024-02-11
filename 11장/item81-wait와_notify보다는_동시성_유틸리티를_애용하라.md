# Item 81 wait와 notify보다는 동시성 유틸리티를 애용하라

자바5에 java.util.concurrent 동시성 유틸리티가 도입되었다.

세가지 범주
- 실행자 프레임워크 (item 80)
- 동시성 컬렉션 concurrent collection
- 동기화 장치 synchronizer


## 동시성 컬렉션 concurrent collection
- List, Queue, Map 같은 표준 컬렉션 인터페이스에 동시성을 가미해 구현한 고성능 컬렉션.
- 여러 스레드가 한번에 접근 가능하기 때문에 스레드 대기시간을 줄여준다.
- ConcurrentHashMap, CopyOnWriteArrayList, CopyOnWriteHashSet ...
- 높은 동시성에 도달하기 위해 동기화를 각자 내부에서 수행.
- 여러 기본 동작을 하나의 원자적 동작으로 묶는 '상태 의존적 수정' 메서드들이 추가됨.
- 자바8에서는 일반 컬렉션 인터페이스에도 디폴트 메서드로 추가.(item21)

### 스레드 안정적 확보 방법
- Lock Stripping 기법.
- ConcurrentHashMap : 전체 map을 여러 조각으로 나누고, 관련된 부분에만 락을 걸어 다른 조각에 여러 스레드가 접근 가능.
- CopyOnWriteArrayList : read는 synchronization 없이 여러 스레드가 읽을 수 있다. write는 전체 ArrayList를 복사하고 최신 컬렉션과 바꿈.


## 동기화 장치 synchronizer
- 스레드가 다른 스레드를 기다릴수 있게 도와줌.
- CountDownLatch, Semaphore, CyclicBarrier, Exchanger, Phaser ...

### wait 과 notify 
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
http://www.java2s.com/Tutorials/Java/Java_Thread/0220__Java_Synchronizers.htm

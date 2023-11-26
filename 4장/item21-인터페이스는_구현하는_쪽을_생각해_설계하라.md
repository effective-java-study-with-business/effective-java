# Item21 인터페이스는 구현하는 쪽을 생각해 설계하라

## 1. 인터페이스의 기본 메서드 default method 살펴보기

- 자바8 이후 생긴 인터페이스에서 사용가능한 기본 구현 메서드 입니다.
- 구현 클래스에서 재정의 하지 않아도, 인터페이스 내부 구현된 메서드를 쓸 수 있습니다.

```java
public interface  IntSequence {
    default boolean hasNext() {
        return true;
    }
    int next();
}
```


## 2. 자바8 에서 디폴트 메서드 사용 예시

- 자바8 에서는 주요 Collection 인터페이스에 람다를 위한 디폴트 메서드가 다수 추가되었습니다.

```java

default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
```

### Collection 의 경우

## 3. 디폴트 메서드 추가로 인한 오류 가능성

### 디폴트 메서드의 잘못된 구현

- 기존 구현체에 런타임 오류를 일으킬 수 있습니다.
- SynchronizedCollection은 스레드의 안정성을 확보하기위해 한번에 하나의 스레드만 객체에 접근하도록 허용하는 컬렉션 입니다.
- 컬렉션 객체에 락을 걸어 값을 한 스레드만 사용하도록 제어합니다.
- 디폴트 메서드로 추가된 removeIf 메서드는 이런 스레드 락을 고려하지 못했으므로 호출시 오류가 발생할 수 있습니다.


```java
default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
```

 - 여러 스레드가 공유하는 환경에서 한 스레드가 remeveIf 를 호출하면 예기치 못한 오류가 발생할 수 있습니다.
 따라서 Collection 인터페이스 스펙에 다음과 같은 유의 사항이 명시되어 있습니다.
```
 * @implSpec
 * The default method implementations (inherited or otherwise) do not apply any
 * synchronization protocol.  If a {@code Collection} implementation has a
 * specific synchronization protocol, then it must override default
 * implementations to apply that protocol.
 // 디폴트 메소드 구현은 동기화 프로토콜을 적용하지 않습니다.
 // 구현에 특정 동기화 프로토콜이 있는 경우 해당 프로토콜을 적용하려면 기본 구현을 재정의해야 합니다.
```

### 인터페이스 다중 구현시 충돌 가능성

- 같은 디폴트 메서드가 구현되면 충돌 오류가 생깁니다.
```java
public interface Person {
    String getName();
    default int getId() { return 0; }
}

public interface Identified {
    default int getId() {
        return Math.abs(hashCode());
    }
}
```

위의 두 인터페이스를 구현하는 클래스를 만든다면 어떻게 될까요?

```java
public class Employee implements Person, Identified {
    ...
}
```
컴파일러는 두 인터페이스가 구현한 메서드 중 하나를 선택할 수 없어 오류가 생깁니다. </br>
 따라서 인터페이스에 디폴트 메서드로 구현 제공하지 않거나, Employee 클래스에서 getId 메서드를 새로 구현하거나, 
Employee 클래스에서 두 디폴트 메서드 중 하나를 선택해 주어야 합나다.

```java
public class Employee implements Person, Identified {
    public int getId() {
        return Identified.super.getId();
    }
}
```

## 4. 요약
- 디폴트 메서드는 새 인터페이스를 만드는 경우 표준 메서드 구현을 쉽게 할 수 있다.
- 예상치 못한 오류 가능성이 있으므로 기존 인터페이스에 디폴트 메서드를 추가하는 경우 신중해야한다.
- 디폴트 메서드를 구현한 이후에는 구현 클래스로 충분한 테스트 후 릴리즈 해야한다.
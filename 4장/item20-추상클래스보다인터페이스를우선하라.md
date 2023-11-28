# 아이템20.추상클래스보다 인터페이스를 우선하라

## 인터페이스의 장점

- 자바 8부터 인터페이스도 디폴트 메서드를 제공할 수 있다.
- 기존 클래스도 손쉽게 새로운 인터페이스를 구현해 넣을 수 있다.
- 인터페이스는 믹스인(mixin) 정의에 안성맞춤이다. (선택적인 기능 추가)
- 계층구조가 없는 타입 프레임워크를 만들 수 있다.
- 래퍼 클래스와 함께 사용하면 인터페이스는 기능을 향상 시키는 안전하고 강력한 수단이 된다. (아이템 18참고)
- 구현이 명백한 것은 인터페이스의 디폴트 메서드를 사용해 프로그래머의 일감을 덜어 줄 수 있다.

### 인터페이스 예시

```java

public interface TimeClient {

    void setTime(int hour, int minute, int second);
    void setDate(int day, int month, int year);
    void setDateAndTime(int day, int month, int year,
                        int hour, int minute, int second);
    LocalDateTime getLocalDateTime();

}
```

### 인터페이스 구현체

```java
public class SimpleTimeClient implements TimeClient {

    private LocalDateTime dateAndTime;

    public SimpleTimeClient() {
        dateAndTime = LocalDateTime.now();
    }

    public void setTime(int hour, int minute, int second) {
        LocalDate currentDate = LocalDate.from(dateAndTime);
        LocalTime timeToSet = LocalTime.of(hour, minute, second);
        dateAndTime = LocalDateTime.of(currentDate, timeToSet);
    }

    public void setDate(int day, int month, int year) {
        LocalDate dateToSet = LocalDate.of(day, month, year);
        LocalTime currentTime = LocalTime.from(dateAndTime);
        dateAndTime = LocalDateTime.of(dateToSet, currentTime);
    }

    public void setDateAndTime(int day, int month, int year,
                               int hour, int minute, int second) {
        LocalDate dateToSet = LocalDate.of(day, month, year);
        LocalTime timeToSet = LocalTime.of(hour, minute, second);
        dateAndTime = LocalDateTime.of(dateToSet, timeToSet);
    }

    public LocalDateTime getLocalDateTime() {
        return dateAndTime;
    }

    public String toString() {
        return dateAndTime.toString();
    }

}
```

## 장점 1. 인터페이스에 메서드를 추가하고 싶은데?

```java
public interface TimeClient {

    void setTime(int hour, int minute, int second);
    void setDate(int day, int month, int year);
    void setDateAndTime(int day, int month, int year,
                        int hour, int minute, int second);
    LocalDateTime getLocalDateTime();

		// 추가될 메서드
		ZonedDateTime getZonedDateTime(String zoneString);

}
```

- 인터페이스에 추가되면 구현체에 전부 오버라이딩 해줘야 하는 문제가 발생!
- 개발자가 스스로 전부 컨트롤할 수 있는 경우 (예를 들면 package-private이고 영향범위를 전부 파악할 수 있는 경우)라면 큰 문제가 발생하진 않겠지만, public 클래스로 여기저기서 사용하고 있다면?! 컴파일 에러 나는 모든 곳을 찾아서 오버라이딩 해주는 번거로움이 발생해버린다…

### 이런 상황을 방지하기 위해 자바 8부터는 디폴트 메서드를 제공해준다!

```java
public interface TimeClient {

    void setTime(int hour, int minute, int second);
    void setDate(int day, int month, int year);
    void setDateAndTime(int day, int month, int year,
                        int hour, int minute, int second);
    LocalDateTime getLocalDateTime();

		// 인터페이스는 static 메서드도 추가 가능
    static ZoneId getZonedId(String zoneString) {
        try {
            return ZoneId.of(zoneString);
        } catch (DateTimeException e) {
            System.err.println("Invalid time zone: " + zoneString + "; using default time zone instead.");
            return ZoneId.systemDefault();
        }
    }

		// 디폴트 메서드로 제공!
    default ZonedDateTime getZonedDateTime(String zoneString) {
        return ZonedDateTime.of(getLocalDateTime(), getZonedId(zoneString));
    }

}
```

- 기능은 추가했지만 구현 클래스들에서 컴파일 에러가 발생하지 않는다!
- 심지어 인터페이스에 추가된 기능을 구현 클래스에서 사용도 가능!

### 하지만 이런 디폴트 메서드로도 구현이 안되는 경우가 있다!

### 바로 인스턴스의 필드를 사용해야 되는 경우! 이럴 땐 추상 클래스를 활용해야 한다.

<aside>
💡 인터페이스와 추상 골격 클래스 (인터페이스와 추상 클래스의 장점을 모두 잡아보자!)
* 인터페이스 - 디폴트 메서드 구현
* 추상 골격 클래스 - 나머지 메서드 구현
* 템플릿 메서드 패턴

</aside>

```java
// 골격 구현을 사용해 완성한 구체 클래스
public class IntArrays {
    static List<Integer> intArrayAsList(int[] a) {
        Objects.requireNonNull(a);

				// 만약 AbstractList가 아니라면 List 인터페이스의
				// 모든 메서드들을 오버라이딩해줘야하는 엄청난 일이 일어난다!
        return new AbstractList<>() {
            @Override public Integer get(int i) {
                return a[i];)
            }

            @Override public Integer set(int i, Integer val) {
                int oldVal = a[i];
                a[i] = val;
                return oldVal;
            }

            @Override public int size() {
                return a.length;
            }
        };
    }
}
```

## 장점 2. 인터페이스는 Mixin이 가능하다!

```java
// 다른 인터페이스를 implemnts 함으로써
// 부가적인 기능들을 자유롭게 추가 가능!
public class SimpleTimeClient implements TimeClient, AutoCloseable {

    private LocalDateTime dateAndTime;

    public SimpleTimeClient() {
        dateAndTime = LocalDateTime.now();
    }
		// 이하 생략

}
```

- 추상 클래스를 상속받아 Mixin으로 사용하기에는 무리가 있다. (이미 이 클래스가 다른 클래스를 상속받고 있는 상황이라면 불가능하니까)

## 장점 3. 관계가 명확하지 않은 경우 계층 구조 없는 타입프레임워크 구성이 가능하다!

```java
// 노래도 부르고 작사작곡도 해버린다면?!
public interface SingerSongwriter extends Singer, Songwriter{

    AudioClip strum();
    void actSensitive();
}
```

- 기존의 인터페이스를 조합해서 새로운 타입을 만드는 것이 굉장히 편리하다. (여러 인터페이스를 상속받는다는 장점)

## 장점 4. 래퍼 클래스와 사용했을 때 안정감 향상

### 래퍼 클래스

```java
// 인터페이스를 확장해서 래퍼 클래스를 만들었다
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s = s; }

    public void clear()               { s.clear();            }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty();   }
    public int size()                 { return s.size();      }
    public Iterator<E> iterator()     { return s.iterator();  }
    public boolean add(E e)           { return s.add(e);      }
    public boolean remove(Object o)   { return s.remove(o);   }
    public boolean containsAll(Collection<?> c)
                                   { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c)
                                   { return s.addAll(c);      }
    public boolean removeAll(Collection<?> c)
                                   { return s.removeAll(c);   }
    public boolean retainAll(Collection<?> c)
                                   { return s.retainAll(c);   }
    public Object[] toArray()          { return s.toArray();  }
    public <T> T[] toArray(T[] a)      { return s.toArray(a); }
    @Override public boolean equals(Object o)
                                       { return s.equals(o);  }
    @Override public int hashCode()    { return s.hashCode(); }
    @Override public String toString() { return s.toString(); }
}
```

### 추상 클래스는 아니지만 상속해서 만들었을 때

```java
// HashSet이 추상클래스는 아니지만 추상클래스라고 한번 가정해보자
public class InstrumentedHashSet<E> extends HashSet<E> {

    private int addCount = 0;

    public InstrumentedHashSet() {
    }

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

}
```

- 추상 클래스를 상속받은 경우, 상위 클래스의 구현여부에 따라 구현 클래스가 깨질 수도 있다. (상위 클래스의 변화에 모든 하위 클래스들이 영향을 받아버림) - 기능 확장이 어려움
- 인터페이스를 활용한 래퍼 클래스의 경우, 구현이 없으니까 바뀔 것이 없다. 굉장히 안전한 확장 방법. 우리가 원하는 기능을 그대로 수행해줌.
- 인터페이스는 또 메서드가 추가가 되는지 바로 알아차릴 수 있음.

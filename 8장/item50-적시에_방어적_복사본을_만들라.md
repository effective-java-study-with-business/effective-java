# 1️⃣. 방어적 복사(Defensive Copy)

자바는 C/C++와 같이 안전하지 않은 언어에서 자주 보는 버퍼 오버런, 배열 오버런, 와일드 포인터와 같은 **메모리 충돌 오류가 일어나지 않는다는 점**에서 안전한 언어이다.

자바로 작성한 클래스는 **시스템의 다른 부분에서 무슨 짓을 하든 그 불변식이 지켜진다**. 그러나 **클라이언트가 여러분의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍**을 해야 합니다.

### 기간을 표현하는 클래스 : 불변식을 지키지 못한다.

```java
public final class Period {
    private final Date start;  // 가변 객체(불변 X)
    private final Date end;

    /**
     * @param  start 시작 시각
     * @param  end 종료 시각. 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발생한다.
     */
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                    start + "가 " + end + "보다 늦다.");
        this.start = start;
        this.end   = end;
    }

    public Date start() {
        return start;
    }
    public Date end() {
        return end;
    }
    ...
}
```

얼핏 보면 이 클래스는 불변으로 보이고, 시작 시각이 종료 시각보다 늦을 수 없다는 불변식이 무리 없이 지켜질 것 같다. 하지만 `Date` 클래스가 가변이라는 사실을 이용하면 어렵지 않게 그 불변식을 깨뜨릴 수 있다.

### 공격 시나리오 1.

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(78);  // p의 내부를 변경했다!
System.out.println(p);
```

대부분 낡은 API들이 가변인 것들이 있기 때문에 같은 이슈가 발생할 수 있다. 따라서 외부 공격으로부터 Period 인스턴스의 내부를 보호하려면 생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)해야 한다.

### 해결 방법 1 - 매개변수의 방어적 복사본 생성

따라서, 외부 공격으로부터 클래스 인스턴스의 내부를 보호하려면 **생성자에서 받은 가변 매개변수 각각을 방어적으로 복사하고, 인스턴스 안에서는 원본이 아닌 복사본을 사용해야 한다.** 

```java
public Period(Date start, Date end) {
   this.start = new Datㅈe(start.getTime());
   this.end = new Date(end.getTime());

   if (this.start.compareTo(this.end) > 0)
       throw new IllegalArgumentException(
               this.start + "가 " + this.end + "보다 늦다.");
    }
```

순서가 부자연스러워 보일 수 있겠지만 반드시 이렇게 작성해야 한다. **멀티스레딩 환경이라면 원본 객체의 유효성을 검사한 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 원본 객체를 수정할 위험**이 있기 때문이다.

~~또한 clone에 대한 이야기를 하는데, 알 필요가 없어서 패스~~

### 공격 시나리오 2.

```java
start = new Date();
end = new Date();
p = new Period(start, end);
p.end().setYear(78);  // p의 내부를 변경했다!
System.out.println(p);
```

두 번째 공격을 막아내려면, **Getter 메서드와 Setter 메소드와 같은 접근 제어자가 가변 필드의 방어적 복사본을 반환해야 한다**고 합니다.

### 해결 방법 2 - 필드의 방어적 복사본 반환

```java
public final class Period {
    ...
    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }
}
```

그렇게 되면 비로서 Period 클래스는 `완벽한 불변`으로 거듭날 수 있으며 완벽한 `캡슐화`가 됩니다. 아무리 악의적인 혹은 부주의한 프로그래머라도 시작 시각이 종료 시각보다 나중일 수 없게 되는 것이다.

## 방어적 복사를 사용할 땐 주의하자.

1. 불변 객체를 조합해 객체를 구성한다면 방어적 복사를 할 일이 줄어든다.
2. 호출자가 컴포넌트 내부를 수정하지 않는다고 확신하거나 객체의 통제권이 명백히 이전하는 경우, 방어적 복사를 하지 않고 수정하지 말아야 함을 명확히 문서화 해야한다.
3. 클래스와 그 클라이언트가 상호 신뢰할 수 있을 때 또는 불변식이 깨지더라도, **영향이 오직 호출한 클라이언트로 국한될 때만 방어적 복사를 생략**해도 된다.

### cf) 낡은 API는 쓰지 말자.

해당 공격은, 자바8 이후로 쉽게 해결할 수 있게 되었습니다. `Date` 대신 불변인 `Instant`이나 `LocalDateTime/ZondeDateTime` 을 사용하면 방어가 가능하다. **`Date`는 낡은 API이니 새로운 코드를 작성할 때는 더 이상 사용하면 안된다.**
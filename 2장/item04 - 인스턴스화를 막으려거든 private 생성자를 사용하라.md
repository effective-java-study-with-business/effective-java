# 아이템 4. 인스턴스화를 막으려거든 private 생성자를 사용하라
## 서론

정적메서드와 정적 필드만을 담은 클래스를 만들고 싶을 때가 있을 것이다.

1. java.lang.Math / java.util.Arrays같은 기본 타입 값이나 배열 관련 메서드를 모아놓을 수 있다.
```java
public class MathAndArrayUtils {
...
    // 정적 메서드: 두 정수의 합을 반환
    public static int add(int a, int b) {
        return a + b;
    }

    // 정적 메서드: 두 정수의 차를 반환
    public static int subtract(int a, int b) {
        return a - b;
    }

    // 정적 메서드: 배열을 정렬
    public static void sortArray(int[] array) {
        Arrays.sort(array);
    }
...
}
```


2. java.util.Collections처럼 특정 인터페이스를 구현하는 객체를생성해주는 정적 메서드or팩터리를 모아놓을 수 있다.(자바8 이상은 이런 메서드를 인터페이스에 넣을 수 있다.)
```java
public interface Vehicle {
    void start();
    void stop();
...
}
```

```java
public class VehicleFactory {
...
    // 내부 클래스: 자동차
    private static class Car implements Vehicle {
        private String carType;

        public Car(String carType) {
            this.carType = carType;
        }

        @Override
        public void start() {
            ...
        }

        @Override
        public void stop() {
            ...
        }
    }
...
}
```

3. final 클래스와 관련한 메서드들을 모아놓을 수 있다. -> final 클래스를 상속한 하위 클래스에 메서드 넣는 건 불가능하기 때문
```java
public final class FinalClass {
    public void someMethod() {
    ...
    }
}
```

## 본론
### 기본 생성자는 public으로 자동 생성
컴파일러는 정적 멤버만 담은 유틸리티 클래스를 인스턴스로 사용할게 아니어도 자동으로 기본 생성자를 public으로 생성하며, 사용자는 구분할 수 없다.

추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다.
-> 하위 클래스를 만들어 인스턴스화 하면 가능(상속X)
```java
abstract class AbstractClass {
    public abstract void abstractMethod();
}

class Subclass extends AbstractClass {
    private int value;

    public Subclass(int value) {
        this.value = value;
    }

    public void abstractMethod() {
        ...
    }
}

public class Main {
    public static void main(String[] args) {
        // 추상 클래스를 직접 인스턴스화하려고 시도
        // 아래 코드는 런타임 에러를 발생
        // AbstractClass obj = new AbstractClass();

        // 추상 클래스를 상속한 하위 클래스를 인스턴스화
        Subclass sub = new Subclass(18);
        sub.abstractMethod();
    }
}
```

### private생성자를 만들어 인스턴스화를 막는다.
```java
//코드 4-1 인스턴스를 만들 수 없는 유틸리티 클래스 - 26p
public class UtilityClass {
    //기본 생성자가 만들어지는 것을 막는다(인스턴스화 방지용).
    private UtilityClass() {
        throw new AssertionError();
    }
}
```

해당 코드는 어떤 환경에서도 클래스가 인스턴스화되는 것을 막아준다.

(팁)그냥 이렇게 두면 생성자가 존재하는데 private로 호출할 수 없어져 직관적이지 않으니 주석을 꼭 달자.
해당 방식은 상속을 불가능하게 하는 효과도 있다. 
private로 선언해버렸으니, 하위가 접근하는 길을 막아버린다.

## 결론
인스턴스화를 막으려거든 private 생성자를 사용하라 그 자체.






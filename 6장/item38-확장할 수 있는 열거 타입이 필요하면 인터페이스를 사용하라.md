# 아이템38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

열거 타입은 거의 모든 상황에서 타입 안전 열거 패턴(typesafe enum pattern)보다 우수하다.

그러나 한 가지 예외가 있다.

타입 안전 열거 패턴은 확장할 수 있으나 열거 타입은 그럴 수 없다는 점이다.

대부분 상황에서 열거 타입을 확장하는 게 좋지 않은 이유

- 확장한 타입의 원소는 기반 타입의 원소로 취급하지만 그 반대는 성립하지 않는다면 이상하지 않은가!
- 기반 타입과 확장된 타입들의 원소 모두를 순회할 방법도 마땅치 않다.
- 마지막으로, 확장성을 높이려면 고려할 요소가 늘어나 설계와 구현이 더 복잡해진다.

그럼에도 확장할 수 있는 열거 타입이 어울리는 쓰임이 최소한 하나는 있으므로 알아보자.

기본 아이디어는 열거 타입이 임의의 인터페이스를 구현할 수 있다는 사실을 이용하는 것이다.

### 인터페이스를 사용한 예시

```java
public interface Operation {
    double apply(double x, double y);
}
```

```java
public enum BasicOperation implements Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override public String toString() {
        return symbol;
    }
}
```

열거 타입인 BasicOperation은 확장할 수 없지만 인터페이스인 Operation은 확장할 수 있다.

이 인터페이스를 연산의 타입으로 사용하면 된다.

이렇게 하면 Operation을 구현한 또 다른 열거 타입을 정의해 기본 타입인 BasicOperation을 대체할 수 있다.

앞의 연산 타입을 확장해 지수 연산(EXP)과 나머지 연산(REMAINDER)을 추가해보자.

확장 가능 열거 타입

```java
public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override public String toString() {
        return symbol;
    }
}
```

새로 작성한 연산은 기존 연산을 쓰던 곳이면 어디든 쓸 수 있다.

(Basic Operation이 아닌) Operation 인터페이스를 사용하도록 작성되어 있기만 하면 된다.

apply가 인터페이스(Operation)에 선언되어 있으니 열거 타입에 따로 추상 메서드로 선언하지 않아도 된다.

기본 열거 타입 대신 확장된 열거 타입을 넘겨 확장된 열거 타입의 원소 모두를 사용하게 할 수도 있다.

```java
public static void main(String[] args) {
    double x = Double.parseDouble(args[0]); // 4
    double y = Double.parseDouble(args[1]); // 2
    test(BasicOperation.class, x, y);
    test(ExtendedOperation.class, x, y);
}

private static <T extends Enum<T> & Operation> void test(
        Class<T> opEnumType, double x, double y) {
    for (Operation op : opEnumType.getEnumConstants()) {
        System.out.printf("%f %s %f = %f%n",
                x, op, y, op.apply(x, y));
    }
}

Input:
4 2

Output:
// BasicOperation
4.000000 + 2.000000 = 6.000000
4.000000 - 2.000000 = 2.000000
4.000000 * 2.000000 = 8.000000
4.000000 / 2.000000 = 2.000000

// ExtendedOperation
4.000000 ^ 2.000000 = 16.000000
4.000000 % 2.000000 = 0.000000
```

main 메서드는 test 메서드에 ExtendedOperation의 class 리터럴을 넘겨 확장된 연산들이 무엇인지 알려준다.

class 리터럴은 한정적 타입 토큰(아이템 33) 역할을 한다.

opEnumType 매개변수의 선언(<T extends Enum<T> & Operation> Class<T>)은 솔직히 복잡한데, Class 객체가 열거 타입인 동시에 Operation의 하위 타입이어야 한다는 뜻이다.

열거 타입이어야 원소를 순회할 수 있고, Operation이어야 원소가 뜻하는 연산을 수행할 수 있기 때문이다.

두 번째 대안은 Class 객체 대신 한정적 와일드카드 타입(아이템 31)인 Collection<? extends Operation>을 넘기는 방법이다.

```java
public static void main(String[] args) {
    double x = Double.parseDouble(args[0]); // 4
    double y = Double.parseDouble(args[1]); // 2
    test(Arrays.asList(BasicOperation.values()), x, y);
    test(Arrays.asList(ExtendedOperation.values()), x, y);
}

private static void test(Collection<? extends Operation> opSet,
                         double x, double y) {
    for (Operation op : opSet) {
        System.out.printf("%f %s %f = %f%n",
                x, op, y, op.apply(x, y));
    }
}

Input:
4 2

Output:
// BasicOperation
4.000000 + 2.000000 = 6.000000
4.000000 - 2.000000 = 2.000000
4.000000 * 2.000000 = 8.000000
4.000000 / 2.000000 = 2.000000

// ExtendedOperation
4.000000 ^ 2.000000 = 16.000000
4.000000 % 2.000000 = 0.000000
```

- 이 코드는 덜 복잡하고 test 메서드가 살짝 더 유연해졌다. 여러 구현 타입의 연산을 조합해 호출할 수 있게 되었다.
- 반면, 특정 연산에서는 EnumSet(아이템 36)과 EnumMap(아이템 37)을 사용하지 못한다.
- 인터페이스를 이용해 확장 가능한 열거 타입을 흉내내는 방식에도 한 가지 사소한 문제가 있다. 바로 열거 타입끼리 구현을 상속할 수 없다는 점이다.
- 아무 상태에도 의존하지 않는 경우에는 디폴트 구현(아이템 20)을 이용해 인터페이스에 추가하는 방법이 있다.  반면 Operation 예는 연산 기호를 저장하고 찾는 로직이 BasicOperation과 ExtendedOperation 모두에 들어가야만 한다.
- 이번 경우에는 중복량이 적으니 문제되진 않지만, 공유하는 기능이 많다면 그 부분을 별도의 도우미 클래스나 정적 도우미 메서드로 분리하는 방식으로 코드 중복을 없앨 수 있을 것이다.

자바 라이브러리도 이번 아이템 에서 소개한 패턴을 사용한다.

그 예로 java.nio.file.LinkOption 열거 타입은 CopyOption과 OpenOption 인터페이스를 구현했다.

**[https://github.com/JetBrains/jdk8u_jdk/blob/master/src/share/classes/java/nio/file/LinkOption.java](https://github.com/JetBrains/jdk8u_jdk/blob/master/src/share/classes/java/nio/file/LinkOption.java)**

### **핵심 정리**

열거 타입 자체는 확장할 수 없지만, 인터페이스와 그 인터페이스를 구현하는 기본 열거 타입을 함께 사용해 같은 효과를 낼 수 있다.

이렇게 하면 클라이언트는 이 인터페이스를 구현해 자신만의 열거 타입(혹은 다른 타입)을 만들 수 있다.

그리고 API가 (기본 열거 타입을 직접 명시하지 않고) 인터페이스 기반으로 작성되었다면 기본 열거 타입의 인스턴스가 쓰이는 모든 곳을 새로 확장한 열거 타입의 인스턴스로 대체해 사용할 수 있다.

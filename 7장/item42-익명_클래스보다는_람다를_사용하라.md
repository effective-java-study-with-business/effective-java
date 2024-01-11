# 아이템 42 익명 클래스보다는 람다를 사용하라

## 익명클래스란?

익명 클래스는 일반적으로 단일 사용 목적을 가지며, 주로 인터페이스나 추상 클래스를 간편하게 구현하기 위해 사용됩니다. 익명 클래스는 그것을 사용하는 코드 블록 내에서만 정의되고 인스턴스화됩니다.

아래 예제들이 익명클래스에 예제입니다.
```java
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 구현 내용
            }
        };
```

```java
class Person {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}

public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Alice", 30));
        people.add(new Person("Bob", 25));
        people.add(new Person("Charlie", 35));

        Comparator<Person> nameComparator = new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return p1.name.compareTo(p2.name);
            }
        };

        Collections.sort(people, nameComparator);

        for (Person p : people) {
            System.out.println(p);
        }
    }
}
```

## 왜 람다를 사용해야 하는가?
전략 패턴처럼, 함수 객체를 사용하는 과거 객체 지향 디자인 패턴에는 익명 클래스면 충분했습니다.
하지만 익명 클래스 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않습니다.

자바8에 와서 추상 메서드가 하나짜리 인터페이스는 특별한 의미를 인정받아, 특별 대우를 받게 되었고, 람다식을 이용해 간결하게 만들 수 있게 되었습니다.

```java
public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Bob", 25));
        people.add(new Person("Charlie", 35));
        people.add(new Person("Alice", 30));

        people.sort((p1, p2) -> p1.name.compareTo(p2.name));

        for (Person p : people) {
            System.out.println(p);
        }
    }
}
```
간결해졌다. ~~반박시 람다 압수~~

여기서 람다, 매개변수(p1,p2)는 Person, 반환값의 타입은 int지만 코드에는 언급이 없습니다.
대신 컴파일러가 문맥을 살펴, 타입을 추론해준 것입니다.

상황에 따라 컴파일러가 타입을 결정하지 못할 수도 있는데, 그럴 때는 프로그래머가 직접 명시해야 합니다.

그래서 책에서는 **타입을 명시해야 코드가 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자**

### 중요포인트
타입 추론에 관해 한마디 덧붙일 게 있다.
아이템26에서는 제네릭의 로 타입을 쓰지 말라 했고, 아이템 29에서는 제네릭을 쓰라 했고, 아이템 30에서는 제네릭 메서드를 쓰라고 했다.
이 조언들은 람다와 함께 쓸 때는 두 배로 중요해진다. 컴파일러가 타입을 추론하는 데 필요한 타입 정보 대부분을 제네릭에서 얻기 때문이다.
우리가 이 정보를 제공하지 않으면 컴파일러는 람다의 타입을 추론할 수 없게 되어, 결국 우리가 일일이 명시해야 한다.

---

람다를 언어 차원에서 지원하면서 기존에는 적합하지 않았던 곳에서도 함수 객체를 실용적으로 사용할 수 있게 되었습니다.

## 더 나아가기

아래는 아이템34의 operation 코드입니다.

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}

public class Calculator {
    public static void main(String[] args) {
        double result = Operation.PLUS.apply(1, 2);
        System.out.println("1 + 2 = " + result);
    }
}

```

람다를 이용한다면 위 코드를 쉽게 구현할 수 있습니다.

```java
import java.util.function.DoubleBinaryOperator;

public enum Operation {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }

    @Override
    public String toString() {
        return symbol;
    }
}

public class Calculator {
    public static void main(String[] args) {
        double result = Operation.PLUS.apply(1, 2);
        System.out.println("1 + 2 = " + result);
    }
}

```

람다 기반 operation 열거 타입을 보면 상수별 클래스 몸체는 더 이상 사용할 이유가 없다고 느낄지 모르겠지만, 꼭 그렇지는 않다고 합니다.
메서드나 클래스와 달리, **람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 설명이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.** 라고 책에서 기술하고 있습니다.

책에서는 명확한 수치를 알려줍니다. **람다는 한 줄일때 가장 좋고, 길어야 세 줄 안에 끝내는게 좋다.**

## 주의점
람다도 익명 클래스처럼 직렬화 형태가 구현별로(가상머신별로) 다를 수 있다.

**따라서 람다를 직렬화하는 일은 극히 삼가야 한다.**



## 요약
1. 람다를 이용하면 자질구레한 코드들이 사라지고, 어떤 동작을 하는지가 명확하게 드러난다. 
2. 타입을 명시해야 코드가 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자.
3. 람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 설명이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.
4. 람다를 직렬화하는 일은 극히 삼가야 한다.
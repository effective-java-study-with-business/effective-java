# 아이템 35 ordinal 메서드 대신 인스턴스 필드를 사용하라

ordinal 메서드는 자바의 enum(열거형) 타입에 내장된 메서드입니다. 

이 메서드는 해당 enum 상수가 그 enum 선언에서 몇 번째 위치에 있는지를 반환합니다.

그러나 이펙티브 자바에서는 ordinal 메서드의 사용을 권장하지 않습니다. 그 이유는 다음과 같습니다.

1. **열거형 정의가 바뀌면 값도 바뀐다.** ordinal 메서드는 열거형 상수의 선언 순서에 의존합니다. 만약 열거형의 순서가 바뀌면 ordinal의 반환값도 바뀌게 되며, 이는 버그로 이어질 수 있습니다.
```java
public enum Planet {
    MERCURY, // 0
    VENUS,   // 1
    EARTH,   // 2
    MARS;    // 3
    //...
}
```

```java
public enum Planet {
    VENUS,   // 0
    MERCURY, // 1     VENUS랑 MERCURY가 변경됨
    EARTH,   // 2
    MARS;    // 3
    //...
}
```


2. **유지보수의 어려움.** 열거형의 순서에 의존하는 코드는 이해하기 어렵고 유지보수하기도 어렵습니다. 다른 개발자가 코드를 보았을 때, 각 ordinal 값이 실제로 어떤 의미를 가지는지 파악하기 어렵습니다.

위와 같은 이유로 JPA 에서 Enum type을 사용할 때, @Enumerted(EnumType.STRING) 사용을 권장합니다.


대신, 이펙티브 자바는 각 enum 상수에 직접 값을 할당하는 방법을 권장합니다. 이를 통해 코드의 가독성과 유지보수성을 높이며, 열거형의 순서가 변경되어도 영향을 받지 않도록 할 수 있습니다.
```java
public enum Planet {
    MERCURY(1), VENUS(2), EARTH(3), MARS(4); // 등등

    private final int order;

    Planet(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
```

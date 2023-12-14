# 아이템 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라

## 태그 달린 클래스
태그 달린 클래스란 두 가지 이상의 의미를 가지고 있으며, 현재 태그 값으로 표현하는 클래스

```java
public class Figure {
    enum Shape { RECTANGLE, CIRCLE };

    // 태그 필드 - 현재 모양을 나타낸다.
    final Shape shape;

    // 다음 필드들은 모양이 사각형(RECTANGLE)일 때만 쓰인다.
    double length;
    double width;

    // 다음 필드는 모양이 원(CIRCLE)일 때만 쓰인다.
    double radius;

    // 원용 생성자
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // 사각형용 생성자
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

### 단점
1. 열거 타입 선언, 태그 필드, switch문 등 쓸데 없는 코드가 많다.
2. 여러 구현이 한 클래스에서 혼합돼 있어서 가독성이 안좋다. 그렇기때문에 메모리도 많이 사용한다.
3. 필드들을 final로 선언하려면 해당 의미에 쓰이지 않는 필드들까지 생성자에서 불필요하게 초기화해야 한다.
4. 또 다른 의미 추가 시 모든 switch문 등의 코드를 찾아 수정해야한다.
5. 인스턴스 타입만으로 현재 나타내는 의미를 알 수 없다.
-> 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다.

## 클래스 계층구조

태그 달린 클래스의 단점을 보완한 클래스 계층구조로 변환한 코드
```java
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}

class Rectangle extends Figure {
    final double length;
    final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}
```

### 장점
1. 쓸데없는 코드들이 사라져 간결하고 명확하다.
2. 살아남은 필드들은 모두 final이다.
3. 각 클래스의 생성자가 모든 필드를 남김없이 초기화하고 추상 메서드를 구현했는지 컴파일러가 확인해준다.
4. 실수로 빼먹은 case문 때문에 런타임 오류가 발생할 일도 없다.
5. 루트 클래스를 건드리지 않고, 독립적 계층구조로 확장하여 사용할 수 있다.
6. 타입이 의미별로 따로 존재하니 변수의 의미를 명시하거나 제한할 수 있다.
7. 특정 의미만 매개변수로 받을 수 있다.
8. 타입 사이의 자연스러운 계층관계를 반영하여 유연하며, 컴파일타임 타입검사능력도 높여준다.

(5)
정사각형을 지원하도록 수정 시 코드

```java
class Square extends Rectangle {
   Square(double side) {
      super(side, side);
   }
}
```
간단하게 루트 수정없이 생성할 수 있다.

## 요약
1. 태그 달린 클래스를 써야 하는 상황은 거의 없다. 계층구조로 대체하는 방법을 생각해보자
2. 기존에 태그 필드를 사용한다면 계층구조로 리펙터링하자


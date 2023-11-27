# 아이템16. public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라.

## 포인트

- 클라이언트 코드가 필드를 직접 사용하면 캡슐화의 장점을 제공하지 못한다.
- 필드를 변경하려면 API를 변경해야 한다.
- 필드에 접근할 때 부수 작업을 할 수 없다.
- package-private 클래스 또는 private 중첩 클래스라면 데이터 필드를 노출해도 문제가 없다.

## “이따금 인스턴스 필드들을 모아놓은 일 외에는 아무 목적도 없는 퇴보한 클래스를 작성하려 할 때가 있다.”

```java
public class Point {
    public double x;
    public double y;

    public static void main(String[] args) {
        Point point = new Point();
        point.x = 10;
        point.y = 20;

        System.out.println(point.x);
        System.out.println(point.y);
    }
}
```

- 캡슐화의 장점을 제공하지 못함
- 캡슐화의 장점(item15참고) : 개발 속도를 높일 수 있고, 관리 비용을 낮출 수 있고, 성능 최적화에 도움이 되고, 재사용성도 높아지고, 개발 난이도도 낮춰줌
- 만약 x, y를 고치려고 하면, Point를 사용하고 있는 모든 곳에서 고쳐줘야 함.
- x, y와 같은 필드에 제약을 주려면 부가적인 작업이 필요한데, 필드에 바로 접근하게 되면 그런 것들을 처리가 불가능해짐.

### 접근자와 변경자 메서드를 활용해 캡슐화 적용

```java
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        // 부가 작업
        return x;
    }
    public double getY() { return y; }

    public void setX(double x) {
        // 부가 작업
        this.x = x;
    }
    public void setY(double y) { this.y = y; }
}
```

- 필드 이름을 바꾸더라도 메서드 이름 유지 가능(그나마 조금 더 유연)
- 메서드를 통해 접근하기 때문에, 부가적인 작업 가능(get 해올 때 값을 검증한다거나, 값의 형태를 원하는 대로 변형해주거나 등등)

### package-private 경우

```java
class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        // 부가 작업
        return x;
    }
    public double getY() { return y; }

    public void setX(double x) {
        // 부가 작업
        this.x = x;
    }
    public void setY(double y) { this.y = y; }
}
```

- public 클래스 아님
- package-private의 경우, 이 패키지를 소유하고 있는 개발자(개인 혹은 팀)만 쓰는 코드이기 때문에, 그나마 이 클래스의 변경에 따른 side-effect가 없다. (라고 책에는 나온다)
- 하지만 그렇다 하더라도 필드에 직접 접근하는 것보다는 메서드를 통해 접근하는 것이 더 안전한 방법. (예를 들어, 필드 이름을 바꾸고 싶을 때, 필드 이름부터 바꾼 다음 메서드 이름을 바꾸는 등 점진적인 변화로 에러 발생을 최소화시키려고)

### public 클래스에서 필드를 노출하되 불변으로

```java
public final class Time {
    private static final int HOURS_PER_DAY    = 24;
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        if (hour < 0 || hour >= HOURS_PER_DAY)
            throw new IllegalArgumentException("Hour: " + hour);
        if (minute < 0 || minute >= MINUTES_PER_HOUR)
            throw new IllegalArgumentException("Min: " + minute);
        this.hour = hour;
        this.minute = minute;
    }

    // 나머지 코드 생략
}
```

- public한 필드지만 final 키워드를 통해 불변으로 만들어 줌으로써, 단점(필드에 직접 접근가능하여 변경해버리는 것, 언제 어디서 값이 변경될지 모름..)을 없앤다.
- 하지만 여전히 메서드의 장점은 취하지 못함…

### Dimension 클래스 성능 문제

```java
public class DimensionExample {

    public static void main(String[] args) {
        Button button = new Button("hello button");
        button.setBounds(0, 0, 20, 10);

        Dimension size = button.getSize();
        System.out.println(size.height);
        System.out.println(size.width);
    }

}
```
<img width="668" alt="스크린샷 2023-11-04 오후 4 04 53" src="https://github.com/effective-java-study-with-business/effective-java/assets/79031788/23ef9698-8cd9-4c57-92dc-c6e9331e8bc7">



- public 하게 노출한 Dimension 클래스
- 언제 어디서라도 Dimension 클래스는 필드 값에 대한 신뢰가 떨어지므로 값을 카피해서 써야함. (자원 낭비, 추후 아이템67참고) - 한두개 카피하는 것이 아니라 수백만개 카피할 경우

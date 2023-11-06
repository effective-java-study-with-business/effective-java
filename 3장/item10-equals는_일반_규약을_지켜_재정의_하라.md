# [Item 10] equals는 일반 규약을 지켜 재정의 하라
## 1. equals 메소드를 재정의 할 필요가 없는 cases
1. 인스턴스가 본질적으로 고유한 경우 : 동작을 위한 Class
2. 인스턴스 간의 논리적 동치성<sup>[1]</sup>을 검사할 필요가 없는 경우
3. 상위 클래스에서 재정의한 equals가 하위 클래스에도 그대로 사용할 수 있는 경우
4. 클래스가 private, package-private여서 equals를 호출할 필요가 없는 경우
    - 이 경우는 equals를 Override하여 사용할 수 없도록 미리 막아두는 것도 좋다.
    ```java
    @Override
    public boolean equals(Object o) {
        throw new RuntimeException("No access allowed.");
    }
    ```

## 2. 그렇다면 언제 equals를 재정의 하여야 하는가?
-  상위 클래스가 두 인스턴스 간의 논리적 동치성을 비교하도록 재정의 되지 않았을 경우
    1. Integer, String 등의 **값 클래스**
        - But, 값 클래스여도 동일한 값을 가진 인스턴스가 둘 이상만들어 지지 않는다면 equals를 재정의할 필요가 없다.(ex : Enum)
    2. equals를 재정의 하면 Map, Set과 같은 자료구조에서 원소로 사용 가능

### equals 메소드 재정의 시 만족해야 하는 규약
1. 반사성
    - x != null
    - x.equals(x)
    - x = x
2. 대칭성
    - x != null && y != null
    - x.equals(y) = true -> y.equals(x) = true
    - x = y -> y = x
3. 추이성
    - x != null && y != null && z != null
    - x.equals(y) = true, y.equals(z) = true -> x.equals(z) = true
    - x = y, y = z -> x = z
4. 일관성
    - x != null && y != null
    - x.equals(y) = true인 경우 x.equals(y)는 항상 true
5. null 아님
    - x != null
    - x.equals(null) = false

### 각 규약 별 예시
1. 대칭성(x = y -> y = x)
    - 새로 정의한 CaseInsensitiveString은 재정의한 equals 메소드 내에서 String 인스턴스까지 비교가 가능하지만,
    - String은 CaseInsensitiveString 인스턴스에 대한 비교를 하지 않으므로(String은 해당 클래스의 존재도 모른다) 대칭성이 위배됨!!
    ```java
    public final class CaseInsensitiveString {
        private final String s;

        public CaseInsensitiveString(String s) {
            this.s = Objects.requireNonNull(s);
        }

        // wrong code - invalid symmetry
        // @Override
        // public boolean equals(Object o) {
        //     if(o instanceof CaseInsensitiveString)
        //         return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
            
        //     if(o instanceof String)
        //         return s.equalsIgnoreCase((String) o);

        //     return false
        // }

        // correct code
        public boolean equals(Object o) {
            return (o instanceof CaseInsensitiveString) &&
                    ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
        }
    }
    ```
2. 추이성(x = y, y = z -> x = z)
    - 상위 클래스에 없는 새로운 필드를 하위 클래스에서 추가 정의 시, 새로운 필드가 equals에 영향을 준다고 가정
    - OOP에서는 concrete class를 확장(extends)하여 새로운 필드 추가 시 equals 규약을 만족시킬 방법 없음
    - 또한, 리스코프 치환 원칙(부모 클래스의 메소드가 자식 클래스에서도 동일하게 작동해야 함)을 위배하지 않으려면 **instanceof**로 타입 비교를 해야함
        - getClass 메소드로 비교 시, 해당 클래스의 정확한 class로만 비교되므로, 상속 관계로 비교할 수 없음
    - 따라서, equals 규약을 깨지 않고 사용하려면 상속보다는 사용하려는 class를 private 필드로 두어 사용하는 것이 바람직
    ```java
    public class ColorPoint {
        private final Point point;
        private final Color color;

        public ColorPoint(int x, int y, Color color) {
            this.point = new Point(x, y);
            this.color = Objects.requireNonNull(color);
        }

        // view method
        public Point asPoint() {
            return this.point;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof ColorPoint)) 
                return false;

            ColorPoint cp = (ColorPoint) o;

            // Point and Color class have overridden equals method.
            return cp.point.equals(this.point) 
                    && cp.color.equals(this.color);
        }

    }
    ```
3. 일관성
    - 불변 객체는 한번 equals로 false일 경우 끝까지 false여야함
    - 또한, equals의 판단에 신뢰할 수 없는 자원으로 비교해서는 안됨
        - ex) URL의 equals에서 host와 IP 주소가 같은지 비교 : 네트워크를 타야하므로 잘못 구현된 경우
4. null-아님
    - instanceof 연산자로 객체 확인 시, 첫번째 피연산자가 null일 경우 무조건 false
    - 따라서, instanceof 연산자를 사용하면 명시적으로 null인지 확인할 필요가 없음

## 정리
1. 자기 자신의 참조인지 확인을 위해 == 연산자를 사용한다 : 성능 최적화
2. instanceof 연산자로 파라미터가 올바른 타입인지 확인한다
3. 파라미터로 들어온 객체와 자신(this)의 대응되는 핵심 필드가 일치하는지 확인한다
4. 단, Float와 Double의 경우는 compare 메소드로 비교해야 한다
    - 부동 소수점이기 때문에
5. equals 재정의 시에는 hashCode도 재정의가 필요하다
6. Overriding을 위하여 파라미터로는 반드시 Object를 받도록 한다
    ```java
    // Class : Foo
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Foo)) return false;
        Foo foo = (Foo) foo;
        return ... ; // compare fields
    }
    ```

## @AutoValue
- 구글에서 제공하는 equals/hashCode를 재정의해주는 framework
- immutable abstract class인 경우 equals 메소드를 재정의
- abstract class 위에 annotation을 사용하면 됨<sup>[2]</sup><sup>[3]</sup>
```java
@AutoValue
public abstract class Parent {
    public abstract String getParentId();
    public abstract long getOrder();
    
    public static Parent create(String parentId, int order) {
        return new Children(parentId, order);
    }
}
```
```java
@Getter
@AutoValue
public final class Children extends Parent {
    private final String parentId;
    private final int order;
    
    AutoValue_AutoValueMoney(String parentId, int order) {
        this.parentId = Objects.requireNonNull(parentId);
        this.order = Objects.requireNonNull(order);
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        // hashCode logics...
        return h;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Parent) {
            Parent parent = (Parent) o;
            return (this.parentId.equals(parent.getParentId()))
              && (this.order == parent.getOrder());
        }
        return false;
    }
}
```

## References
[1] https://namu.wiki/w/%EB%8F%99%EC%B9%98#s-2.2 <br>
[2] https://github.com/google/auto/blob/main/value/userguide/index.md<br>
[3] https://www.baeldung.com/introduction-to-autovalue

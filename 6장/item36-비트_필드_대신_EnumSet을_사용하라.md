# [Item 36] 비트 필드 대신 EnumSet을 사용하라
## 1. Bit Field
- 열거한 값들이 집합으로 사용될 경우, 각 값을 2^n(n = 서로 다른 임의의 값) 값을 할당하여 열거하는 식으로 사용했었음
- 여러 값들을 사용하기 위해 각 값의 비트별 OR 연산(|)을 통해 하나로 표현하는 것이 **비트 필드**

```java
import lombok.Getter;

public class Arrow {
    public static final int BOLD = 1 << 0; // 1
    public static final int DOTTED = 1 << 1; // 2
    public static final int FILLED = 1 << 2; // 4

    @Getter
    private int currentStyle;

    public Arrow(int style) {
        this.currentStyle = style;
    }

    public void applyStyle(int style) {
        this.currentStyle = style;
    }
}
```
```java
public class BitFieldTest {
    public static void main(String[] args) {
        Arrow arrow = new Arrow(Arrow.BOLD | Arrow.DOTTED);
        System.out.println(arrow.getCurrentStyle()); // 3

        arrow.applyStyle(Arrow.BOLD | Arrow.DOTTED | Arrow.FILLED);
        System.out.println(arrow.getCurrentStyle()); // 7
    }
}

```

## 2. Side Effects of Bit Field
- 비트 필드는 정수 열거 상수의 단점 + 추가 단점이 존재함
### 정수 열거 상수의 단점
1. 타입 안정성 보장하지 않음
   - 일반 정수와 == 연산이 가능하기 때문에 다른 열거 상수와도 비교가 가능할 수 있음
2. 표현력이 좋지 않다
   - 값만 보고 어떤 것을 표현하는지 추론 불가
   - 문자열로 출력하기도 까다로움
### 비트 필드의 추가적인 단점
1. 위와 같이 비트필드를 그대로 출력 시, 해석하기가 어려움
2. 비트 필드 하나에 녹아 있는 모든 원소를 순회하기 까다로우
3. 최대 몇 비트가 필요한지 API 작성 시 미리 예측 필요
   - int 내지는 long 선택 필요
   - API를 수정하지 않고는 최대 비트 수를 늘릴 수 없음

## 3. EnumSet?
- java.util 패키지에서 제공하는 Set 인터페이스 기반의 자료구조
- Enum을 Set 자료구조에 보관하므로 타입 안전!
- 다른 어떤 Set 구현체와도 함께 사용 가능
- EnumSet의 내부는 비트 벡터로 구현됨
- 원소가 64개 이하라면 원소를 long 변수 하나로 표현하여 비트 필드와 비슷한 성능을 냄 : RegularEnumSet
  - xxxAll 과 같은 대량 작업은 산술 연산을 써서 구현
- 원소가 64개 초과라면 배열로 원소를 관리함 : JumboEnumSet
- 그러나, EnumSet은 여전히 java 내부에서는 불변 EnumSet을 만들 수 없다..!
    ```java
    package java.util;
    
    public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E>
        implements Cloneable, java.io.Serializable {
        // The class of all the elements of this set.
        final transient Class<E> elementType;
        
        // All of the values comprising E.  (Cached for performance.)
        final transient Enum<?>[] universe;
    
        // Constructor
        
        public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
            Enum<?>[] universe = getUniverse(elementType);
            if (universe == null)
                throw new ClassCastException(elementType + " not an enum");
    
            if (universe.length <= 64)
                return new RegularEnumSet<>(elementType, universe);
            else
                return new JumboEnumSet<>(elementType, universe);
        }
        // ...
    }
    ```
    ```java
    package java.util;
    
    class RegularEnumSet<E extends Enum<E>> extends EnumSet<E> {
        /**
         * Bit vector representation of this set.  The 2^k bit indicates the
         * presence of universe[k] in this set.
         */
        private long elements = 0L;
    
        RegularEnumSet(Class<E> elementType, Enum<?>[] universe) {
            super(elementType, universe);
        }
    
        void addRange(E from, E to) {
            elements = (-1L >>> (from.ordinal() - to.ordinal() - 1)) << from.ordinal();
        }
    
        void addAll() {
            if (universe.length != 0)
                elements = -1L >>> -universe.length;
        }
        // ...
    }
    ```
### 위의 Arrow를 EnumSet을 활용하여 바꾼 예시
```java
import lombok.Getter;
import java.util.Set;

public class Arrow2 {

    public enum Style {
        BOLD, DOTTED, FILLED
    }

    @Getter
    private Set<Style> currentStyle;

    public Arrow2(Set<Style> style) {
        this.currentStyle = style;
    }

    public void applyStyle(Set<Style> style) {
        this.currentStyle = style;
    }
}
```
```java
import java.util.EnumSet;

public class EnumSetTest {
    public static void main(String[] args) {
        Arrow2 arrow = new Arrow2(EnumSet.of(Arrow2.Style.BOLD, Arrow2.Style.DOTTED));
        System.out.println(arrow.getCurrentStyle()); // [BOLD, DOTTED]

        arrow.applyStyle(EnumSet.allOf(Arrow2.Style.class));
        System.out.println(arrow.getCurrentStyle()); // [BOLD, DOTTED, FILLED]
    }
}
```
- 매개변수에 EnumSet을 명시한 것이 아닌 Set인 이유는 클라이언트가 어떤 자료형을 보낼 지 모르기 때문에 호환성을 높이고자 함
- 매개변수는 인터페이스로 받는 것이 좋은 습관!

## References
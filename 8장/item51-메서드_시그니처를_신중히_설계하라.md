# [Item 51] 메서드 시그니처를 신중히 설계하라
## 1. 메서드 이름을 신중히 짓기
- 표준 명명 규칙 따르기 [ref. Item 68]
```text
<< 패키지 >>
1. 패키지는 요소를 점으로 구분하여 계층적으로 지음
2. 각 요소는 대부분 8자 이하의 소문자로 이루어짐
    - 너무 길다면 약어를 사용하는 것도 좋음 ex) util(utilities), awt(Abstract Window Toolkit)
3. 조직 밖에서도 사용되는 패키지면 조직의 인터넷 도메인 이름을 역순으로 사용 ex) com.google

<< 클래스 & 인터페이스 >>
1. 1개 이상의 단어로 구성하며 각 단어는 대문자로 시작 ex) File, HttpConnection
2. 널리 통용되는 줄임말을 제외하고는 약어 사용 지양

<< 메소드 >>
1. 클래스 & 인터페이스와 동일하게 1개 이상의 단어로 구성하고 각 단어는 대문자로 시작하되,
   맨 첫 단어는 소문자 사용 ex) addAll, containsValue
2. 널리 통용되는 줄임말을 제외하고는 약어 사용 지양
```
- 긴 이름 피하기
- 자바 라이브러리의 API 참조해보기
  - ex) Collections.java (Java Collection API에서 여러 유용한 연산을 대신 해주는 Util성 클래스이므로 참고하기 좋음)

## 2. 편의 메서드를 너무 많이 만들지 않기
- 클래스나 인터페이스는 자신의 각 기능을 완벽히 수행하는 메서드로 제공
- 자주 쓰일 경우에만 별도의 약칭 메서드로 두기 ex) min, max, isEmpty
- **확신이 서지 않으면 만들지 않기**
- 사용자의 눈높이메 맞춰 기본 기능을 조합하여 만들 수 있도록 제공하는 것도 좋은 방법(직교성)
  - 이후 매개변수 목록을 짧게 유지하는 것과 밀접한 연관

## 3. 매개변수 목록은 짧게 유지
- **4개 이하**가 좋음
- 같은 타입의 매개변수 여러 개가 연달아 나오는 것은 최대한 피하기

### 매개변수를 줄일 수 있는 방법을 알아보자.txt
1. 여러 메소드로 쪼갠다.
    - 직교성이 높아지는 효과!
    ```text
    직교성이란?
    - 사전적인 뜻 : 두 벡터에는 서로 영향을 주는 성분이 없음
    - SW 설계 영역 : 공통점이 없는 기능이 잘 분리됨 -> 기능을 원자적으로 쪼개서 제공
    => 결국 API도 기본 기능만 잘 갖추면 사용자가 알아서 조합하여 복잡한 기능도 구현 가능!
       그런 의미에서 편의 메소드 개수도 줄어든다는 것!
    - 모든 기능을 다 쪼갠다기보단, 추상화 수준에 맞게 조절해야 함
    ex) MSA(직교성이 높음) <-> 모놀리식(직교성이 낮음)
    ```
    ex) java.util.List 인터페이스를 사용하는 자료구조(ArrayList)에서 부분 리스트의 인덱스 찾기
    ```java
    package java.util;
   
    public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    
        // ...
    
        public int indexOf(Object o) {
            return indexOfRange(o, 0, size);
        }

        int indexOfRange(Object o, int start, int end) {
            Object[] es = elementData;
            if (o == null) {
                for (int i = start; i < end; i++) {
                    if (es[i] == null) {
                        return i;
                    }
                }
            } else {
                for (int i = start; i < end; i++) {
                    if (o.equals(es[i])) {
                        return i;
                    }
                }
            }
            return -1;
        }
        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList<>(this, fromIndex, toIndex);
        }
        // ...
    }
    ```
    ```java
    import java.util.List;
    
    public class SubListIndexMain {
        public static void main(String[] args) {
            List<String> languages = List.of("C", "Python", "Kotlin", "Java", "JavaScript");
            List<String> referredLangs = languages.subList(2, 4); // [Kotlin, Java]
            System.out.println(referredLangs.indexOf("Java")); // 1
        }
    }    
    ```
2. 매개변수 여러 개를 묶어주는 도우미 클래스를 만듬
```java
import lombok.Builder;

public class CardGame {

    public enum Shape {
        DIA, HEART, CLOVER, SPADE
    }

    // helper class
    public static class CardAttribute {
        private int number;
        private Shape shape;
        private String color;

        @Builder
        public CardAttribute(int number, Shape shape, String color) {
            this.number = number;
            this.shape = shape;
            this.color = color;
        }
    }

    // Parameter with helper class is easier to use
    public void pickup(String gamer, CardAttribute card) {
        System.out.printf("%s picked up %s %s %d card!", gamer, card.shape, card.color, card.number);
    }

    public void pickup(String gamer, int number, Shape shape, String color) {
        System.out.printf("%s picked up %s %s %d card!", gamer, shape, color, number);
    }

}
```
```java
public class CardGameMain {
    public static void main(String[] args) {
        CardGame game = new CardGame();

        // Order should be checked...
        game.pickup("Minah", 7, CardGame.Shape.DIA, "Red");

        CardGame.CardAttribute card = CardGame.CardAttribute.builder()
                .shape(CardGame.Shape.DIA)
                .color("Red")
                .number(7)
                .build();

        game.pickup("Minah", card);
    }
}
```
3. 객체 생성에 사용한 빌더 패턴을 메서드 호출에 응용
    - 1번과 2번을 혼합한 것(위의 코드의 builder 패턴 참조)
    - 매개변수가 많지만, 일부만 있어도 괜찮은 경우에 사용
    - 모든 매개변수를 하나로 추상화 한 객체 정의
    - 클라이언트에서 세터를 호출하여 필요한 값 설정 및 유효성 검사

## 4. 매개변수의 타입으로는 인터페이스 사용
- 매개변수로 적합한 클래스가 있는 경우 클라이언트에게 선택권을 줄 수 있음
- 특정 클래스에 의존하여 구현해야하는 경우가 아니라면, 구현체를 파라미터로 제한하는 것은 클라이언트가 복사 비용을 치뤄 사용해야 함
- ex) 매개변수에 ArrayList 대신 List를 받아 로직 구현

## 5. boolean 보다는 원소 2개짜리 열거 타입을 사용하기
- Enum의 경우는 별도의 이름과 값이 명명되어 있으므로 읽기 쉬워짐
- 이후 다른 원소가 추가될 필요가 있을 때 변경하기 용이함
```java
public class Astronomy {

    public enum CelestialBody {
        STAR, PLANET // it can be added freer than boolean
        // COMET, BLACK_HOLE ... etc are available! 
    }

    // This would be better than boolean parameter
    public static void light(CelestialBody astro) {
        if(astro == CelestialBody.PLANET)
            System.out.println("sparkle");
    }

    public static void light(boolean isStar) {
        if(isStar)
            System.out.println("sparkle");
    }

}
```
```java
public class AstronomyMain {
    public static void main(String[] args) {
        Astronomy.light(true);
        Astronomy.light(Astronomy.CelestialBody.STAR);
    }
}
```


## References
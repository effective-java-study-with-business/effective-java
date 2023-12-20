# [Item 30] 이왕이면 제네릭 메서드로 만들라
## 1. Generic Method
- 제너릭 타입처럼 타입 매개변수를 지정하여 작성하는 메소드
- 아래 예시처럼 **메소드의 접근 제한자와 반환 타입 사이**에 타입을 적으면 됨
- Object로 객체를 주고 받는 것보단 명시적인 형변환이 필요 없고 안전함
```java
import java.util.*;

class SetUtil {
   public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
      Set<E> result = new HashSet<>(s1);
      result.addAll(s2);
      return result;
   }
}
```
- 위의 예시에서 반환 타입, 매개변수 2개의 타입이 모두 같아야 함
- 다를 경우에는 한정적 와일드카드 타입(?)을 사용하여 유연하게 대처 가능

## 2. Generic Singleton Factory
- 불변 객체를 어떤 타입으로도 활용할 수 있게 만들때 유용
- 제네릭은 컴파일 단계가 아닌 런타임 시에 타입 매개변수 정보가 소거됨
- 하나의 객체를 어떤 타입으로든 매개변수화 가능
- 이때, 요청한 타입 매개변수에 맞게 그 객체의 타입을 바꿔주는 정적 팩터리 생성 필요
- 이것이 제네릭 싱글턴 팩터리! - Collections에서 자주 애용

```java
public class Collections {
    // 1st example of Generic Singleton Factory pattern
    public static final <T> Set<T> emptySet() {
        return (Set<T>) EMPTY_SET;
    }
    
    // 2nd example of Generic Singleton Factory pattern
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> reverseOrder() {
        return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
    }

    private static class ReverseComparator implements Comparator<Comparable<Object>>, Serializable {
        @java.io.Serial
        private static final long serialVersionUID = 7207038068494060240L;

        static final ReverseComparator REVERSE_ORDER = new ReverseComparator();

        public int compare(Comparable<Object> c1, Comparable<Object> c2) {
            return c2.compareTo(c1);
        }

        @java.io.Serial
        private Object readResolve() { return Collections.reverseOrder(); }

        @Override
        public Comparator<Comparable<Object>> reversed() {
            return Comparator.naturalOrder();
        }
    }
}
```

### 항등함수로 작성해보는 제네릭 싱글턴 팩터리
- 항등함수?
  - 모든 원소를 자기 자신으로 대응하는 함수 : f(x1) = x1, f(x2) = x2 ....
  - 자바에서의 항등 함수는 입력 값의 변화 없이 자기 자신을 반환하는 함수를 의미<sup>[1]</sup>
  - 이미 자바는 Function.identity에 아래와 같이 정의가 되어있음
    ```java
    @FunctionalInterface
    public interface Function<T, R> {
        static <T> Function<T, T> identity() {
            return t -> t;
        }
    }
    ```
    
- 연습을 위해 아래와 같이 만들고 적용할 수 있음
```java
import java.util.List;

public class UnaryOperator<T> {
    private static UnaryOperator<Object> IDENTITY_FUNC = (t) -> t;

    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunc() {
        return (UnaryOperator<T>) IDENTITY_FUNC;
    }

    public static void main(String[] args) {
        List<String> subjects = List.of("공업수학", "자료구조", "알고리즘");
        UnaryOperator<String> sameSubject = identityFunc();
        for(String subject : subjects)
            System.out.println(sameSubject.apply(subject));
        
        List<Double> scores = List.of(4.0, 3.5, 4.0);
        UnaryOperator<Double> sameScore = identityFunc();
        for(Double score : scores)
            System.out.println(sameScore.apply(score));
    }
}
```

## 3. Recursive Type Bound
- 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위 한정
- 주로 비교 연산을 위한 Comparable 인터페이스와 함께 쓰임
- 아래 예시는 Comparable을 implements한 클래스만 타입 매개변수로 받겠다고 명시한 메소드

```java
public class ComparableUtil {
    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c == null || c.isEmpty())
            throw new IllegalArgumentException("No element in collection");

        // initialization
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);
        }
        
        return result;
    }
}
```

## References
[1] https://taeu.kr/18

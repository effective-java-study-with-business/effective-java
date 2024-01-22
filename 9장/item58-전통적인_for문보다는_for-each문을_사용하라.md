# [Item 58] 전통적인 for문보다는 for-each문을 사용하라

## 1. for VS for-each
- for문의 경우 인덱스를 명시하여 배열, collection을 순회하므로 관리할 요소 종류 증가
```java
// TODO auto-completed keywords examples
```
- 향상된 for문 사용 : for-each문은 인덱스 관리 없이 원소를 순회하므로 가독성이 높음
```java
// TODO auto-completed keywords examples
```
- 중첩 반복문을 사용 시에는 인덱스 접근이 필요한 구문이 아니라면 for-each가 더더욱 좋다!
  - 특히 Iterator을 이용하여 next() 메소드로 접근 시에는 for-each를 사용하자(권장!)
```java
// TODO auto-completed keywords examples..............
```

## 2. Conditions of Neglect Usage for-each
1. destructive filtering : remove 메소드 호출 시
    - removeIf 메서드를 사용하여 명시적으로 컬렉션을 순회하는 일을 회피 가능
2. transforming : 리스트나 배열 순회 시 원소의 값 or 전체 교체 시, 인덱스 사용 권장
3. parallel iteration : 병렬 순회 시, 각 반복자와 인덱스 변수를 사용하여 명시적 제어 필요

## 3. Iterable Interface?
- for-each문은 iterable 인터페이스를 구현한 객체 : 무엇이든 순회 가능
- Iterable 인터페이스는 메서드가 단 한개 뿐!
```java
package java.lang;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public interface Iterable<T> {
    // iterator can call enhanced for statement
    Iterator<T> iterator();

    // for-each method : can call .forEach(Consumer)
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : this) {
            action.accept(t);
        }
    }
    
    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
}

```
- 특정 클래스에 순회 동작이 예상된다면 Collection까지는 아니어도 Iterable을 구현하는 방향으로 구상하기
  - 위의 interface처럼 forEach를 사용할 수 있으므로 압도적 감사!

## 3. Conclusion
- index가 명시적으로 필요하거나 2번에 부합한 내용이 아니라면 **모든 for 구문을 for-each로 변경하자**

## References
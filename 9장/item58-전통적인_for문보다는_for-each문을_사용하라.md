# [Item 58] 전통적인 for문보다는 for-each문을 사용하라

## 1. for VS for-each
### For
- for문의 경우 인덱스를 명시하여 배열, collection을 순회하므로 관리할 요소 종류 증가
- index를 이용하여 접근하기 때문에, collection의 ArrayList나 array 순회 시에 빠름<sup>[1]</sup>
  - LinkedList를 for문으로 돌리면 느리다!<sup>[2][3]</sup>
```java
import java.util.*;

public class KeywordUtil {
    public void processKeywords(List<String> keywords) {
        for (int i = 0; i < keywords.size(); i++) {
            if(keywords.get(i).length() == 0)
                continue;
            
            log.debug(keywords.get(i));
            // other logics without processing data via index
        }
    }
}
```
### For-Each
- 향상된 for문 사용 : for-each문은 인덱스 관리 없이 원소를 순회하므로 가독성이 높음
- collection 내부의 Iterator을 사용하여 순회함 : LinkedList 성능이 괜찮음<sup>[2][3]</sup>
- Random Access<sup>Ref)</sup>가 존재하지 않는 Collection에서 강함!
- 인덱스는 무조건 1씩 증가하여 순회
```java
import java.util.*;

@Slf4j
public class KeywordUtil {
    public void processKeywords(List<String> keywords) {
        for (String keyword : keywords) {
            if(keyword.length() == 0)
                continue;

            log.debug(keyword);
            // other logics without processing data via index
        }
    }
}
```
- 중첩 반복문을 사용 시에는 인덱스 접근이 필요한 구문이 아니라면 for-each가 더더욱 좋다!
  - 특히 Iterator을 이용하여 next() 메소드로 접근 시에는 for-each를 사용하자(권장!)
```java
import java.util.*;

public class KeywordUtil {
    public void processKeywords(Iterator<String> keywords) {
        for (;keywords.hasNext();) {
            // must assign local variable due to avoid NoSuchElementException
            String keyword = keywords.next();
            if(keyword.length() == 0)
                continue;

            log.debug(keyword);
            // other logics without processing data via index
        }
    }

    public void processKeywords(Iterator<String> keywords) {
        for (String keyword : keywords) {
            if(keyword.length() == 0)
                continue;

            log.debug(keyword);
            // other logics without processing data via index
        }
    }
}
```

### Ref) Random(Direct) Access?<sup>[4]</sup>
- 배열(array)의 특정 element에 이동, 삽입 등의 작업 시, 그 위치로 바로 가는 방식
  - 배열의 0번째 element(혹은 head)부터 타고타고 가는 것이 아님!
- 배열은 연속적인 공간에 elements가 위치해있고, 컴파일러는 해당 배열의 pointer를 찾음
  - 이것을 base address라고 칭할 수 있음
- 각 자료형은 파일의 크기가 정해져있고, 어떤 작업(접근)을 할 인덱스가 주어지면 단순한 formula로도 특정 element에 바로 접근 가능
  - 1차원 배열의 경우는 아래와 같다고 보면 됨
  ```
  Real Memory Location of Index = Base Address + (Size of Element * Index)
  ```
- Collection에는 특정 index에 Random(Direct)으로 Access가 불가함
- 하지만, 일반 array의 경우에는 Random Access가 가능

## 2. Conditions of Neglect Usage for-each
1. destructive filtering : remove 메소드 호출 시
    - removeIf 메서드를 사용하여 명시적으로 컬렉션을 순회하는 일을 회피 가능
```java
import java.util.*;
   
public class KeywordUtil {
    public void removeEmptyKeywords(List<String> keywords) {
        // better examples
        keywords.removeIf(keyword -> keyword.length() == 0);
        
        // this can be run, but it's not good at readability
        for(int i=0;i<keywords.size();i++) {
            if(keywords.get(i).length() == 0)
                keywords.remove(i);
        }
    }
}   
```
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

### Concurrency Problems between for statement and forEach()
- 

## 3. Conclusion
- index가 명시적으로 필요하거나 2번에 부합한 내용이 아니라면 **모든 for 구문을 for-each로 변경하자**

## References
[1] https://braindisk.tistory.com/156
[2] https://stackoverflow.com/questions/11555418/why-is-the-enhanced-for-loop-more-efficient-than-the-normal-for-loop
[3] https://wnwngus.tistory.com/57
[4] https://inside.caratlane.com/arrays-understanding-the-random-access-3d07983b20ca
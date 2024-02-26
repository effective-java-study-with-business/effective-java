# [Item 69] 예외는 진짜 예외 상황에만 사용하라
## 1. 누구인가. 누가 이렇게 예외를 사용하는가?
- 예외를 사용한 코드 vs 일반 관용구를 사용한 코드<sup>[1]</sup>
```java
import java.util.stream.Stream;

public class Comparison {

    public static void main(String[] args) throws InterruptedException {
        Bird[] birds = Stream.generate(Bird::new)
                .limit(100000000)
                .toArray(Bird[]::new);

        withException(birds);

        withFor(birds);

        withForEach(birds);
    }

    public static void withException(Bird[] birds) {
        long start = System.nanoTime();

        try {
            int index = 0;
            
            while(true)
                birds[index++].flying();
            
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        
        System.out.printf("Exception Statement : %s\n", System.nanoTime() - start);
    }

    public static void withFor(Bird[] birds) {
        long start = System.nanoTime();

        for(int index=0;index<birds.length;index++)
            birds[index].flying();

        System.out.printf("For Statement : %s\n", System.nanoTime() - start);
    }

    public static void withForEach(Bird[] birds) {
        long start = System.nanoTime();

        for (Bird bird : birds)
            bird.flying();

        System.out.printf("For Each Statement : %s\n", System.nanoTime() - start);
    }

}
```
- 어차피 JVM은 배열에 접근할 때마다 배열의 bound를 넘지 않는지 검사
- 가독성은 당연 for-each문이 가장 압도적
- 성능은 대체적으로 exception이 제일 구림
  - pc의 상태에 영향을 받아 일부 케이스의 경우에는 for, foreach가 느리나, 자주 있는 케이스는 아님
  - 책에는 원소 100개짜리로 2배의 성능 차이가 났다고 하나.. 실제 자세한 코드가 없어 어떻게 테스트를 했는지는 **몰?루**
  ```java
  // Exception
  try {
      int i=0;
      while(true)
          range[i++].climb();
  } catch(ArrayIndexOutOfBoundsException e) {}
    
  // For Each
  for(Mountain m : range)
      m.climb();
  ```
- exception은 예외 상황에 필요한 용도로 만들어졌으므로, 빠른 성능에 대한 동기가 약함
  - 실제로 위의 코드는 try 블록에서 예외를 만들어서 catch 블록으로 던짐
  - 생각보다 **예외 객체 생성에 비용이 많이 든다** <sup>[2][3]</sup>
- 코드를 try-catch 블록에 넣으면 JVM이 적용할 수 있는 최적화가 제한
  - 다른 블럭에 있는 객체 참조 등의 경우에 생길 sudo?
- 배열 순회 시, 표준 관용구(for, foreach)는 JVM이 알아서 중복 검사를 하지 않도록 최적화 함 
- **따라서 배열에 더 이상 원소가 없어 반복문을 반복하지 못하는건 예외가 아니라 지극히 정상적인 일이다**

## 2. API에서의 예외 처리
- 좋은 API는 정상적인 제어 흐름 시, 예외를 호출할 일이 없음

### Case 1) 상태 검사 메서드
- 특정 상태에서만 호출할 수 있는 **상태 의존적** 메서드를 사용하는 클래스는 반드시 **상태 검사** 메서드도 함께 제공
  - 그래야 예외 없이 상태를 검사하여 상태 의존적 메서드를 사용해도 될지 말지 클라이언트가 결정할 수 있기 때문!
  - ex) Iterator의 next()와 hasNext()
  ```java
  // if hasNext is exist
  for(Iterator<Integer> iter = originalCollection.iterator();iter.hasNext();) {
    int current = iter.next();
    // other logics ...
  }
  
  // without hasNext? -> DO NOT USE THIS ONE !!!
  try {
    Iterator<Integer> iter = originalCollection.iterator();
    while(true) {
        int current = iter.next();
        // other logics ...
    } catch (NoSuchElementException e) {}
  }
  ```
- 대체적으로 대부분의 케이스에는 상태 검사 메서드를 제공하는 편이 가독성이 좋고 발견하기가 좋음
  - 상태 검사 메서드는 호출을 잊었을 시, 상태 의존적 메서드가 예외를 던지기 때문에 빠르게 알아차릴 수 있음
    
### Case 2) Empty optional OR 특수한 값(Null etc.) 반환
- 상태 검사 메서드 대신 **특수한 값**으로 제어를 하는 cases
  1. 외부 동기화 없이 여러 스레드가 동시에 접근 가능
  2. 외부 요인으로 상태 변경 가능
     - 상태 검사 ~ 상태 의존적 메서드 사용 사이에 값이 변할 수 있으므로 별개의 flag를 두는게 나음
  3. 성능이 중요한 상황에서 상태 검사 메서드가 상태 의존적 메서드의 작업 일부를 중복 수행 시
- Cautions : flag나 Null 등의 특수한 값을 쓸 때는 반드시 명시적으로 체크를 해야 함
  - 미 검사 시 예외를 발견하기 어려울 수 있음
  - 자신이 없다면 Optional을 사용하여 무조건 체크하는 것도 좋음

## 3. Conclusion
- 예외는 예외 상황에만 사용하고, 절대로 일상적인 제어 흐름용으로는 쓰지 말아야 함
- 잘 설계된 API는 클라이언트가 정상적인 제어 흐름에서 예외를 사용할 일이 없게 해야함

## References
[1] https://madplay.github.io/post/measure-elapsed-time-in-java
[2] https://www.nowwatersblog.com/cs/Exception#%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC
[3] https://recordsoflife.tistory.com/1376#3.%20%EC%84%B1%EB%8A%A5%20%EC%B8%A1%EC%A0%95
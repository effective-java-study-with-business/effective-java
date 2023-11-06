# [Item 19] 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라
## 1. 상속을 고려한 설계?
1. 메소드 재정의 시에 일어나는 일을 정확히 정리하여 문서화
    - 상속을 위한 클래스는 재정의 가능한 메소드들을 내부적으로 어떻게 이용하는지 문서로 남겨야 함
    - 공개된 메소드에서 클래스 자신의 메소드를 호출할 수 있기 때문에 유의해야 함
2. 코드 내에서 @implSpec 태그(Implementation Requirements)를 달면 자바독에서 메소드의 내부 동작 방식을 설명할 수 있도록 문서 생성
    - API가 '어떻게' 작동하는지 작성하는 것은 좋은 API 문서의 작성 방법은 아니지만, **상속이 캡슐화를 위배**하므로, 안전한 상속을 위해서라면 어쩔 수 없다.
    - IDE 내에서 보고싶다면 -tag "implSpec:a:Implementation Requirements:"를 명령줄 매개변수로 지정하면 된다.
        - IntelliJ의 경우 우측 상단의 실행 아이콘 좌측의 콤보박스 > Edit Configurations > Build and run의 텍스트 박스에 해당 매개변수를 추가하면 된다.<sup>[1]</sup>
   ```
   @implSpec 예시
   - List.java의 sort(Comparator<? super E> c)
   Implementation Requirements : The default implementation obtains an array containing all elements in this list, sorts the array,
                                 and iterates over this list resetting each element from the corresponding position in the array.
                                 (This avoids the n^2 log(n) performance that would result from attempting to sort a linked list in place.)
   구현 요구 사항: 기본 구현은 이 목록의 모든 요소를 ​​포함하는 배열을 얻고, 배열을 정렬하고,
                 이 목록을 반복하여 배열의 해당 위치에서 각 요소를 재설정합니다.
                 (이렇게 하면 연결된 목록을 제자리에 정렬하려고 시도할 때 발생하는 n^2 log(n) 성능이 방지됩니다.)
   ```

## 2. 어떤 메소드를 protected로 만들 것인가?
```
실제 하위 클래스를 만들어 테스트 해보는 것이 유일함.
```
- Effective Java 저자의 **경험** 상, 3개의 하위 클래스 정도를 만들어 테스트 해보면 결과가 나온다.
- 상속용 클래스는 배포 전에 반드시 하위 클래스를 만들어 검증해야 한다!

## 3. 상속용 클래스를 만들 때의 주의 사항
1. 상속용 클래스의 생성자는 어떤 식으로든 재정의가 가능한 메소드를 호출하면 안됨
    - 상위 클래스의 생성자가 먼저 실행 : 하위 클래스에서 재정의한 메소드가 하위 클래스의 생성자보다 먼저 호출된다.
    ```java
    //잘못된 예시
    public class ExceptionHandler {
        // This constructor should be executed at first
        public ExceptionHandler() {

            // printLog() is run before executing RuntimeExceptionHandler(Sub's) constructor
            printLog();
        }

        // overridable
        public void printLog() {}

    }
    ```
    ```java
    public final class CustomExceptionHandler extends ExceptionHandler{
        
        // Not initialized final field, this will be initialized in constructor
        private final LocalDateTime occurredTime;

        public CustomExcpeitonHandler() {
            this.occurredTime = LocalDateTime.now();
        }

        // Overridable method. Super class constructor call this method.
        @Override
        public void printLog() {
            System.out.println(occurredTime.toString());
        }
        
        public static void main(String[] args) {
            CustomExceptionHandler h = new CustomExceptionHandler();
            h.printLog();
        }

    }
    ```
    - 위의 경우는 CustomExceptionHandler에서 occurredDate의 toString 메소드를 부르려고 하면, 상위 클래스의 생성자가 printLog 호출 시 NPE를 호출
    - CustomExcpetionHandler의 생성자로 객체를 생성해도 상위 클래스인 ExceptionHandler의 생성자를 호출하기 때문에, 이 시점에서 printLog 메소드는 하위 클래스의 생성자 호출이 끝나지 않았기 때문에 occurredTime이 초기화 되지 않은 상태
    - 이 상태에서 ExceptionHandler의 생성자가 호출하는 printLog가 CustomExceptionHandler의 printLog이기 때문에 occurredTime == null이므로 toString()에서 NPE가 뜨게 된다.

## 4. 해결 방법
```
상속용으로 쓰이지 않은 클래스는 상속을 하지 못하도록 지정하는 것이다!
```
1. 클래스에 final을 선언하여 상속을 못하도록 한다.
    ```java
    public final class NonOverridableClass {
        // Constructors, fields, methods ...
    }
    ```
2. 모든 생성자를 private, package-private로 선언 후에 public 정적 factory 메소드를 만들어준다.
    - 이 경우, 내부에서 다양한 하위 클래스를 만들어서 쓸 수 있다.
    ```java
    public class OverridableClass {
        private OverridableClass() {}

        public static OverridableClass of() {
            return new OverridableClass();
        }
    }
    ```

3. 구체 클래스에서 상속이 필요하다면, override가 가능한 메소드를 호출하는 코드를 제거하고, 이를 문서로 남긴다.
    - override 가능 메소드는 자신의 코드를 private 선언된 메소드로 옮기고 이 메소드를 호출하도록 수정한다.
    - overridable method를 호출하는 다른 코드도 위의 private 선언된 메소드를 직접 부르도록 한다.
    ```java
    public class OverridableClass {
        public overridable() {
            helper();
        }

        private helper() {
            // logics...
        }
    }
    ```

4. 가급적이면 interface를 두어 상속하도록 하는 것이 좋다.
## References
[1] https://codedragon.tistory.com/7943

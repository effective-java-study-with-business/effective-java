# [Item 24] 멤버 클래스는 되도록 static으로 만들라
## 1. Nested Class
```
다른 클래스 안에 정의된 클래스
중첩 클래스는 반드시 중첩 클래스를 감싼 바깥의 클래스에서 쓰여야 한다
```
### 종류
1. static member class
2. member class(non-static)
3. anonymous class
4. local class
- 이 중, 1번을 제외한 나머지는 내부 클래스(inner class) 취급을 받는다.

## 2. Static Member Class
1. Inner Class 취급을 받지 않음
    - 바깥 인스턴스의 초기화 여부와 상관 없이 독립적으로 존재하므로 내부 클래스 취급을 받지 않음
    - 실제 메모리를 확인해보면, static으로 정의된 멤버 클래스는 외부 클래스와의 참조가 없음<sup>[1]</sup>
      - 메모리 누수로부터 안전함
2. **정적 멤버 클래스는 멤버 클래스가 직접 밖의 인스턴스를 부를 일이 없을 때 사용**
3. 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 class로 사용<sup>[2]</sup>
   ```java
   import java.util.function.BiFunction;

   public class Calculator {
       // Enum already includes static keyword
       public enum Operation {
            PLUS((v1, v2) -> (v1 + v2)),
            MINUS((v1, v2) -> (v1 - v2));

            private BiFunction<Integer, Integer, Integer> expression;

            Operation(BiFunction<Integer, Integer, Integer> expression) {
                this.expression = expression;
            }

            public Integer calculate(Integer v1, Integer v2) {
                return expression.apply(v1, v2);
            }
       }
   }
   ```
   ```java
   public class CalculatorMain {
        public static void main(String[] args) {
            int v1 = 10;
            int v2 = 5;
   
            System.out.printf("Plus Result : %d\n", Calculator.Operation.PLUS.calculate(v1, v2)); // 10
            System.out.printf("Minus Result : %d\n", Calculator.Operation.MINUS.calculate(v1, v2)); // 5
        }
   }
   ```
4. 바깥 클래스가 표현하는 객체의 한 부분(구성요소)을 나타낼 시 사용
   - 예시 : Map.Entry (Map에서 한 튜플의 key - value를 모두 가지고 있는 자료 구조)
   - Entry의 메소드로는 getKey(), getValue(), setValue()는 직접 Map을 사용하지 않고 돌아가는 구조<sup>[3]</sup>
5. 정적 멤버 클래스가 공개된 클래스의 public, protected로 선언되어 있다면, 하위 호환성이 깨짐
   - 정적 멤버 클래스도 공개 API가 되니 상속 등으로 깨질 수 있으니 각별히 유의

## 3. Non-Static Member Class
1. 비정적 멤버 클래스는 바깥 인스턴스와 연결되어 있음
   - 실제로 메모리 확인 시, 바깥 인스턴스와 참조가 되어있음<sup>[1]</sup>
   - 이 경우, 멤버 클래스가 숨은 참조를 갖고 있기 때문에 바깥 클래스가 더이상 사용되지 않음에도 불구하고 GC되지 않을 수 있음
   - 결국 섬세하게 사용하지 않으면 **메모리 누수**가 일어나게 된다
2. 바깥 인스턴스 - 비정적 멤버 클래스 간의 관계는 멤버 클래스가 인스턴스화 될 때 확립 됨
   - 이미 멤버 클래스가 생성자를 통해 인스턴스로 만들어졌으므로 변경할 수 없음
3. 비정적 멤버 클래스는 어댑터<sup>[4]</sup>를 정의할 때 사용됨
   - 어댑터 패턴? 호환되지 않는 인터페이스들을 연결하여 사용할 수 있게 하는 패턴
   ```java

   ```

## 4. Anonymous Class
1. 이름도 없고 바깥 클래스의 멤버라고 할 수 없음
   - 쓰이는 시점에 선언과 동시에 인스턴스가 만들어지므로 멤버라 명명할 수 없음
2. 대신 코드의 어디에서나 쓰일 수 있음
3. 정적 변수는 가질 수 없음
   - final 선언 된 상수는 가질 수 있다
4. non-static일 때만 바깥 인스턴스 참조 가능
5. 선언 지점에서 **단 한 번만** 인스턴스 사용 가능
6. 익명이기 때문에 instanceof와 같은 연산자 사용 불가능
7. 여러 인터페이스 구현 및 인터페이스 구현+상속하여 사용 불가
8. 익명 클래스를 사용하는 클라이언트는 익명 클래스가 상위 클래스로부터 상속한 멤버만 사용 가능
   ```java
   class Article {
        public publish() {
            return "Publishing done.";
        }
   }
   class Main {
        public static void main(String[] args){
            // Anonymous class
            Article draft = new Article() {
                @Override
                public publish() {
                    return "Draft is temporary saved.";
                }
            };
            System.out.println(draft.publish());
        }
   }
   ```
   ```java
   
   ```

## 5. Local Class
1. 지역 변수를 선언할 수 있는 곳이면 어디든 선언 가능
2. 대신 범위도 지역 변수처럼 선언된 블록 내에서만 사용 가능
3. 멤버 클래스처럼 이름을 가질 수 있고 선언된 블록이라면 여러 번 사용 가능
4. 익명 클래스처럼 non-static으로 사용할 때만 바깥 인스턴스 참조 가능, static 멤버 가질 수 없음
5. final로 선언된 지역 변수에만 접근 가능<sup>[5]</sup>
   - Java 8 이후로는 effectively final 변수에도 접근 가능
   ```java
   void sayNameAlias() {
        final String name = "Min Ah Shin";
        class NameAlias {
            String alias = "";
            NameAlias(String name) {
                for(char c : name.toCharArray()) { 
                    if(Character.isUpperCase(c))
                        alias += c;
                }
            }    
        }
   }
   ```

## References
[1] https://bottom-to-top.tistory.com/47 <br>
[2] https://kukim.tistory.com/68 <br>
[3] https://docs.oracle.com/javase/8/docs/api/java/util/Map.Entry.html <br>
[4] https://jusungpark.tistory.com/22 <br>
[5] https://live-everyday.tistory.com/189
## 0️⃣. 서론

메서드가 던지는 예외는, 그 메서드를 올바로 사용하는 데 아주 중요한 정보이다. 발생 가능한 예외를 문서로 남기지 않으며 그 클래스나 인터페이스를 효과적으로 사용하기 어렵거나 불가능할 수 도 있기 때문이다. 따라서 **예외를 문서화하는 것은 매우 중요**하다

- 검사 예외(Checked Exception)
- 비검사 예외(Unchecked Exception)

## 1️⃣. 검사 예외는 따로따로 선언하자

검사 예외는 항상 따로따로 선언하고, 각 예외가 발생하는 상황을 자바독의 `@throws` 태그를 활용해 정확히 문서화 하자.

```java
/**
 * 주석의 설명문
 * 
 * @throws java.io.FileNotFoundException 지정된 파일을 찾을 수 없습니다
*/
```

```java
/**
     * 상품의 변경된 갯수를 반환한다
     * 상품의 재고 개수가 변경이 가능하지 않는 경우 InvalidStockStatusException 예외를 발생
     *
     * @param id 상품의 id
     * @return 변경된 갯수
     * @throws NotFoundException                상품을 찾지 못한 경우 해당 예외 발생
     * @throws InvalidStockStatusException 상품의 재고개수 변경이 가능하지 않는 경우 해당 예외 발생
*/
```

극단적인 예로, Exception이나 Throwable을 던진다고 선언해서는 안 된다. **메서드 사용자에게 각 예외에 대처할 수 있는 힌트를 주지 못할뿐더러, 같은 맥락에서 발생할 수 있는 다른 예외들까지 모두 삼켜버려 API 사용성을 크게 떨어트린다.**

```java
// 잘못 선언한 예
public void testMethod() throws Exception {

}

// or

public void testMethod() throws Throwable {

}
```

다만, main 메서드는 오직 JVM만이 호출하므로 Exception을 던지도록 선언해도 괜찮다.

```java
/**
 * @throws IllegalStateException
 */
public void testMethod(String parameter) throws IllegalStateException {
  
}
```

## 2️⃣. **비검사 예외도 문서화하자**

자바에서 요구하는 것은 아니지만 비검사 예외도 검사 예외처럼 문서화해두면 좋다. **비검사 예외는 일반적으로 프로그래밍 오류를 뜻한다.** 따라서 자신이 일으킬 수 있는 오류들이 무엇인지 알려주면 프로그래머는 자연스럽게 오류가 발생하지 않게 코딩하게 된다.

### public 메소드면 비검사 예외를 문서화하자

잘 정비된 비검사 예외의 문서는 그 메서드를 성공적으로 수행하기위한 전제조건이 된다. **public 메서드라면 필요한 전제조건을 문서화해야하며, 그 수단으로 가장 좋은 것이 바로 비검사 예외를 문서화하는 것**이다.

특히, **인터페이스 메서드에서 비검사 예외를 문서화하는 것이 중요**하다. **문서화한 전제조건이 인터페이스의 일반 규약에 속하게 되어 그 인터페이스를 구현한 모든 구현체가 일관되게 동작하도록 해주기 때문**이다.

## 3️⃣. 검사 예외와 비검사 예외를 구분하라

- 예외를 @throws 로 문서화하되, 비검사 예외는 메서드 선언의 throws 목록에 넣지 말자.
- 검사 예외인지, 비검사 예외인지에 따라 해야할 일이 달라지므로 둘을 확실히 구분해주는 것이 좋다.

![Untitled.png](..%2F..%2F..%2F..%2FDownloads%2FUntitled.png)
![Untitled (1).png](..%2F..%2F..%2F..%2FDownloads%2FUntitled%20%281%29.png)
![Untitled (2).png](..%2F..%2F..%2F..%2FDownloads%2FUntitled%20%282%29.png)

### 비검사 예외의 문서화가 어려운 이유가 있다.

- 비검사 예외도 문서화하라고 했지만, 현실적으로 불가능할 때도 있다.
    - 다른 사람이 작성한 클래스를 사용하는 메소드가 있다고 가정하자.
    - 발생 가능한 모든 예외를 문서화 했다.
    - 근데, 후에 이 외부 클래스가 새로운 비검사 예외를 던지게 되면?
    - 아무 수정도 되지 않은 메소드는 문서에 언급되지 않은 새로운 비검사 예외를 전파

## 4️⃣. 한 클래스의 많은 메소드가 같은 이유로 예외를 던진다면

```java
/**
 * @throws NullPointerException - 모든 메서드는 param에 null이 넘어오면 NullPointerExcetpion을 던진다.
 */
class Dummy throws NullPointerException {

    public void A(String param) {
       ...
    }

    public void B(String param) {
       ...
    }

    public void C(String param) {
       ...
    }
}
```

한 클래스의 많은 메서드가 같은 이유로 같은 예외를 던진다면 각각에 메서드에 선언하기보다 클래스 설명에 추가하는 방법을 고려하자.

## 5️⃣. 결론

- 메서드가 던질 가능성이 있는 모든 예외를 문서화하자
- 검사 예외든, 추상 메서드든 구현 메서드든 모두 문서화해야 한다.
- 예외를 문서화할 때는 @throws 태그를 사용하자.
- 검사 예외만 throws 문에 일일이 선언하고, 비검사 예외는 메서드 선언에 기입하지 말자.
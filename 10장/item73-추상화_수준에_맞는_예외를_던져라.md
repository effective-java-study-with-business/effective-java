# [Item 73] 추상화 수준에 맞는 예외를 던져라
## 1. 저수준 예외를 처리하지 않으면..?
- 저수준 예외란?
  - 실제 로직에서 발생할 수 있는 구체화 단계의 예외
- 수행하려는 일과 관련 없어 보이는 예외가 튀어나옴
- 결국 윗 레벨의 API를 오염시킴
- **따라서, 상위 계층에서는 저수준 예외를 catch하여 자신의 추상화 수준에 맞는 예외로 바꿔 던질 필요가 있음**
  - 이는 **예외 번역(exception translation)**이라고 명명함

## 2. Exception Translation(예외 번역) & Chaining(예외 연쇄)?
### Exception Translation
- 저수준 예외를 추상화 수준에 맞는 고수준 예외로 치환하여 던져버리는 행위
```java
public abstract class AbstractSequentialList<E> extends AbstractList<E> {
    /**
    * Returns the element at the specified position in this list.
    *
    * <p>This implementation first gets a list iterator pointing to the
    * indexed element (with {@code listIterator(index)}).  Then, it gets
    * the element using {@code ListIterator.next} and returns it.
    *
    * @throws IndexOutOfBoundsException {@inheritDoc}
    */
    public E get(int index) {
        try {
          // next() method ListIterator.java interface throws NoSuchElementException
            return listIterator(index).next();
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }
}
```
- 위의 예시는 java.util.AbstractSequentialList로 List interface의 골격을 구현함
- ListIterator의 next() 메소드 선언부는 원소가 없을 경우 NoSuchElementException을 throw함
- 예시의 인덱스에 접근하기 위해 순회하며 없는 위치를 참조해야 하는 경우에는 NoSuchElementException보다는 IndexOutOfBoundsException이 추상화 수준에 맞음 

### Exception Chaining
- 예외 번역 시, 저수준 예외가 디버깅에 도움이 되면 예외 연쇄(exception chaining)를 사용
- 예외 연쇄란, **문제의 근본 원인인 저수준 예외를 고수준 예외에 차례로 실어 보내는 방식**
- Throwable.getCause()를 통해 언제든 저수준 예외를 꺼내 디버깅에 활용 가능
- 고수준 예외의 생성자는 예외 연쇄 용으로 설계된 상위 클래스의 생성자에 "cause"를 전달
  - 결국 Throwable 생성자까지 건네지게 됨
```java
public class AlreadyExistResourceException extends Exception { 
    public AlreadyExistResourceException(Throwable cause) {
        super(cause);
    }
}
```
- 예외 연쇄는 문제의 원인을 프로그램에서 접근할 수 있게 해줌
- 원인과 고수준 예외의 스택 추적 정보를 통합하여 반환해줌
- 그렇다고 예외 번역을 남용하는 것이 능사는 아님

## Conclusion
- 예외를 상위 계층에 노출하기 어렵거나, 스스로 처리 불가할 때는 예외 번역을 사용하여 처리
- 예외 연쇄를 이용하면 맥락에 맞는 고수준 예외를 던지며, 근본 원인을 알려줌
- 상위 계층 메서드의 파라미터를 하위 계층으로 건네기 전에 검사하여 예외 발생을 방지
- 하위 계층에서 예외를 피할 수 없다면 상위 계층에서 처리하고, log를 잘 남겨두도록 해야함

## References
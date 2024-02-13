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
public class UsableUtil {
    public int 
}
```

### Exception Chaining
- 예외 번역 시, 저수준 예외가 디버깅에 도움이 되면 예외 연쇄(exception chaining)를 사용
- 예외 연쇄란, **문제의 근본 원인인 저수준 예외를 고수준 예외에 차례로 실어 보내는 방식**
- Throwable.getCause()를 통해 언제든 저수준 예외를 꺼내 디버깅에 활용 가능
- 고수준 예외의 생성자는 예외 연쇄 용으로 설계된 상위 클래스의 생성자에 "cause"를 전달
  - 결국 Throwable 생성자까지 건네지게 됨

## References
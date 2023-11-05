# [Item 03] private 생성자나 열거 타입으로 싱글턴임을 보증하라
## 1. What is Singleton?
```
인스턴스를 "하나만" 생성할 수 있는 클래스
```
### 장점
1. 메모리 절약 및 효율적인 자원 관리
2. 인스턴스에 대한 접근 및 제어 간소화
    - 1개의 객체 당 1개의 인스턴스만 만드므로, 이에 대해 접근하거나 제어하는 기능(메소드 등)을 만들기 수월함
3. (경우에 따라) 필요할 때 인스턴스를 생성하도록 유도할 수 있음

### 단점
1. 유연성 감소
    - 해당 인스턴스를 전역적으로 사용하고 있다면, 변경 및 확장에 대처하기 어려움
2. 어려운 테스트
    - mock을 구현하여 다양한 환경/시나리오를 가진 클라이언트에서 테스트 진행 어려움
3. 동시성에 취약함
    - 한 인스턴스를 공유하여 사용하므로, 스레드 등으로 동시에 접근 시에는 별도의 처리가 필요함<sup>[1]</sup><sup>[2]</sup>

### Singleton 사용 예시
- Logging, DB Connection, Java Runtime ...<sup>[3]</sup>

### Singleton 생성 방법
1. public static final 필드로 인스턴스를 선언
2. 정적 팩토리 메소드로 인스턴스 반환
3. 원소가 하나인 Enum 타입 선언

## 2. public static final 필드로 인스턴스를 선언
```java
public class FooConnection {
    public static final FooConnection INSTANCE = new FooConnection();
    // INSTANCE field may initiate when this class is loading, ref [3]

    private FooConnection() {} // private constructor

    // and below this comment, some business logics can be written
}
```
- 생성자를 private으로 1개만 선언하여 인스턴스 생성을 제어한다.
- public으로 선언되어 어디서나 INSTANCE라는 멤버로 생성된 FooConnection 인스턴스에 접근 가능하다.
- static으로 되어있어 클래스 로딩 시에 인스턴스 또한 생성된다.(Early Initialization)
- final 필드이므로, 클래스 로딩 후에는 INSTANCE가 변경될 수 없다.

### 하지만, Reflection API를 사용하면 인스턴스가 복제가 된다고?
- 권한이 있는 클라이언트는 아래와 같이 JAVA의 Reflection API로 생성자에 접근하여 인스턴스를 ~~무한~~ 생성해버릴 수 있다!
- 그러한 사태를 대비하기 위해 생성자 안에 아래와 같이 방어 로직을 추가하여 싱글턴을 깨버리려는 로켓단에게 무지개 반사를 시전할 수 있다.
```java
public class FooConnection {

    public static final FooConnection INSTANCE = new FooConnection();

    private FooConnection() {

        // Defense for Reflection API
        if(this.INSTANCE != null)
            throw new RuntimeException("No way!");
    }

}
```

## 정적 팩토리 메소드로 인스턴스 반환
```java
public class FooConnection {

    // INSTANCE field may initiate when this class is loading
    private static final FooConnection INSTANCE = new FooConnection();

    private FooConnection() {

        // Defense for Reflection API
        if(INSTANCE != null)
            throw new RuntimeException("No way!");
    }

    public static FooConnection getInstance() {
        return INSTANCE;
    }

}
```
- 앞서 소개한 public static final 필드와의 차이점이라고는 INSTANCE라는 변수의 접근 제한자와 이에 따른 접근 가능한 정적 팩토리 메소드의 유무이다.
- 즉, 필드에 직접 접근하느냐, 메소드를 통해 반환 받느냐의 차이이다.

## 그렇다면 두 방법의 각 장점은?
||public static final 필드|정적 팩토리 메소드|
|----|-----------------------|-----------------------|
|장점|1. 클래스가 싱글턴인 것이 API에 명백하게 드러남<br>2. 간결함|1. API를 바꾸지 않고도 싱글턴이 아니게 변경 가능<br>2. 정적 팩토리 -> 제네릭 싱글턴 팩토리로 만들 수 있음<sup>Item30 참고</sup><br>3. 정적 팩토리의 메소드 참조를 공급자로 사용<sup>Item43,44 참고</sup>|
- 하지만, 싱글턴으로 구현 시에 정적 팩토리 메소드의 장점이 필요한 순간이 아니라면, public static final 필드 방식으로 사용하는 것이 편하다.

## Serialization?
```
싱글턴이 깨질 수도 있는 의외의 문제는 역직렬화 시에 나오게 된다.
```
1. 이 문제는 public static final 필드, 정적 팩토리 메소드 둘 다 마주할 수 있는 문제점이다.
2. 우선, 인스턴스의 필드를 transient(일시적)이라고 선언한다.
3. readResolve 메소드를 아래와 같이 작성하여 직렬화 된 인스턴스를 역직렬화 할 때 새로운 인스턴스의 탄생을 막을 수 있다.<sup>Item89 참고</sup>
```java
private Object readResolve() {
    return INSTANCE;
}
``` 

## 원소가 하나인 Enum 타입으로 선언
- public 필드 방식과 유사하지만, 위에 언급되었던 직렬화 문제를 쉽게 피해나갈 수 있다.
- 또한 Reflection API를 사용한 인스턴스 복제 문제에서도 자유롭다.
- 그리고, 굉장히 간단하다!
```java
public enum FooConnection {
    INSTANCE;

    // some logics ....
}
```
- 단, 해당 싱글턴 클래스가 Enum외의 클래스 상속 시에는 해당 방법을 사용할 수 없으므로 public 필드 또는 정적 팩토리 방식을 사용하도록 하자.

## References
[1] https://www.linkedin.com/advice/0/what-benefits-drawbacks-using-singleton-1e <br>
[2] https://www.linkedin.com/advice/0/what-benefits-drawbacks-using-singleton-pattern-1e <br>
[3] https://www.digitalocean.com/community/tutorials/java-singleton-design-pattern-best-practices-examples <br>

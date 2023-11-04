# 아이템1. 생성자 대신 정적 팩토리 메서드 고려

## 생성자 대신 정적 팩토리 메서드를 사용함으로써 얻을 수 있는 장단점을 알아보자.

### 장점 1. 이름을 가질 수 있다. (동일한 시그니처의 생성자는 두개 가질 수 없다.)

- 컴파일 에러가 나는 코드

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    public Order(Product product, boolean prime) {
				this.product = product;
				this.prime = prime;
    }

    public Order(Product product, boolean urgent) { // 동일한 시그니처 불가능
        this.product = product;
				this.urgent = urgent;
    }

}
```

- 컴파일 에러는 안 나는 코드…

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    public Order(Product product, boolean prime) {// 생성자다 보니 이름이 클래스와 같아야함
				this.product = product;
				this.prime = prime;
    }

    public Order(boolean urgent, Product product) {// 클래스와 동일한 타입만 리턴
        this.product = product;
				this.urgent = urgent;
    }

}
```

- 메서드 명으로 확실하게 어떤 역할을 하는 메서드인지 정의하자(정적 팩토리 메서드 활용, 팩토리 디자인 패턴과는 무관하게 단순히 반환하고자 하는 객체 리턴)

```java
public class Order {

    private boolean prime;

    private boolean urgent;

    private Product product;

    private OrderStatus orderStatus;

    public static Order primeOrder(Product product) {
        Order order = new Order();
        order.prime = true;
        order.product = product;
        return order;
    }

    public static Order urgentOrder(Product product) {
        Order order = new Order();
        order.urgent = true;
        order.product = product;
        return order;
    }

}
```

- 즉, 생성자의 시그니처가 중복되는 경우에 고민하면 좋을 것 같다!

### 장점 2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.

- 자바의 생성자는 기본적으로 호출될 때마다 새로운 인스턴스를 만든다.

```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

		public static void main(String[] args) { // 매번 새롭게 인스턴스 생성
			System.out.println(new Settings());
			System.out.println(new Settings());
			System.out.println(new Settings());
		}

}
```

- 하지만 경우에 따라서는 어떤 인스턴스를 매번 만들지, 특정한 경우에만 만들지 통제가 필요한 경우가 있다.
- 생성자를 통해 인스턴스를 만들면 통제가 불가능하다.
- Settings 인스턴스를 하나만 만들어보자!

```java

public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    private Settings() {} // 생성자는 외부에서 호출 못하게끔 private

    private static final Settings SETTINGS = new Settings(); // 미리 만들어놓은 인스턴스

    public static Settings getInstance() { // 정적 팩토리 메서드
        return SETTINGS;
    }

}
```

- 위와 같이 만들어두면 다른 클래스에서는 오로지 정적 팩토리 메서드(getInstance)를 통해서만 인스턴스를 가져올 수 있다.
- 생성자(public)로는 인스턴스의 생성을 컨트롤할 수 없지만, 객체 생성을 팩토리 내부에서 컨트롤하고 가져오기만 하도록 팩토리 메서드 활용
- FlyWeight Pattern과 어느정도 통용되는 개념
- FlyWeight Pattern이란? → 참고 자료 : [https://velog.io/@hoit_98/디자인-패턴-Flyweight-패턴](https://velog.io/@hoit_98/%EB%94%94%EC%9E%90%EC%9D%B8-%ED%8C%A8%ED%84%B4-Flyweight-%ED%8C%A8%ED%84%B4)

### 장점 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

### 장점 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

- 매개변수에 따라서 다른 인스턴스를 리턴

```java
public class HelloServiceFactory {

    public static HelloService of(String language) { // 언어에 따라 다른 인스턴스
				if (language.equals("ko")) {
					return new KoreanHelloService();
				} else {
					return new EnglishHelloService();
				}
    }
		
		public static void main(String[] args) {
			HelloService ko = HelloServiceFactory.of("ko");
		}

}
```

- 자바 8 이후부터는 인터페이스에 static 메서드 선언 가능

```java
public interface HelloService {

    String hello();

    static HelloService of(String language) { // 인터페이스에서는 접근제한저 없으면 public
				if (language.equals("ko")) {
					return new KoreanHelloService();
				} else {
					return new EnglishHelloService();
				}
    }
}
```

```java
public class HelloServiceFactory {
		
		public static void main(String[] args) {
			HelloService eng = HelloServiceFactory.of("eng");
			System.out.println(eng.hello());
		}

}
```

### 장점 5. 정적 팩토리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

```java
public class HelloServiceFactory {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
				// 구현체가 없어도 됨 (어떤 임의의 구현체가 올지 모름), 유연함
        ServiceLoader<HelloService> loader = ServiceLoader.load(HelloService.class);
        Optional<HelloService> helloServiceOptional = loader.findFirst();
        helloServiceOptional.ifPresent(h -> {
            System.out.println(h.hello());
        });

				// EnglishHelloService에 의존적
        HelloService helloService = new EnglishHelloService();
        System.out.println(helloService.hello());

    }

}
```

### 단점 1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩토리 메서드만 제공하면 하위 클래스를 만들 수 없다.

```java
public class Settings {

    private boolean useAutoSteering;

    private boolean useABS;

    private Difficulty difficulty;

    private Settings() {} // 생성자가 private

    private static final Settings SETTINGS = new Settings();

    public static Settings getInstance() {
        return SETTINGS;
    }

}
```

### 단점 2. 정적 팩토리 메서드는 프로그래머가 찾기 어렵다.

- 생성자가 없고, 정적 팩토리 메서드만 있다면 프로그래머가 찾기 어렵다.
- 추후 클래스 규모가 커지고 정적 팩토리 메서드의 개수가 크게 증가한다면, 어떤 메서드가 생성자와 같은 역할을 하는지 한눈에 파악하기 어려워진다.
    - 단점 보완하기 위해 네이밍 패턴을 제안 (ex : of - 매개변수를 받아서 만들 경우, get - 미리 만들어 놓은 인스턴스 가져오기)
    - 문서화하기 (ex : 주석 - 이 클래스의 인스턴스는 getInstance()를 통해 사용한다.)

## 결론

- 정적 팩토리 메서드와 public 생성자는 각자의 쓰임새가 있으니 상대적인 장단점을 이해하고 사용하자
- 정적 팩토리를 사용하는 게 유리한 경우가 많으니 무작정 public 생성자를 제공하기 전에 고민해보자

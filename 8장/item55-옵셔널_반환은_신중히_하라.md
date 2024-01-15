# 아이템 55 옵셔널 반환은 신중히 하라

자바 8 전에는 메서드가 특정 조건에서 값을 반환할 수 없을때 예외를 던지거나, null을 return 반환하는 방법이 있었다.

예외는 진짜 예외일때 사용해야하고, 스택 추적 전체를 캡처하므로 비용도 높다.

null을 반환하면 별도의 null 처리 로직을 작성해야한다. 이때, null이 반환될 수 있는 코드인지를 반환되지 않는 코드인지 파악도 해야하며, 잘못했을때 NPE가 발생할 수 있다.

## 자바 8

자바 8로 버전이 올라가면서 추가로 하나의 선택지로 Optional<T> 이 생겼고, null이 아닌 T 타입을 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.

옵셔널은 원소를 최대 1개 가질 수 있는 **불변** 컬렉션이다. Optional<T>가 Collection<T>를 구현하지 않았지만, 원칙적으로는 그렇다는 말이다.

## 옵셔널

보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할때 T 대신 Optional<T>를 반환하도록 선언하면 된다.

그러면 유효한 반환값이 없을 때는 빈 결과를 반환하는 메서드가 만들어진다.

옵셔널을 반환하는 메서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작다.

아래 함수는 주어진 컬렉션에서 최댓값을 뽑아주는 메서드이다.

```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty())
        throw new IllegalArgumentException("빈 컬렉션");

    E result = null;
    for (E e : c)
        if (result = null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);

    return result;
}
```

이 메서드 에서는 빈 컬렉션을 건네면 IllegalArgumentException을 던진다.

```java
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
    if (c.isEmpty())
        return Optional.empty();
    E result = null;
    
    for (E e : c)
        if (result = null || e.compareTo(result) > 0)
            result = Objects.requireNonNull(e);
    
    return Optional.of(result);
}
```
옵셔널을 반환하도록 구현하기는 어렵지 않다. 적절한 정적 팩터리를 사용해 옵셔널을 생성해주기만 하면 된다.

옵셔널을 사용할때 Optional.of(value), value에 null이 들어가면 NPE가 발생하니 주의 해야한다.
null 값도 허용하는 옵셔널을 반환할때는 Optional.ofNullable(value)를 사용하면 된다.

**하지만 옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말아야 한다, 옵셔널을 도입한 취지를 완전히 무시하는 행위다.**

## 옵셔널 선택기준

그렇다면 null을 반환하거나 예외를 던지는 대신 옵셔널 반환을 선택해야하는 기준은 무엇인가?

옵셔널은 **검사 예외와 취지가 비슷하다.** 즉, 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다.
비검사 예외를 던지거나 null을 반환한다면 API 사용자가 그 사실을 인지하지 못해 끔찍한 결과로 이어질 수 있다.
하지만 검사 예외를 던지면 클라이언트에서는 반드시 이에 대처하는 코드를 작성해넣어야 한다.

비슷하게, 메서드가 옵셔널을 반환한다면 클라이언트는 값을 받지 못했을때 취할 행동을 선택해야한다. 그중 하나는 기본값을 설정하는 방법이다.

## 옵셔널 활용

```java
// 기본값 설정
String lastWord = max(words).orElse("단어 없음");

// 원하는 예외 발생
String lastWord = max(words).orElseThrow(TemperTantrumException::new);

// 항상 값이 채워져 있다고 가정
String lastWord = max(words).get();
```
### Supplier<T>

기본값을 설정하는 비용이 아주 커서 부담이 될 때가 있다. 그럴 때는 Supplier<T>를 인수로 받는 orElseGet을 사용하면, 값이 처음 필요할 때 Supplier<T>를 사용해 생성하므로 초기 설정 비용을 낮출 수 있다.

### isPresent
옵셔널이 채워져 있으면 true를, 비어 있으면 false를 반환한다. 

이 메서드로는 원하는 모든 작업을 수행할 수 있지만 신중히 사용해야 한다. 실제로 isPresent를 쓴 코드 중 상당수는 앞서 언급한 메서드들로 대체할 수 있으며, 그렇게 하면 더 짧고 명확하고 용법에 맞는 코드가 된다.


## 컬렉션,스트림,배열,옵셔널 금지

반환값으로 옵셔널을 사용한다고 해서 무조건 득이 되는건 아니다.

**컬렉션,스트림,배열,옵셔널** 같은 컨테이너 타입은 옵셔널로 감싸면 안 된다. 빈 Optional<List<T>>를 반환하기 보다는 빈 List<T>를 반환하는게 좋다.
빈 컨테이너를 그대로 반환하면 클라이언트에 옵셔널 처리 코드를 넣지 않아도 된다.

## 옵셔널 선언 기준

**결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T>를 반환한다.**

그런데 이렇게 하더라도 Optional<T>를 반환하는 데는 대가가 따른다. Optional도 엄연히 새로 할당하고 초기화해야 하는 객체이고, 그 안에서 값을 꺼내려면 메서드를 호출해야 하니 한 단계를 더 거치는 셈이다.

그래서 성능이 중요한 상황에서는 옵셔널이 맞지 않을 수 있다.

박싱된 기본타입을 담는 옵셔널은 기본 타입 자체보다 무거울 수 밖에 없다. 값을 두 겹이나 감싸기 때문이다. 그래서 자바 API 설계자는 int, long, double 전용 옵셔널 클래스들을 준비해놨고, 이들은 OptionalInt, OptionalLong, OptionalDouble 이다.
이 옵셔널들도 Optional<T> 가 제공하는 메서드를 거의 다 제공한다.

이렇게 대체재까지 있으니 박싱된 **기본타입을 담은 옵셔널을 반환하는 일은 없도록 하자.**

## 옵셔널을 맵의 값으로 사용하면 절대 안된다.
만약 그리 한다면 맵 안에 키가 없다는 사실을 나타내는 방법이 두 가지가 된다. 

1. 키 자체가 없는 경우,
2. 키 는 있지만 그 키가 속이 빈 옵셔널인 경우

쓸데 없이 복잡성만 높여서 혼란과 오류 가능성을 키울 뿐이다.

옵셔널을 컬렉션의 키, 값, 원소나 배열의 원소로 사용하는게 적절한 상황은 거의 없다.
> 직접 고민해봤는데도 없는거 같음
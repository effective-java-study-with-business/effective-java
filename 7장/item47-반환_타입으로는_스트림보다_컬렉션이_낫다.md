# 아이템 47 반환 타입으로는 스트림보다 컬렉션이 낫다

## Stream

Stream에서는 반복(iteration)을 지원하지 않기 때문에 API에서 Stream만 반환하도록 한다면 반복과 stream을 잘 시기적절하게 사용하기를 원하는 사용자는 불만을 토로할 수 있다. Stream을
사용할 수도 반복을 사용할 수도 있게 지원해야 한다.

```java
    public static void main(String[] args) {
    Stream<String> stream = Stream.of("132262b", "이고르", "권준호");
    for (String str : stream::iterator) {
        System.out.println(str);
    }
}
```

> Method reference expression is not expected here

라는 에러가 나온다.

Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐만 아니라, Iterable 인터페이스가 정의한 방식대로 동작한다.

그럼에도 for-each로 스트림을 반복할 수 없는 이유는 Stream이 Iterable을 확장(extend)하지 않아서다.

> Stream에 Iterable 인터페이스 정의된 iterator를 구현하지 않았다는 소리.

```java
public interface BaseStream<T, S extends BaseStream<T, S>>
        extends AutoCloseable {
    /**
     * Returns an iterator for the elements of this stream.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">terminal
     * operation</a>.
     *
     * @return the element iterator for this stream
     */
    Iterator<T> iterator();
    
    ...
}
```

## 스트림을 위한 끔찍한 방법으로 우회하기

```java
    public static void main(String[] args) {

    Stream<String> stream = Stream.of("132262b", "이고르", "권준호");
    for (String str : (Iterable<String>) stream::iterator) {
        System.out.println(str);
    }
}
```

```bash
> Task :Main47.main()
132262b
이고르
권준호
```

위 오류를 잡기 위해 Iterable로 적절히 형변환 해주었더니 동작되는 되는 모습을 확인할 수 있다.

![img.png](images/불편.png)

하지만 책에서는 작동은 하지만 실전에서 쓰기에는 너무 난잡하고 직관성이 떨어지는 행위. 라고 하고 있고, 이를 위해 어댑터 메서드를 사용하면 한결 나아진다고 합니다.

## 어댑터 사용

```java
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}
```

iterableOf 메서드를 통해 명시적으로 Iterable으로 반환할 수 있다.

```java
    public static void main(String[] args) {
    Stream<String> stream = Stream.of("132262b", "이고르", "권준호");
    for (String str : iterableOf(stream)) {
        System.out.println(str);
    }
}
```

```bash
> Task :Main47.main()
132262b
이고르
권준호
```

![img_1.png](images/편안.png)

어댑터 메서드를 사용하면 자바의 타입 추론이 문맥을 잘 파악하여 어댑터 메서드 안에서 따로 형변환하지 않아도 된다.

하지만 위에서 **" API에서 Stream만 반환하도록 한다면 반복과 stream을 잘 시기적절하게 사용하기를 원하는 사용자는 불만을 토로할 수 있다."** 라고 했으니, stream으로도 중개할 수 있도록
해야하고, 자바는 이를 위한 어댑터도 제공하지 않고 있기에 구현해야 한다.

```java
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
```

- 만약 API가 Iterable만 반환하면 스트림 파이프라인에서 이를 처리할 수 없게 된다. 이 경우, 위와 같이 어댑터를 구현해서 사용할 수 있다.
- 만약 이 메서드가 오직 스트림 파이프라인에서만 쓰일 걸 안다면 마음 놓고 스트림을 반환해도 된다.
- 반대로 반환된 객체들이 반복문에서만 쓰일 걸 안다면 Iterable을 반환하도록 한다.
- 공개 API를 작성할 때는 스트림 파이프라인을 사용하는 사람과 반복문에서 쓰려는 사람 모두를 고려해야 한다.
- Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공하기 때문에 반복과 스트림을 동시에 지원한다.
- 원소 시퀀스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는 게 일반적으로 최선이다.
- - 반환하는 시퀀스의 크기가 메모리에 올려도 안전할 만큼 작다면 ArrayList나 HashSet 같은 표준 컬렉션 구현체를 반환하는 게 최선일 수도 있다.
- - 하지만 단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올려서는 안 된다.
- - 반환할 시퀀스가 크지만 표현을 간결하게 할 수 있다면 전용 컬렉션의 구현을 고려해보도록 한다. 이때 AbstractList를 이용하면 전용 컬렉션을 손쉽게 구현할 수 있다.
- - AbstractCollection을 활용해서 Collection 구현체를 작성할 때는 Iterable용 메서드 외에 contains와 size만 더 구현하면 된다.
- - 만약 반복이 시작되기 전에 시퀀스의 내용을 확정할 수 없는 등의 이유로 contains와 size를 구현하는 게 불가능할 때는 컬렉션보다는 스트림이나 Iterable을 반환하는 편이 낫다.
- - 별도의 메서드를 두어 두 방식을 모두 제공할 수도 있다.


**참고 : Collection의 size 메서드는 int 값을 반환하는데, 따라서 이 경우 시퀀스의 최대 길이는 Integer.MAX_VALUE 혹은 2^31 - 1 로 제한된다.** 

## 추가

> 근데 그냥 스트림으로 반환하고 stream.forEach(System.out::println); 돌리면 안됨? 생각함.

## 핵심 정리

- 원소 시퀀스를 반환하는 메서드를 작성할 때는, 스트림과 반복에서의 사용을 모두 고려해야 한다.
- 컬렉션을 반환할 수 있다면 컬렉션을 반환하도록 한다.
- 반환 전부터 이미 원소들을 컬렉션에 담아 관리하고 있거나 컬렉션을 하나 더 만들어도 될 정도로 원소 개수가 적다면 ArrayList 같은 표준 컬렉션에 담아 반환하도록 한다.
- 만약 그렇지 않다면 전용 컬렉션을 구현할지 고민해보도록 한다.
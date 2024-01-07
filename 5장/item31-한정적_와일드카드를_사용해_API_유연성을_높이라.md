# 아이템 31. 한정적 와일드카드를 사용해 API 유연성을 높이라

아이탬 28에서도 이야기했듯 매개변수화 타입은 불공변이다.

서로 다른 타입 Type1과 Type2가 있을 때 List<Type1>은 List<Type2>의 하위 타입도 상위 타입도 아니다.

예를들어 List<String>은 List<Object>의 하위 타입이 아니다. -> 리스코프 치환 원칙에 어긋난다.

## 불공변 방식 보다 유연한 무언가가 필요!

## pushAll() - 생산자(Producer)
와일드카드 타입을 사용하지 않은 pushAll 메서드 -> 결합이 있다.
```java
...
public void pushAll(Iterable<E> src) {
    for (E e : src) {
        push(E);
    }
}
...
public static void main(String[] args) {
    Stack<Number> numberStack = new Stack<>();
    Iterable<Integer> iterable = ...;
        
    numberStack.pushAll(iterable);
}
...
```

에러
-> java: incompatible types: java.lang.Iterable<java.lang.Integer> cannot be converted to java.lang.Iterable<java.lang.Number>
정확한 타입 E의 요소를 받지 못했기 때문이다.

## popAll() - 소비자(Consumer)
와일드카드 타입을 사용하지 않은 popAll 메서드 -> 결합이 있다.

```java
...
public void popAll(Collection<E> dst) {
    while(!isEmpty())
      dat.add(pop());
}
...
```
에러
-> java: incompatible types: java.lang.Iterable<java.lang.Integer> cannot be converted to java.lang.Iterable<java.lang.Number>
정확한 타입 E의 요소를 받지 못했기 때문이다.

## 한정적 와일드카드 
pushAll의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위타입의 Iterable'이어야 한다.
```java
...
public void pushAll(Iterable<? extends E> src) {
    for (E e : src) {
        push(E);
    }
}
...
```

또한 popAll의 입력 매개변수의 타입은 'E의 Collection'이 아니라 'E의 상위 타입의 Collection'이어야 한다.
-> 모든 타입은 자기 자신의 상위 타입이다.
```java
...
public void popAll(Collection<? super E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
...
```

요약
-> 유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라!

## 꼭 외우자! PECS!!
팩스(PECS - 흉근) : producer-extends, consumer-super

pushAll()은 스택이 사용할 인스턴스를 생산하므로 extends
popAll()은 스택의 인스턴스를 소비하므로 super

### example1
다음 union 메서드는 어떻게 선언해야할까?
```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2)
```

답:
```java
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)
```
-> 반환타입에는 한정적 와일드카드 타입을 사용시, 유연성은 낮춰주고 클라이언트 코드에서 와일드카드 타입을 써야함.
클라이언트 코드:
```java
...
  Set<Integer> integers = Set.of(1,3,5);
  Set<Double> doubles = Set.of(2.0, 4.0, 6.0);
  Set<Number> numbers = union(integers, doubles);
...
```

### example2
다음 union 메서드는 어떻게 선언해야할까?
```java
public static <E extends Comparable<E>> E max(List<E> list)
```

답:
```java
public static <E extends Comparable<? super E>> E max(List<? extends E> list)
```
두개의 PECS공식이 사용되었다.
->타입 매개변수의 Comparable 인터페이스는 비교를 위해 사용하는 소비하여 정수를 반환하므로 PECS공식에 의해 super를 사용해야 한다. == Comparator
->입력 매개변수에서 E인스턴스를 생산하므로 extends를 사용해야한다.

```java
List<SchduledFuture<?>> scheduledFutures = ...;
```
해당 리스트는 오직 수정된 max로만 처리할 수 있다.

### example3
다음 비한정적 타입 매개변수와 비한정적 와일드카드를 사용한 선언 중 어떤것이 더 나을까? 나은 이유는 무엇인가?
```java
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

답:
public API라면 간단한 두번째가 낫다. 어떤 리스트든 이 메서드에 넘기면 명시한 인덱스의 원소들을 교환해주며, 신경 써야 할 타입 매개변수도 없다.
-> 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체하라.

다만 다음의 코드는 컴파일되지 않는다.
```java
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i));
}
```

어떻게 해야할까?

와일드카드의 실제 타입을 알려주는 Helper를 활용한다.
```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(i, list.set(j, list.get(i));
}
public static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i));
}
```
-> Helper의 시그니처는 앞의 버렸던 첫번째 swap 메서드와 똑같다.

## 요약
1. 조금 복잡하더라도 와일드카드 타입을 적용하려면 API가 훨씬 유연해진다.
2. PECS 공식을 꼭 기억하자

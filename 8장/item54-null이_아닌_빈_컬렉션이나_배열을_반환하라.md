#  Item54 null이 아닌, 빈 컬렉션이나 배열을 반환하라

### 빈컬렉션을 확인해서 null을 반환하는 경우

컬렉션이 비어있다면 null을 반환하는 경우를 살펴보자.

```java
private final List<Cheese> cheesesInStock = ...;

public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? null
        : new ArrayList<>(cheesesInStock);
}
```

null을 반환하는 경우 오류가 발생할 수 있으므로 null처리 코드를 만들어주어야 한다.
```java
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null) {
    for(Cheese cheese: cheeses) {
        pizzaria.putTopping(cheese);
    }
}
```
대부분 리스트 데이터 자체를 반환하거나 리스트 반복문의 돌려 처리하는 로직을 사용하는데, 빈 컬렉션을 사용한다고해서 잘못되지 않는다.
item55에 관련 이야기 한것처럼 같은 이유로 옵셔널 리스트 객체를 구지 사용하지 않아도 된다. 

```java
public List<Cheess> getCheeses() {
    return new ArrayList<>(cheesesInStock);
}
```

또한 새로운 비어있는 컬렉션 객체를 만든다고 해서 성능차이는 거의 없다.
심지어 조건문을 쓰지않고 바로 생성 반환하면 되므로, 코드가 더 간결해 보인다.

repository에서 디비에서 리스트를 조회하고 반환시에도, 조회가 되지않아도 빈 배열객체로 들어가므로 그대로 반환해주는 것이 좋다.

```java
public Cheese[] getCheeses() {
    return cheesesInStock.toArray(new Cheese[0]);
}
```
만약 비어있는 컬랙션 객체 생성으로 인한 성능이 걱정된다면,
미리 길이0 인 배열을 선언해두고 사용하는것도 좋은 대안이다.

만약 배열의 크기가 충분하지 않으면 toArray가 컬렉션 크기에 맞게 배열을 할당한다.
이 동작을 이용하려면 크기가 0인 임시배열을 전달하자.

```java
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
    return cheeseInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```


단 다음과 같이 toArray 메소드에 넘기는 배열을 미리 할당하는건 오히려 성능이 떨어진다고 한다.
```java
return cheeseInStock.toArray(new Cheese[cheesesInStock.size()];
```

List toArray 메서드를 분석해보면,
만약 배열의 크기가 리스트 사이즈보다 작을때 Arrays.copyOf 로 복사해서 반환하고
배열의 크기가 리스트 사이즈보다 크거나 같으면 System.arraycopy 로 기존 배열에 할당해 반환한다.
이 두가지 동작의 차이가 성능의 차이가 되는것 같다.

```java
public <T> T[] toArray(T[] arr) {
    // 인자로 받은 배열이 리스트 사이즈보다 작을때 그대로 복사해서 반환.
    if(arr.length < size) {
        // Arrays.copyOf(원본 배열, 복사할 길이)
        return (T[]) Arrays.copyOf(elementData, size);
    }
    // 인자로 받은 배열이 리스트 사이즈보다 크거나 같을때,
    // 배열의 원소들을 유지한채 리스트 요소들만 삽입.
    // 마지막은 null 할당 표시해 어디까지 할당되었는지 알려주기위해 설계되었다고 한다.
    else {
        // System.arraycopy(원본배열, 원본배열 시작위치, 복사할 배열, 복사할배열 시작위치, 복사할 요소 수)
        System.arraycopy(elementData, 0, arr, 0, size);

        if(arr.length > size) arr[size] = null;

        return arr;
    }
}
```

List의 <T> T[] toArray(T[] a) 메서드 특징 참고
- https://shipilev.net/blog/2016/arrays-wisdom-ancients/

## 요약 정리
- null이 아닌 빈 배열이나 컬렉션을 반환하자.
- null 반환시 오류 처리 코드가 늘어나게되고, 빈 객체를 할당 반환한다고해도 성능차이가 크게없다.
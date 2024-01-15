#  Item46 스트림에서는 부작용 없는 함수를 사용하라

---
## 1. 스트림의 패러다임
- 함수형 프로그래밍에 기초한 패러다임.
- 계산을 일련의 변환으로 재구성하는 단계는 순수 함수 이어야 합니다.
- 순수함수란 입력만이 결과에 영향을 주는 함수
- 다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 않아야합니다.
---
## 2. 잘못 사용한 경우
- 내부에서 외부 상태를 수정하는 경우 순수함수가 아닙니다.
- 다음은 파일의 문자를 읽어 문자가 몇번 등장하는지 세어 맵에 담는 예제 입니다.

```java
Map<String, Long> freq = new HashMap<>();
try(Stream<String> words = new Scanner(file).tokens()) {
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
      });
}
```
외부 맵에 키를 담아서 숫자를 업데이트 머지 해주는 로직인데, 람다 외부의 다른 객체 상태를 수정하는 코드가 들어가므로 좋지 않은 코드입니다.


```java
Map<String, Long> freq;
try(Stream<String> words = new Scanner(file).tokens()) {
    freq = words.collect(groupingBy(word.toLowerCase(), counting()));
}
```

### forEach 사용시 주의사항
- 스트림 계산의 결과 보고를 위해서만 사용하고, 계산에 사용 X
- 다음과 같이 수행의 결과를 보고, 기록하는 역할 정도로 사용할 수 있겠습니다.

```java
try(Stream<String> words = new Scanner(file).tokens()) {
    words.collect(groupingBy(word -> word, counting()))
        .forEach((key, value) -> logger.log(new LogEvent(key, value)));
}
```
---
## 3. 수집기. Collectors의 사용법

스트림 함수인 collect의 동작 방식은 인자로 받은 Collector로 reducing 연산을 수행해 결과를 반환합니다.
Collectors는 Collector의 구현체는 아니고 내부 클래스로 미리 정의된 CollectorImpl을 가지고 있습니다.
정적 메서드가 호출 될때마다 CollectorImpl을 만들어 반환합니다.
많이 사용하는 toList, toMap 등을 모아둔 일종의 유틸 클래스 입니다.

### 원소 모으기
#### toList()
가장 빈번히 사용. but, type, thread-safe 하지는 않음.
```java
final List<String> strings = List.of("a", "b", "b", "w", "a", "c");
final List<String> collect = strings.stream().sorted(Comparator.reverseOrder())
        .collect(toList());
System.out.println(collect);
```
#### toMap() 
고유한 키를 가질때 사용.</br>
세번째 인수는 merge function으로 key가 중복때 어떤값을 취할지 지정해 줄수 있습니다.
```
toMap(keyMapper, valueMapper, (oldVal,newVal)-> newVal);
```

아티스트와 가장 높은 판매량을 가진 앨범을 매칭하여 수집하는 예제
```
Artist artist1 = new Artist("artist1");
Artist artist2 = new Artist("artist2");
Album album1 = new Album(artist1, "album1", 1000L);
Album album2 = new Album(artist2, "album2", 2000L);
Album album3 = new Album(artist1, "album3", 3000L);

List<Album> albums = List.of(album1, album2, album3);
Map<Artist, Album> topHits = albums.stream().collect(
    toMap(Album::getArtist, a -> a, BinaryOperator.maxBy(Comparator.comparing(Album::getSales)))
);
```

#### groupingBy()
원소들을 카테고리별로 모아 반환합니다.
두번째 인수로 다운스트림 수집기도 전달해 분류 수집 가능합니다.
```java
Map<Artist, Long> collectByArtist = albums.stream()
        .collect(groupingBy(Album::getArtist, counting()));
```
```
{Artist(name=artist1)=2, Artist(name=artist2)=1}
```
#### partitionBy()
분류함수 Predicate를 인수로 받아 조건에 따라 키가 Boolean인 맵을 모아 반환합니다.
```java
Map<Boolean, List<Album>> collectBySales = albums.stream()
        .collect(partitioningBy(album -> album.getSales() > 1500L));
```
```
{false=[Album(artist=Artist(name=artist1), title=album1, sales=1000)], 
true=[Album(artist=Artist(name=artist2), title=album2, sales=2000), Album(artist=Artist(name=artist1), title=album3, sales=3000)]}
```
#### minBy(), maxBy()
인수로 받은 비교자를 이용해 스트림 값이 최소 또는 최고값 원소를 반환합니다.
Stream 인터페이스에도 같은 기능이 정의 되어있습니다.

### joining
StringBilder를 이용해 문자열을 연결해줍니다. 연결 문자열 지정도 가능합니다.
```java
String titles = albums.stream().map(Album::getTitle).collect(joining(","));
```
---
## 4. 정리
- 스트림 사용시 관련 객체와 모든 함수 객체가 부작용이 없어야 합니다.
- forEach는 계산 자체가 아닌 결과 보고시에만 사용하도록 합니다.
- Collectors 수집기를 잘 활용합시다.

---
### + 추가 질문사항

1. flatMap 소개
flatMapping 은 다중 레벨로 나뉜 스트림을 연산하여 하나의 스트림으로 만들어 결과를 도출합니다.
groupingBy 또는 partitionedBy 와 함께 사용시 유용합니다.
다음의 예시를 보면 한번 그루핑된 스트림을 처리하는데 쓸수 있고, downstream 인자로 만들어질 타입도 지정할 수 있습니다.
```java
Map<String, Set<LineItem>> itemsByCustomerName = orders.stream().collect(
          groupingBy(Order::getCustomerName,
                     flatMapping(order -> order.getLineItems().stream(),
                                 toSet())));
```

2. reduce 소개
Stream.reduce(accumulator) 함수는 스트림 요소들을 연산해 하나의 요소로 도출합니다.
연산을 수행하는 함수 인자로 BinaryOperator 객체를 받습니다.
BinaryOperator는 T 타입 인자 두개를 받고, T타입 객체를 리턴하는 함수형 인터페이스입니다.

```java
Stream<Integer> intStream = Stream.of(1, 2, 3, 4, 5);
Optional<Integer> sum = intStream.reduce((x, y) -> x + y);
```

Collectors의 정적 요약 연산 대부분 Collectors.reducing 메서드를 이용해 값을 도출합니다.
reducing 메서드를 사용하면 커스텀해서 기능을 만들어 사용할 수 있습니다.
예를 들어 다음과 같이 앨범 누적 판매수를 도출할 수 있습니다.
```java
Long sales = albums.stream()
        .collect(reducing(
                0L, //연산 초기값
                Album::getSales, //각 요소에 적용 변환함수
                Long::sum)//요약할 BinaryOperator 누적함수
        );
```
실제로는 위의 예제처럼 장황하게 쓰지않고, stream의 reduce 메서드를 쓰면 됩니다.




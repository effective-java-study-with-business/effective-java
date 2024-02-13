# 아이템 61. 박싱된 기본 타입보다는 기본 타입을 사용하라

기본 타입 : int, double, boolean
박싱된 기본 타입 : Integer, Double, Boolean

## 주된 차이점 3가지

1. 박싱된 기본 타입은 값에 더해 식별성을 갖는다. 박싱된 기본 타입의 두 인스턴스는 값이 같아도 서로 다르다고 식별될 수 있다.

2. 박싱된 기본 타입은 유효하지 않은 값, null을 가질 수 있다.

3. 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다.


## 타입 사용 예시 1

### 잘못 구현된 비교자
```java
Comparator<Integer> naturalOrder = (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);

naturalOrder.compare(new Integer(42), new Integer(42)) // 1 출력
```
별다른 문제를 찾기 어렵고, 테스트 또한 잘 통과한다.

naturalOrder.compare을 실행하게 되면 예상은 0이 나와야하지만 결과는 1이 나온다.

i == j 이 부분에서 결국 42의 같은 값의 박싱된 기본타입은 다른 데이터로 취급하여 0이 아닌 1이 나오는 것!


### 해결방법
```java
Comparator<Ineteger> naturalOrder = (iBoxed, jBoxed) ->{
        int i=iBoxed,j=jBoxed;  // 오토 박싱
        return i<j ?-1:(i==j?0:1);
        }
```
오토 박싱 과정을 추가하여 기본 타입으로 수정 후 비교연산자를 진행한다.

## 타입 사용 예시 2

### 기이하게 동작하는 프로그램
```java
public cass Unbelievable {
    static Integer i;  // 예외 발생!

    public static void main(String[] args){
    	if (i == 42)
        	System.out.println("믿을 수 없군!");
    }
}
```
i == 42 를 진행 할 때 NullPointerException을 뱉는다.

당연히 박싱된 기본 타입 Integer i는 null이 초기값이기 때문에 오류가 나온다.

### 해결방법
i를 int로 선언해주면 된다.(...ㅎ)

## 타입 사용 예시 3

### 속도를 늦추는 경우
```java
public static void main(String[] args) {
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++) {
        sum += i;
    }
    System.out.println(sum);
}
```
오류는 없지만 sum을 박싱된 기본타입으로 선언했기 때문에 박싱과 언박싱이 반복해서 일어나 성능이 확 느려진다.

### 해결방법
기본 타입으로 선언하자!

## 박싱된 기본 타입은 언제 써야할까?

1. 컬렉션의 원소, 키, 값으로 쓴다. 
-> 컬렉션은 기본타입을 담을 수 없다.
2. 매개변수화 타입이나 매개변수화 메서드의 타입 매개변수에 사용한다. 
-> 자바 언어는 타입 매개변수로 기본 타입을 지원하지 않는다. 
3. 리플렉션을 통해 메서드를 호출할 때 사용한다.(아이템 65)

## 요약
1. 웬만하면 기본타입을 쓰자.
2. 오토박싱은 번거로움을 줄여주지만, 위험을 없애주지는 않는다.
3. 기본타입을 박싱하는 작업은 필요없는 객체를 생성하는 부작용을 나을수 있다.

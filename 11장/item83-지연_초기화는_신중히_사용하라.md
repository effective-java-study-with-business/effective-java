# item83. 지연 초기화는 신중히 사용하라

> 지연초기화(lazy initialization) 는 필드의 초기화 시점을 그 값이 처음 필요할때까지 늦추는 기법이다.

그래서 값이 전혀 쓰이지 않으면 초기화도 일어나지 않는다. 

다른 모든 최적화와 마찬가지로 지연 초기화에 대해 해줄 최선의 조언은 "필요할 때까지는 하지 말라" 라고 소개하고 있다.

지연 초기화는 양날의 검이다.

클래스 혹은 인스턴스 생성 시의 초기화 비용은 줄지만, 그 대신 지연 초기화하는 필드에 접근하는 비용이 커진다.

## 멀티스레드 환경

멀티스레드 환경에서는 지연 초기화를 하기가 까다롭고, 지연 초기화 하는 필드를 둘 이상의 스레드가 공유한다면 어떤 형태로든 반드시 동기화해야 한다.

이번 아이템에서 다루는 모든 초기화 기법은 스레드 세이프하다.

**하지만 대부분의 상황에서 일반적인 초기화가 지연 초기화 보다 낫다.** 라는걸 기억해야한다.


## 인스턴스 필드를 선언할 때 수행하는 일반적인 초기화

final 사용

```java
private final FeilType field = computeFieldValue();
```


## 인스턴스 필드의 지연초기화

**지연 초기화가 초기화 순환성을 깨뜨릴 것 같으면 synchronized를 사용하자**

```java
private FieldType field2;
private synchronized FieldType getField2() {
  if (field2 == null)
    field2 = computeFieldValue();
  return field2;
}
```

## 정적 필드용 지연 초기화 홀더 클래스 관용구

**성능 때문에 정적 필드를 지연 초기화해야 한다면 지연 초기화 홀더 클래스를 사용하자.**

클래스는 클래스가 처음 쓰일 때 비로소 초기화된다는 특성을 이용한 것.

```java
private static class FieldHolder {
  static final FieldType field = computeFieldValue();
}

private static FieldType getField() { 
	return FieldHolder.field; 
}
```

getField가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서, FieldHolder 클래스 초기화가 일어난다.

getField 메서드가 필드에 접근하면서 동기화를 전혀 하지 않으니, 성능이 느려질 거리가 전혀 없다는 것이다.

## 인스턴스 필드 지연 초기화용 이중검사
```java
private volatile FieldType field;

private FieldType getField() {
  FieldType result = field;
  if (result != null) return result;
  
  synchronized (this) {
      if(field == null)
          field = computeFieldValue();
      return field;
  }
}
```
초기화된 필드에 접근할 때의 동기화 비용을 없애준다.

필드의 값을 두 번 검사하는 방식으로, 한 번은 동기화 없이 검사하고, 필드가 초기화 되지 않았으면 동기화 하여 검사한다.


이중검사를 정적 필드에도 적용할 수 있지만 굳이 그럴 이유는 없다. 이보다는 지연 초기화 홀더 클래스 방식이 더 낫다.

이중검사에는 언급해둘 만한 변종이 두 가지 있다.
반복해서 초기화 해도 상관없는 인스턴스 필드를 지연초기화해야 할 때가 있는데, 이런 경우라면 synchronized 부분을 생략해도 된다.

## 중복 허용 지연 초기화
```java
private volatile FieldType field;

private FieldType getField() {
  FieldType result = field;
  if (result == null)
    field5 = result = computeFieldValue();
  return result;
}
```

모든 스레드가 필드의 값을 다시 계산해도 상관없고, 필드의 타입이 long과 double을 제외한 다른 기본 타입이라면, 단일 검사의 필드 선언에서 `volatile`는 없애도 된다.

## 정리
- 대부분의 필드는 지연시키지 말고, 즉시 초기화 시키기는게 정신건강에 좋다.
- 성능 때문에, 위험한 초기화 순환을 막기 위해 꼭 지연 초기화를 해야한다면 올바른 방법을 사용하자.


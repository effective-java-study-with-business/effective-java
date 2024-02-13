# 아이템 72. 표준 예외를 사용하라

자바 라이브러리는 대부분 API에서 쓰기에 충분한 수의 예외를 제공한다.

## 장점

1. API가 다른 사람이 익히고 사용하기 쉬워진다.
2. API를 사용한 프로그램도 낯선 예외를 던지지 않는다.
3. 예외 클래스가 적을수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게 걸린다.

## IllegalArgumentException

호출자가 인수로 부적절한 값을 넘길 때 던지는 예외이다.
가장 많이 사용된다.

다음 두 예외는 IllegalStateException으로 사용할 수 있지만, 관례상 구분하여 사용하자!
### NullPointException
Null값을 허용하지 않는 메서드에서 null을 건넬 때 던진다.

### IndexOutOfBoundsException
시퀀스의 허용 범위를 넘는 값을 건넬 때 던진다.

## IllegalStateException

대상 객체의 상태가 호출된 메서드를 수행하기에 적합하지 않을 때 주로 던진다.
제대로 초기화되지 않은 객체를 사용하려 할 때 던질 수 있다.

## ConcurrentModificationException
단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수정하려 할 때 던진다.
(외부 동기화 방식으로 사용하려고 설계한 객체도 마찬가지다.)

## UnsupportedOperationException
클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때 던진다.
대부분 객체는 자신이 정의한 메서드를 모두 지원하니 흔히 쓰이는 예외는 아니다.
ex) 원소만 넣을 수 있는 List 구현체에 대고 누군가 remove 메서드를 호출 하면 이 예외를 던진다.

## 직접 재사용에 안좋은 예외

Exception, RuntimeException, Throwable, Erro
이 예외들은 직접 재사용 하지 말자. 이 클래스는 추상클래스라고 생각해야한다.
포괄하는 클래스이므로 안정적으로 테스트 할 수 없다.


## IllegalArgumentException VS IllegalStateException
IllegalStateExceptpion : 인수 값이 잘못 들어왔는데, 무엇이었든 어차피 실패했을 경우
IllegalArguementException : 인수 값이 정상으로 들어왔을때 성공했을 경우

## 요약
1. 표준 예외를 쓰자~

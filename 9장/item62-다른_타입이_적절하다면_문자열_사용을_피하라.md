# item62. 다른 타입이 적절하다면 문자열 사용을 피하라

# 문자열을 쓰지 말아야 할 사례들

## 1. 문자열은 다른 값 타입을 대신하기에 적합하지 않다.

많은 사람이 파일, 네트워크, 키보드 입력으로부터 데이터를 받을 때 주로 문자열을 사용한다. 하지만 입력받을 데이터가 진짜 String 일때만 그렇게 하는 것이 좋다.

받은 데이터가 수치형이라면, int, float, BigInteger 등 적당한 수치 타입으로 변환해야 한다. 또한 ‘예/아니오’ 라면 boolean 타입이나 적절한 열거 타입으로 변환해야 한다.

기본 타입이든 참조 타입이든 적절한 값이 있다면 그것을 사용하고, 없다면 새로 만들어서 사용하라.

## 2. 문자열은 열거 타입을 대신하기에 적합하지 않다.

아이템 34에서 나오듯, 상수를 열거할 때는 문자열보다는 열거 타입이 월등히 우월하다.

## 3. 문자열은 혼합 타입을 대신하기에 적합하지 않다.

혼합된 데이터를 하나의 문자열로 표현하는 것은 좋지 않은 생각이다.

```java
// 혼합 타입을 문자열로 표현.. 좋지 않다!
String compoundKey = className + "#" + i.next();
```

위 예제는 각 요소를 개별로 접근하기 위해 문자열을 파싱한다. 따라서 느리고, 귀찮고, 오류 가능성도 커진다. 또한, 적절한 equals, toString, compareTo 메서드를 제공할 수 없고, 오직 String이 지원하는 기능에만 의존해야 한다.

이런 경우 보통 private 정적 멤버 클래스로 선언하는 편이 좋다.

```java
public class Compound {
    private CompoundKey compoundKey;

    private static class CompoundKey {
        private String className;
        private String delimiter;
        private int index;

        public CompoundKey(String className, String delimiter, int index) {
            this.className = className;
            this.delimiter = delimiter;
            this.index = index;
        }
    }
}
```

## 4. 문자열로 권한을 표현하는 경우

```java
public class ThreadLocal {
    private ThreadLocal() {} //객체 생성 불가
    
    // 현 스레드의 값을 키로 구분해 저장한다.
    public static void set(String key, Object value);
    
    // (키가 가르키는) 현 스레드의 값을 반환한다.
    public static Object get(String key);
}
```

위의 예제는 각 스레드가 자신만의 변수를 갖게 해주는 기능을 갖고 있다. 하지만 스레드 구분용으로 키 문자열을 받고 있는데, 여기서 문제가 발생한다.

바로 문자열 키가 전역 namespace에서 공유되고 있다는 것이다. 만약 key가 중복이 되는 경우 같은 값을 공유할 수 있고, 또한 의도적으로 같은 키를 이용해 다른 클라이언트의 값을 가져올 수도 있다. 그렇기 때문에 문자열로 하게되면 보안에도 취약하고 제대로 동작하지 않을 수 있기 때문에 좋지 않은 방법이다.

## 4-1. 리팩토링

문자열로 권한을 표현하기보다는 별도의 클래스로 분리해보자.

```java
public class ThreadLocal {
    private ThreadLocal() {} //객체 생성 불가

    public static class Key {
        key() {}
    }

    //위조 불가능한 고유 키를 생성한다.
    public static Key getKey() {
		    return new Key();
    }

    public static void set(Key key, Object value);
    public static Object get(Key key);
}
```

key 클래스로 권한을 구분했다. 하지만 여기서 끝이 아니다.

get/set 메서드는 더이상 정적 메서드일 필요가 없다. 따라서 Key의 인스턴스 메서드로 변경한다. 이렇게 하면 key는 더 이상 스레드 지역변수를 구분하기 위한 키가 아니라, 그 자체가 스레드 지역변수가 된다.

결과적으로 상위 레벨 클래스인 ThreadLocal 클래스는 하는 일이 없어지므로 중첩 클래스 key의 이름을 ThreadLocal로 변경해보자

```java
// Key를 ThreadLocal로 변경
public final class ThreadLocal {
    public ThreadLocal();
    public void set(Object value);
    public Object get();
}
```

이 메서드는 get으로 얻은 Object를 형 변환해서 사용해야 하므로 타입이 안전하지 않다. 이는 매개변수화 타입을 추가하여 타입을 안전하게 만들 수 있다.

```java
// 매개변수화로 타입안전성 확보
public final class ThreadLocal<T> {
    public ThreadLocal();
    public void set(T value);
    public T get();
}
```

## 정리

- 더 적합한 데이터 타입이 있거나 새로 작성할 수 있다면, 문자열보다 그 타입을 사용하자.

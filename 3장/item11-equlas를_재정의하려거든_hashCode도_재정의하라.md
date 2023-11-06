# 아이템11-1. hashCode 규약

## 포인트

- equals 비교에 사용하는 정보가 변경되지 않았다면 hashCode는 매번 같은 값을 리턴해야 된다. (변경되거나, 어플리케이션을 다시 실행한다면 달라질 수 있다.)
- 두 객체에 대한 equals가 같다면, hashCode의 값도 같아야 한다.
- 두 객체에 대한 equals가 다르더라도, hashCode의 값은 같을 수 있지만 해시 테이블 성능을 고려해 다른 값을 리턴하는 것이 좋다. (성능 상 느려지지만 에러는 아님)

## 1. equals만 구현되어 있을 경우

```java

public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix   = rangeCheck(prefix,   999, "prefix");
        this.lineNum  = rangeCheck(lineNum, 9999, "line num");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }

}
```

### 실행해보자!

```java
public class HashMapTest {

    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        PhoneNumber number1 = new PhoneNumber(123, 456, 789);
        PhoneNumber number2 = new PhoneNumber(123, 456, 789);

        // 같은 인스턴스인데 다른 hashCode
        System.out.println(number1.equals(number2));
        System.out.println(number1.hashCode());
        System.out.println(number2.hashCode());

        map.put(number1, "alex");
        map.put(number2, "Bae");

        String s = map.get(number2);
        System.out.println(s);

    }
}
```

### PhoneNumber는 같은데 두 개의 해시코드가 다르다!!

### 근데 map에서 꺼냈을 때는 잘 꺼내지는데…? 잘 동작하는 거 같은데 무슨 문제라도??

![스크린샷 2023-10-27 오후 1 31 48](https://github.com/effective-java-study-with-business/effective-java/assets/79031788/82d6cf4d-6809-48b1-a400-987d28977753)


## 값 클래스들은 또다른 값을 넣더라도 같은 값이 같다고 판단이 되어야 한다!

```java
public class HashMapTest {

    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        PhoneNumber number1 = new PhoneNumber(123, 456, 789);
        PhoneNumber number2 = new PhoneNumber(123, 456, 789);

        // 같은 인스턴스인데 다른 hashCode
        System.out.println(number1.equals(number2));
        System.out.println(number1.hashCode());
        System.out.println(number2.hashCode());

        map.put(number1, "alex");
        map.put(number2, "Bae");
				// 같은 값을 넣어보자
        String s = map.get(new PhoneNumber(123,456,789));
        System.out.println(s);

    }
}
```

### 결과는?

![스크린샷 2023-10-27 오후 1 37 56](https://github.com/effective-java-study-with-business/effective-java/assets/79031788/287a972e-d3b2-48d5-8849-6d252b544436)


### 분명 똑같은 넘버로 2개나 map에 들어가 있음에도 불구하고 null이 나온다!

## 왜 그럴까???

## 이유를 알아보기 위해 HashMap.java를 열어보자

![스크린샷 2023-10-27 오후 1 47 34](https://github.com/effective-java-study-with-business/effective-java/assets/79031788/13d8a8ca-5dfd-4193-8f51-8e4c65325113)


### 자바의 HashMap에서는 값을 넣을 때 hashCode값을 같이 넣어주고 있었답니다~

### 추가적으로 값을 넣을 때 hash 메서드를 실행시킴으로써 어떤 버킷에 넣을 지 정하고 넣습니다. 꺼낼 때도 마찬가지로 key에 대한 hashCode값을 먼저 가져와서, hashCode를 통해 해당 해시의 버킷에 담겨있는 Object를 가져오게 되는거죠!

## 그럼 다시 이전 코드로 돌아와서

```java
public class HashMapTest {

    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        PhoneNumber number1 = new PhoneNumber(123, 456, 789);
        PhoneNumber number2 = new PhoneNumber(123, 456, 789);

        // 같은 인스턴스인데 다른 hashCode
        System.out.println(number1.equals(number2));
        System.out.println(number1.hashCode());
        System.out.println(number2.hashCode());

        map.put(number1, "alex");
        map.put(number2, "Bae");
				// 아! 해시코드값을 같이 안 넣어줘서 그랬구나!
				// 그래서 이 값에 대한 해시값(버킷)이 없어서 못 찾았던 거구나!
        String s = map.get(new PhoneNumber(123,456,789));
        System.out.println(s);

    }
}
```

## 그리고 또 다시 드는 의문…

## 다른 값(인스턴스)인데 같은 hashCode를 갖게 되면 어떡하지…?

## 직접 테스트해보자!

### 이전의 phoneNumber 클래스에서 hashCode를 오버라이딩 해보았다.

```java

public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix   = rangeCheck(prefix,   999, "prefix");
        this.lineNum  = rangeCheck(lineNum, 9999, "line num");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }
		
		// 무조건 hashCode를 42가 나오게끔 설정
    @Override
    public int hashCode() {
        return 42;
    }

}
```

### 그리고 실행용 코드를 수정해보자

```java
public class HashMapTest {

    public static void main(String[] args) {
        Map<PhoneNumber, String> map = new HashMap<>();
        PhoneNumber number1 = new PhoneNumber(123, 456, 789);
				// 다른 값으로 세팅
        PhoneNumber number2 = new PhoneNumber(456, 789, 111);

        // 다른 인스턴스인데 같은 hashCode를 쓴다면?
        System.out.println(number1.equals(number2));
        System.out.println(number1.hashCode());
        System.out.println(number2.hashCode());

        map.put(number1, "alex");
        map.put(number2, "Bae");

        // 같은 해시코드 값을 갖은 인스턴스가 여러개인데 과연 찾을 수 있을것인가!
        String s = map.get(number1);
        System.out.println(s);

    }
}
```

### “해시 충돌이 나서 못 찾아오지 않을까” 라고 생각이 들던중…

### 결과는?!

![스크린샷 2023-10-27 오후 2 35 58](https://github.com/effective-java-study-with-business/effective-java/assets/79031788/7d5aebc6-c9b7-4938-9b56-581fa38405d4)


### alex라는 number1의 value를 잘 찾아와버렸다!

## 근데 왜…? 이게 가능한거지…? 왜?

### 결론부터 말하자면, 해시 충돌이 발생하면 해시 버킷에 담겨있는 Object를 Object가 아닌 LinkedList로 만들어준다. 집어넣을 때도 해시 코드가 같으면 같은 버킷에 일단 같이 들어가게 되는데, 그 버킷안에 들어있는 LinkedList에 들어가게 되는 것!

### 그리고 꺼낼 때 그 LinkedList를 꺼내서 그 리스트 안에 있는 값들을 하나하나 equals()로 비교를 해주는 것! 그래서 결국 찾아오기는 해준다.

### 하지만 이렇게 조회해오는 방식은 HashMap의 이점을 전혀 살리지 못하고, 그냥 리스트를 사용하는 것과 똑같다.

- HashMap은 HashCode를 가지고 값에 접근하기 때문에 (마치 배열의 인덱스를 바로 알고 접근하는 것처럼) 꺼내는 비용이나 넣는 비용이나 O(1)이다.
- 하지만 이런식으로 LinkedList의 형식으로 넣고 빼게 되면 O(n)만큼의 비용이 발생하게 된다.


# 아이템11-2. hashCode 구현방법

## 포인트

```java
// 전형적인 hashCode 메서드
@Override public int hashCode() {
        int result = Short.hashCode(areaCode); // 1

				// 다른 예시
				// 참조 타입일 경우 -> point.hashCode();
				// 배열의 경우 -> Arrays.hashCode();
				
        result = 31 * result + Short.hashCode(prefix); // 2
        result = 31 * result + Short.hashCode(lineNum); // 3
        return result;
    }
```

- 핵심 필드 하나의 값의 해쉬값을 계산해서 result 값을 초기화 한다.
- 기본 타입은 Type.hashCode / 참조 타입은 해당 필드의 hashCode / 배열은 모든 원소를 재귀적으로 위의 로직을 적용하거나, Arrays.hashCode / result = 31 * result + 해당 필드의 hashCode 계산값
- result를 리턴

## 여기서 생기는 궁금증… 왜 31을 곱하는거지?

> 31을 곱하는 이유는 소수이면서 홀수이기 때문이다.
> 
> 
> 만일 그 값이 짝수였고, 곱셈 결과가 오버플로되었으면 정보는 사라졌을 것이다. 2로 곱하는 것은 비트를 왼쪽으로 shift하는 것과 같기 때문이다. 소수를 사용하는 이점은 그다지 분명하지 않지만 전통적으로 널리 사용된다.
> 
> 31의 좋은 점은 곱셈을 시프트와 뺄셈 조합으로 바꾸면 더 좋은 성능을 낼 수 있어서라고 한다.
> 

### 흠.. 그렇다고 한다

## 사실 우리는 대부분 해시코드 메서드의 오버라이딩을 인텔리제이에게 맡긴다

```java
@Override public int hashCode() {
        return Objects.hash(lineNum, prefix, areaCode);
}
```

- 주의할 점 : equals에서 lineNum, prefix, areaCode로 비교를 하고 있다면, “성능을 위해 lineNum을 빼자!” 이런 행위는 하지 말아야 된다는 것!

### Object.hash()메서드 내부

![스크린샷 2023-10-27 오후 3 55 49](https://github.com/effective-java-study-with-business/effective-java/assets/79031788/ff51a585-a299-4bdc-9f2b-57549320ad65)


### 결국 비슷하게 만들어준다. 굳이 복잡하게 우리가 만들 필요 없다는 것!

## 구아바에서 제공하는 라이브러리가 있지만? 해시코드 때문에 라이브러리를 추가한다… 그리고 그 해싱 알고리즘에 대한 신뢰도… 등 고려해서 사용하는게 좋을 것 같다.

## 만약 클래스가 불변한다는 가정이 있다면 미리 정의해서 캐싱해서 쓸 수도 있다.

```java
// 해시코드를 지연 초기화하는 hashCode 메서드 - 스레드 안정성까지 고려해야 한다.
    private int hashCode; // 자동으로 0으로 초기화된다. 

    @Override public int hashCode() {
        if (this.hashCode != 0) {
            return hashCode;
        }

        synchronized (this) {
            int result = hashCode;
            if (result == 0) {
                result = Short.hashCode(areaCode);
                result = 31 * result + Short.hashCode(prefix);
                result = 31 * result + Short.hashCode(lineNum);
                this.hashCode = result;
            }
            return result;
        }
    }
```

- 필드에 해시코드값을  저장해두고 재사용
- 해시코드가 필요한 그 순간에 계산(미리 생성자에서 계산이 아니라)
- 지연 초기화(Lazy Initialization) → 관련 자료 : [https://ktaes.tistory.com/89](https://ktaes.tistory.com/89)
- 스레드 안정성 신경써야 된다. (추후에 공부해보자)
- 간단하게 설명하면 hashCode()라는 메서드 안에 하나의 스레드만 들어오면 좋겠지만, 여러 개의 스레드가 동시에 들어올 수 있다. 동일한 값을 가진 불변 객체인데 해시 코드가 다를 수 있다는 점! (스레드 두개가 동시에 들어와서 서로 계산을 막 엇갈려서 하다가 리턴을 하면서 필드에 값을 설정하는 순간? 내가 원했던 값이 아닌데!)

## 이런 방법들 외에도

```java
@EqualsAndHashCode
public final class PhoneNumber {
```

### 롬복이 제공하는 어노테이션을 쓰면 끝나긴 한다 (사용성 측면에서 매우 편리할듯)

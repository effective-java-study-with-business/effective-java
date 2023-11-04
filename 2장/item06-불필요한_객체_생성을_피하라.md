#아이템 6. 불필요한 객체 생성을 피하라

같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 더 빠르고 세련되었다.

## 문자열 생성

쓰면 안되는 예
```java
String s = new String("bikini");
```
다음 문장은 실행이 될 때마다 String 인스턴스를 새로 만든다.
-> (단점)완전 똑같은 행위이며, 반복문같은 구문에 있다면 쓸데없는 인스턴스가 만들어진다.

올바른 예시
```java
String s = "bikini";
```
하나의 String 인스턴스를 사용하므로 효율적이다.
-> 이렇게 하면 가상 머신 안에서 이와 같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다.(JLS 자바표준)

## 정적 팩터리 메서드를 사용

생성자 대신 정적 팩터리 메서드를 제공하는 불변 클래스에서는 정적 팩터리 메서드를 사용해 불필요한 객체 생성을 피할 수 있다.

```java
boolean b = new Boolean(String); //자바 9 이상 자제 17은 'Boolean(java.lang.String)' is deprecated and marked for remova
boolean b = Boolean.valueOf(String);
```

## 비싼 객체

생성비용이 비싼 객체가 반복해서 필요하다면 캐싱하여 재사용하길 권한다. 

문자열이 유효한 로마 숫자인지 정규표현식으로 판별하는 메서드이다.
```java
static boolean isRomanNumeral(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```

String.matches는 가장 쉽게 정규 표현식에 매치가 되는지 확인하는 방법이긴 하지만 성능이 중요한 상황에서 반복적으로 사용하기에 적절하지 않다.

matches내부에서는 다음과 같이 매번 정규식을 가지고 새로운 패턴을 만들어냄 -> 한번 쓰고 GC 대상이 됨
```java
public boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }
```

즉, 다음과 같이 불변 Pattern 인스턴스 초기화 과정을 거쳐 직접 생성하여 나중에 재사용하게 해야함
```java
public class RomanNumber {

    private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeral(String s) {
        return ROMAN.matcher(s).matches();
    }

}
```
성능이 개선되고, 코드도 명확해지며, 행위의 의미가 잘 드러난다.

하지만, isRomanNumeral에서 클래스가 초기화 된 후 이 메서드를 한번도 호출하지 않는다면 ROMAN은 쓸데없이 초기화된 꼴이다. "지연초기화"로 불필요한 초기화를 없앨 수는 있지만, 코드를 복잡하게 만들고 성능은 크게 개선되지 않기 때문

(지연초기화 : 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법)

## 어댑터(View)

실제 작업은 뒷단 객체에 위임하고, 자신은 제 2의 인터페이스 역할을 해주는 객체이다. 즉, 뒷단 객체 외에는 관리할 상태가 없으므로 뒷단 객체 하나당 어댑터 하나씩만 만들어지면 충분하다.

Map의 keySet() 메서드는 키를 담은 Set 어댑터(뷰)를 호출한다. 반환된 객체 중 하나를 수정하면 다른 객체가 따라서 바뀐다. 모두가 똑같은 Map 인스턴스를 반환하기 때문이다. -> Set이 동일한 데이터의 뷰를 제공하기 때문
```java
public class Main {
    public static void main(String[] args) {

        Map<String,String> map = new HashMap<>();

        map.put("java","");
        map.put("c","");

        Set<String> set1 = map.keySet();
        Set<String> set2 = map.keySet();

        System.out.println(set1.size()); //  => 2
        System.out.println(set2.size()); // = > 2

        map.remove("c");
        System.out.println(set1.size()); //  => 1
        System.out.println(set2.size()); //  => 1
    }
}
```

## 오토박싱
프로그래머가 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환해주는 기술이다.
불필요한 객체를 만들어내는 예이다. 
기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐려주지만, 완전히 없애주는 것은 아니다.

```java
public class Main {
     public static void main(String[] args) {
        Long sum = 0L;
        for (long i = 0 ; i <= Integer.MAX_VALUE ; i++) {
            sum += i;
        }
        System.out.println(sum);
    }
}
```
위 코드는 불필요한  불변Long 객체에 long형 i를 계속 더하게 되므로 불필요한 Long 인스턴스가 약 2의 31 제곱개 생성된다.
박싱된 타입보다는 기본타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자.

Long 객체가 지속적으로 만들어지는 이유는, Long 클래스는 immutable하기 때문입니다. 즉, Long 객체를 생성하면 그 값을 변경할 수 없습니다. 따라서, sum += i 연산을 수행할 때마다 새로운 Long 객체가 생성됩니다. 이러한 객체 생성은 많은 메모리를 소비하고 성능을 저하시킬 수 있습니다. 이를 방지하기 위해서는 long[ 타입을 사용하는 것이 좋습니다]

## 주의
무거운 객체가 아닌 단순 객체 생성을 피하고자 풀(pool)을 만들지는 말자! 다만 비용이 비싸 재사용이 필요한 DB같은 경우는 제외
-> 코드를 햇갈리게 만들고, 메모리 사용량을 늘리고 성능을 떨어트린다.



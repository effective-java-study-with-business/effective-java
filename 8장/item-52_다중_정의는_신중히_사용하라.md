# item52. 다중 정의는 신중히 사용하라

이름이 같은 메서드가 매개변수의 타입이나 개수만 다르게 갖는 형태를 **다중정의(overloading)**라고 한다. 이 다중 정의를 사용할 때는 신중해야 한다.

# 컴파일 타임에 정해지는 다중정의 메서드

컬렉션을 집합, 리스트, 그 외로 구분하고자 만든 프로그램이다.

```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> lst) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections)
            System.out.println(classify(c));
    }
}
```

"집합", "리스트", "그 외"를 차례로 출력할 것 같지만, 실제로 수행해보면 "그 외"만 세번 연달아 출력한다.

왜냐하면 다중정의된 세 classify 중 어느 메서드를 호출할지가 컴파일타임에 정해지기 때문이다.

컴파일타임에는 for 문 안의 c는 항상 Collection<?> 타입이다.

런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는 데는 영향을 주지 못한다.

따라서 컴파일타임의 매개변수 타입을 기준으로 항상 세 번째 메서드인 classify(Collection<?>)만 호출하는 것이다.

이처럼 직관과 어긋나는 이유는 재정의한 메서드는 동적으로 선택되고, 다중정의한 메서드는 정적으로 선택되기 때문이다.

메서드를 재정의했다면 해당 객체의 런타임 타입이 어떤 메서드를 호출할지의 기준이 된다. 컴파일타임에 그 인스턴스의 타입이 무엇이었냐는 상관없다. 다음 코드는 이러한 상황을 구체적으로 보여준다.

```java
public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
                new Wine(), new SparklingWine(), new Champagne());

        for (Wine wine : wineList)
            System.out.println(wine.name());
    }
}
```

Wine 클래스에 정의된 name 메서드는 하위 클래스인 SparklingWine과 Champagne에서 재정의된다.

예상한 것처럼 이 프로그램은 "포도주", "발포성 포도주", "샴페인" 을 차례로 출력한다.

## 다중 정의 문제를 피하는 방법 1 - instance of

CollectionClassifier에서의 컴파일타임 다중정의 메서드 결정 문제는 모든 classify 메서드를 하나로 합친 후 instanceof로 명시적으로 검사하면 말끔히 해결된다.

```java
public static String classify(Collection<?> c) {
    return c instanceof Set  ? "집합" :
            c instanceof List ? "리스트" : "그 외";
}
```

**헷갈릴 여지가 있는 다중정의는 최대한 지양하자**

개발자에게는 재정의가 정상적으로 보이고, 다중정의가 예외적으로 보일 수 있다.

즉, 재정의한 메서드는 개발자가 기대한 대로 동작하지만, CollectionClassifier 예시처럼 다중정의한 메서드는 이러한 기대를 가볍게 무시한다.

다중정의가 혼동을 일으키는 상황을 최대한 피해야 한다. 정확히 어떻게 사용했을 때 다중정의가 혼란을 주느냐에

대해서는 논란의 여지가 있다. 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자. 가변인수를 사용하는 메서드라면 다중정의를 아예 하지 말아야 한다. (item 53)

다중정의하는 대신 메서드 이름을 다르게 지어주는 길은 항상 열려있다.

## 다중 정의 문제를 피하는 방법 2 - **메서드를 분리하여 정의하기**

ObjectOutPutStream 클래스의 경우를 살펴보자. 이 클래스의 write 메서드는 모든 기본 타입과 일부 참조 타입용 변형을 가지고 있다. 그런데 다중정의가 아닌, 모든 메서드에 다른 이름을 지어주는 길을 택했다. 

이 방식의 장점은 read 메서드의 이름과 짝을 맞추기 좋다는 점이다.

writeBoolean(boolea), writeInt(int), writeLong(long) 

readBoolean(), readInt(), readLong() 

ObjectOutPutStream 클래스의 경우를 살펴보자. 이 클래스의 write 메서드는 모든 기본

타입과 일부 참조 타입용 변형을 가지고 있다. 그런데 다중정의가 아닌, 모든 메서드에 다른

이름을 지어주는 길을 택했다. writeBoolean(boolea), writeInt(int), writeLong(long) 처럼.

이 방식의 장점은 read 메서드의 이름과 짝을 맞추기 좋다는 점이다. readBoolean(),

readInt(), readLong() 처럼. 실제로도 이렇게 되어 있다.

## **다중 정의** 문제를 피하는 **방법 3 - 정적 팩터리 메서드 사용**

한편, 생성자는 이름을 다르게 지을 수 없으니 두 번째 생성자부터는 무조건 다중정의가 된다. 하지만 정적 팩터리라는 대안을 활용할 수 있는 경우가 많다. ([item 1](https://jithub.tistory.com/265?category=861592)) 또한 생성자는 재정의할 수 없으니 다중정의와 재정의가 혼용될 걱정은 하지 않아도 된다. 그래도 여러 생성자가 같은 수의 매개변수를 받아야 하는 경우를 완전히 피해갈 수는 없으니, 그럴 때를 대비해 안전 대책을 배워두면 도움이 될 것이다.

## **다중 정의 문제를 피하는 방법 4 - 근본적으로 다른 매개변수 타입 사용**

매개변수가 같은 다중정의 메서드가 많더라도, 그중 어느 것이 주어진 매개변수 집합을 처리할지가 명확히 구분된다면 헷갈릴 일은 없을 것이다. 즉, 매개변수 중 하나 이상이 "근본적으로 다르다"면 헷갈릴 일이 없다.

> 근본적으로 다르다는 것은 null이 아닌 두 타입의 값을 서로 어느 쪽으로든
> 
> 
> 형변환할 수 없다는 뜻이다.
> 

이 조건만 충족하면 어느 다중정의 메서드를 호출할지가 매개변수들의 런타임 타입만으로 결정된다. 따라서 컴파일타임 타입에는 영향을 받지 않게 되고, 혼란을 주는 주된 원인이 사라진다.

# 다중 정의 시 주의를 기울여야 하는 이유

다음과 같은 코드가 있다. 이 프로그램의 결과 값은 어떻게 출력될까?

```java
public class SetList {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();

        for (int i = -3; i < 3; i++) {
            set.add(i);
        }

        for (int i = 0; i < 3; i++) {
            set.remove(i);
        }
        System.out.println(set);
    }

}
```

[-3, -2, -1, 0, 1, 2] 의 값에서 [0, 1, 2]를 지우니까 [-3, -2, -1]이 출력되어야 할 것 같다.

실제로 [-3, -2, -1]이 출력되면서 테스트가 통과한다. Set의 remove() 메서드의 시그니처는 remove(Object)기 때문에 정상적으로 0 이상의 값을 지운다.

그렇다면 List는 어떨까?

```java
public class SetList {
    public static void main(String[] args) {
       List<Integer> list = new ArrayList<>();

        for (int i = -3; i < 3; i++) {
            list.add(i);
        }
        for (int i = 0; i < 3; i++) {
            list.remove(i);
        }
        System.out.println(list);
    }

}
```

똑같이 remove 메서드를 세 번 호출한다. 그러면 이 프로그램은 음이 아닌 값, 즉 0, 1, 2를

제거한 후 "[-3, -2, -1] [-3, -2, -1]" 을 출력하리라 예상할 것이다. 하지만 실제로는

집합에서는 음이 아닌 값을 제거하고, 리스트에서는 홀수를 제거한 후 "[-3, -2, -1]

[-2, 0, 2]" 를 출력한다. 무슨 일일까?

위의 코드에서는 remove(Object)가 아닌 remove(int index) 메서드가 선택된다. 따라서 값이 아닌 index의 원소를 제거하기 때문에 [-2, 0, 2]라는 값이 출력되는 것이다.

Java4까지는 Object와 int가 근본적으로 달라 문제가 없었지만, Java5에 오토 박싱이 도입되면서 이 개념이 흐트러졌다. 즉, 이제는 int와 Integer가 근본적으로 다르지 않다는 것이다. 이 문제는 remove를 호출할 때 매개변수를 Integer로 형 변환해주면 해결된다.

위의 예시에서 중요한 점은 제네릭과 오토 박싱(신규 기능)이 추가되면서 기존의 List 인터페이스가 취약해졌다는 것이다. (다중정의에 의해)

이 예시만으로도 다중정의를 왜 신중하게 사용해야 하는지에 대한 충분한 근거가 된다.

## **핵심 정리**

- 반적으로 매개변수 수가 같을 때는 다중정의를 피하는 게 좋다.
- 만약 다중정의를 피할 수 없는 상황이라면 헷갈릴 만한 매개변수는 형변환하여 정확한 다중정의 메서드가 선택되도록 하자.

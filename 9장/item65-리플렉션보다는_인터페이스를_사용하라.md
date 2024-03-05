## 1️⃣. 리플렉션 이란?

리플렉션(`java.lang.reflect`)를 이용하면 프로그램에서 임의의 클래스에 접근할 수 있습니다.

- Class 객체가 주어지면 그 클래스의 생성자, 메소드, 필드에 해당하는 생성자, 메소드, 필드 인스턴스를 가져올 수 있다.
- 인스턴스들로 해당 클래스의 멤버 명, 필드 타입, 메서드 시그니처 등을 가져올 수 있다.
- 인스턴스를 활용해 각각의 **연결된 실제 생성자, 메소드, 필드를 조작**할 수 있다.
  - 생성자로 클래스 인스턴스 생성
  - 메서드 호출(`Method.invoke`)
  - 필드 접근
- 컴파일 당시에 존재하지 않던 클래스도 이용할 수 있다.

## 2️⃣. 리플랙션 단점

- 컴파일타임에 타입 검사가 주는 이점을 하나도 누릴 수 없다
- 리플렉션을 이용하면 코드가 지저분하고 장황해진다.
- **성능이 떨어진다.**
  - 리플렉션을 통한 메서드 호출은 일반 메서드 호출보다 훨씬 느리다.

## 3️⃣. 단점은 피하고 이점은 취하자

- 해당 예시는 Set<String> 인터페이스의 인스턴스를 생성하는데, 정확한 클래스는 명령줄의 첫 번쨰 인수로 확정한다.
- 그 후, 생성한 집합(Set)에 두 번째 이후의 인수들을 추가한 다음 화면에 출력한다.
- 첫 번째 인수와 상관업시 이후의 인수들에서 중복을 제거한 후 출력한다.
- 해당 코드는 첫 번째 인수로 지정한 클래스가 무엇이냐에 따라 달라지는데, HashSet이면 무작위 순서가 될 것이고, TreeSet으로 지정하면 알파벳 순서로 출력될 것이다.

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class ReflectionMain {
    public static void main(String[] args) {
        // 클래스명 Class 객체로 변환
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 생성자
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 집합의 인스턴스를 만든다.
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        s.addAll(Arrays.asList(args).subList(1, args.length));

    }
}
```

리플렉션은 위 예시 정도로만 사용해도 충분한 경우가 많다. 하지만, 이 예는 리플랙션의 단점 2가지를 보여준다.

1. 런타임 시 총 6가지 예외를 던질 수 있다.
2. 클래스 이름만으로 인스턴스를 생성하기 위해서는 25줄 코드가 작성되야 한다.
  - ReflectiveOperationException을 잡도록 해 이 코드를 줄일 수 있다.

    ```java
    import java.lang.reflect.Constructor;
    import java.lang.reflect.InvocationTargetException;
    import java.util.Arrays;
    import java.util.Set;
    
    public class ReflectionMain {
        public static void main(String[] args) {
            try {
                // 클래스명 Class 객체로 변환
                Class<? extends Set<String>> cl = (Class<? extends Set<String>>) Class.forName(args[0]);
    
                // 생성자
                Constructor<? extends Set<String>> cons = cl.getDeclaredConstructor();
    
                // 집합의 인스턴스를 만든다.
                Set<String> s = cons.newInstance();
    
                // 인자를 집합에 추가
                s.addAll(Arrays.asList(args).subList(1, args.length));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }
    ```


## 4️⃣. 그래서 하고 싶은 말이 뭐야?

- 리플렉션은 런타임에 존재하지 않을 수도 있는 다른 클래스, 메서드, 필드와의 의존성을 관리할 때 적합하다.
- 컴파일 타임에는 알 수 없는 클래스를 사용하는 프로그램을 작성하면 리플렉션을 사용해야 한다.
- 되도록, 객체 생성에만 사용하고 생성한 객체를 이용할 떄는 적절한 인터페이스나 컴파일타임에 알 수 있는 상위 클래스로 형변환해 사용하자.

---

## 5️⃣. 리플렉션의 사용법

### 1.

```java
@Test
void testGetDeclaredFields(){
    Object person = new Person();
    Field[] fields = person.getClass().getDeclaredFields();
    List<String> actual = getFieldNames(fields);

    assertTrue(Arrays.asList("name", "age").containsAll(actual));
}

private List<String> getFieldNames(Field[] fields) {
    List<String> fieldNames = new ArrayList<>();
    // 필드 정보에서 필드명을 가져온다
    for (Field field : fields) {
        fieldNames.add(field.getName());
    }
    return fieldNames;
}
```

### 2.

```java
package item65;

public interface Eating {

    String eats();
}
```

```java
public abstract class Animal implements Eating {

    private static final String CATEGORY = "mammal";
    private String name;

    protected abstract String getSound();
}
```

```java
public class Cat extends Animal{
    @Override
    protected String getSound() {
        return "ya ong";
    }

    @Override
    public String eats() {
        return "fish";
    }
}
```

```java
@Test
void testGetObjectClassName(){
    Object cat = new Cat();
    Class<?> clazz = cat.getClass();

    assertEquals("Cat", clazz.getSimpleName());
    assertEquals("item65.Cat", clazz.getName());
    assertEquals("item65.Cat", clazz.getCanonicalName());
}
```

- 이 외에도 부모 클래스, 구현 인터페이스, 패키지, 생성자, 메소드 명 등 많은 정보를 알 수 있습니다.

## 6️⃣. 어디서 활용하고 있을까?

- 하나의 인터페이스 DTO → 다수의 구체 클래스를 포함하고 있을 때, 요청에 따른 분기점을 알고 싶을 때
- **전략패턴에서 사용자가 선택한 전략 클래스 이름을 받고 싶을 때**
  - 만약 없다면, 코드에 if문이 덕지덕지..?

## 7️⃣. 참고하면 좋은 영상

[[10분 테커톡] 헙크의 자바 Refelction](https://www.youtube.com/watch?v=RZB7_6sAtC4)

[[10분 테코톡] 파랑, 아키의 리플렉션](https://www.youtube.com/watch?v=67YdHbPZJn4)

## +) 코드가 실행이 안되시는 분을 위해
![스크린샷 2024-01-31 04.51.38.png](..%2F..%2F..%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-01-31%2004.51.38.png)
![스크린샷 2024-01-31 04.54.20.png](..%2F..%2F..%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-01-31%2004.54.20.png)
![스크린샷 2024-01-31 04.55.56.png](..%2F..%2F..%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-01-31%2004.55.56.png)
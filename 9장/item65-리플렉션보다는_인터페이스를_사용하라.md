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

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class RflectionMain {
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
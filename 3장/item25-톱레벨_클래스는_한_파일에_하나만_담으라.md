# 아이템 25 톱레벨 클래스는 한 파일에 하나만 담으라

## 톱레벨 클래스란?
책에는 톱레벨 이라 적혀있어, 이게 전기톱 할때 그 톱인가? 라는 생각을 했습니다.

"톱(top) 레벨 클래스"는 프로그래밍에서 특정 언어나 기술 환경에서 최상위에 위치한 클래스를 가리키는 용어입니다.

일반적으로, 톱 레벨 클래스는 다른 클래스나 모듈의 상위 계층에 있으며, 보통 어플리케이션의 진입점이 될 수 있습니다. 예를 들어, Java에서는 톱 레벨 클래스는 보통 public static void main(String[] args) 메서드를 포함하는 클래스일 것입니다. 

## 예제

```java
// A.class
public class A {
    static final String Comment = "A class내에 A";
}

public class B {
    static final String Comment = "A class내에 B";
}

// B.class
public class A {
    static final String Comment = "B class내에 A";
}

public class B {
    static final String Comment = "B class내에 B";
}
```

위 와 같은 형태의 class를 작성하게 되면 어떤 소스 파일을 먼저 컴파일 하는지에 따라 결과가 달라집니다.

1. 컴파일 오류 발생: Java 컴파일러는 위의 코드를 컴파일하는 동안 "class B is public, should be declared in a file named B.java"와 같은 오류를 발생시킬 것입니다. 이 오류는 한 파일에 여러 개의 톱 레벨 클래스를 포함하면 안된다는 규칙을 위배했기 때문에 발생합니다.

2. 클래스 파일 생성: 각각의 톱 레벨 클래스에 대해 별도의 클래스 파일이 생성되지만, 파일 이름은 컴파일 시에 첫 번째로 나오는 톱 레벨 클래스의 이름으로 결정됩니다. 따라서 두 번째 클래스 파일은 첫 번째 클래스 파일을 덮어쓰게 됩니다.

3. 클래스 로딩 및 사용: 클래스를 로드할 때 클래스로더는 클래스 파일의 이름을 참고하여 로드하게 됩니다. 위의 코드에서는 두 번째 클래스 파일이 첫 번째 클래스 파일을 덮어써서, 실제로는 덮어쓰인 클래스가 로딩되게 됩니다. 이는 의도치 않은 동작을 초래할 수 있으며, 예상치 못한 결과를 가져올 수 있습니다.

## 정적 맴버 클래스를 사용하자

아래는 정적 클래스로 변경한 예제입니다. Java에서 정적 클래스는 특별한 키워드가 없이 클래스 내에 static 키워드가 붙은 정적 멤버를 가지는 클래스입니다. 정적 클래스는 중첩 클래스로서, 특정 인스턴스에 종속되지 않습니다.

```java
// A.java
public class A {
    static final String CommentA = "A class 내에 A";

    private static class B {
        static final String CommentB = "A class 내에 B";
    }
}

// B.java
public class C {
    static final String CommentC = "B class 내에 A";

    private static class D {
        static final String CommentD = "B class 내에 B";
    }
}

```

이렇게 변경하면 각 클래스는 정적 멤버를 포함한 중첩 클래스로 정의되었습니다. 이렇게 하면 컴파일이 정상적으로 이루어지며, 클래스 파일도 각각 생성됩니다. 각 클래스는 독립적으로 사용될 수 있고, 이름 충돌 문제도 발생하지 않습니다.

## PS

요새 나오는 IDE에서는 바로 걸러줘서 발생할 일이 없긴 합니다.
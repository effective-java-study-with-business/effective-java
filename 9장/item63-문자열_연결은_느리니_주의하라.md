# 문자열 연결은 느리니 주의하라

문자열 연결 연산자(+)는 여러 문자열을 하나로 합쳐주는 편리한 수단이다.

하지만 본격적으로 사용하면 성능 저하를 감내하기 어렵다.

**문자열 연결 연산자로 문자열 n개를 잇는 시간은 n2에 비례한다.**
문자열은 불변이라서 두 문자열을 연결할 경우 양쪽의 내용을 모두 복사해야 하므로 성능 저하는 피할 수 없는 결과다.

```java
public String statement(int count, String str) {
    String result = "";
    for (int i = 0; i < count; i++) {
        result = str;
    }
    return result;
}
```

품목이 많을 경우 이 메서드는 심각하게 느려질 수 있다.

## 성능올리기 StringBuilder.
```java
public String statement2() {
	StringBuilder b = new StringBuilder(numItems() * LINEWIDTH);
    for (int i = 0; i < numItems(); i++)
    	b.append(lineForItem(i));
	return b.toString();
}
```

자바6 이후 문자열 연결 성능을 다방면으로 개선했지만, 이 두 메서드의 성능 차이는 여전히 크다.

## 진짜로 느릴까?

```java
public class Main63 {

    public static void main(String[] args) {
        String statement = statement(100000, "이딴게 문자열?");
        System.out.println(statement);
    }

    public static String statement(int count, String str) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += str;
        }
        return result;
    }

    public static String statement2(int count, String str) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < count; i++)
            b.append(str);
        return b.toString();
    }

}
```

위 처럼 예제 코드를 작성했고, statement와 statement2를 자바 8환경에서 사용했을때,

statement = 10.464s

![java8_statement](https://github.com/effective-java-study-with-business/effective-java/assets/75984011/df883aef-8be8-46b0-902f-af1b74c5e6c9)

statement2 = 534ms
![java8_statement2](https://github.com/effective-java-study-with-business/effective-java/assets/75984011/fed3a31f-379b-463a-93b8-950e76b31e44)

**java9 버전부터는 문자열 연결 방식이 달라져 +을 해도 StringBuilder를 사용하는 것과 같은 효과를 낸다고 들었으나, 생각과 다른 결과가 나왔다.**

자바 17로 변경했을때,

statement = 3.945s
![java17_statement](https://github.com/effective-java-study-with-business/effective-java/assets/75984011/05c22eab-28f0-4c36-82ed-8c5786d7aaf7)

statement2 = 263ms
![java17_statement2](https://github.com/effective-java-study-with-business/effective-java/assets/75984011/c25501de-7df5-423e-9fcd-bcbf629b696b)

---

> StringBuilder를 쓰는게 더 최적화 적인거 같다.

---

[JEP 280: Indify String Concatenation](https://openjdk.org/jeps/280)
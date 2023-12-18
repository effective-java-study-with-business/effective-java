#  Item40 @Override 애너테이션을 일관되게 사용하라

## 1. @Override
- 상위 타입의 메서드를 재정의할때 사용합니다.
- 일관되게 사용하면 버그를 예방해 줍니다.

## 2. 사용하지 않았을때의 경우

알파벳 두객 구성된 문자열을 가진 클래스 Set을 생성하는 얘제 
```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram bigram) {
        return bigram.first == this.first &&
                bigram.second == this.second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size()); // 몆개로 출력이 될까?
    }
}
```

알파멧을 10히 순회하며 같은 소문자 2갤 구성된 Bigram을 생성해 Set 을 생성해 줍니다. </br>
중복을 허용하지 않으므로 26개가 생성될것으로 생각되지만, </br>
260이 출력됩니다!

## 3. 원인과 해결책
Object 의 equals() 메서드는 Object 타입 파라메터를 가지는데
```java
public boolean equals(Object obj) {
    return (this == obj);
}
```
재정의를 의미하는 @Override를 사용하지 않아 메서드 오버로딩이 되었고,</br>
같은 값을 가진 값이지만 다른 객체 참조를 하므로 다른 값으로 인식해 걔속 더해졌기 때문입니다.

```java
@Override
public boolean equals(Object bigram) {
    if(!(o instanceof Bigram)) {
        return false;
    }
    Bigram b = (Bigram) bigram;
    return b.first == this.first &&
        b.second == this.second;
}
```

- 만약 기존 잘못된 메서드에 @Override 를 사용하면 컴파일 시점에 오류를 정정해주기 때문에 
습관화 하게 된다면 오류를 줄일 수 있습니다.
- 대부분의 IDE 상에서는 자동으로 붙여줍니다.
- 구체클래스에서 상위 추상메서드를 재정의한 경우 달지 않아도 상관없습니다.
- @Override 어노테이션을 습관화 하면 올바른 시그니처인지 바로 확인이 가능하니 일관성있게 작성하는것이 좋습니다.
- 추상클래스나 인터페이스에서는 재정의하는 모든 메서드에 달아, 새로 추가한 메서드가 없음을 보여줍니다.

## 4. 요약
- @Override 를 달아 메서드 재정의시 오류를 줄일 수 있습니다.
- 항상 달아야 할 필요는 없지만 습관화하여 코드를 명확하게 보이게 합시다.
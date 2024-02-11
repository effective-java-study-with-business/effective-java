# item87-커스텀 직렬화 형태를 고려해보라

# 커스텀 직렬화 형태를 고려하라

- 클래스가 Serializable을 구현하고 기본 직렬화 형태를 사용한다면 다음 릴리스 때 버리려 한 현재의 구현에 발이 묶이게 된다.
- **먼저 고민해보고 괜찮다고 판단될 때만 기본 직렬화 형태를 사용하라.**
    - 기본 직렬화 형태는 유연성, 성능, 정확성 측면에서 신중히 고민한 후 합당할 때만 사용해야 한다.
- **객체의 물리적 표현과 논리적 내용이 같다면 기본 직렬화 형태라도 무방하다.**
    - 예로, 성명을 간략히 표현한 클래스(이름, 성, 중간이름), 3개의 멤버로 구성된 클래스의 경우 기본 직렬화 형태를 사용해도 무방하다.
- 기본 직렬화 형태가 적합하다고 결정했더라도 불변식 보장과 보안을 위해 readObject 메서드를 제공해야 할 때가 많다. (아이템 88, 아이템 90)

# 기본 직렬화 선택에 적합하지 않은 경우

### 기본 직렬화 형태에 적합하지 않은 클래스

```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }
    
    ... // 나머지 코드는 생략
}
```

- 논리적으로 이 클래스는 일련의 문자열을 표현하며, 물리적으로는 문자열들을 이중 연결 리스트로 연결했다.
- 이 클래스에 기본 직렬화 형태를 사용하면 각 노드의 양방향 연결 정보를 포함해 모든 엔트리(Entry)를 철두철미하게 기록한다. (기본 직렬화 형태를 사용하여 각 노드에 연결된 노드들까지 모두 표현된다)
- 이렇듯, 객체의 물리적 표현과 논리적 표현의 차이가 클 때 기본 직렬화 형태를 사용하면 아래의 4가지 문제가 생긴다.
    - 공개 API가 현재의 내부 표현 방식에 영구히 묶인다.
    - 많은 공간을 차지할 수 있다.
    - 시간이 많이 소요될 수 있다.
    - 스택 오버플로를 일으킬 수 있다.

# 합리적인 직렬화 형태

StringList를 위한 합리적인 직렬화 형태는 무엇일까?

### **StringList를 올바르게 직렬화한 예**

```java
public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;

    // 이번에는 직렬화 하지 않는다.
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    // 지정한 문자열을 리스트에 추가한다.
    public final void add(String s) { ... }

    /**
     * StringList 인스턴스를 직렬화한다.
     */
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size);

        // 모든 원소를 순서대로 기록한다.
        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int numElements = stream.readInt();

        for (int i = 0; i < numElements; i++) {
            add((String) stream.readObject());
        }
    }
    ... // 나머지 코드는 생략
}
```

- StringList의 필드 모두가 transient라도 writeObject, readObject는 각각 먼저 defaultWriteObject, defaultReadObject를 호출한다.
- 클래스의 인스턴스 필드 모두가 transient면 defaultWriteObject, defaultReadObject를 호출하지 않아도 된다고 들었을지 모르지만, 직렬화 명세는 이 작업을 무조건 하라고 요구한다.
- 그래야 transient가 아닌 인스턴스 필드가 추가된 다음 릴리스에서도 상호 호환되기 때문이다.
- 구버전 readObject 메서드에서 defaultReadObject를 호출하지 않는다면 역직렬화할 때 StreamCorruptedException이 발생할 것이다.

### **transient**

- 기본 직렬화를 수용하든 하지 않든 defaultWriteObject 메서드를 호출하면 transient로 선언하지 않은 모든 인스턴스 필드가 직렬화 된다.
- 따라서 transient로 선언해도 되는 인스턴스 필드에는 모두 transient 한정자를 붙여야 한다.
- 캐시된 해시 값처럼 다른 필드에서 유도되는 필드도 여기에 해당한다.
- 해당 객체의 논리적 상태와 무관한 필드라고 확신할 때만 transient 한정자를 생략해야한다.
- 그래서 커스텀 직렬화 형태를 사용한다면, 대부분의 (혹은 모든) 인스턴스 필드를 transient로 선언해야한다.
- **기본 직렬화를 사용한다면 transient 필드들은 역직렬화될 때 기본값으로 초기화된다.**
    - 객체 참조 필드는 null, 숫자 타입은 0, boolean 타입은 false
- 만약 기본값을 그대로 사용하면 안된다면, readObject 메서드에서 defaultReadObject를 호출한 다음, 해당 필드를 원하는 값으로 복원해야한다.
- 혹은 그 값을 처음 사용할때 초기화하는 방법도 있다.

### **동기화 메커니즘 직렬화**

- 기본 직렬화 사용 여부와 상관없이 객체의 전체 상태를 읽는 메서드에 적용해야 하는 동기화 메커니즘을 직렬화에도 적용해야 한다.
- 따라서 에컨대 모든 메서드를 synchronized로 선언하여 스레드 안전하게 만든 객체에서 기본 직렬화를 사용하려면 writeObject도 다음 코드처럼 synchronized로 선언해야 한다.

```java
private synchronized void writeObject(ObjectOutputStream stream)
throws IOException {
    stream.defaultWriteObject();
}

```

- writeObject 메서드 안에서 동기화하고 싶다면 클래스의 다른 부분에서 사용하는 락 순서를 똑같이 따라야 한다.
- 그렇지 않으면 자원 순서 교착상태에 빠질 수 있다.

### **직렬화와 UID**

- **어떤 직렬화 형태를 택하든 직렬화 가능 클래스 모두에 직렬 버전 UID를 명시적으로 부여하자**
- 이렇게 하면 직렬 버전 UID가 일으키는 잠재적인 호환성 문제가 사라진다.(아이템 86)
- 직렬 버전 UID를 명시하지 않으면 이 값을 생성하기 때문에, 직렬 버전 UID를 명시하면 성능이 조금이라도 빨라진다.
- 직렬 버전 UID 선언은 각 클래스에 아래 같은 한 줄만 추가해주면 끝이다.

```java
private static final long serialVersionUID = <무작위로 고른long 값>;
```

- 직렬 버전 UID가 없는 기존 클래스를 구버전으로 직렬화된 인스턴스와 호환성을 유지한 채 수정하고 싶다면, 구 버전에서 사용한 자동 생성된 값을 그대로 사용해야한다.
- 이 값은 직렬화된 인스턴스가 존재하는 구버전 클래스를 serialver 유틸리티에 입력으로 주어 실행하면 얻을 수 있다.
- **구버전으로 직렬화된 인스턴스들과의 호환성을 끊으려는 경우를 제외하고는 직렬 버전 UID를 절대 수정하지 말자.**

### **정리**

- 자바의 기본 직렬화 형태는 객체를 직렬화한 결과가 해당 객체의 논리적 표현에 부합할 때만 사용하고, 그렇지 않다면 객체를 적절히 설명하는 커스텀 직렬화 형태를 고려하자
- 직렬화 형태도 공개 메서드를 설계하는 것처럼 시간을 들여 설계해야 한다.
- 한번 공개된 메서드는 향후 릴리스에서 제거할 수 없듯이, 직렬화 형태에 포함된 필드도 마음대로 제거할 수 없다. (직렬화 호환성을 위해 영원히 지원해야함)
- 잘못된 직렬화 형태를 선택하면 해당 클래스의 복잡성과 성능에 영구히 부정적인 영향을 미친다.

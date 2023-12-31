# 1. Effective Java 내용

## 0️⃣. 서론

C, C++ 처럼 메모리를 직접 관리해야 하는 언어를 쓰다가 자바처럼 가비지 컬럭터를 갖춘 언어로 넘어오면서 프로그래머의 삶이 훨씬 편해진다. 하지만, 자칫 메모리 관리에 소홀해져 더 큰 문제가 발생할 수 있다.

이제부터 어떤 부분에서 신경써야 하는지 이야기 해보자.

## 1️⃣. 메모리 누수

**이펙티브 자바**에서 해당 코드에 메모리 누수가 있다고 한다. 어디인지 추측해보자.

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack(){
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e){
        ensureCapacity();
        elements[size++] = e;
    }
    
    public Object pop(){
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    /**
     * 원소를 위한 공간을 적어도 하나 이상 확보한다.
     * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
     */
    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```

바로, **스택에서 꺼내진 객체들을 가비지 컬렉터가 회수하지 않는다**. 해당 배열은 실질적으로 데이터를 넣기만 하고 있지, 실제로 빼고 있는 것은 아니다.

이 스택이 그 객체들의 다 쓴 참조(`obsolete reference`)를 여전히 갖고 있기 때문이다. elemens 배열의 활성 영역(인덱스가 size보다 작은 곳)밖의 참조들을 가르킨다.

가비지 컬렉션 언어에서는 메모리 누수를 찾기 까다롭다. 객체 참조 하나를 살려두면 가비지 컬렉터는 그 객체뿐 아니라 그 객체를 참조하는 모든 객체(그리고 또 그 객체를 참조하는 모든 객체…)를 회수해가지 못한다.

여기서 Stack 자체가 정리되지 못한다. Stack이 갖고 있는 Objec[] elements가 가지고 있는 요소들이 정리되지 못하기 때문이다.

해법은 간단하다. 해당 참조를 다 썼을 때 null처리(참조 해제)를 하면 된다.

```java
public class Stack {
	...

    // 코드 7-2 제대로 구현한 pop 메서드 (37쪽)
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }
}
```

다 쓴 참조를 null 처리하면 다른 이점도 따라오는데, 다른 참조를 사용하려하면 NullPointerException을 발생시킬 것이다.

하지만 **객체 참조를 null 처리 하는 일은 예외적인 경우여야 한다**. 다 쓴 참조를 해제하는 가장 좋은 방법은 그 참조를 담은 변수를 유효 범위(scope) 밖으로 밀어내는 것이다. 변수의 범위를 최소가 되게 정의했으면 자연스럽게 이뤄진다.

```java
public Object pop() {
				...
        Integer age = 28;
        age = null;
}
```

해당 함수는 끝나게 되면 **자동으로 GC의 대상이 되기 때문에 무의미한 null은 하지 말아야 한다는 것**이다.

## 2️⃣. 메모리 직접 관리

Stack 클래스가 문제가 됐던 이유는 **메모리를 직접 관리했기 때문**이다. elements 배열로 저장소 풀을 만들어 원소들을 관리했다. 배열의 활성 영역에 속한 원소들이 사용되고 비활성 영역은 쓰이지 않는다. 가비지 컬렉터는 이걸 알 수 없다. 가비지 컬렉터 입장에서는 비활성 영역에서 참조하는 객체도 똑같이 유효하다. 그래서 프로그래머는 비활성 영역이 되는 순간 null 처리해서 해당 객체를 더는 쓰지 않을 것임을 알려야 한다.

**자기 메모리를 직접 관리하는 클래스라면 프로그래머는 메모리 누수에 주의해야 한다.**

## 3️⃣. 캐시

캐시 역시 메모리 누수를 일으키는 주범이다. 객체 참조를 캐시에 넣고, 객체를 다 쓴 뒤에도 한참을 놔두는 일이 자주 있다.

```java
Object key1 = new Object();
Object value1 = new Object();

Map<Object, List> cache = new HashMap<>();
cache.put(key1, value1);
```

일단 기본적으로 Object는 Strong Reference라는 걸 알아야 한다. **따라서, key1이 실질적으로 null이 되지 않는 이상 혹은 스코프 범위를 벗어나지 않는 이상 여전히 HashMap은 key1에 대한 Reference를 가지고 있고, 이것은 gc의 대상이 되지 않는다는 것이다.**

### 해법

```java
Object key1 = new Object();
Object value1 = new Object();

Map<Object, List> cache = new WeakHashMap<>();
cache.put(key1, value1);
```

이 키를 Weak라는 레퍼런스로 감싸서 들어간다. 하드 레퍼런스(new로 생성한 일반적인 방법)가 없어지면 정리된다.

- 캐시를 만들 때 보통은 캐시 엔트리의 유효 기간을 정확히 정의하기 어렵다. 그래서 시간이 지날 수록 엔트리의 가치를 떨어뜨리는 방식을 흔히 사용한다. 이런 방식에서는 쓰지 않는 엔트리를 이따금 청소 해줘야 한다.(ScheduledThreadPoolExecutor)

# 2. 개인적인 내용

```java
@Builder(access = AccessLevel.PRIVATE)
@Value(staticConstructor = "of")
@Getter
public class Person {

    String name;

    public static Person of(String name){
        return Person.builder()
                     .name(name)
                     .build();
    }

    public enum Kinds {
        MAN, WOMAN
    }

    public class OuterClass {
        private int temp;

        public void printPerson() {
            System.out.println(name);
        }
    }
}
```

- 위 클래스를 보시고, 어떤 문제점이 있는지 확인해보세요 :D

# 3. 그 외 디테일한 내용

## 1️⃣. 실제 Stack 코드

```java
public class Stack<E> extends Vector<E> {
		...
		public synchronized E pop() {
		        E obj;
		        int len = size();
		        obj = peek();
		        removeElementAt(len - 1);
		        return obj;
		    }
		...
}
```

여기서 `removeElementAt` 함수를 보게 되면,

```java
public synchronized void removeElementAt(int index) {
        if (index >= elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " +
                                                     elementCount);
        }
        else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int j = elementCount - index - 1;
        if (j > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, j);
        }
        modCount++;
        elementCount--;
        **elementData[elementCount] = null**; /* to let gc do its work */
    }
```

`elementData[elementCount] = null`

즉, java.utils에 있는 Stack은 GC의 대상이 되도록 해준다.
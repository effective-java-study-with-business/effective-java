# 아이템29.이왕이면 제네릭 타입으로 만들라

어떤 클래스들을 만들다 보면 클래스 안에 다른 객체들을 담는 역할을 하는 클래스들을 만드는 경우가 있다. (예를 들면 스택)

다른 것들을 담는 클래스들은 제네릭 타입으로 만드는 것이 유용하다. 특히 Object 타입을 담고있을 때, 명확하게 제네릭을 사용해서 타입을 유도하면 런타임에 ClassTypeException들이 발생하는 것을 줄일 수 있다.

```java
// Object를 이용한 제네릭 스택
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

		// 꺼낼 때도 Object 타입
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

    // Object를 사용하면, 사용할 시 형변환을 해줘야 함.
    public static void main(String[] args) {
        Stack stack = new Stack();
        for (String arg : List.of("a", "b", "c"))
            stack.push(arg);
        while (!stack.isEmpty())
            System.out.println(((String)stack.pop()).toUpperCase());
    }
}
```

### 제네릭 타입을 활용해서 스택을 구현해보기 1

```java
// E[]를 이용한 제네릭 스택
public class Stack<E> {
    private E[] elements; // Object 대신 제네릭 타입의 배열을 선언
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    // 배열을 사용한 코드를 제네릭으로 만드는 방법 1
    // 배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
    // 따라서 타입 안전성을 보장하지만,
    // 이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
    @SuppressWarnings("unchecked")
    public Stack() {
				// 배열을 만들 때 형변환을 한번만 하면 된다.
				// 대신 힙 오염이 발생할 수 있음.
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0)
            throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

}
```

### 제네릭 타입을 활용해서 스택을 구현해보기 2

```java
// Object[]를 이용한 제네릭 Stack
public class Stack<E> {
		// E[] 타입이 아니라 Object 배열로 만듦
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    
    public Stack() {
				// Object 배열을 그대로 쓰다보니 힙 오염의 여지가 없다.
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    // 배열을 사용한 코드를 제네릭으로 만드는 방법 2
    // 비검사 경고를 적절히 숨긴다.
    public E pop() {
        if (size == 0)
            throw new EmptyStackException();

        // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
				// 단점 : 무언가를 꺼낼 때마다 형변환을 해줘야함.
        @SuppressWarnings("unchecked") E result = (E) elements[--size];

        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

}
```

포인트

- 배열을 사용하는 코드를 제네릭으로 만들 때 해결책 두가지
1. 제네릭 배열(E[]) 대신 Object 배열을 생성한 뒤에 제네릭 배열로 형변환 한다.
    1. 형변환을 배열 생성시 한번만 한다.
    2. 가독성이 좋다.
    3. 힙 오염이 발생할 수 있다.
2. 제네릭 배열 대신 Object 배열을 사용하고, 배열이 반환한 원소를 E로 형변환 한다.
    1. 원소를 읽을 때마다 형변환을 해줘야 한다.

- 대부분 1번 방법을 많이 채택. (배열 생성시 힙 오염이 발생하지 않도록 주의해서)

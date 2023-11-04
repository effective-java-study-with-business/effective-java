# Item18 상속보다는 컴포지션을 사용하라

## 1. 상속이란?
- **is a**관계. A cat is an animal. 
- 상속 관계의 클래스들은 강하게 결합합니다.
### 예시
```java
abstract class Abs {
	abstract void foo();
}

public class ClassA extends Abs{

	public void foo(){	
	}
	
}

class ClassB extends Abs{
		
	public void foo(){
	}
	
}

class Test {
	
	ClassA a = new ClassA();
	ClassB b = new ClassB();

	public void test(){
		a.foo();
		b.foo();
	}
}
```

## 2. 컴포지션 이란?
- **has a, part of** 관계. A House has a room.
- 합성 관계를 통해 객체를 참조하여 데코레이터 패턴을 만듭니다.
- 기존 클래스가 새로운 클래스의 구성요소로 쓰입니다.
### 예시
```java
class Test extends Abs{
	private Abs obj = null;
	
	Test(Abs o){
		this.obj = o;
	}
	
	public void foo(){
		this.obj.foo();
	}
}

public class Client {
    public static void main(String[] args) {
        Abs a = new Test(new ClassA());
        a.foo();
        Abs b = new Test(new ClassB());
        b.foo();
    }
}
```
추상클래스 또는 인터페이스를 private 필드로 두고 나머지 인터페이스의 메서드를 구현해 
동일한 상위 클래스만을 통하는 일관된 방식을 만들 수 있습니다.

## 3. 언제 상속과 컴포지션을 사용할까?
- 상속은 반드시 하위 클래스가 상위 클래스의 진짜 하위타입인 상황에서 쓰여야합니다.
- 클래스 A를 상속하는 클래스 B를 만들때 "B가 A 인가?" 확신해야 합니다.
- 그렇지 않으면 참조 관계를 활용한 컴포지션 구성을 권장합니다.

## 4. 래퍼클래스와 전달클래스 활용하기
- 기존 클래스를 확장하는 대신 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 합니다.
- 원하는 기능을 덧씌우는 전달 클래드들을 쉽게 구현할 수 있습니다.

### 예시
```java
// Set 인스턴스를 감싸고 있는 래퍼 클래스
public class InstrumentedHashSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}

// 재사용 할 수 있는 전달 클래스
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return s.isEmpty();
    }

    public boolean contains(Object o) {
        return s.contains(o);
    }

    public Iterator<E> iterator() {
        return s.iterator();
    }
    
    // 중략...

    @Override
    public boolean equals(Object o) {
        return s.equals(o);
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    @Override
    public String toString() {
        return s.toString();
    }
}
```
인터페이스당 하나씩 ForwardingSet 같은 전달클래스를 만들고 이것을 구현해서 기능을 덧씌울 수 있습니다.

```java
Set<E> set = new InstrumentedSet<>(new HashSet<>(INIT_CAPACITY));
```
위의 구조로 Set 구현체라면 InstrumentedSet을 이용해 모두 계측이 가능합니다.

## 5. 요약
- 상속은 강력하게 결합하여 캡슐화를 해침니다.
- 상속은 상위 클래스와 하위 클래스가 **is-a** 관계일 때만 사용 권장합니다.
- 전달 클래스를 활용한 컴포지션 구성을 사용해 결합도를 낮출 수 있습니다.
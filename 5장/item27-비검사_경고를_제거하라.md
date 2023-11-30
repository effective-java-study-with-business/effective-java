#아이템27 - 비검사 경고를 제거하라.


## 1. 제네릭과 관련된 컴파일러 경고 수정하기
- 비검사 형변환 경고, 비검사 메서드 호출 경고, 비검사 매개변수화 가변인수 타입 경고, 비검사 변환 경고 등이 있습니다.
- 비겸사 경고는 `-Xlint:unchecked` 옵션을 추가해 컴파일시 컴파일러가 알려주는데, IDE 상에서 친절하게 미리 경고해주므로, 발견시 맞춰 수정해주면 됩니다.
- Raw 타입 사용하지 말고, 다이아몬드 연산자 `<>`를 붙여주어 타입을 추론하게 하여 경고를 없앱니다.
- 가능한 모든 비검사 경고를 무시하지 말고 수정해줍니다.
- 프로젝트 내 build.gradle 설정에 다음을 추가하면 경고 문구를 볼 수 있습니다.
```java
  tasks.withType(JavaCompile) {
  options.compilerArgs << "-Xlint:unchecked"
  }
 ```

<img width="1100" alt="image" src="https://github.com/effective-java-study-with-business/effective-java/assets/45473375/1537225c-2632-44fa-bf64-48dfc3c11c96">


## 2. 타입 안전하다고 확신할 수 있고, 경고를 무시할 수 있는 경우
- @SuppressWarnings("unchecked") 애너테이션을 달아서 경고를 숨길수 있습니다. 
- 개별 지역변수 선언부터 클래스 전체까지 범위를 지정해 달아줄 수 있습니다. 
- 최대한 좁은 범위에 설정하여 예기치못한 경고까지 Suppress 하지 않도록 합니다. 

## 3. 비검사 경고 숨기는 범위 제한하기

실제 ArrayList의 toArray 메서드에서 메서드 단위의 애너테이션이 달려있습니다.
```java
@SuppressWarnings("unchecked")
public <T> T[] toArray(T[] a) {
    if (a.length < size)
    // Make a new array of a's runtime type, but my contents:
        return (T[]) Arrays.copyOf(elementData, size, a.getClass());
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

다음과 같이 Arrays.copyOf 부분으로만 비검사 경고를 숨기는 범위를 최소로 좁힐 수 있습니다.
```java
public <T> T[] toArray(T[] a) {
	if (a.length < size){
        // 생성한 배열과 매개변수로 받은 배열의 타입이 모두 T[]로 같다.
        @SuppresWarnings("unchecked") T[]result=
            (T[])Arrays.copyOf(elements,size,a.getClass());
        return result;
    }
	System.arraycopy(elements, 0, a, 0, size);
	if (a.length > size)
		a[size] = null;
	return a;
}
```

## 4. @SuppresWarnings("unchecked") 사용시 주석달기
- 경고를 무시해도 안전한 이유를 꼭 주석으로 남겨줍니다.
- 혹여 타인이 코드를 잘못 수정해 타입 안전성을 잃는 상황을 예방합니다.



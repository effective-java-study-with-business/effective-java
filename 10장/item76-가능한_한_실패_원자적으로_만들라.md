# item 76 가능한 한 실패 원자적으로 만들라

데이터를 다룰때와 같이, 메서드에도 원자성(atomicity)이 지켜져야 한다는 내용을 다루고 있다.

> 호출된 메서드는가 실패하더라도 해당 객체는 메서드 호출 전 상태로 유지해야 한다.

책에서는 4가지 방법을 제시하고 있다.

## 1. 불변 객체로 설계

불변 객체는 태생적으로 실패 원자적이다. 

메서드가 실패하면 새로운 객체가 만들어지지 않을 수 있으나, 기존 객체가 불안정한 상태에 빠질 일은 결코 없다.

불변객체의 상태는 생성 시점에 고정되어 절대 변하지 않기 때문이다.

## 2. 선행작업으로 유효성 검사를 진행.

가장 흔한 방법으로 사용하는 방법.

객체 내부를 변경하기전엔 잠재적 예외 가능성을 대부분 걸러내는 방법.

```java
public Object pop() {
    if(size == 0){
        throw new EmptyStackException();
    }
    Object result = elements[--size];
    elements[size] = null;
    return result;
}
```

사실 if 문이 없더라도 스택이 비었다면 예외를 던진다.

다만 size 의 값이 음수가 되어 다음 번 호출도 실패하게 만들며, 이때 던지는 ArrayIndexOutofBoundsException은 추상화 수준이 상황에 어울리지 않다고 볼수 있다.

이와 비슷한 취지로 실패할 가능성이 있는 모든 코드를, 객체의 상태를 바꾸는 코드보다 앞에 배치하는 방법도 있다.

## 3. 객체의 임시 복사본에서 작업을 수행하고, 성공하면 기존 객체와 교체하기

데이터를 임시 자료구조에 저장해 작업하는게 더 빠를 때 적용하기 좋은 방식

예를 들어 정렬 메서드에서 정렬을 수행하기 전에 입력 리스트의 원소들을 배열로 옮겨 담는다. 배열을 사용하면 정렬 알고리즘의 반복문에서 원소들에 훨씬 빠르게 접근할 수 있기 때문이다.

성능을 높여 줄 뿐만 아니라 , 혹시나 정렬에 실패하더라도 입력 리스트는 변하지 않는 효과를 얻을 수 있다.

```java
public class SortExample {
    public static void main(String[] args) {
        List<Integer> originalList = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5));
        System.out.println("Original list: " + originalList);

        try {
            // 임시 복사본에서 작업
            List<Integer> sortedList = sortList(new ArrayList<>(originalList)); // 임시 리스트로 정렬 시도
            originalList.clear(); // 성공 시, 원본 리스트를 비우고
            originalList.addAll(sortedList); // 정렬된 내용으로 채움
        } catch (Exception e) {
            // 정렬 실패 시, 원본 리스트 유지
        }
    }

    private static List<Integer> sortList(List<Integer> tempList) throws Exception {
        Integer[] tempArray = tempList.toArray(new Integer[0]);
        Arrays.sort(tempArray); // 배열에서 정렬 수행
        return Arrays.asList(tempArray); // 정렬된 배열을 리스트로 변환
    }
}

```

## 4. 작업 도중 발생하는 실패를 가로채는 복구 코드를 작성하여 작업 전 상태로 되돌리는 방법

주로 디스크 기반의 내구성을 보장해야 하는 자료구조에 쓰이지만 자주 쓰이지는 않는다.

---

실패 원자성은 일반적으로 권장되는 덕목이지만 항상 달성할 수 없는 것은 아니다. 

예를 들어 두 스레드가 동기화 없이 같은 객체를 동시에 수정한다면 그 객체의 일관성이 깨질 수 있다.

실패 원자적으로 만들 수 있더라도 항상 그리 해야 하는것도 아니다. 실패 원자성을 달성하기 위한 비용이나 복잡도가 아주 큰 연산도 있기 때문이다.

또한 메서드 명세에 기술한 예외라면 혹시나 예외가 발생하더라도 객체의 상태는 메서드 호출 전과 똑같이 유지돼야 한다는 것이 기본규칙이다. 이를 지키지 못한다면 API 설명에 명시해야 하지만 잘 지켜지지 않고 있다. 

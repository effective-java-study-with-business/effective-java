# [Item 19] 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라
## 1. 상속을 고려한 설계?
1. 메소드 재정의 시에 일어나는 일을 정확히 정리하여 문서화
    - 상속을 위한 클래스는 재정의 가능한 메소드들을 내부적으로 어떻게 이용하는지 문서로 남겨야 함
    - 공개된 메소드에서 클래스 자신의 메소드를 호출할 수 있기 때문에 유의해야 함
2. 코드 내에서 @implSpec 태그(Implementation Requirements)를 달면 자바독에서 메소드의 내부 동작 방식을 설명할 수 있도록 문서 생성
    - API가 '어떻게' 작동하는지 작성하는 것은 좋은 API 문서의 작성 방법은 아니지만, **상속이 캡슐화를 위배**하므로, 안전한 상속을 위해서라면 어쩔 수 없다.
    - IDE 내에서 보고싶다면 -tag "implSpec:a:Implementation Requirements:"를 명령줄 매개변수로 지정하면 된다.
        - IntelliJ의 경우 우측 상단의 실행 아이콘 좌측의 콤보박스 > Edit Configurations > Build and run의 텍스트 박스에 해당 매개변수를 추가하면 된다.<sup>[1]</sup>
   ```
   @implSpec 예시
   - List.java의 sort(Comparator<? super E> c)
   Implementation Requirements : The default implementation obtains an array containing all elements in this list, sorts the array,
                                 and iterates over this list resetting each element from the corresponding position in the array.
                                 (This avoids the n^2 log(n) performance that would result from attempting to sort a linked list in place.)
   구현 요구 사항: 기본 구현은 이 목록의 모든 요소를 ​​포함하는 배열을 얻고, 배열을 정렬하고,
                 이 목록을 반복하여 배열의 해당 위치에서 각 요소를 재설정합니다.
                 (이렇게 하면 연결된 목록을 제자리에 정렬하려고 시도할 때 발생하는 n^2 log(n) 성능이 방지됩니다.)
   ```

## 2. 어떤 메소드를 protected로 만들 것인가?
```
실제 하위 클래스를 만들어 테스트 해보는 것이 유일함.
```
- Effective Java 저자의 **경험** 상, 3개의 하위 클래스 정도를 만들어 테스트 해보면 결과가 나온다.
- 상속용 클래스는 배포 전에 반드시 하위 클래스를 만들어 검증해야 한다!

## 3. 상속용 클래스를 만들 때의 주의 사항
1. 상속용 클래스의 생성자는 어떤 식으로든 재정의가 가능한 메소드를 호출하면 안됨
    - 상위 클래스의 생성자가 
3. ㅇ
4. 
## References
[1] https://codedragon.tistory.com/7943

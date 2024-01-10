# Effective Java Study
- Effective Java 책을 읽고, 내용을 기억하기 쉽게 정리하며, 각자 예시 코드를 작성하여 실무에 도입해보기 위한 스터디입니다.

## Study 시간
- 매주 수요일, 21:00

## 발표 자료 제출일
- 매주 토요일 23:59분까지

## Ground Rule
### 1. 수요일까지 각자 발표를 맡은 부분(1 아이템)의 예시 코드와 함께 md 파일로 정리하여 각자의 branch에서 commit 및 PR을 올린다.
      -> 예시 코드는 1~2개 정도 코드 블록으로 감싸고, 타인이 읽었을 때 이해하기 쉽도록 주석을 달아 올리도록 한다.
      -> 각자 사용하는 branch는 각자의 github id로 만들어 사용하도록 한다.
      -> PR의 경우 주어진 제목 및 템플릿을 이용하도록 한다.
        (PR 템플릿 링크 : TBD)
### 2. 발표 전까지 다른 사람이 준비한 발표 자료 및 Effective Java 책을 읽어보고, issue에 질문 사항을 작성한다.
      -> 이 때, 이미 올린 PR을 issue에 태그한다.
      -> issue도 마찬가지로 주어진 제목 양식 및 템플릿을 이용하도록 한다.
        (issue 템플릿 링크 : TBD)<br>
### 3. 등록된 issue는 issue에 해당하는 아이템을 준비한 사람 또는 공부한 다른 사람이 답변을 남긴다.
      -> 해당 아이템 발표자는 필수로 답변을 남기도록 한다.
### 4. 발표 날에 각자 작성한 md 파일 및 코드를 공유하도록 한다.

## Schedules
### 2장 객체 생성과 파괴
- [X] Item 01. 생성자 대신 정적 팩터리 메서드를 고려하라 - @hbae
- [X] Item 02. 생성자에 매개변수가 많다면 빌더를 고려하라 - @laughcryrepeat
- [X] Item 03. private 생성자나 열거 타입으로 싱글턴임을 보증하라 - @minahshin
- [X] Item 04. 인스턴스화를 막으려거든 private 생성자를 사용하라 - @dudtjr0831
- [X] Item 05. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라 - @132262B
- [X] Item 06. 불필요한 객체 생성을 피하리 - @JJong0416
- [X] Item 07. 다 쓴 객체 참조를 해제하라 - @dudtjr0831
- [X] Item 09. try-finally보다는 try-with-resources를 사용하라 - @laughcryrepeat

### 3장 모든 객체의 공통 메서드
- [X] Item 10. equals는 일반 규약을 지켜 재정의하라 - @minahshin
- [X] Item 11. equals를 재정의하려거든 hashCode도 재정의하라 - @hbae
- [X] Item 12. toString을 항상 재정의하라 - @JJong0416
- [X] Item 14. Comparable을 구현할지 고려하라 - @132262B

### 4장 클래스와 인터페이스
- [X] Item 15. 클래스와 멤버의 접근 권한을 최소화하라 - @dudtjr0831
- [X] Item 16. public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라 - @hbae
- [X] Item 17. 변경 가능성을 최소화하라 - @JJong0416
- [X] Item 18. 상속보다는 컴포지션을 사용하라 - @laughcryrepeat
- [X] Item 19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라 - @minahshin
- [X] Item 20. 추상 클래스보다는 인터페이스를 우선하라 - @hbae
- [X] Item 21. 인터페이스는 구현하는 쪽을 생각해 설계하라 - @laughcryrepeat
- [X] Item 22. 인터페이스는 타입을 정의하는 용도로만 사용하라 - @JJong0416
- [X] Item 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라 - @dudtjr0831
- [X] Item 24. 멤버 클래스는 되도록 static으로 만들라 - @minahshin
- [X] Item 25. 톱레벨 클래스는 한 파일에 하나만 담으라 - @132262B

### 5장 제네릭
- [X] Item 26. 로 타입은 사용하지 말라 - @132262B
- [X] Item 28. 배열보다는 리스트를 사용하라 - @JJong0416
- [X] Item 29. 이왕이면 제네릭 타입으로 만들라 - @hbae
- [X] Item 30. 이왕이면 제네릭 메서드로 만들라 - @minahshin
- [X] Item 31. 한정적 와일드카드를 사용해 API 유연성을 높이라 - @dudtjr0831
- [X] Item 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라 - @JJong0416
- [X] Item 33. 타입 안전 이종 컨테이너를 고려하라 - @dudtjr0831

### 6장 열거 타입과 애너테이션
- [X] Item 34. int 상수 대신 열거 타입을 사용하라 - @hbae
- [X] Item 35. ordinal 메서드 대신 인스턴스 필드를 사용하라 - @132262B
- [X] Item 36. 비트 필드 대신 EnumSet을 사용하라 - @minahshin
- [X] Item 37. ordinal 인덱싱 대신 EnumMap을 사용하라 - @laughcryrepeat
- [X] Item 39. 명명 패턴보다 애너테이션을 사용하라 - @JJong0416
- [X] Item 40. @Override 애너테이션을 일관되게 사용하라 - @laughcryrepeat

### 7장 람다와 스트림
- [X] Item 42. 익명 클래스보다는 람다는 사용하라 - @132262B
- [X] Item 43. 람다보다는 메서드 참조를 사용하라 - @dudtjr0831
- [ ] Item 44. 표준 함수형 인터페이스를 사용하라 - @minahshin
- [ ] Item 45. 스트림은 주의해서 사용하라 - @minahshin
- [X] Item 46. 스트림에서는 부작용 없는 함수를 사용하라 - @laughcryrepeat
- [ ] Item 47. 반환 타입으로는 스트림보다 컬렉션이 낫다 - @132262B
- [X] Item 48. 스트림 병렬화는 주의해서 적용하라 - @dudtjr0831

### 8장 메서드
- [X] Item 49. 매개변수가 유효한지 검사하라 - @hbae
- [X] Item 50. 적시에 방어적 복사본을 만들라 - @JJong0416
- [X] Item 51. 메서드 시그니처를 신중히 설계하라 - @minahshin
- [ ] Item 52. 다중정의는 신중히 사용하라 - @hbae
- [ ] Item 53. 가변인수는 신중히 사용하라 - @JJong0416
- [ ] Item 54. null이 아닌, 빈 컬렉션이나 배열을 반환하라 - @laughcryrepeat
- [X] Item 55. 옵셔널 반환은 신중히 하라 - @132262B

### 9장 일반적인 프로그래밍 원칙
- [ ] Item 57. 지역변수의 범위를 최소화하라 - @dudtjr0831
- [ ] Item 58. 전통적인 for 문보다는 for-each 문을 사용하라 - @minahshin
- [ ] Item 59. 라이브러리를 익히고 사용하라 - @JJong0416
- [ ] Item 60. 정확한 답이 필요하다면 float와 double은 피하라 - @laughcryrepeat
- [ ] Item 61. 박싱된 기본 타입보다는 기본 타입을 사용하라 - @dudtjr0831
- [ ] Item 62. 다른 타입이 적절하다면 문자열 사용을 피하라 - @hbae
- [ ] Item 63. 문자열 연결은 느리니 주의하라 - @132262B

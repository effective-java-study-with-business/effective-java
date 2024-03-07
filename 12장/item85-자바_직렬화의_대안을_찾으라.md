# [Item 85] 자바 직렬화의 대안을 찾으라
## 1. 자바 직렬화의 위험성
- 직렬화란?<sup>[1]</sup>
  - Object -> Stream of Bytes 형태의 연속적인 데이터로 인코딩 (<-> 역직렬화)
  - Stream of Bytes(바이트 스트림)으로 인코딩 한 데이터를 DB의 데이터나 파일로 떨굴 수 있음
- Stream of Bytes(바이트 스트림)이란?
  - 스트림 : 출발지 ~ 목적지까지 데이터가 흐르는 **통로**
    - 자바에서는 파일의 입출력을 직접 다루지 않고 Stream이라는 이상적인 흐름, 중간 매개체를 통해 다루고 있음<sup>[2]</sup>
  - 자바는 1 byte를 스트림의 기본 단위로 책정 -> 따라서 **바이트 스트림**이 된다!

### 자바 직렬화란?
```text
- 직렬화
java.io.Serializable을 implements하여 자바 내에서 바이트 스트림으로 인코딩을 진행한다.
```
```text
- 역직렬화
바이트 스트림으로 인코딩 된 것을 객체로 읽기 위해서는 java.io.ObjectInputStream의 readObject를 통해 읽는다.
```
- 여기서 문제는, 역직렬화 시 readObject 메소드는 자바의 **reflection API를 통해 객체를 만들어냄**
- reflection API를 사용하면, 접근 제한자에 상관 없이 모든 생성자, 멤버 변수, 메소드에 접근이 가능
- 결국, 공격자가 역직렬화 하는 과정에서 해당 객체의 모든 코드, 자바 표준 라이브러리, 서드파티 라이브러리 등을 멋대로 수행 할 수 있는 리스크가 존재
- 따라서, **신뢰할 수 없는 스트림을 역직렬화 하면, 그 과정에서 공격의 빌미를 차고 넘치게 줄 수 있음**
  - 원격 코드 실행(Remote Code Execution), 서비스 거부(DoS) 등의 공격을 일으킬 수 있음

## 2. Gadget
- 역직렬화 과정 시 호출되어 위험한 동작을 수행하는 메서드
- 여러 gadget을 함께 사용하여 gadget chain이 구성되기도 함
- 공격자가 하드웨어의 네이티브 코드를 멋대로 실행할 수 있는 gadget chain이 있기도 함
- 신중하게 제작한 바이트 스트림만 역직렬화 필요
- **역직렬화 폭탄** : 역직렬화에 시간이 오래 걸리는 짧은 스트림을 역직렬화 하는 것만으로도 DoS에 노출 가능
  - ex) 다수의 HashSet 인스턴스를 역직렬화 하는 행위 : 해당 원소의 HashCode를 계산해야 하므로 영원히 역직렬화를 해야함

## 3. Cross-Platform Structured-Data Representation : 자바 직렬화의 대안
- 직렬화 위험을 피하는 best는 바로.. 아무것도 역직렬화 하지 않는 것!
- 그리고 굳이 자바 직렬화를 쓸 필요가 없음
- 훌륭한 대안인 크로스-플랫폼 구조화 된 데이터 표현(...)이 있기 때문임

### 크로스-플랫폼 구조화 된 데이터 표현? 그게 뭔데 오덕아
1. 자바 직렬화보다 훨씬 간단 : 임의 객체 그래프를 자동으로 직렬화/역직렬화 하지 않음
2. attribute - value의 집합으로 구성되는 간단하고 구조화 된 데이터 객체 사용
3. 기본 타입 몇 개, 배열 타입만 지원함
4. 예시로는 JSON과 protobuf(by Google)이 있음

### JSON?
```json
{
  "key" : "value",
  "array": ["v1", "v2"],
  "object": {
    "object_key": 1
  }
}
```
- 브라우저 - 서버 통신용으로 만든 텍스트 기반의 데이터
- 데이터를 "표현"하기 위해 사용 : 텍스트 기반 표현에는 JSON이 효율적

### Protobuf? <sup>[3]</sup>
```
message Person {
    required string name = 1;
    required int32 id = 2;
    optional string email = 3;

    enum PhoneType {
        MOBILE = 0;
        HOME = 1;
        WORK = 2;
    }

    message PhoneNumber {
        required string number = 1;
        optional PhoneType type = 2 [default = HOME];
    }

    repeated PhoneNumber phone = 4;
}
```
- 서버 간 데이터를 교환하기 위한 바이너리 기반의 데이터
- 바이너리 기반이라 효율이 좋음
- 문서를 위한 schema를 올바르게 쓰도록 강요함 : 타입 명시 필수!
- 바이너리 뿐만 아니라 텍스트 표현도 지원하긴 함

## 4. 자바 직렬화를 어떻게든 피할 수 없다면?
- 역직렬화 한 데이터가 안전한지 확신할 수 없으면 java.io.ObjectInputFilter를 사용
  - 데이터 스트림이 역직렬화 전에 필터를 설정
- 블랙리스트 방식(기본 수용) : 잠재적으로 위험한 클래스를 거부
- 화이트리스트 방식(기본 거부) : 안전하다고 알려진 클래스만 수용
  - 화이트리스트 방식을 추천함
- 그럼에도 불구하고 직렬화 폭탄은 걸러낼 수 없으므로, JSON이라던지 protobuf라던지 등의 크..로스-플랫폼 구조화 된 데이터 표현을 사용하자

## References
[1] https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%A7%81%EB%A0%AC%ED%99%94Serializable-%EC%99%84%EB%B2%BD-%EB%A7%88%EC%8A%A4%ED%84%B0%ED%95%98%EA%B8%B0
[2] https://tcpschool.com/java/java_io_stream
[3] https://blog.naver.com/oidoman/220773055827
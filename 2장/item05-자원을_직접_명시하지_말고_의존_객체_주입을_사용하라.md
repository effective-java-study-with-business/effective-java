# 아이템 5 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

## 의존 객체 주입이 무엇인가?

의존 객체 주입은 객체 간의 의존 관계를 외부에서 주입하여 코드의 유연성과 재사용성을 높이고 테스트 용이성을 향상하는데 도움을 줍니다.

## 코드 예제로 의존 객체 주입 이해하기

예를 들어, 메시지 전송 서비스를 구현한다고 가정해봅시다.

최초 버전의 코드는 다음과 같이 클래스 MessageSender 또는 MessageUtils를 만들어 사용합니다.

```java
class MessageSender {
    private MessageSender() {
    }

    public final static MessageSender instance = new MessageSender();

    public static MessageSender getInstance() {
        return instance;
    }

    public final void sendMessage() {
        // 대충 web push를 보내는 코드...
    }
}

class MessageUtils {

    private MessageUtils() {
    }

    public static void sendMessage() {
        // 대충 web push를 보내는 코드...
    }
}
```

이 코드에서는 MessageSender와 MessageUtils라는 두 개의 클래스가 하나의 역할을 하는 메시지 전송 기능을 구현합니다.
이후 추가 요구사항이 발생하여, 앱 푸시 메시지를 전송해야 할 필요가 생겼다고 가정해봅시다.

이런 상황에서 아래와 같이 의존 객체 주입을 사용하면 다음과 같이 코드를 개선할 수 있습니다.

```java
interface MessageSender {
    void sendMessage(String message);
}

class WebPushMessageSender implements MessageSender {
    public void sendMessage(String message) {
// 구체적인 웹 푸시 메시지 전송 로직
    }
}

class AppPushMessageSender implements MessageSender {
    public void sendMessage(String message) {
// 구체적인 앱 푸시 메시지 전송 로직
    }
}

class MessageService {
    private final MessageSender messageSender;

    public MessageService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendPush(String message) {
        messageSender.sendMessage(message);
    }
}

public class Item5 {

  public static void main(String[] args) {
    MessageService message = new MessageService(new WebPushMessageSender());
    message.sendPush("1+1=2");
  }
}
```

인스턴스를 생성할 때 생성자에 필요한 자원을 주입함으로써 코드를 더 유연하게 만들었습니다.

MessageService 클래스는 인터페이스 MessageSender를 통해 의존하며,
WebPushMessageSender 또는 AppPushMessageSender를 주입함으로써 쉽게 메시지 전송 동작을 변경할 수 있습니다.

의존 객체 주입은 소프트웨어 개발에서 유연성과 테스트 용이성을 높이는 핵심적인 기법 중 하나입니다. 코드의 유연성과 재사용성을 높이는 방법 중 하나로, 다음과 같은 장점을 갖고 있습니다.

- 새로운 요구사항 대처: 기존 코드에 새로운 요구사항이 추가될 때, 의존 객체 주입을 사용하면 새로운 구현 클래스를 만들어 주입함으로써 기존 코드를 변경하지 않고도 새로운 동작을 추가할 수 있습니다.

- 테스트 용이성: 단위 테스트를 수행하기 쉽게 만듭니다. 특히 모의 객체(Mock)를 주입하여 특정 동작을 테스트할 수 있으므로 코드의 품질을 향상시킬 수 있습니다.

- 단일 책임 원칙(Single Responsibility Principle, SRP) 적용: 의존 객체 주입은 인터페이스와 구현 클래스를 분리하여 단일 책임 원칙을 따르는 설계를 강조합니다. 이렇게 하면 클래스가
  한가지 역할만 수행하도록 보장하고, 유지보수가 더 쉬워집니다.

## Spring에서는 어떻게 의존성 주입을 하는가?

[[Spring] 의존성 주입의 정의 및 의존성 주입 3가지 방식 (생성자 주입, 수정자 주입, 필드 주입)](https://jindory.tistory.com/entry/Spring-%EC%9D%98%EC%A1%B4%EC%84%B1-%EC%A3%BC%EC%9E%85-3%EA%B0%80%EC%A7%80-%EB%B0%A9%EC%8B%9D-%EC%83%9D%EC%84%B1%EC%9E%90-%EC%A3%BC%EC%9E%85-%EC%88%98%EC%A0%95%EC%9E%90-%EC%A3%BC%EC%9E%85-%ED%95%84%EB%93%9C-%EC%A3%BC%EC%9E%85#%EC%-A%A-%ED%--%--%EB%A-%--%EC%-D%--%--%EC%-D%--%EC%A-%B-%EC%--%B-%--%EC%A-%BC%EC%-E%--)

## 요약

필요한 자원을 직접 생성하거나 관리하지 말고, 대신 필요한 자원을 외부에서 주입받도록 하자.
#  Item60 정확한 답이 필요하다면 float와 double은 피하라.

### float, double 타입 특성
- 과학과 공학 계산용으로 설계되었다.
- 이진 부동수소점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 '근사치'로 계산하도록 설계되었다.
- 부동소수점 오차 때문에 소수점 이하 정확한 계산, 금융 계산과는 맞지 않는다.
```java
double result = 1.03 - 0.42;
if(result > 0.61) {
    System.out.println("over");
} else if(result == 0.61) {
    System.out.println("correct");
} else {
    System.out.println("under");
}
    System.out.println(result);
```
출력 결과
``` 
over
0.6100000000000001
```

특히 결과값의 범위 비교연산이 있는 경우 부동소수점 오차 때문에 더욱 주의해야 한다.

### BigDecimal을 사용한 경우

```java
import java.math.BigDecimal;

final BigDecimal TEN_CENTS = new BigDecimal(".10");

int itemsBought = 0;
BigDecimal funds = new BigDecimal("1.00");
for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0; price = price.add(TEN_CENTS)) {
    funds = funds.subtract(price);
    itemsBought++;
}
System.out.println(itemsBought + "개 구입");
System.out.println("잔돈(달러): " + funds);
```
예시의 아이템은 값은 누적되어 더해져서 
item 4개 구입, 잔돈은 0이 남는다.

BigDecimal 단점
- 기본타입보다 느리다.
- 쓰기 불편하다.

### 대신 정수 타입을 사용하자
소수점을 직접관리하면 보다 정확하게 계산할 수 있다.
소수점을 쓰기보다 가장 작은 단위 계산으로 쪼개서 연산하면 오차를 줄일 수 있다.</br>
다음과 같이 달러를 센트로 단위를 바꿔 계산하자.
```java
int itemsBought = 0;
int funds = 100;
for (int price = 10; funds >= price; price += 10) {
    funds -= price;
    itemsBought++;
    System.out.println(funds);
}
System.out.println(itemsBought + "개 구입");
System.out.println("잔돈(센트): " + funds);
```

```
90
70
40
0
4개 구입
잔돈(센트): 0
```

### 요약 정리
- 정확한 답이 필요한 계산에는 float, double 타입의 계산을 하지 말아야한다.
- 성능이 좋지 않을 수 있지만 BigDecimal은 단위가 크거나 반올림 수행 등 정확한 계산시 좋은 타입이다.
- 8자리 이상 단위가 크지 않고 성능이 중요하다면 int, long 을 사용하자.


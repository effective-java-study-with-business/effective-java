**표준 라이브러리**를 유용하게 쓰면, 다음과 같은 장점들이 존재한다.

1. 핵심적인 일과 크게 관련 없는 문제를 해결하느라 시간을 허비하지 않아도 된다.
2. 노력하지 않아도 성능이 지속해서 개선된다.
3. 기능이 점점 많아진다.
4. 다른 개발자들이 읽고 유지보수, 재활용 하기 좋은 코드가 된다.

### 표준 라이브러리 사용 예시 : Random

### 직접 구현

```java
static Random rnd = new Random();

static int random(int n) {
	return Math.abs(rnd.nextInt()) % n;
}

public static void main(String[] args){
	int n = 2 * (Integer.MAX_VALUE / 3);
    int low = 0;
    for (int i = 0; i < 1000000; i++)
    	if (random(n) < n/2)
        	low++;
    System.out.println(low);
}
```

해당 코드는 n이 그리 크지 않은 2의 제곱수라면 얼마 지나지 않아 같은 수열이 반복되고, n이 2의 제곱수가 아니라면 몇몇 숫자가 평균적으로 더 반환되는 문제가 존재한다. 더불어 지정한 범위의 바깥수가 종종 튀어나올 수도 있다.(rnd.nextInt()가 MIN_VALUE를 반환하는 경우)

### Random.nextInt(n)

`Random.nextInt(n)` 는 위의 문제들을 이미 해결해놓았고, 자세한 동작 방식은 몰라도 된다. 참고로 자바 7 이후부터는 `Random`이 아닌 `ThreadLocalRandom` 을 사용하자. 포크 조인 풀이나, 병렬 스트림에서는 `SplittableRandom` 을 사용하는 것이 좋다.

### 표준 라이브러리 사용 예시 : InputStream

지정한 URL의 내용을 가져오는 명령줄 어플리케이션을 만든다 가정해보자.(리눅스의 curl 명령어) 자바 9 이후 도입된 InputStream의 `transfer()` 메서드를 사용하면 쉽게 구현 가능하다.

```java
public static void main(String[] args) throws IOException {
	try (InputStream in = new URL(args[0]).openStream()) {
    	in.transferTo(System.ouf);
    }
}
```

라이브러리 중 `java.lang`, `java.util`, `java.io` 하위 패키지들에는 익숙해지는 것이 좋다. 자바 표준 라이브러리에서 원하는 기능을 찾지 못했다면, 고품질의 서드파티(*ex) google의 구아바* ) 라이브러리까지 찾아보고 마지막 대안으로 직접 구현하도록 하자.

# **자바 프로그래머가 익혀야할 필수 표준 라이브러리들**

- java.lang
- java.util
- java.io
- Collection framework
- Stream [아이템 45~48]
- java.util.concurrent → 알아두면 도움이 됨

라이브러리를 최대한 사용하려 해보고, 원하는 기능을 찾지 못했다면 고품질의 서드파티 라이브러리를 찾아보자.

그것도 못찾았다면 직접 구현하자.

> 📚 핵심 정리
>
>
> 일반적으로 라이브러리의 코드는 직접 작성한 것보다 품질이 좋고, 점차 개선될 가능성이 크기 때문에 직접 구현하지 말고 라이브러리를 사용하자.
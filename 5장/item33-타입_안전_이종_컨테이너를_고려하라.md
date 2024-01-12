# 아이템 33. 타입 안전 이종 컨테니어를 고려하라

## 타입 안전 이종 컨테이너 패턴

### 타입 안전 이종 컨테이너 패턴 - API
```java
public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance) {
        ...
    }

    public <T> T getFavorite(Class<T> type) {
        ...
    }
}
```
일반 맵처럼 보이지만, 키가 매개변수화 되었다.
클라이언트는 put하거나 get 할때, Class의 객체를 알려주면 된다.

### 타입 안전 이종 컨테이너 패턴 - 클라이언트
```java
public class Favorites {
    public static void main(String[] args) {
        Favorites favorites = new Favorites();

        favorites.putFavorite(String.class, "Java");
        favorites.putFavorite(Integer.class, 0xcafebabe);
        favorites.putFavorite(Class.class, Favorites.class);

        String favoriteString = favorites.getFavorite(String.class);
        int favoriteInteger = favorites.getFavorite(Integer.class);
        Class<?> favoriteClass = favorites.getFavorite(Class.class);

        System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getName());
    }
}
```

1. 이렇게 구현하게 되면, Favorites의 인스턴스는 타입 안전하다.(String을 요청했는데 Integer 반환하는 일이 절대 없다.)
2. 모든 키의 타입이 제각각이라, 여러가지 타입의 원소를 담을 수 있다.

### 타입 안전 이종 컨테이너 패턴 - 구현
```java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```

맵 변수 favorites는 비한정적 와일드카드 타입이라 아무것도 넣을 수 없다고 생각할 수 있지만, 와일드카드 타입이 중첩되었다는 뜻이다.
즉, 모든 키가 서도 다른 매개변수화 타입일 수 있다는 뜻이다.

getFavorite의 꺼내는 맵의 값은 Object이므로 cast 메서드를 통해 class의 객체타입 매개변수를 T로 반환해야한다.

### cast 메서드

형변환 연산자의 동적버전이다.
이 메서드는 단순히 주어진 인수가 class 객체가 알려주는 타입의 인스턴스인지를 검사한 다음, 맞다면 그 인수를 그대로 반환하고, 아니면 ClassCastException을 던진다.
즉, favorites 맵 안의 값은 해당 키의 타입과 항상 일치하기 때문에 적합한 메서드이다.

인수를 그대로 반환한다면 굳이 사용하는 이유가 뭘까?
-> cast 메서드의 시그니처가 Class 클래스가 제네릭이라는 이점을 완벽히 사용하기 때문이다.
```java
public class Class<T> {
    T cast(Object obj);
}
```
cast의 반환 타입은 Class 객체의 타입 매개변수와 같다.

## 제약 조건 2가지

### 동적 형변환으로 런타임 타입 안정성 확보
악의적인 클라이언트가 Class 객체를 (제네릭이 아닌) 로 타입으로 넘기면 Favorites 인스턴스의 타입 안전성이 쉽게 깨진다.
```java
f.putFavorite((Class)Integer.class, "문자열");
int favoriteInteger = f.getFavorite(Integer.class);
```
-> putFavorite일 때에는 오류 없이 되고 getFavorite을 할때에는 ClassCastException을 던진다.

해결방법
```java
public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }
}
```
-> instance의 타입이 type으로 명시한 타입과 같은지 확인 후 검사하여 사용(동적 형변환 이용)

### 실체화 불가 타입에는 사용 할 수 없다.
String이나 String[]은 저장할 수 있어도 List<String>은 저장할 수 없다. 
List<String>용 Class 객체를 얻을 수 없다. List는 무조건 List.class를 사용하기 때문이다.

-> 해당 제약 조건에 대한 만족스러운 우회는 없다고 기술되어있음.

*슈퍼 타입 토큰으로 해결 시도
```java
public class Favorites {
    private Map<TypeRef<?>, Object> favorites = new HashMap<>();

    public <T> T getFavorite(TypeRef<T> tr) {
        if(tr.type instanceof Class<?>){ //일반클래스인 경우
            return ((Class<T>)tr.type).cast(favorites.get(tr));
        }else{ //제네릭타입인 경우
            return ((Class<T>)((ParameterizedType)tr.type).getRawType()).cast(favorites.get(tr));
        }
    }

    public <T> void putFavorite(TypeRef<T> tr, T instance) {
        favorites.put(Objects.requireNonNull(tr), instance);
    }

    public static void main(String[] args) {

        Favorites f = new Favorites();
        f.putFavorite(new TypeRef<List<String>>(){}, Arrays.asList("개","고양이","앵무"));

        List<String> listOfString = f.getFavorite(new TypeRef<List<String>>(){});

        listOfString.forEach((s -> {
            System.out.println(s);
        }));

    }
}
```
```java
abstract class TypeRef<T> {
    Type type;

    public TypeRef(){
        Type stype = getClass().getGenericSuperclass();
        if(stype instanceof ParameterizedType){
            this.type = ((ParameterizedType)stype).getActualTypeArguments()[0];
        }else throw new RuntimeException();
    }
    public int hashCode(){
        return type.hashCode(); //type을 기준으로 식별(type은 Class이므로 Class레벨만 식별됨)
    }
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass().getSuperclass() != o.getClass().getSuperclass()) return false;
        TypeRef<?> that = (TypeRef<?>) o;
        return type.equals(that.type); //마찬가지로 두 객체 간의 type을 비교
    }

}
```

## 한정적 타입 토큰을 사용한 애너테이션 API
```java
public <T extends Annotation> T getAnnotation(Class<T> annotationType);
```
AnnotatedElement 인터페이스에 선언된 메서드로, 대상 요소에 달려있는 애너테이션을 런타임에 읽어오는 기능을 한다.
주어진 대상 요소(클래스, 메서드, 필드 등)에 달려 있는 애너테이션 중에서 매개변수로 전달된 annotationType과 일치하는 것을 찾아 반환합니다. 만약 해당 애너테이션이 발견되지 않으면 null을 반환한다.

즉, 애너테이션된 요소는 그 키가 애너테이션 타입인, 타입 안전 이종 컨테이너인 것이다.

## asSubClass 메서드
한정적 타입 토큰을 안전하게 형변환해주는 인스턴스 메서드

위의 getAnnotation의 한정적 타입 토큰을 비한정적 타입 토큰을 이용해 원하는 형태로 형변환해준다.
```java
static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName){
    Class<?> annotationType = null; // 비한정적 타입 토큰
    try {
        annotationType = Class.forName(annotationTypeName);
    }catch (Exception exception){
        throw new IllegalArgumentException(exception);
    }
    return element.getAnnotation(annotationType.asSubclass(Annotation.class));
}
```


## 요약
1. 컨테이너 자체가 아닌 키를 타입 매개변수로 바꾸면 일반적인 제네릭 형태에서 다룰수 있는 고정된 매개변수의 제약에서 벗어난 타입 안전 이종 컨테이너를 만들 수 있다.
2. 타임 안전 이종 컨테이너는 Class를 키로 쓰며, 이런 식으로 쓰이는 Class 객체를 타입 토큰이라 한다.
3. 직접 구현한 키 타입도 쓸 수 있다.

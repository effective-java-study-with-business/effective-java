# 아이템 2 생성자에 매개변수가 많다면 빌더를 고려하라

## 1. 점층적 생성자 패턴과 자바빈스 패턴

### 점층적 생성자 패턴(telescoping constructor pattern)

```java
public class NutritionFacts {

    private final int servingSize; //필수
    private final int servings; //필수
    private final int calories; //선택
    private final int fat; //선택
    private final int sodium; //선택
    private final int carbohydrate; //선택

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
        this(servingSize, servings, calories, fat, sodium, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodium = sodium;
        this.carbohydrate = carbohydrate;
    }
}
```

`NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);`

- 전통적으로 많이 써왔던 방식입니다.
- 매개변수를 하나씩 늘려 모든 매개변수를 받는 생성자를 만들어 둡니다.
- 매개변수가 많거나 같은 타입이 많을 경우 가독성이 매우 떨어지므로 이 때문에 버그 찾기가 힘듭니다.

### 자바빈즈 패턴

```java
public class NutritionFacts {

    private int servingSize = -1; //필수
    private int servings = -1; //필수
    private int calories = 0; //선택
    private int fat = 0; //선택
    private int sodium = 0; //선택
    private int carbohydrate = 0; //선택

    public NutritionFacts() {
    }

    // 세터 매서드들
    public void setServingSize(int val) {
        servingSize = val;
    }

    public void setServings(int val) {
        servings = val;
    }

    public void setCalories(int val) {
        calories = val;
    }

    public void setFat(int val) {
        fat = val;
    }

    public void setSodium(int val) {
        sodium = val;
    }

    public void setCarbohydrate(int val) {
        carbohydrate = val;
    }
}
```

```java
NutritionFacts cocaCola = new NutritionFacts();
    cocaCola.setServingSize(240);
    cocaCola.setServings(8);
    cocaCola.setCalories(100);
    cocaCola.setSodium(35);
    cocaCola.setCarbohydrate(27);
```

- 기본 생성자에 setter 매서드를 조합해서 객체를 생성합니다.
- 객체의 매개변수를 언제든지 추가, 변경이 가능하므로 일관성이 깨집니다. 이는 자바빈즈 패턴의 심각한 단점입니다.

## 2. 빌더패턴

```java
public class NutritionFacts {

    private final int servingSize; //필수
    private final int servings; //필수
    private final int calories; //선택
    private final int fat; //선택
    private final int sodium; //선택
    private final int carbohydrate; //선택

    public static class Builder {

        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```
- 빌더의 메서드 체이닝을 활용하면 간결하게 객체 생성 코드를 작성할 수 있습니다.
```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
    .calories(100).codium(35).carbohydrate(27).build();
```
- 입력 매개변수를 validation하는 코드를 빌더 매서드에 삽입하여 더욱 견고하게 매개변수를 검사할 수 있습니다.
- 빌더 패턴은 계층적으로 설계된 클래스와 함께 사용하기 좋은 패턴입니다.

## 3. 계층 설계된 클래스의 빌더패턴

```java
import java.util.EnumSet;
import java.util.Objects;

public abstract class Pizza {

    public enum Topping {HAM, MUSHROOM, ONION, PEPPER, SAUSAGE}

    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }
        // 구체 하위 클래스를 반환합니다.
        abstract Pizza build();
        // 하위 클래스에서 Builder를 리턴 하도록 재정의 합니다.
        protected abstract T self();
    }
    
    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }
}
// 뉴욕 피자
public class NyPizza extends Pizza {
    public enum Size { SMALL, MEDIUM, LARGE }
    private final Size size;
    
    public static class Builder extends Pizza.Builder<Builder> {
        private Size size;
        
        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }
        
        @Override public NyPizza build() {
            return new NyPizza(this);
        }
        
        @Override protected Builder self() {
            return this;
        }
    }
    
    private NyPizza(Builder builder) {
        super(builder);
        size = builder.size;
    }
}

// 칼조네 피자
public class Calzone extends Pizza {
    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder> {
        private boolean sauceInside = false;

        public Builder sauceInside() {
            sauceInside = true;
        }
        
        @Override public Calzone build() {
            return new Calzone(this);
        }

        @Override protected Builder self() {
            return this;
        }
    }

    private Calzone(Builder builder) {
        super(builder);
        size = builder.size;
    }
}
```
```java
NyPizza pizza = new NyPizza.Builder(SMALL)
    .addTopping(SAUSAGE).addTopping(ONION).build();
Calzone calzone = new Calzone.Builder()
    .addTopping(HAM).sauceInside().build();
```
- 빌더 패턴은 객체 생성을 유연하게 설계할 수 있습니다.
- 중복된 코드는 추상클래스에 모으고 상속 후 종류에 따라 구현하기도 빌더패턴에서 가능합니다.
- 빌더 패턴으로 코드가 단순하지는 않지만 매개변수가 많을 경우 객체 생성코드는 훨씬 간결해집니다.

## 4. Lombok의 @Builder 살펴보기

실제로 현업에서 클래스 내부에 빌더를 삽입하기 보다는
Lombok 라이브러리의 @Builder 어노테이션을 활용하여 간편하게 빌더 객체 생성을 합니다.
```java

@Builder
class Example<T> {

    private T foo;
    private final String bar;
}
```
@Builder 어노테이션을 선언하면 자동으로 다음과 같이 변환해줍니다.
```java
class Example<T> {

    private T foo;
    private final String bar;

    private Example(T foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    public static <T> ExampleBuilder<T> builder() {
        return new ExampleBuilder<T>();
    }

    public static class ExampleBuilder<T> {

        private T foo;
        private String bar;

        private ExampleBuilder() {
        }

        public ExampleBuilder foo(T foo) {
            this.foo = foo;
            return this;
        }

        public ExampleBuilder bar(String bar) {
            this.bar = bar;
            return this;
        }

        @java.lang.Override
        public String toString() {
            return "ExampleBuilder(foo = " + foo + ", bar = " + bar + ")";
        }

        public Example build() {
            return new Example(foo, bar);
        }
    }
}
```

# Item37 ordinal 인덱싱 대신 EnumMap을 사용하라

## 1. ordinal()을 배열 인덱스로 사용하지 말 것

다음 식물의 사이클 타입을 열거타입으로 가지고 있는 식물 클래스가 있습니다.
```java
public class Plant {
    enum LifeCycle {
        ANNUAL, PERENNIAL, BIENNIAL
    }
    
    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
```
garden Plant 배열
```java
Plant rice = new Plant("벼", Plant.LifeCycle.ANNUAL);
Plant corn = new Plant("옥수수", Plant.LifeCycle.ANNUAL);
Plant barley = new Plant("보리", Plant.LifeCycle.BIENNIAL);
Plant rosemary = new Plant("로즈마리", Plant.LifeCycle.PERENNIAL);

List<Plant> garden = Arrays.asList(rice, corn, barley, rosemary);
```

###열거타입의 ordinal 을 배열 인덱스로 사용한 예시
```java
Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
    plantsByLifeCycle[i] = new HashSet<>();
}

for (Plant plant : garden) {
    plantsByLifeCycle[plant.lifeCycle.ordinal()].add(plant);
}

for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
    System.out.printf("%s : %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
}

```
- 배열이 제네릭과 호환 되지않아 비검사 형변환을 매번 수행해야 합니다.
- 각 인덱스가 정수타입과 맞게 사용되어야 하는데 `ArrayIndexOutOfBoundsException` 오류의 가능성이 있습니다.

## 2. EnumMap 특징
### 장점
- Enum 타입을 key로 사용하는 Map
- Array를 이용하므로 성능이 좋습니다.
- 해싱 과정이 필요없어 HashMap보다 빠릅니다.
- Enum 타입만 key로 넣을 수 있습니다.
### 단점
- key에 null 값이 들어가면 NullPointException 발생.
- thread-safe 하지 않습니다.

## 2. EnumMap을 사용해 데이터와 열거 타입을 매핑
### 예시
```java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

for (Plant.LifeCycle lifeCycle : LifeCycle.values()) {
    plantsByLifeCycle.put(lifeCycle,new HashSet<>());
}

for (Plant plant : garden) {
    plantsByLifeCycle.get(plant.lifeCycle).add(plant);
}

System.out.println(plantsByLifeCycle);
```
- 코드가 짧고 명료합니다.
- 배열 인덱스를 계산하는데 오류가 날 가능성이 없습니다.
- EnumMap 내부구현에서 배열을 사용하므로 성능도 더 좋습니다.

## 3. 스트림을 사용한 코드의 경우

###ver.1
```java
System.out.println(garden.stream().
    collect(Collectors.groupingBy(plant -> plant.lifeCycle))
);
```

###ver.2
```java
System.out.println(garden.stream()
    .collect(Collectors.groupingBy(
        plant -> plant.lifeCycle,
        () -> new EnumMap<>(Plant.LifeCycle.class),Collectors.toSet())
    )
);
```
 - 스트림을 사용해 맵 수집할 경우 존재하는 데이터 위주로 그루핑하므로 그루핑이 안되는 열거타입은 키에서 누락되어 결과가 다릅니다.
 - 1번에서는 맵을 2개 만들고, 2번에서는 맵을 3개 만듭니다.

## 4. 다차원 관계의 경우 EnumMap<..., EnumMap<...>>
물질의 상태와 상태변이에 따른 전이를 나타낸 열거 타입 예시 입니다.

###ver.1 ordinal을 사용한 경우
```java
public enum Phase {
SOLID, LIQUID, GAS;

    public enum Transition {
        MELT,FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        private static final Transition[][] TRANSITIONS = {
                {null, MELT, SUBLIME},
                {FREEZE, null, BOIL},
                {DEPOSIT, CONDENSE, null}
        };

        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```
- 불필요하지만 어쩔수없이 null 상태가 들어가야 함.
- 상태가 추가될 시 코드를 많이 변경해야하고, 잘못하면 런타임 오류가 발생할 확률이 큰 코드입니다.

###ver.2 중첩 EnumMap 사용한 경우
```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID),
        FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS),
        CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS),
        DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }
        // 전이 맵 초기화
        private static final Map<Phase, Map<Phase, Transition>> transitionMap = Stream.of(values())
            .collect(Collectors.groupingBy(t -> t.from, 
                () -> new EnumMap<>(Phase.class), 
                Collectors.toMap(t -> t.to, 
                    t -> t, 
                    (x,y) -> y, // 병합 함수.중복되는 값이 있으면 갱신. 중복이 없으므로 사용하지 않음.
                    () -> new EnumMap<>(Phase.class)))
            );

        public static Transition from(Phase from, Phase to) {
            return transitionMap.get(from).get(to);
        }
    }
}
```

- 전이를 나타내는 열거타입은 이전상태와 이후 상태를 쌍으로 가지도록 표현합니다.
- 상전이 맵은 이전 상태로 그룹핑된 이후 상태를 키로 가진 전이 맵을 value로 가집니다.

### PLAZMA 플라스마 상태와 각 전이 형태가 추가되는 경우
```java
public enum Phase {
    SOLID, LIQUID, GAS, PLASMA;

    public enum Transition {
        MELT(SOLID, LIQUID),
        FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS),
        CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS),
        DEPOSIT(GAS, SOLID),
        IONIZE(GAS, PLASMA),
        DEIONIZE(PLASMA, GAS);
    }
}
```
- 코드 수정 필요없이 간단하게 타입 추가하면 됩니다.
- 시간, 공간 낭비 없이 명확한 코드.

## 5. 정리

- 코드에서 열거 타입의 ordinal을 쓰는 것은 좋지 않다.
- 코드가 명료하고, 성능이 좋은 EnumMap을 사용하자.
- 다차원 관계는 EnumMap<..., EnumMap<...>> 으로 표현하자.

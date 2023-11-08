# 아이템 14 Comparable을 구현할지 고려하라

## Comparable이 무엇인가?
객체 간의 순서를 정의하고 비교할 수 있는 기능을 추가하는 데 사용됩니다. 주로 정렬과 관련된 작업에서 유용하게 활용됩니다.

```java
// 대충 이런 형태이다.
public interface Comparable<T> {
    int compareTo(T other);
}
```
Comparable이 구현되어 있다면 아래와 같이 손쉽게 정렬할 수 있다.

```java
class Main {
    public static void main(String[] args) {
        int[] a = {5, 2, 1, 3, 5};
        Arrays.sort(a);
        System.out.println(Arrays.toString(a));
        // [1, 2, 3, 5, 5]
    }
}
```

## 그럼 어떤 상황에서 Comparable을 구현해야 하는가?
책에 서는 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하라고 나와 있습니다.

자바 플랫폼 라이브러리의 모든 값 클래스와 열거타입 또한 Comparable을 구현하고 있다고 합니다.

## 어떻게 구현하는가?

백문이 불여일견, 한번 보는 것이 좋을 거 같아 예제를 준비하였습니다.

예제에서는 사람들을 정렬하는 것을 예제로 준비하였고

성, 이름, 나이 순서로 정렬하여 성이 같으면 이름으로 비교, 이름이 같으면 나이가 더 많은 사람이 우선 정렬되도록 예시를 작성하였습니다.

```java
public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Smith", "Alice", 30));
        people.add(new Person("Johnson", "Bob", 25));
        people.add(new Person("Smith", "Charlie", 35));
        people.add(new Person("Johnson", "Anna", 30));

        Collections.sort(people); // 성, 이름, 나이를 기준으로 정렬

        for (Person person : people) {
            System.out.println(person);
        }
    }

    class Person implements Comparable<Person> {
        private final String 성;
        private final String 이름;
        private final int 나이;

        public Person(String 성, String 이름, int 나이) {
            this.성 = 성;
            this.이름 = 이름;
            this.나이 = 나이;
        }

        public String get성() {
            return 성;
        }

        public String get이름() {
            return 이름;
        }

        public int get나이() {
            return 나이;
        }

        @Override
        public int compareTo(Person other) {
            // 성을 기준으로 비교
            int lastNameComparison = this.성.compareTo(other.get성());
            if (lastNameComparison != 0) {
                return lastNameComparison;
            }

            // 성이 같을 경우 이름을 기준으로 비교
            int firstNameComparison = this.이름.compareTo(other.get이름());
            if (firstNameComparison != 0) {
                return firstNameComparison;
            }

            // 성과 이름이 같을 경우 나이를 기준으로 비교
            return Integer.compare(this.나이, other.get나이());
        }

        @Override
        public String toString() {
            return "Person{성='" + 성 + "', 이름='" + 이름 + "', 나이=" + 나이 + '}';
        }
    }
}
```
객체에서는 Comparable 인터페이스를 implements 받아, compareTo를 Override하여 구현하거나, 아래와 같이 람다를 이용하여 구현할 수 있습니다.

```java
public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Smith", "Alice", 30));
        people.add(new Person("Johnson", "Bob", 25));
        people.add(new Person("Smith", "Charlie", 35));
        people.add(new Person("Johnson", "Anna", 30));

        // 람다식을 사용하여 성, 이름, 나이 순서로 정렬
        Collections.sort(people, (person1, person2) -> {
            int lastNameComparison = person1.get성().compareTo(person2.get성());
            if (lastNameComparison != 0) {
                return lastNameComparison;
            }

            int firstNameComparison = person1.get이름().compareTo(person2.get이름());
            if (firstNameComparison != 0) {
                return firstNameComparison;
            }

            return Integer.compare(person1.get나이(), person2.get나이());
        });

        for (Person person : people) {
            System.out.println(person);
        }
    }

    static class Person {
        private final String 성;
        private final String 이름;
        private final int 나이;

        public Person(String 성, String 이름, int 나이) {
            this.성 = 성;
            this.이름 = 이름;
            this.나이 = 나이;
        }

        public String get성() {
            return 성;
        }

        public String get이름() {
            return 이름;
        }

        public int get나이() {
            return 나이;
        }

        @Override
        public String toString() {
            return "Person{성='" + 성 + "', 이름='" + 이름 + "', 나이=" + 나이 + '}';
        }
    }
}


```
클래스에 필드가 여러개 일때 핵심 필드 부터 비교해라에 해당하는 예제이기도 합니다.

## CompareTo 규약

```java
class Person implements Comparable<Person> {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compareTo(Person other) {
            // 이름을 기준으로 비교
            int nameComparison = this.name.compareTo(other.name);
            if (nameComparison != 0) {
                return nameComparison;
            }

            // 이름이 같을 경우 나이를 기준으로 비교
            return Integer.compare(this.age, other.age);
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + '}';
        }
    }
```
1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다는 것이다.
```java
public class Item14_2 {
    
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Alice", 30));
        people.add(new Person("Bob", 25));
        people.add(new Person("David", 30));
        people.add(new Person("Charlie", 35));

        // 1. 두 객체 참조 순서를 바꿔도 예상 결과가 동일
        Collections.sort(people);
        for (Person person : people) {
            System.out.println(person);
        }

        // 결과
        // Person{name='Alice', age=30}
        // Person{name='Bob', age=25}
        // Person{name='Charlie', age=35}
        // Person{name='David', age=30}
    }
}
```

2. 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다는 뜻이다.

```java
        // 2. 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면 첫 번째는 세 번째보다 크다
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Bob", 25);
        Person person3 = new Person("Charlie", 35);

        int compareResult1 = person1.compareTo(person2);
        int compareResult2 = person2.compareTo(person3);
        int compareResult3 = person1.compareTo(person3);

        System.out.println("Compare Result 1: " + compareResult1);
        System.out.println("Compare Result 2: " + compareResult2);
        System.out.println("Compare Result 3: " + compareResult3);

        // Compare Result 1: -1
        // Compare Result 2: -1
        // Compare Result 3: -2
```

3. 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야 한다는 뜻이다.

```java
        Person person4 = new Person("Alice", 30);
        int compareResult4 = person1.compareTo(person4);

        System.out.println("Compare Result 4: " + compareResult4);
        
        // Compare Result 4: 0
```

## compareTo 메서드 작성 요령

- Comparable은 타입을 인수로 받는 제네릭 인터페이스이므로 compareTo 메서드의 인수 타입은 컴파일타임에 정해진다.

- 입력 인수의 타입을 확인하거나 형변환할 필요가 없다는 뜻이다. 인수의 타입이 잘못됐다면 컴파일 자체가 되지 않는다.

- null을 인수로 넣어 호출하면 NullPointerException을 던져야 하며, 물론 실제로도 인수(이 경우 null)의 멤버에 접근하려는 순간 이 예외가 던져질 것이다.

- compareTo 메서드는 각 필드가 동치인지를 비교하는 게 아니라 그 순서를 비교한다.

- 객체 참조 필드를 비교하려면 compareTo 메서드를 재귀적으로 호출한다.

- Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 비교자(Comparator)를 대신 사용한다.

- 비교자는 직접 만들거나 자바가 제공하는 것 중에 골라 쓰면 된다.

이 책의 2판에서는 compareTo 메서드에서 정수 기본 타입 필드를 비교할 때는 < 와 >를, 실수는 Double.compare, Fload.compare를 사용하라고 권했다. 하지만 자바 7부터는 상황이 변했다. compare가 새롭게 추가 되었다. <와 > 를 사용하는 이전 방식은 거추장스럽고 오류를 유발하니, 이제는 추천하지 않는다.

## java8과 Comparator
java8에서는 Comparator 인터페이스가 일련의 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성 할 수 있게 되었습니다.

예시

```java
public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Smith", "Alice", 30));
        people.add(new Person("Johnson", "Bob", 25));
        people.add(new Person("Smith", "Charlie", 35));
        people.add(new Person("Johnson", "Anna", 30));

        // Comparator를 사용하여 성, 이름, 나이 순서로 정렬
        Comparator<Person> personComparator = new Comparator<Person>() {
            @Override
            public int compare(Person person1, Person person2) {
                int lastNameComparison = person1.get성().compareTo(person2.get성());
                if (lastNameComparison != 0) {
                    return lastNameComparison;
                }

                int firstNameComparison = person1.get이름().compareTo(person2.get이름());
                if (firstNameComparison != 0) {
                    return firstNameComparison;
                }

                return Integer.compare(person1.get나이(), person2.get나이());
            }
        };

        Collections.sort(people, personComparator);

        for (Person person : people) {
            System.out.println(person);
        }
    }
}
```

비교자 생성 메서드를 이용하면 더 간결하고 깔끔하게 사용할 수 있습니다.
```java
        // Comparator를 사용하여 성, 이름, 나이 순서로 정렬
        Comparator<Person> personComparator = Comparator
                .comparing(Person::get성)
                .thenComparing(Person::get이름)
                .thenComparingInt(Person::get나이);
```

Comparator 방식은 간결하게 사용할 수 있지만 약간에 속도저하가 발생한다.


### Comparator는 왜 속도 저하가 발생하는가?

1. 내부 비교 로직의 효율성: Comparable은 객체 자체의 클래스 내에 비교 로직을 가지고 있기 때문에 객체의 비교에 대한 최적화가 더 쉽게 이루어질 수 있습니다. 이는 JVM이 객체 비교를 최적화하기 위해 더 좋은 기회를 가지게 하며, 불필요한 비교 작업을 줄일 수 있습니다. 반면에 Comparator를 사용하는 경우 비교 로직이 외부에 있기 때문에 JVM이 최적화하기가 더 어려울 수 있습니다.

2. Comparator 객체 생성 비용: Comparator를 사용하는 경우마다 비교를 수행하는 Comparator 객체를 생성해야 합니다. 이 객체 생성 비용은 Comparable을 사용할 때보다 더 많은 오버헤드를 유발할 수 있습니다. 또한 Comparator 객체의 재사용을 고려하지 않으면 비교 작업마다 객체를 계속 생성해야 하므로 성능 저하가 발생할 수 있습니다.


## 요약

- 순서를 고려해야하는 값 클래스를 작성하게 된다면 꼭 Comparable 인터페이스를 구현하여  인스턴스들을 쉽게 정렬/검색하고 비교 기능을 제공하는 컬렉션과 어우러져야 한다.
- compareTo()에서는 필드 값 비교시에 <,>를 사용하지 말고 박싱된 기본 타입 클래스가 제공하는 정적 메서드인 compare()이나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하도록 한다.

### 함께 보면 좋은 자료
[자바 [JAVA] - Comparable 과 Comparator의 이해](https://st-lab.tistory.com/243)
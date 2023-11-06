package item07;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE)
@Value(staticConstructor = "of")
@Getter
public class Person {

    String name;

    public static Person of(String name){
        return Person.builder()
                     .name(name)
                     .build();
    }

    public enum Kinds {
        MAN, WOMAN
    }

    public class OuterClass {
        private int temp;

        public void printPerson() {
            System.out.println(name);
        }
    }
}

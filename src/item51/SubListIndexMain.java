package item51;

import java.util.List;

public class SubListIndexMain {
    public static void main(String[] args) {
        List<String> languages = List.of("C", "Python", "Kotlin", "Java", "JavaScript");
        List<String> referredLangs = languages.subList(2, 4);
        System.out.println(referredLangs.indexOf("Java"));
    }
}

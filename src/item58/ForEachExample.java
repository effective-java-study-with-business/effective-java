package item58;

import java.util.Iterator;
import java.util.List;

public class ForEachExample {

    public static void main(String[] args) {

        List<String> keywords = new java.util.ArrayList<>(List.of("a", "b", "", "d", "e"));

        // better examples
        keywords.removeIf(String::isEmpty);

        for(int i=0;i< keywords.size();i++) {
            if(keywords.get(i).length() == 0)
                keywords.remove(i);
        }

        System.out.println(keywords);
    }

}

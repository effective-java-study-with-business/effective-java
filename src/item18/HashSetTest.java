package item18;

import java.util.List;
import java.util.TreeSet;

public class HashSetTest {

    public static void main(String[] args) {
        InstrumentedHashSet<String> instrumentedHashSetInherited = new InstrumentedHashSet<>();
        instrumentedHashSetInherited.addAll(List.of("1", "2", "3"));

        System.out.println("size: " + instrumentedHashSetInherited.size());
        System.out.println("count: " + instrumentedHashSetInherited.getAddCount());

        InstrumentedSet<String> instrumentedSetComposition = new InstrumentedSet<>(new TreeSet<>());
        instrumentedSetComposition.addAll(List.of("1", "2", "3"));

        System.out.println("size: " + instrumentedSetComposition.size());
        System.out.println("count: " + instrumentedSetComposition.getAddCount());
    }

}

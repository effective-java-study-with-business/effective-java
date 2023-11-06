package item07;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class Cache {
    public static void main(String[] args) {
        Object key1 = new Object();
        Object value1 = new Object();

        Map<Object, List> cache = new WeakHashMap<>();
        cache.put(key1, List.of(value1));
    }
}

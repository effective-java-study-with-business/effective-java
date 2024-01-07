package item32;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ItemTest {
    static <T> List<T> flatten(List<List<T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    @Test
    void test_One(){
        List<List<Integer>> nestedLists = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5),
            Arrays.asList(6, 7, 8)
        );

        // 동작: flatten 메서드를 사용하여 리스트를 평탄화
        List<Integer> flattenedList = flatten(nestedLists);

        // 단언: 예상된 결과와 비교
        List<Integer> expectedList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(expectedList, flattenedList);
    }
}

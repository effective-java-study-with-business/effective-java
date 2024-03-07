package item45;

import java.util.List;
import java.util.stream.Stream;

public class StreamDebug {

    public static void main(String[] args) {
        List<Integer> underTen = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Stream<Integer> withoutTerminal = underTen.stream()
                .filter(num -> num % 2 == 0)
                .map(even -> even * 10);


        withoutTerminal.sorted();
    }


}

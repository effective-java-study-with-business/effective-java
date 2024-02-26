package item69;

import java.util.stream.Stream;

public class Comparison {

    public static void main(String[] args) throws InterruptedException {
        Bird[] birds = Stream.generate(Bird::new)
                .limit(100000000)
                .toArray(Bird[]::new);

        withException(birds);

        withFor(birds);

        withForEach(birds);
    }

    public static void withException(Bird[] birds) {
        long start = System.nanoTime();

        try {
            int index = 0;
            while(true)
                birds[index++].flying();
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        System.out.printf("Exception Statement : %s\n", System.nanoTime() - start);
    }

    public static void withFor(Bird[] birds) {
        long start = System.nanoTime();

        for(int index=0;index<birds.length;index++)
            birds[index].flying();

        System.out.printf("For Statement : %s\n", System.nanoTime() - start);
    }

    public static void withForEach(Bird[] birds) {
        long start = System.nanoTime();

        for (Bird bird : birds)
            bird.flying();

        System.out.printf("For Each Statement : %s\n", System.nanoTime() - start);
    }

}

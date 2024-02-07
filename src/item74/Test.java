package item74;

import java.util.HashSet;


public class Test {

    public static void main(String[] args) throws Exception{
        method1(new HashSet<>());
        method2(new HashSet<>());
    }

    /**
     * 메소드 1
     * @param test
     * @throws NullPointerException
     */
    public static void method1(HashSet<String> test) throws  Exception {
        System.out.println("hello World");
    }

    /**
     * 메소드 2
     * @param test
     * @throws NullPointerException
     */
    public static void method2(HashSet<String> test) {
        System.out.println("hello World");
    }
}

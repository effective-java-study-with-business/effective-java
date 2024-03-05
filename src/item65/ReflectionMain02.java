package item65;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

public class ReflectionMain02 {
    public static void main(String[] args) {
        try {
            // 클래스명 Class 객체로 변환
            Class<? extends Set<String>> cl = (Class<? extends Set<String>>) Class.forName(args[0]);

            // 생성자
            Constructor<? extends Set<String>> cons = cl.getDeclaredConstructor();

            // 집합의 인스턴스를 만든다.
            Set<String> s = cons.newInstance();

            // 인자를 집합에 추가
            s.addAll(Arrays.asList(args).subList(1, args.length));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}

package item65;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Test01 {

    @Test
    void testGetDeclaredFields(){
        Object person = new Person();
        Field[] fields = person.getClass().getDeclaredFields();
        List<String> actual = getFieldNames(fields);


        assertTrue(Arrays.asList("name", "age").containsAll(actual));
    }

    @Test
    void testGetObjectClassName(){
        Object cat = new Cat();
        Class<?> clazz = cat.getClass();

        assertEquals("Cat", clazz.getSimpleName());
        assertEquals("item65.Cat", clazz.getName());
        assertEquals("item65.Cat", clazz.getCanonicalName());
    }

    private List<String> getFieldNames(Field[] fields) {
        List<String> fieldNames = new ArrayList<>();
        // 필드 정보에서 필드명을 가져온다
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }
}

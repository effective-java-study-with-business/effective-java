package item03;

import item03.public_static.FooConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionAccessMain {

    /**
     * Access to Constructor via Reflection API
     * @param args
     */
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        Constructor<?> fooConstructor = FooConnection.class.getDeclaredConstructor(null);
        fooConstructor.setAccessible(true);

        FooConnection newFooConnection = (FooConnection) fooConstructor.newInstance();

        System.out.printf("FooConnection.INSTANCE and newFooConnection are same instance? : %b",
                FooConnection.INSTANCE.equals(newFooConnection));
    }

}

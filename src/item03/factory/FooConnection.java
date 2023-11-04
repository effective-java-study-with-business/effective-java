package item03.factory;

import java.io.Serial;
import java.io.Serializable;

public class FooConnection implements Serializable {

    // INSTANCE field may initiate when this class is loading
    private static final FooConnection INSTANCE = new FooConnection();
    private transient int connectionInt = 0;

    private FooConnection() {

        // Defense for Reflection API
        if(INSTANCE != null)
            throw new RuntimeException("Cannot make second instance anyway!!!");
    }

    public static FooConnection getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    public int getConnectionInt() {
        return connectionInt;
    }
}

package item03.public_static;

import java.io.Serializable;

public class FooConnection implements Serializable {

    // INSTANCE field may initiate when this class is loading
    public static final FooConnection INSTANCE = new FooConnection();

    private FooConnection() {

        // Defense for Reflection API
        // If you want to test access to this constructor by reflection, please comment out if block
        if(INSTANCE != null)
            throw new RuntimeException("Cannot make second instance anyway!!!");
    }

    private Object readResolve() {
        return INSTANCE;
    }

}

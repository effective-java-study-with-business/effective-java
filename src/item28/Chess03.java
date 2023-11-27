package item28;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chess03<T> {
    private final List<T> chessPiece;

    public Chess03(Collection<T> choices) {
        chessPiece = new ArrayList<>(choices);
    }

    public T chess() {
        Random rnd = ThreadLocalRandom.current();
        return chessPiece.get(rnd.nextInt(chessPiece.size()));
    }
}

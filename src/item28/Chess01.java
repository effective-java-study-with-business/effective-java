package item28;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chess01 {
    private final Object[] chessPiece;

    public Chess01(Collection piece) {
        chessPiece = piece.toArray(new Object[0]);
    }

    public Object chess() {
        Random rnd = ThreadLocalRandom.current();
        return chessPiece[rnd.nextInt(chessPiece.length)];
    }

    public static void main(String[] args) {

    }
}

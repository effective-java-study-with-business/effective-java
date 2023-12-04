package item28;

import java.util.Collection;

public class Chess02<T> {
    private final T[] chessPiece;

    public Chess02(Collection<T> piece) {
        // chessPiece = piece.toArray();
        chessPiece = null; // 임시
    }
}
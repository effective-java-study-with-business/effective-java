package item36;

import lombok.Getter;

public class Arrow {
    public static final int BOLD = 1 << 0; // 1
    public static final int DOTTED = 1 << 1; // 2
    public static final int FILLED = 1 << 2; // 4

    @Getter
    private int currentStyle;

    public Arrow(int style) {
        this.currentStyle = style;
    }

    public void applyStyle(int style) {
        this.currentStyle = style;
    }
}
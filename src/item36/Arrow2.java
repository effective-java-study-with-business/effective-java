package item36;

import lombok.Getter;
import java.util.Set;

public class Arrow2 {

    public enum Style {
        BOLD, DOTTED, FILLED
    }

    @Getter
    private Set<Style> currentStyle;

    public Arrow2(Set<Style> style) {
        this.currentStyle = style;
    }

    public void applyStyle(Set<Style> style) {
        this.currentStyle = style;
    }
}

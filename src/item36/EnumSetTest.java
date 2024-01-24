package item36;

import java.util.EnumSet;

public class EnumSetTest {
    public static void main(String[] args) {
        Arrow2 arrow = new Arrow2(EnumSet.of(Arrow2.Style.BOLD, Arrow2.Style.DOTTED));
        System.out.println(arrow.getCurrentStyle());

        arrow.applyStyle(EnumSet.allOf(Arrow2.Style.class));
        System.out.println(arrow.getCurrentStyle());
    }
}

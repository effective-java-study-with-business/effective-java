package item36;

public class BitFieldTest {

    public static void main(String[] args) {
        Arrow arrow = new Arrow(Arrow.BOLD | Arrow.DOTTED);
        System.out.println(arrow.getCurrentStyle());

        arrow.applyStyle(Arrow.BOLD | Arrow.DOTTED | Arrow.FILLED);
        System.out.println(arrow.getCurrentStyle());
    }
}

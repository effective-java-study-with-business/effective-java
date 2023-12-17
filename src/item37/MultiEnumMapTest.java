package item37;

import java.util.List;

public class MultiEnumMapTest {

    public static void main(String[] args) {
        Phase.Transition transition = Phase.Transition.from(Phase.GAS, Phase.LIQUID);
        System.out.println(transition);
    }

}

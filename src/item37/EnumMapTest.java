package item37;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnumMapTest {

    public static void main(String[] args) {
        Plant rice = new Plant("벼", Plant.LifeCycle.ANNUAL);
        Plant corn = new Plant("옥수수", Plant.LifeCycle.ANNUAL);
        Plant barley = new Plant("보리", Plant.LifeCycle.BIENNIAL);
        Plant rosemary = new Plant("로즈마리", Plant.LifeCycle.PERENNIAL);

        List<Plant> garden = Arrays.asList(rice, corn, barley, rosemary);
        usingOrdinalArray(garden);
    }

    public static void usingOrdinalArray(List<Plant> garden) {
        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }

        for (Plant plant : garden) {
            plantsByLifeCycle[plant.lifeCycle.ordinal()].add(plant);
        }

        for (int i = 0 ; i < plantsByLifeCycle.length ; i++) {
            System.out.printf("%s : %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}

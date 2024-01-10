package item51;

import lombok.Builder;

public class CardGame {

    public enum Shape {
        DIA, HEART, CLOVER, SPADE
    }

    // helper class
    public static class CardAttribute {
        private int number;
        private Shape shape;
        private String color;

        @Builder
        public CardAttribute(int number, Shape shape, String color) {
            this.number = number;
            this.shape = shape;
            this.color = color;
        }
    }

    // Parameter with helper class is easier to use
    public void pickup(String gamer, CardAttribute card) {
        System.out.printf("%s picked up %s %s %d card!\n", gamer, card.shape, card.color, card.number);
    }

    public void pickup(String gamer, int number, Shape shape, String color) {
        System.out.printf("%s picked up %s %s %d card!\n", gamer, shape, color, number);
    }

}

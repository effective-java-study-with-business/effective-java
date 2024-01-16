package item51;

public class CardGameMain {
    public static void main(String[] args) {

        CardGame game = new CardGame();

        // Order should be checked...
        game.pickup("Minah", 7, CardGame.Shape.DIA, "Red");

        CardGame.CardAttribute card = CardGame.CardAttribute.builder()
                .shape(CardGame.Shape.DIA)
                .color("Red")
                .number(7)
                .build();

        game.pickup("Minah", card);

    }
}

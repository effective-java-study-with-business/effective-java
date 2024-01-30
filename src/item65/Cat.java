package item65;

public class Cat extends Animal{
    @Override
    protected String getSound() {
        return "ya ong";
    }

    @Override
    public String eats() {
        return "fish";
    }
}

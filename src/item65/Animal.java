package item65;

public abstract class Animal implements Eating {

    private static final String CATEGORY = "mammal";
    private String name;

    protected abstract String getSound();
}

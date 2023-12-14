package item24;

public class ArticleMain {
    public static void main(String[] args){
        // Anonymous class
        Article draft = new Article() {
            @Override
            public String publish() {
                return "Draft is temporary saved.";
            }
        };
        System.out.println(draft.publish());
    }
}

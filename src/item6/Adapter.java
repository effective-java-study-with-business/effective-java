public class Main {
    public static void main(String[] args) {

        Map<String,String> map = new HashMap<>();

        map.put("java","");
        map.put("c","");

        Set<String> set1 = map.keySet();
        Set<String> set2 = map.keySet();

        System.out.println(set1.size()); //  => 2
        System.out.println(set2.size()); // = > 2

        map.remove("c");
        System.out.println(set1.size()); //  => 1
        System.out.println(set2.size()); //  => 1
    }
}

// package item46;
//
// import java.util.*;
// import java.util.function.BinaryOperator;
// import java.util.stream.Stream;
//
// import static java.util.stream.Collectors.*;
//
// public class StreamCount {
//
//     public static void main(String[] args) {
//         final String file = "apple banana cherry blueberry banana banana cherry";
//
//         final Map<String, Long> freq;
//         try(Stream<String> words = new Scanner(file).tokens()) {
//             freq = words.collect(groupingBy(word -> word, counting()));
//         }
//         System.out.println(freq);
//
//         // 리스트 sort 하여 재배열 수집
//         final List<String> strings = List.of("a", "b", "b", "w", "a", "c");
//         final List<String> collect = strings.stream().sorted(Comparator.reverseOrder())
//                 .collect(toList());
//         System.out.println(collect);
//
//         // 맵 생성 수집
//         Artist artist1 = new Artist("artist1");
//         Artist artist2 = new Artist("artist2");
//         Album album1 = new Album(artist1, "album1", 1000L);
//         Album album2 = new Album(artist2, "album2", 2000L);
//         Album album3 = new Album(artist1, "album3", 3000L);
//
//         List<Album> albums = List.of(album1, album2, album3);
//         Map<Artist, Album> topHits = albums.stream().collect(
//                 toMap(Album::getArtist, a -> a, BinaryOperator.maxBy(Comparator.comparing(Album::getSales)))
//         );
//         System.out.println(topHits);
//
//         Map<Artist, Long> collectByArtist = albums.stream().collect(groupingBy(Album::getArtist, counting()));
//         System.out.println(collectByArtist);
//
//         Map<Boolean, List<Album>> collectBySales = albums.stream().collect(partitioningBy(album -> album.getSales() > 1500L));
//         System.out.println(collectBySales);
//
//         Comparator<Album> comparator = Comparator.comparing(Album::getSales);
//         Optional<Album> max = albums.stream().collect(maxBy(comparator));
//         System.out.println(max.get());
//
//         String titles = albums.stream().map(Album::getTitle).collect(joining(","));
//         System.out.println(titles);
//     }
// }

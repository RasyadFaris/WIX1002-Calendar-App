import java.util.List;
import java.util.stream.Collectors;

public class SearchEngine {
    public static List<Event> searchByTitle(List<Event> events, String query) {
        return events.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
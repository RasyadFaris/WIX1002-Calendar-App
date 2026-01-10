package service;

import model.Event;
import java.util.List;
import java.util.stream.Collectors;

public class SearchService {
    public static List<Event> searchByTitle(List<Event> events, String query) {
        return events.stream()
                .filter(event -> event.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
package service;

import model.Event;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsService {
    
    public String getBusiestDayOfWeek(List<Event> events) {
        if (events.isEmpty()) return "No Data";

        // Group by DayOfWeek (MONDAY, TUESDAY...) and count them
        Map<String, Long> counts = events.stream()
            .collect(Collectors.groupingBy(
                e -> e.getStart().getDayOfWeek().toString(), 
                Collectors.counting()
            ));

        // Find the entry with the max value
        return counts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " events)")
            .orElse("No Data");
    }

    public long getTotalDurationInHours(List<Event> events) {
        return events.stream()
            .mapToLong(e -> java.time.Duration.between(e.getStart(), e.getEnd()).toHours())
            .sum();
    }
}
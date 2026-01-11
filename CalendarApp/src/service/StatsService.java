package service;

import model.Event;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class StatsService {
    
    public String getBusiestDayOfWeek(List<Event> events) {
        if (events.isEmpty()) return "No Data";

        // Group by DayOfWeek (MONDAY, TUESDAY...) and count them
        Map<String, Long> counts = events.stream()
            .collect(Collectors.groupingBy(
                e -> e.getstartDateTime().getDayOfWeek().toString(), 
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
            .mapToLong(e -> Duration.between(e.getstartDateTime(), e.getendDateTime()).toHours())
            .sum();
    }

    /**
     * Get total number of events
     */
    public long getTotalEventCount(List<Event> events) {
        return events.size();
    }

    /**
     * Get average event duration in minutes
     */
    public double getAverageEventDuration(List<Event> events) {
        if (events.isEmpty()) return 0;
        
        long totalMinutes = events.stream()
            .mapToLong(e -> Duration.between(e.getstartDateTime(), e.getendDateTime()).toMinutes())
            .sum();
        
        return (double) totalMinutes / events.size();
    }

    /**
     * Get longest event duration in hours
     */
    public long getLongestEventDuration(List<Event> events) {
        if (events.isEmpty()) return 0;
        
        return events.stream()
            .mapToLong(e -> Duration.between(e.getstartDateTime(), e.getendDateTime()).toHours())
            .max()
            .orElse(0);
    }

    /**
     * Get shortest event duration in minutes
     */
    public long getShortestEventDuration(List<Event> events) {
        if (events.isEmpty()) return 0;
        
        return events.stream()
            .mapToLong(e -> Duration.between(e.getstartDateTime(), e.getendDateTime()).toMinutes())
            .min()
            .orElse(0);
    }

    /**
     * Get events by month
     */
    public Map<YearMonth, Long> getEventsPerMonth(List<Event> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                e -> YearMonth.from(e.getstartDateTime()),
                Collectors.counting()
            ));
    }

    /**
     * Get busiest month
     */
    public String getBusiestMonth(List<Event> events) {
        if (events.isEmpty()) return "No Data";
        
        Map<YearMonth, Long> monthCounts = getEventsPerMonth(events);
        return monthCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " events)")
            .orElse("No Data");
    }

    /**
     * Get events by day of month
     */
    public Map<Integer, Long> getEventsPerDay(List<Event> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                e -> e.getstartDateTime().getDayOfMonth(),
                Collectors.counting()
            ));
    }

    /**
     * Get event titles count (frequency of each title)
     */
    public Map<String, Long> getEventFrequency(List<Event> events) {
        return events.stream()
            .collect(Collectors.groupingBy(
                Event::getTitle,
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                HashMap::new
            ));
    }

    /**
     * Get most frequent event
     */
    public String getMostFrequentEvent(List<Event> events) {
        if (events.isEmpty()) return "No Data";
        
        Map<String, Long> frequency = getEventFrequency(events);
        return frequency.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " times)")
            .orElse("No Data");
    }

    /**
     * Get total duration in minutes
     */
    public long getTotalDurationInMinutes(List<Event> events) {
        return events.stream()
            .mapToLong(e -> Duration.between(e.getstartDateTime(), e.getendDateTime()).toMinutes())
            .sum();
    }

    /**
     * Get events in a specific date range
     */
    public long getEventCountInDateRange(List<Event> events, LocalDateTime start, LocalDateTime end) {
        return events.stream()
            .filter(e -> {
                LocalDateTime eventStart = e.getstartDateTime();
                return !eventStart.isBefore(start) && !eventStart.isAfter(end);
            })
            .count();
    }

    /**
     * Get average events per day
     */
    public double getAverageEventsPerDay(List<Event> events) {
        if (events.isEmpty()) return 0;
        
        long dayCount = events.stream()
            .map(e -> e.getstartDateTime().toLocalDate())
            .distinct()
            .count();
        
        return dayCount > 0 ? (double) events.size() / dayCount : 0;
    }

    /**
     * Get summary statistics as a formatted string
     */
    public String getStatsSummary(List<Event> events) {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Calendar Statistics ===\n");
        summary.append("Total Events: ").append(getTotalEventCount(events)).append("\n");
        summary.append("Total Duration: ").append(getTotalDurationInHours(events)).append(" hours\n");
        summary.append("Average Duration: ").append(String.format("%.2f", getAverageEventDuration(events))).append(" minutes\n");
        summary.append("Longest Event: ").append(getLongestEventDuration(events)).append(" hours\n");
        summary.append("Busiest Day: ").append(getBusiestDayOfWeek(events)).append("\n");
        summary.append("Busiest Month: ").append(getBusiestMonth(events)).append("\n");
        summary.append("Most Frequent Event: ").append(getMostFrequentEvent(events)).append("\n");
        summary.append("Avg Events Per Day: ").append(String.format("%.2f", getAverageEventsPerDay(events))).append("\n");
        
        return summary.toString();
    }
}
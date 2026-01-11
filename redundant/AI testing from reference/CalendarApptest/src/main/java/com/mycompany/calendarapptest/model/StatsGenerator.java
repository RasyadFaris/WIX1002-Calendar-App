package com.mycompany.calendarapptest.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsGenerator {
    public static void showStats(List<Event> events) {
        System.out.println("=== Event Statistics ===");
        System.out.println("Total Events: " + events.size());
        
        // Example: Busiest Day of Week
        Map<String, Long> dayCounts = events.stream()
            .collect(Collectors.groupingBy(e -> e.getStart().getDayOfWeek().toString(), Collectors.counting()));
            
        dayCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .ifPresent(e -> System.out.println("Busiest Day: " + e.getKey() + " (" + e.getValue() + " events)"));
    }
}
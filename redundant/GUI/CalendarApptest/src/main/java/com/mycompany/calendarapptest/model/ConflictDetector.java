package com.mycompany.calendarapptest.model;

import java.util.List;

public class ConflictDetector {
    public static boolean hasConflict(Event newEvent, List<Event> existingEvents) {
        for (Event e : existingEvents) {
            // Skip self (for updates)
            if (e.getId() == newEvent.getId()) continue;
            
            // Check overlap
            if (newEvent.getStart().isBefore(e.getEnd()) && newEvent.getEnd().isAfter(e.getStart())) {
                System.out.println("Conflict detected with event: " + e.getTitle());
                return true;
            }
        }
        return false;
    }
}
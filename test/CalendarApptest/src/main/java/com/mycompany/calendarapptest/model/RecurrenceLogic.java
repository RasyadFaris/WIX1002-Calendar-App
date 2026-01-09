package com.mycompany.calendarapptest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecurrenceLogic {
    public static List<Event> generateOccurrences(RecurrentEvent re, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        List<Event> occurrences = new ArrayList<>();
        LocalDateTime current = re.getStart();
        int count = 0;
        
        // Parse interval (e.g., "1d", "1w")
        long amount = Long.parseLong(re.getInterval().replaceAll("[^0-9]", ""));
        char unit = re.getInterval().charAt(re.getInterval().length() - 1);

        while (true) {
            // Check termination conditions
            if (re.getRecurrenceEndDate() != null && current.toLocalDate().isAfter(re.getRecurrenceEndDate())) break;
            if (re.getRepeatCount() > 0 && count >= re.getRepeatCount()) break;
            if (current.isAfter(rangeEnd)) break; // Optimization: stop if beyond view range

            // Add if within range
            if (!current.isBefore(rangeStart) && !current.isAfter(rangeEnd)) {
                // Create a temporary event copy for this occurrence
                Event occ = new Event(re.getId(), re.getTitle(), re.getDescription(), current, 
                                      current.plus(java.time.Duration.between(re.getStart(), re.getEnd())));
                occurrences.add(occ);
            }

            // Increment
            if (unit == 'd') current = current.plusDays(amount);
            else if (unit == 'w') current = current.plusWeeks(amount);
            else if (unit == 'm') current = current.plusMonths(amount);
            
            count++;
        }
        return occurrences;
    }
}
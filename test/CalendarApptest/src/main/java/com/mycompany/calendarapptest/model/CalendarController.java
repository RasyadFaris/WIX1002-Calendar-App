package com.mycompany.calendarapptest.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarController {
    private List<Event> events;
    private List<RecurrentEvent> recurrentEvents;

    public CalendarController() {
        try {
            events = FileManager.loadEvents();
            recurrentEvents = FileManager.loadRecurrentRules(events);
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
            events = new ArrayList<>();
            recurrentEvents = new ArrayList<>();
        }
    }

    public void addEvent(Event e) {
        if (ConflictDetector.hasConflict(e, events)) {
            System.out.println("Warning: Event overlaps with an existing event!");
        }
        events.add(e);
        save();
    }
    
    public void addRecurrentEvent(RecurrentEvent re) {
        events.add(re); // Add base part
        recurrentEvents.add(re); // Add rule part
        save();
    }

    public void deleteEvent(int id) {
        events.removeIf(e -> e.getId() == id);
        recurrentEvents.removeIf(re -> re.getId() == id);
        save();
    }

    // Get all events (including expanded recurring ones) for a specific time range
    public List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) {
        List<Event> result = new ArrayList<>();
        
        // 1. Add normal events
        for (Event e : events) {
            boolean isRecurrent = recurrentEvents.stream().anyMatch(re -> re.getId() == e.getId());
            if (!isRecurrent && !e.getStart().isAfter(end) && !e.getEnd().isBefore(start)) {
                result.add(e);
            }
        }

        // 2. Expand recurring events
        for (RecurrentEvent re : recurrentEvents) {
            result.addAll(RecurrenceLogic.generateOccurrences(re, start, end));
        }

        result.sort(Comparator.comparing(Event::getStart));
        return result;
    }

    public int getNextId() {
        return events.stream().mapToInt(Event::getId).max().orElse(0) + 1;
    }

    private void save() {
        try {
            FileManager.saveEvents(events, recurrentEvents);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
    
    public List<Event> getAllEvents() { return events; }
}
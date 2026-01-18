package service;

import model.Event;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static final String EVENT_FILE = "data/event.csv"; 
    private final ArrayList<Event> events = new ArrayList<>();

    public EventManager() {
        File file = new File(EVENT_FILE);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        loadEvents();
    }

    public void loadEvents() {
        events.clear();
        File file = new File(EVENT_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; 

                String[] p = line.split(",");
                if (p.length < 5) continue; 

                int id = Integer.parseInt(p[0].trim());
                String title = p[1].trim();
                String desc = p[2].trim();
                LocalDateTime start = LocalDateTime.parse(p[3].trim());
                LocalDateTime end = LocalDateTime.parse(p[4].trim());
                
                int reminder = 0;
                if (p.length >= 6) {
                    try {
                        reminder = Integer.parseInt(p[5].trim());
                    } catch (NumberFormatException e) {
                        reminder = 0;
                    }
                }

                events.add(new Event(id, title, desc, start, end, reminder));
            }
        } catch (IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    public void saveEvents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.println("eventId,title,description,startDateTime,endDateTime,reminderMinutes");

            for (Event e : events) {
                // Use the toCSV() method in Event class
                pw.println(e.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving events: " + e.getMessage());
        }
    }

    public int getNextEventId() {
        return events.stream().mapToInt(Event::getId).max().orElse(0) + 1;
    }

    public void addEvent(Event event) {
        events.add(event);
        saveEvents();
    }

    public List<Event> getAllEvent() {
        return events;
    }

    public Event findEventById(int id) {
        return events.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public boolean deleteEvent(int id) {
        boolean removed = events.removeIf(e -> e.getId() == id);
        if (removed) saveEvents();
        return removed;
    }

    public boolean updateEvent(Event updatedEvent) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId() == updatedEvent.getId()) {
                events.set(i, updatedEvent);
                saveEvents();
                return true;
            }
        }
        return false;
    }
}
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
        // Ensure the data directory exists before trying to load
        File file = new File(EVENT_FILE);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        
        loadEvents();
    }

    // loadEvents
    public void loadEvents() {
        events.clear();
        File file = new File(EVENT_FILE);
        
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines

                String[] p = line.split(",");
                if (p.length < 5) continue; // basic validation

                events.add(new Event(
                        Integer.parseInt(p[0].trim()),
                        p[1].trim(),
                        p[2].trim(),
                        LocalDateTime.parse(p[3].trim()),
                        LocalDateTime.parse(p[4].trim())
                ));
            }
        } catch (IOException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
    }

    // saveEvents
    public void saveEvents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.println("eventId,title,description,startDateTime,endDateTime");

            for (Event e : events) {
                pw.println(String.format("%d,%s,%s,%s,%s",
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getstartDateTime(),
                        e.getendDateTime()
                ));
            }
        } catch (IOException e) {
            System.out.println("Error saving events: " + e.getMessage());
        }
    }

    // getNextEventId ---
    public int getNextEventId() {
        return events.stream()
                .mapToInt(Event::getId)
                .max()
                .orElse(0) + 1;
    }

    // addEvent ---
    public void addEvent(Event event) {
        events.add(event);
        saveEvents();
    }

    // getAllEvent ---
    public List<Event> getAllEvent() {
        return events;
    }

    // findEventById ---
    public Event findEventById(int id) {
        return events.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // deleteEvent ---
    public boolean deleteEvent(int id) {
        boolean removed = events.removeIf(e -> e.getId() == id);
        if (removed) {
            saveEvents();
        }
        return removed;
    }

    // updateEvent ---
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
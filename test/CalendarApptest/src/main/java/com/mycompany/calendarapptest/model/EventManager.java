package com.mycompany.calendarapptest.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventManager {

    private static final String EVENT_FILE = "src/data/event.csv";
    private ArrayList<Event> events = new ArrayList<>();

    public EventManager() {
        loadEvents();
    }

    public void loadEvents() {
        events.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String desc = parts[2];
                LocalDateTime start = LocalDateTime.parse(parts[3]);
                LocalDateTime end = LocalDateTime.parse(parts[4]);

                events.add(new Event(id, title, desc, start, end));
            }
        } catch (IOException e) {
            System.out.println("No existing events found.");
        }
    }

    public void saveEvents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.println("eventId,title,description,startDateTime,endDateTime");
            for (Event e : events) {
                pw.println(e.getId() + "," +
                           e.getTitle() + "," +
                           e.getDescription() + "," +
                           e.getStart() + "," +
                           e.getEnd());
            }
        } catch (IOException e) {
            System.out.println("Error saving events.");
        }
    }

    public void addEvent(Event e) {
        events.add(e);
        saveEvents();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}

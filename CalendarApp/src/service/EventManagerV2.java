package com.mycompany.calendarapptest.model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventManagerV2 {

    private static final String EVENT_FILE = "src/data/event.csv";
    private final ArrayList<Event> events = new ArrayList<>();

    public EventManagerV2() {
        loadEvents();
    }

    public void loadEvents() {
        events.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                events.add(new Event(
                        Integer.parseInt(p[0]),
                        p[1],
                        p[2],
                        LocalDateTime.parse(p[3]),
                        LocalDateTime.parse(p[4])
                ));
            }
        } catch (IOException e) {
            System.out.println("No existing events found.");
        }
    }

    public void saveEvents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.println("eventId,title,description,startDateTime,endDateTime");

            for (Event e : events) {
                pw.println(
                        e.getId() + "," +
                        e.getTitle() + "," +
                        e.getDescription() + "," +
                        e.getStart() + "," +
                        e.getEnd()
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving events.");
        }
    }

    public void addEvent(Event event) {
        events.add(event);
        saveEvents();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}

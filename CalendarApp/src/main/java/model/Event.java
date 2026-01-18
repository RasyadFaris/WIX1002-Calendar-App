package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    protected int eventId;
    protected String title;
    protected String description;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    protected int reminderMinutes; 

    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // constructor
    public Event(int eventId, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, int reminderMinutes){
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reminderMinutes = reminderMinutes;
    }

    // Overloaded constructor for backward compatibility (defaults reminder to 0)
    public Event(int eventId, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this(eventId, title, description, startDateTime, endDateTime, 0);
    }
    
    public int getId (){ return eventId; }
    public String getTitle (){ return title; }
    public String getDescription (){ return description; }
    public LocalDateTime getstartDateTime () { return startDateTime; }
    public LocalDateTime getendDateTime () { return endDateTime; }
    
    public int getReminderMinutes() { return reminderMinutes; }
    public void setReminderMinutes(int min) { this.reminderMinutes = min; }

    public String toCSV() {
        // Format: eventId,title,description,start,end,reminderMinutes
        return String.format("%d,%s,%s,%s,%s,%d",
                eventId, title, description, 
                startDateTime.format(DATETIME_FORMAT), 
                endDateTime.format(DATETIME_FORMAT),
                reminderMinutes);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s: %s (%s)", eventId, title, description, startDateTime);
    }
}
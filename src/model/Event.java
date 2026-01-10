package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    protected int eventID;
    protected String title;
    protected String description;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;

    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Event(int eventID, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.eventID = eventID;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
    public int getId (){
        return eventID;
    }
    public String getTitle (){
        return title;
    }
    public String getDescription (){
        return description;
    }
    public LocalDateTime get startDateTime () {
        return startDateTime;
    }
    public LocalDateTime get endDateTime () {
        return endDateTime;
    }

    public String toCSV() {
        // Format: eventId, title, description, startDateTime, endDateTime
        return String.format("%d,%s,%s,%s,%s",
                eventId, title, description, startDateTime.format(DATETIME_FORMAT), endDateTime.format(DATETIME_FORMAT));
    }

    @Override
    public String toString() {
        return String.format("[%d] %s: %s (%s to %s)", 
            eventId, title, description, startDateTime, endDateTime);
    }

}

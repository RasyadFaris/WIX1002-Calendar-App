import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    protected int eventId;
    protected String title;
    protected String description;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    
    public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Event(int eventId, String title, String description, LocalDateTime start, LocalDateTime end) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startDateTime = start;
        this.endDateTime = end;
    }

    public int getId() { return eventId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStart() { return startDateTime; }
    public LocalDateTime getEnd() { return endDateTime; }

    public String toCSV() {
        // Format: eventId, title, description, startDateTime, endDateTime
        return String.format("%d,%s,%s,%s,%s",
                eventId, title, description, startDateTime.format(DATETIME_FMT), endDateTime.format(DATETIME_FMT));
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s: %s (%s to %s)", 
            eventId, title, description, startDateTime, endDateTime);
    }
}
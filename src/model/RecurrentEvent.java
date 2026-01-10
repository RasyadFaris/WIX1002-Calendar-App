package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.w3c.dom.events.Event;

public class RecurrentEvent {
    private String interval; //e.g. "1d" "1w" "1m"
    private int repeatCount; // 0 if using endDate
    private LocalDate endDate; // skips if using count

    public RecurrentEvent (Event event, String interval, int repeatCount, LocalDate endDate) {
        super(event.getId(), event.getTitle(), event.getDescription(), event.getStart(), event.getEnd());
        this.interval = interval;
        this.repeatCount = repeatCount;
        this.endDate = endDate;
    }

    public String getInterval() { 
        return interval;
    }
    public int getRepeatCount() { 
        return repeatCount;
    }
    public LocalDate getRecurrenceEndDate() { 
        return endDate;
    }

    public String recurrenceToCSV() {
        String endStr = (endDate != null) ? endDate.toString() : "0";
        return String.format("%d,%s,%d,%s", eventId, interval, repeatCount, endStr);
    }
}

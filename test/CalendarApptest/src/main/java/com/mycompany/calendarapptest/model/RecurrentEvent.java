package com.mycompany.calendarapptest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecurrentEvent extends Event {
    private String interval; // e.g., "1d", "1w"
    private int repeatCount; // 0 if using endDate
    private LocalDate endDate; // null if using count

    public RecurrentEvent(Event e, String interval, int repeatCount, LocalDate endDate) {
        super(e.getId(), e.getTitle(), e.getDescription(), e.getStart(), e.getEnd());
        this.interval = interval;
        this.repeatCount = repeatCount;
        this.endDate = endDate;
    }

    public String getInterval() { return interval; }
    public int getRepeatCount() { return repeatCount; }
    public LocalDate getRecurrenceEndDate() { return endDate; }

    public String toRecurrentCSV() {
        // Format: eventId, recurrentInterval, recurrentTimes, recurrentEndDate
        // Note: PDF says put 0 if not used.
        String endStr = (endDate != null) ? endDate.toString() : "0";
        return String.format("%d,%s,%d,%s", eventId, interval, repeatCount, endStr);
    }
}
package service;

import model.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService {
    public List<Event> getUpcomingEvents(List<Event> allEvents) {
        LocalDateTime now = LocalDateTime.now();

        return allEvents.stream()
        .filter(event -> {
            // Use the event's custom reminder time, or default to 24h (1440 mins) if 0
            int minutesBefore = event.getReminderMinutes() > 0 ? event.getReminderMinutes() : 1440;
            
            LocalDateTime notifyTime = event.getstartDateTime().minusMinutes(minutesBefore);
            
            // Trigger if NOW is past the notification time, but before the actual event start
            return now.isAfter(notifyTime) && now.isBefore(event.getstartDateTime());
        })
        .collect(Collectors.toList());
    }
}
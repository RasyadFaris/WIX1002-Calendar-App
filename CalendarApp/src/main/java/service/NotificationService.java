package service;

import model.Event;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;;

public class NotificationService {
    public List<Event> getUpcomingEvents(List<Event> allEvents) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plus(24, ChronoUnit.HOURS);

        return allEvents.stream()
        .filter(event -> event.getstartDateTime().isAfter(now) && event.getstartDateTime().isBefore(tomorrow))
        .collect(Collectors.toList());
    }
}

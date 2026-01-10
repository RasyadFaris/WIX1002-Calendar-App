package service; 

import model.Event;
import model.RecurrentEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecurrenceManager {

    public static List<Event> generateOccurrences(RecurrentEvent re) {
        List<Event> occurrences = new ArrayList<>();

        LocalDateTime currentStart = re.getstartDateTime();
        LocalDateTime currentEnd = re.getendDateTime();

        int count = 0;

        // Parse interval, e.g. "1d", "2w"
        String interval = re.getInterval();
        int amount = Integer.parseInt(interval.substring(0, interval.length() - 1));
        char unit = interval.charAt(interval.length() - 1);

        while (true) {

            // Stop by repeat count
            if (re.getRepeatCount() > 0 && count >= re.getRepeatCount()) {
                break;
            }

            // Stop by end date
            if (re.getRecurrenceEndDate() != null &&
                currentStart.toLocalDate().isAfter(re.getRecurrenceEndDate())) {
                break;
            }

            // Add occurrence
            Event occ = new Event(
                re.getId(),
                re.getTitle(),
                re.getDescription(),
                currentStart,
                currentEnd
            );
            occurrences.add(occ);

            // Move to next occurrence
            if (unit == 'd') {
                currentStart = currentStart.plusDays(amount);
                currentEnd = currentEnd.plusDays(amount);
            } else if (unit == 'w') {
                currentStart = currentStart.plusWeeks(amount);
                currentEnd = currentEnd.plusWeeks(amount);
            } else if (unit == 'm') {
                currentStart = currentStart.plusMonths(amount);
                currentEnd = currentEnd.plusMonths(amount);
            }

            count++;
        }

        return occurrences;
    }
}

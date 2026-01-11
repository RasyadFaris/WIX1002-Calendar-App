package service; 

import model.Event;
import java.util.List;

public class ConflictService {

    public static boolean isClashing(Event newEvent, List<Event> existingEvents) {

        for (Event e : existingEvents) {

            // Skip checking against itself (useful during edit)
            if (e.getId() == newEvent.getId()) {
                continue;
            }

            // Overlap condition
            boolean overlap =
                newEvent.getstartDateTime().isBefore(e.getendDateTime()) &&
                newEvent.getendDateTime().isAfter(e.getstartDateTime());

            if (overlap) {
                return true;
            }
        }

        return false;
    }
}
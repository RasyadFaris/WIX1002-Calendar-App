package service; 

import model.Event;
import model.RecurrentEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecurrenceManager {

    private static final String RECURRENT_FILE = "data/recurrent.csv";
    private final List<RecurrentEvent> recurrentEvents = new ArrayList<>();
    
    public RecurrenceManager(List<Event> baseEvents) {
        loadRecurrentEvents(baseEvents);
    }

    public void loadRecurrentEvents(List<Event> baseEvents) {
        recurrentEvents.clear();
        File file = new File(RECURRENT_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                // Format: eventId, interval, count, endDate
                if (p.length < 4) continue;

                int eventId = Integer.parseInt(p[0].trim());
                String interval = p[1].trim();
                int count = Integer.parseInt(p[2].trim());
                String dateStr = p[3].trim();
                LocalDate endDate = dateStr.equals("0") ? null : LocalDate.parse(dateStr);

                // Find the base event to attach this rule to
                Event base = baseEvents.stream()
                        .filter(e -> e.getId() == eventId)
                        .findFirst()
                        .orElse(null);

                if (base != null) {
                    RecurrentEvent re = new RecurrentEvent(base, interval, count, endDate);
                    recurrentEvents.add(re);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading recurrent rules: " + e.getMessage());
        }
    }

    public void saveRecurrentEvents() {
        // Ensure directory exists
        File file = new File(RECURRENT_FILE);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("eventId,recurrentInterval,recurrentTimes,recurrentEndDate");

            for (RecurrentEvent re : recurrentEvents) {
                pw.println(re.recurrenceToCSV());
            }
        } catch (IOException e) {
            System.out.println("Error saving recurrent rules.");
        }
    }

    public void addRecurrentEvent(RecurrentEvent re) {
        recurrentEvents.add(re);
        saveRecurrentEvents();
    }

    public static List<Event> generateOccurrences(RecurrentEvent re) {
        List<Event> occurrences = new ArrayList<>();
        LocalDateTime currentStart = re.getstartDateTime();
        LocalDateTime currentEnd = re.getendDateTime();
        int count = 0;

        String interval = re.getInterval();
        // Basic validation to prevent crash on empty interval
        if (interval == null || interval.length() < 2) return occurrences; 

        int amount = Integer.parseInt(interval.substring(0, interval.length() - 1));
        char unit = interval.charAt(interval.length() - 1);

        while (true) {
            if (re.getRepeatCount() > 0 && count >= re.getRepeatCount()) break;
            if (re.getRecurrenceEndDate() != null &&
                currentStart.toLocalDate().isAfter(re.getRecurrenceEndDate())) break;

            // Safety break to prevent infinite loops if logic fails
            if (count > 500) break; 

            Event occ = new Event(re.getId(), re.getTitle(), re.getDescription(), currentStart, currentEnd);
            occurrences.add(occ);

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

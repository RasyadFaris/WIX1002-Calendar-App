import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String EVENT_FILE = "data/event.csv";
    private static final String RECURRENT_FILE = "data/recurrent.csv";

    public static List<Event> loadEvents() throws IOException {
        List<Event> events = new ArrayList<>();
        File file = new File(EVENT_FILE);
        if (!file.exists()) return events;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("eventId")) continue; // Skip header
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                
                Event e = new Event(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    LocalDateTime.parse(parts[3].trim()),
                    LocalDateTime.parse(parts[4].trim())
                );
                events.add(e);
            }
        }
        return events;
    }

    public static List<RecurrentEvent> loadRecurrentRules(List<Event> allEvents) throws IOException {
        List<RecurrentEvent> recEvents = new ArrayList<>();
        File file = new File(RECURRENT_FILE);
        if (!file.exists()) return recEvents;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("eventId")) continue;
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                
                // Find matching base event
                Event base = allEvents.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
                if (base != null) {
                    String interval = parts[1].trim();
                    int count = Integer.parseInt(parts[2].trim());
                    String dateStr = parts[3].trim();
                    LocalDate endDate = dateStr.equals("0") ? null : LocalDate.parse(dateStr);
                    
                    recEvents.add(new RecurrentEvent(base, interval, count, endDate));
                }
            }
        }
        return recEvents;
    }

    public static void saveEvents(List<Event> events, List<RecurrentEvent> recEvents) throws IOException {
        // Save Basic Events
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.println("eventId, title, description, startDateTime, endDateTime");
            for (Event e : events) pw.println(e.toCSV());
        }

        // Save Recurrent Rules
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECURRENT_FILE))) {
            pw.println("eventId, recurrentInterval, recurrentTimes, recurrentEndDate");
            for (RecurrentEvent re : recEvents) pw.println(re.toRecurrentCSV());
        }
    }
}
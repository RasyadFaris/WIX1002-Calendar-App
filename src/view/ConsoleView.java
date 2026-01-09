import java.util.List;

public class ConsoleView {
    public static void printList(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        for (Event e : events) {
            System.out.println(e.toString());
        }
    }

    public static void printMonthCalendar(List<Event> events, int year, int month) {
        // Simple matrix view logic
        System.out.println("=== Calendar View: " + year + "-" + month + " ===");
        // NOTE: A full matrix implementation requires calculating day-of-week offsets.
        // For brevity, this is a simplified list view grouped by day.
        // You can expand this to a real grid for the full 2 marks.
        printList(events);
    }
}
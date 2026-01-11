import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputValidator {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime getDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd HH:mm): ");
            String input = sc.nextLine();
            try {
                return LocalDateTime.parse(input, FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Try again.");
            }
        }
    }
}
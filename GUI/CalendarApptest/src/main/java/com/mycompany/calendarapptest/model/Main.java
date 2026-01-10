package com.mycompany.calendarapptest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    private static CalendarController controller = new CalendarController();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Calendar App");
        
        // Reminder check on launch
        LocalDateTime now = LocalDateTime.now();
        var upcoming = controller.getEventsInRange(now, now.plusHours(24));
        if (!upcoming.isEmpty()) {
            System.out.println("You have " + upcoming.size() + " events in the next 24 hours!");
        }

        boolean running = true;
        while (running) {
            System.out.println("\n1. Add Event\n2. View Events (List)\n3. Search\n4. Delete\n5. Backup\n6. Stats\n7. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    addEventFlow();
                    break;
                case "2":
                    viewEventsFlow();
                    break;
                case "3":
                    System.out.print("Enter search term: ");
                    ConsoleView.printList(SearchEngine.searchByTitle(controller.getAllEvents(), sc.nextLine()));
                    break;
                case "4":
                    System.out.print("Enter Event ID to delete: ");
                    try {
                        controller.deleteEvent(Integer.parseInt(sc.nextLine()));
                        System.out.println("Deleted.");
                    } catch (NumberFormatException e) { System.out.println("Invalid ID."); }
                    break;
                case "5":
                    System.out.print("Enter backup folder path (e.g. backups/mybackup): ");
                    try {
                        BackupManager.backup(sc.nextLine());
                    } catch (Exception e) { System.out.println("Backup failed: " + e.getMessage()); }
                    break;
                case "6":
                    StatsGenerator.showStats(controller.getAllEvents());
                    break;
                case "7":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void addEventFlow() {
        System.out.print("Title: ");
        String title = sc.nextLine();
        System.out.print("Description: ");
        String desc = sc.nextLine();
        LocalDateTime start = InputValidator.getDate(sc, "Start");
        LocalDateTime end = InputValidator.getDate(sc, "End");

        int id = controller.getNextId();
        
        System.out.print("Is this recurring? (y/n): ");
        if (sc.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Interval (e.g., 1d, 1w): ");
            String interval = sc.nextLine();
            System.out.print("Repeat count (0 if using date): ");
            int count = Integer.parseInt(sc.nextLine());
            RecurrentEvent re = new RecurrentEvent(new Event(id, title, desc, start, end), interval, count, null);
            controller.addRecurrentEvent(re);
        } else {
            controller.addEvent(new Event(id, title, desc, start, end));
        }
        System.out.println("Event added!");
    }

    private static void viewEventsFlow() {
        System.out.println("Viewing events for next 30 days...");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(30);
        ConsoleView.printList(controller.getEventsInRange(start, end));
    }
}
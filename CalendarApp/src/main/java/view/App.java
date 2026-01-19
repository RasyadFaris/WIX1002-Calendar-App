package view;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Event;
import model.RecurrentEvent;
import service.BackupService; 
import service.ConflictService;
import service.EventManager;
import service.NotificationService;
import service.RecurrenceManager;
import service.SearchService;
import service.StatsService;

public class App extends Application {

    private final EventManager eventManager = new EventManager();
    private final RecurrenceManager recurrenceManager = new RecurrenceManager(eventManager.getAllEvent());
    private final StatsService statsService = new StatsService();
    private final NotificationService notificationService = new NotificationService();

    private BorderPane menuPane = new BorderPane();    
    private LocalDate watchDate = LocalDate.now();

    @Override
    public void start(Stage mainStage) {
        
        // Notification / Reminder Logic
        List<Event> upcoming = notificationService.getUpcomingEvents(eventManager.getAllEvent());
        
        if (!upcoming.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reminder");
            alert.setHeaderText("Upcoming Events");
            
            StringBuilder message = new StringBuilder();
            LocalDateTime now = LocalDateTime.now();

            for (Event e : upcoming) {
                // Countdown Logic (Hours, Minutes, Seconds)
                Duration diff = Duration.between(now, e.getstartDateTime());
                if (diff.isNegative()) diff = Duration.ZERO;

                long hours = diff.toHours();
                long minutes = diff.toMinutesPart(); 
                long seconds = diff.toSecondsPart(); 

                message.append("• ").append(e.getTitle())
                       .append(String.format("\n   Starts in: %02d hours, %02d minutes, %02d seconds\n", hours, minutes, seconds));
            }
            alert.setContentText(message.toString());
            alert.showAndWait();
        }

        // Layout
        VBox menuBox = new VBox();
        HBox headerBox = new HBox(15);
        headerBox.setPadding(new Insets(10,15,10,15));
        headerBox.setStyle("-fx-background-color: #00c3ff;");
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Button menuButton = new Button("≡ Menu");
        menuButton.setStyle("-fx-background-color: #ffffff; -fx-font-weight: bold;");
        Label calendarLabel = new Label("WIX1002 - Calendar App");
        calendarLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        headerBox.getChildren().addAll(menuButton, calendarLabel);

        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(10,15,10,15));
        optionsBox.setStyle("-fx-background-color: #80d9f4;");
        optionsBox.setAlignment(Pos.CENTER);
        String buttonStyle = "-fx-background-color: #ffffff; -fx-min-width: 150px;";

        Button calendarButton = new Button("View Calendar");
        calendarButton.setStyle(buttonStyle);
        calendarButton.setOnAction(e -> {
            menuPane.setCenter(createCalendarView());
            if(menuBox.getChildren().contains(optionsBox)) menuBox.getChildren().remove(optionsBox);
        });

        Button addEventBtn = new Button("Add Event");
        addEventBtn.setStyle(buttonStyle);
        addEventBtn.setOnAction(e -> {
            menuPane.setCenter(createAddEventPage(null));
            if(menuBox.getChildren().contains(optionsBox)) menuBox.getChildren().remove(optionsBox);
        });

        Button viewEventsBtn = new Button("View Events");
        viewEventsBtn.setStyle(buttonStyle);
        viewEventsBtn.setOnAction(e-> {
            menuPane.setCenter(createViewEventsPage());
            if(menuBox.getChildren().contains(optionsBox)) menuBox.getChildren().remove(optionsBox);
        });
        
        Button statsBtn = new Button("Statistics");
        statsBtn.setStyle(buttonStyle);
        statsBtn.setOnAction(e -> {
            menuPane.setCenter(createStatsPage());
            if(menuBox.getChildren().contains(optionsBox)) menuBox.getChildren().remove(optionsBox);
        });

        Button settingsBtn = new Button("Settings");
        settingsBtn.setStyle(buttonStyle);
        settingsBtn.setOnAction(e-> {
            menuPane.setCenter(createSettingsPage(mainStage));
            if(menuBox.getChildren().contains(optionsBox)) menuBox.getChildren().remove(optionsBox);
        });

        optionsBox.getChildren().addAll(calendarButton, addEventBtn, viewEventsBtn, statsBtn, settingsBtn);
        menuBox.getChildren().add(headerBox);

        menuButton.setOnAction(e-> {
            if (menuBox.getChildren().contains(optionsBox)) menuBox.getChildren().remove(optionsBox);
            else menuBox.getChildren().add(optionsBox);
        });

        menuPane.setTop(menuBox);
        menuPane.setCenter(createCalendarView()); 

        Scene scene = new Scene(menuPane, 800, 600);
        mainStage.setTitle("Calendar Application");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private VBox createCalendarView() {
    // Layout Setup
    VBox calendarLayout = new VBox(15);
    calendarLayout.setPadding(new Insets(30)); 
    calendarLayout.setAlignment(Pos.CENTER);
    calendarLayout.setStyle("-fx-background-color: #f0f8ff;"); 

    // Header: Navigation and Month Label
    HBox header = new HBox(20);
    header.setAlignment(Pos.CENTER);
    Button btnPrev = new Button("<");
    Button btnNext = new Button(">");
    Label lblMonth = new Label(watchDate.getMonth().toString() + " " + watchDate.getYear());
    lblMonth.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    btnPrev.setOnAction(e -> { watchDate = watchDate.minusMonths(1); menuPane.setCenter(createCalendarView()); });
    btnNext.setOnAction(e -> { watchDate = watchDate.plusMonths(1); menuPane.setCenter(createCalendarView()); });
    header.getChildren().addAll(btnPrev, lblMonth, btnNext);

    // Grid: Day Header and Date Logic
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(5); grid.setVgap(5);

    String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    for (int i = 0; i < 7; i++) {
        Label dayLabel = new Label(days[i]);
        dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
        dayLabel.setMinWidth(100);
        grid.add(dayLabel, i, 0);
    }

    YearMonth ym = YearMonth.of(watchDate.getYear(), watchDate.getMonth());
    int totalDaysInMonth = ym.lengthOfMonth();
    int startOffset = watchDate.withDayOfMonth(1).getDayOfWeek().getValue();
    if (startOffset == 7) startOffset = 0;

    // Day Cells and Event Logic
    for (int day = 1; day <= totalDaysInMonth; day++) {
        int totalIndex = startOffset + day - 1;
        int col = totalIndex % 7;
        int row = (totalIndex / 7) + 1;

        VBox dayBox = new VBox(2);
        dayBox.setAlignment(Pos.TOP_LEFT);
        dayBox.setStyle("-fx-border-color: lightgrey; -fx-padding: 2; -fx-min-width: 100; -fx-min-height: 60; -fx-background-color: white;");
        dayBox.getChildren().add(new Label(String.valueOf(day)));

        LocalDate gridDate = watchDate.withDayOfMonth(day);
        for (Event e : eventManager.getAllEvent()) {
            if (e instanceof RecurrentEvent) {
                List<Event> occurrences = RecurrenceManager.generateOccurrences((RecurrentEvent) e);
                for (Event occ : occurrences) {
                    if (occ.getstartDateTime().toLocalDate().equals(gridDate)) addEventLabel(dayBox, occ);
                }
            } else if (e.getstartDateTime().toLocalDate().equals(gridDate)) {
                addEventLabel(dayBox, e);
            }
        }
        grid.add(dayBox, col, row);
    }

    // Trigger CLI Output
    printCLI();

    calendarLayout.getChildren().addAll(header, grid);
    return calendarLayout;
}

    private void printCLI() {
        // Header
        System.out.println("\n" + watchDate.getMonth().toString().substring(0, 3) + " " + watchDate.getYear());
        System.out.println("Su  Mo  Tu  We  Th  Fr  Sa");
    
        // Grid Alignment Logic
        YearMonth ym = YearMonth.of(watchDate.getYear(), watchDate.getMonth());
        int startOffset = watchDate.withDayOfMonth(1).getDayOfWeek().getValue();
        if (startOffset == 7) startOffset = 0; 
        
        for (int i = 0; i < startOffset; i++) {
            System.out.print("    ");
        }
    
        List<String> eventDetails = new ArrayList<>();
    
        // Main Output Loop
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate current = watchDate.withDayOfMonth(day);
            List<Event> dayEvents = new ArrayList<>();
            
            for (Event e : eventManager.getAllEvent()) {
                if (e instanceof RecurrentEvent) {
                    List<Event> occurrences = RecurrenceManager.generateOccurrences((RecurrentEvent) e);
                    for (Event occ : occurrences) {
                        if (occ.getstartDateTime().toLocalDate().equals(current)) dayEvents.add(occ);
                    }
                } else if (e.getstartDateTime().toLocalDate().equals(current)) {
                    dayEvents.add(e);
                }
            }
    
            // Marker and Row Formatting
            String marker = dayEvents.isEmpty() ? " " : "*";
            System.out.printf("%2d%s", day, marker);
    
            for (Event e : dayEvents) {
                eventDetails.add("* " + day + ": " + e.getTitle() + " (" + e.getstartDateTime().toLocalTime() + ")");
            }
    
            if ((startOffset + day) % 7 == 0) {
                System.out.println();
            } else {
                System.out.print(" ");
            }
        }
        
        // Bottom Details List
        System.out.println("\n");
        if (eventDetails.isEmpty()) {
            System.out.println("No events scheduled.");
        } else {
            for (String detail : eventDetails) {
                System.out.println(detail);
            }
        }
        System.out.println("------------------------------\n");
    }


    private void addEventLabel(VBox container, Event e) {
        Label eventLabel = new Label("• " + e.getTitle());
        eventLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #007bbd;");
        container.getChildren().add(eventLabel);
    }
   
    private VBox createAddEventPage(Event eventToEdit) {
        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(30));
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setStyle("-fx-background-color: #f0f8ff;");

        boolean isEditing = (eventToEdit != null);
        Label titleLabel = new Label(isEditing ? "Edit Event" : "Create New Event");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nameInput = new TextField(); nameInput.setPromptText("Event Title"); nameInput.setMaxWidth(300);
        TextField descInput = new TextField(); descInput.setPromptText("Description"); descInput.setMaxWidth(300);
        DatePicker dateInput = new DatePicker(LocalDate.now()); dateInput.setMaxWidth(300);

        HBox timeBox = new HBox(10); timeBox.setAlignment(Pos.CENTER);
        TextField startTimeInput = new TextField("09:00"); startTimeInput.setMaxWidth(100);
        TextField endTimeInput = new TextField("10:00"); endTimeInput.setMaxWidth(100);
        timeBox.getChildren().addAll(new Label("Time:"), startTimeInput, new Label("to"), endTimeInput);

        // Reminder Input
        HBox reminderBox = new HBox(10); reminderBox.setAlignment(Pos.CENTER);
        TextField reminderInput = new TextField("0"); reminderInput.setMaxWidth(80);
        reminderBox.getChildren().addAll(new Label("Remind me (mins before):"), reminderInput);

        CheckBox recurCheckBox = new CheckBox("Repeat this event?");
        VBox recurOptions = new VBox(10); recurOptions.setAlignment(Pos.CENTER); recurOptions.setVisible(false);
        ComboBox<String> intervalBox = new ComboBox<>();
        intervalBox.getItems().addAll("Daily (1d)", "Weekly (1w)", "Monthly (1m)");
        intervalBox.setValue("Weekly (1w)"); 
        TextField repeatCountInput = new TextField("0"); repeatCountInput.setPromptText("Repeat Count"); repeatCountInput.setMaxWidth(200);
        recurOptions.getChildren().addAll(new Label("Frequency:"), intervalBox, new Label("Repeat Counts:"), repeatCountInput);

        recurCheckBox.setOnAction(e -> recurOptions.setVisible(recurCheckBox.isSelected()));

        if (isEditing) {
            nameInput.setText(eventToEdit.getTitle());
            descInput.setText(eventToEdit.getDescription());
            dateInput.setValue(eventToEdit.getstartDateTime().toLocalDate());
            startTimeInput.setText(eventToEdit.getstartDateTime().toLocalTime().toString());
            endTimeInput.setText(eventToEdit.getendDateTime().toLocalTime().toString());
            reminderInput.setText(String.valueOf(eventToEdit.getReminderMinutes())); 

            if (eventToEdit instanceof RecurrentEvent) {
                RecurrentEvent re = (RecurrentEvent) eventToEdit;
                recurCheckBox.setSelected(true); recurOptions.setVisible(true);
                repeatCountInput.setText(String.valueOf(re.getRepeatCount()));
                if (re.getInterval().equals("1d")) intervalBox.setValue("Daily (1d)");
                else if (re.getInterval().equals("1w")) intervalBox.setValue("Weekly (1w)");
                else if (re.getInterval().equals("1m")) intervalBox.setValue("Monthly (1m)");
            }
        }

        Label statusLabel = new Label("");
        Button saveBtn = new Button(isEditing ? "Update Event" : "Save Event");
        saveBtn.setStyle("-fx-background-color: #00c3ff; -fx-text-fill: white; -fx-font-weight: bold;");

        saveBtn.setOnAction(e -> {
            try {
                LocalDate date = dateInput.getValue();
                LocalTime startT = LocalTime.parse(startTimeInput.getText());
                LocalTime endT = LocalTime.parse(endTimeInput.getText());
                LocalDateTime startDT = LocalDateTime.of(date, startT);
                LocalDateTime endDT = LocalDateTime.of(date, endT);
                int reminderMins = Integer.parseInt(reminderInput.getText()); 

                int eventId = isEditing ? eventToEdit.getId() : eventManager.getNextEventId();
                Event finalEvent = new Event(eventId, nameInput.getText(), descInput.getText(), startDT, endDT, reminderMins);
                
                if (!isEditing && ConflictService.isClashing(finalEvent, eventManager.getAllEvent())) {
                    statusLabel.setText("Time Conflict!"); statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }

                if (recurCheckBox.isSelected()) {
                    String selectedStr = intervalBox.getValue();
                    String code = selectedStr.substring(selectedStr.indexOf("(") + 1, selectedStr.indexOf(")")); 
                    int count = Integer.parseInt(repeatCountInput.getText());
                    RecurrentEvent re = new RecurrentEvent(finalEvent, code, count, null);
                    
                    if (isEditing) eventManager.updateEvent(re); 
                    else eventManager.addEvent(re);
                    recurrenceManager.addRecurrentEvent(re); 

                } else {
                    if (isEditing) eventManager.updateEvent(finalEvent); 
                    else eventManager.addEvent(finalEvent);
                    
                    // Remove accidental recurrences if switching to Normal
                    recurrenceManager.removeRecurrentEvent(finalEvent.getId());
                }
                
                statusLabel.setText("Event Saved!");
                statusLabel.setStyle("-fx-text-fill: green;");
                if (!isEditing) { nameInput.clear(); descInput.clear(); }

            } catch (Exception ex) {
                statusLabel.setText("Error: Check inputs."); statusLabel.setStyle("-fx-text-fill: red;");
                ex.printStackTrace();
            }
        });

        formLayout.getChildren().addAll(titleLabel, nameInput, descInput, dateInput, timeBox, reminderBox, recurCheckBox, recurOptions, saveBtn, statusLabel);
        return formLayout;
    }

    private VBox createViewEventsPage() {
        VBox eventslayout = new VBox(15);
        eventslayout.setPadding(new Insets(30)); eventslayout.setAlignment(Pos.CENTER);
        eventslayout.setStyle("-fx-background-color: #f0f8ff;");

        Label listLabel = new Label("Scheduled Events");
        listLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox searchBox = new HBox(10); searchBox.setAlignment(Pos.CENTER);
        TextField searchInput = new TextField(); searchInput.setPromptText("Search by Title...");
        Button searchBtn = new Button("Search"); 
        Button clearBtn = new Button("Reset");
        searchBox.getChildren().addAll(searchInput, searchBtn, clearBtn);

        ListView<String> eventList = new ListView<>();
        eventList.setMaxWidth(600); eventList.setMaxHeight(300);
        refreshEventList(eventList, eventManager.getAllEvent());

        Label statusLabel = new Label("");
        Button editBtn = new Button("Edit Selected"); editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        Button deleteBtn = new Button("Delete Selected"); deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;");

        editBtn.setOnAction(e -> {
            String selectedItem = eventList.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.equals("No events found.")) {
                try {
                    String idStr = selectedItem.substring(1, selectedItem.indexOf("]"));
                    Event eventToEdit = eventManager.findEventById(Integer.parseInt(idStr));
                    if (eventToEdit != null) menuPane.setCenter(createAddEventPage(eventToEdit));
                } catch (Exception ex) { statusLabel.setText("Error opening edit page."); }
            }
        });

        deleteBtn.setOnAction(e -> {
            String selectedItem = eventList.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.equals("No events found.")) {
                try {
                    String idStr = selectedItem.substring(1, selectedItem.indexOf("]"));
                    eventManager.deleteEvent(Integer.parseInt(idStr));
                    refreshEventList(eventList, eventManager.getAllEvent());
                    statusLabel.setText("Event deleted."); statusLabel.setStyle("-fx-text-fill: green;");
                } catch (Exception ex) { statusLabel.setText("Error deleting event."); }
            }
        });

        searchBtn.setOnAction(e -> {
            List<Event> results = SearchService.searchByTitle(eventManager.getAllEvent(), searchInput.getText());
            refreshEventList(eventList, results);
        });
        clearBtn.setOnAction(e -> { searchInput.clear(); refreshEventList(eventList, eventManager.getAllEvent()); });

        HBox buttons = new HBox(10, editBtn, deleteBtn); buttons.setAlignment(Pos.CENTER);
        eventslayout.getChildren().addAll(listLabel, searchBox, eventList, buttons, statusLabel);
        return eventslayout;
    }

    private void refreshEventList(ListView<String> list, List<Event> data) {
        list.getItems().clear();
        if (data.isEmpty()) list.getItems().add("No events found.");
        else {
            for (Event e : data) {
                String reminderStr = (e.getReminderMinutes() > 0) ? " [Remind: " + e.getReminderMinutes() + "m]" : "";
                
                String timeStr = String.format("(%s @ %s-%s)", 
                    e.getstartDateTime().toLocalDate(),
                    e.getstartDateTime().toLocalTime(),
                    e.getendDateTime().toLocalTime()
                );

                if (e instanceof RecurrentEvent) {
                    List<Event> occurrences = RecurrenceManager.generateOccurrences((RecurrentEvent) e);
                    for (Event occ : occurrences) {
                        String occTimeStr = String.format("(%s @ %s-%s)", 
                            occ.getstartDateTime().toLocalDate(),
                            occ.getstartDateTime().toLocalTime(),
                            occ.getendDateTime().toLocalTime()
                        );
                        list.getItems().add(String.format("[%d] (Repeat) %s: %s %s%s", 
                            e.getId(), occ.getTitle(), occ.getDescription(), occTimeStr, reminderStr));
                    }
                } else {
                    list.getItems().add(String.format("[%d] %s: %s %s%s", 
                        e.getId(), e.getTitle(), e.getDescription(), timeStr, reminderStr));
                }
            }
        }
    }

    private VBox createSettingsPage(Stage stage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30)); layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f8ff;");

        Label titleLabel = new Label("Data Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label descLabel = new Label("Full System Backup (Single File)");
        Label statusLabel = new Label("Ready");

        Button backupBtn = new Button("Backup Data");
        backupBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        backupBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Folder to Save Backup");
            File selectedDirectory = chooser.showDialog(stage);
            if (selectedDirectory != null) {
                try {
                    BackupService.backup(selectedDirectory.getAbsolutePath());
                    statusLabel.setText("Success! Saved calendar_full_backup.txt");
                    statusLabel.setStyle("-fx-text-fill: green;");
                } catch (IOException ex) { statusLabel.setText("Backup Failed: " + ex.getMessage()); statusLabel.setStyle("-fx-text-fill: red;"); }
            }
        });

        Button restoreBtn = new Button("Restore Data");
        restoreBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        restoreBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Folder Containing Backup");
            File selectedDirectory = chooser.showDialog(stage);
            if (selectedDirectory != null) {
                try {
                    BackupService.restore(selectedDirectory.getAbsolutePath());
                    eventManager.loadEvents(); 
                    recurrenceManager.loadRecurrentEvents(eventManager.getAllEvent());
                    statusLabel.setText("Restore Success! Data reloaded.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                } catch (IOException ex) { statusLabel.setText("Restore Failed: " + ex.getMessage()); statusLabel.setStyle("-fx-text-fill: red;"); }
            }
        });

        layout.getChildren().addAll(titleLabel, descLabel, statusLabel, backupBtn, restoreBtn);
        return layout;
    }

    private VBox createStatsPage() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30)); layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f8ff;");
        List<Event> allEvents = eventManager.getAllEvent();

        layout.getChildren().addAll(
            new Label("Total Events: " + statsService.getTotalEventCount(allEvents)),
            new Label("Busiest Day: " + statsService.getBusiestDayOfWeek(allEvents)),
            new Label("Busiest Month: " + statsService.getBusiestMonth(allEvents)),
            new Label("Most Frequent: " + statsService.getMostFrequentEvent(allEvents)),
            new Button("Refresh Stats")
        );
        return layout;
    }

    public static void main(String[] args) { launch(args); }
}

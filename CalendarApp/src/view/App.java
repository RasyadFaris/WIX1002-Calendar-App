package view;
import model.Event;
import model.RecurrentEvent;
import service.BackupService;
import service.ConflictService;
import service.EventManager;
import service.RecurrenceManager;
import service.SearchService;
import service.StatsService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private final EventManager eventManager = new EventManager();
    private final RecurrenceManager recurrenceManager = new RecurrenceManager(eventManager.getAllEvent());
    private final StatsService statsService = new StatsService();

    private BorderPane menuPane = new BorderPane();    
    private LocalDate watchDate = LocalDate.now();

    @Override
    public void start(Stage mainStage) {

        VBox menuBox = new VBox();

        HBox headerBox = new HBox(15);
        headerBox.setPadding(new Insets(10,15,10,15));
        headerBox.setStyle("-fx-background-color: #00c3ff;");
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Button menuButton = new Button("≡ Menu");
        menuButton.setStyle("-fx-background-color: #ffffff; -fx-font-weight: bold;");
        
        Label calendarLabel = new Label("WIX1002 - Calendar App");
        calendarLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        calendarLabel.setAlignment(Pos.CENTER);
        
        headerBox.getChildren().addAll(menuButton, calendarLabel);

        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(10,15,10,15));
        optionsBox.setStyle("-fx-background-color: #80d9f4;");
        optionsBox.setAlignment(Pos.CENTER);

        String buttonStyle = "-fx-background-color: #ffffff;";

        Button calendarButton = new Button("View Calendar");
        calendarButton.setStyle(buttonStyle);
        calendarButton.setOnAction(event -> {
            menuPane.setCenter(createCalendarView());
            menuBox.getChildren().remove(optionsBox);
        });

        Button addEventBtn = new Button("Add Event");
        addEventBtn.setStyle(buttonStyle);
        addEventBtn.setOnAction(event -> {
            menuPane.setCenter(createAddEventPage());
            menuBox.getChildren().remove(optionsBox);
        });

        Button viewEventsBtn = new Button("View Events");
        viewEventsBtn.setStyle(buttonStyle);
        viewEventsBtn.setOnAction(event-> {
            menuPane.setCenter(createViewEventsPage());
            menuBox.getChildren().remove(optionsBox);
        });
        
        Button statsBtn = new Button("Statistics");
        statsBtn.setStyle(buttonStyle);
        statsBtn.setOnAction(event -> {
            menuPane.setCenter(createStatsPage());
            menuBox.getChildren().remove(optionsBox);
        });

        Button settingsBtn = new Button("Settings");
        settingsBtn.setStyle(buttonStyle);
        settingsBtn.setOnAction(event-> {
            menuPane.setCenter(createSettingsPage(mainStage));
            menuBox.getChildren().remove(optionsBox);
        });

        optionsBox.getChildren().addAll(calendarButton, addEventBtn, viewEventsBtn, statsBtn, settingsBtn);
        
        menuBox.getChildren().add(headerBox);

        menuButton.setOnAction(e-> {
            if (menuBox.getChildren().contains(optionsBox)) {
                menuBox.getChildren().remove(optionsBox);
            } else {
                menuBox.getChildren().add(optionsBox);
            }
        });

        menuPane.setTop(menuBox);
        menuPane.setCenter(createCalendarView()); 

        Scene scene = new Scene(menuPane, 800, 600);
        mainStage.setTitle("Calendar Application");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private VBox createCalendarView() {
        VBox calendarLayout = new VBox(15);
        calendarLayout.setPadding(new Insets(20));
        calendarLayout.setAlignment(Pos.CENTER);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER);
        
        Button btnPrev = new Button("<");
        Button btnNext = new Button(">");
        Label lblMonth = new Label(watchDate.getMonth().toString() + " " + watchDate.getYear());
        lblMonth.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        btnPrev.setOnAction(e -> { 
            watchDate = watchDate.minusMonths(1); 
            menuPane.setCenter(createCalendarView()); 
        });
        btnNext.setOnAction(e -> { 
            watchDate = watchDate.plusMonths(1); 
            menuPane.setCenter(createCalendarView()); 
        });

        header.getChildren().addAll(btnPrev, lblMonth, btnNext);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d; -fx-min-width: 90; -fx-alignment: center;");
            grid.add(dayLabel, i, 0);
        }

        YearMonth ym = YearMonth.of(watchDate.getYear(), watchDate.getMonth());
        int totalDaysInMonth = ym.lengthOfMonth();
        
        int startOffset = watchDate.withDayOfMonth(1).getDayOfWeek().getValue();
        if (startOffset == 7) startOffset = 0;

        for (int day = 1; day <= totalDaysInMonth; day++) {
            int totalIndex = startOffset + day - 1;
            int col = totalIndex % 7;
            int row = (totalIndex / 7) + 1;

            VBox dayBox = new VBox(2);
            dayBox.setAlignment(Pos.TOP_LEFT);
            dayBox.setStyle("-fx-border-color: lightgrey; -fx-padding: 5; -fx-min-width: 80; -fx-min-height: 70; -fx-background-color: white;");
            
            Label lblDayNum = new Label(String.valueOf(day));
            lblDayNum.setStyle("-fx-font-weight: bold;");
            dayBox.getChildren().add(lblDayNum);

            LocalDate gridDate = watchDate.withDayOfMonth(day);
            
            List<Event> dayEvents = new ArrayList<>();
            for (Event e : eventManager.getAllEvent()) {
                if (e instanceof RecurrentEvent) {
                    List<Event> occurrences = RecurrenceManager.generateOccurrences((RecurrentEvent) e);
                    for (Event occ : occurrences) {
                        if (occ.getstartDateTime().toLocalDate().equals(gridDate)) {
                            dayEvents.add(occ);
                        }
                    }
                } else if (e.getstartDateTime().toLocalDate().equals(gridDate)) {
                    dayEvents.add(e);
                }
            }

            for (Event e : dayEvents) {
                Label eventLabel = new Label("• " + e.getTitle());
                eventLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #007bbd;");
                dayBox.getChildren().add(eventLabel);
            }

            grid.add(dayBox, col, row);
        }

        calendarLayout.getChildren().addAll(header, grid);
        return calendarLayout;
    }
   
    private VBox createAddEventPage() {
        VBox formLayout = new VBox(15);
        formLayout.setPadding(new Insets(30));
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setStyle("-fx-background-color: #f0f8ff;");

        Label titleLabel = new Label("Create New Event");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Event Title");
        nameInput.setMaxWidth(300);

        TextField descInput = new TextField();
        descInput.setPromptText("Description");
        descInput.setMaxWidth(300);

        DatePicker dateInput = new DatePicker(LocalDate.now());
        dateInput.setMaxWidth(300);

        HBox timeBox = new HBox(10);
        timeBox.setAlignment(Pos.CENTER);
        TextField startTimeInput = new TextField("09:00");
        startTimeInput.setMaxWidth(100);
        TextField endTimeInput = new TextField("10:00");
        endTimeInput.setMaxWidth(100);
        timeBox.getChildren().addAll(new Label("Time:"), startTimeInput, new Label("to"), endTimeInput);

        CheckBox recurCheckBox = new CheckBox("Repeat this event?");
        VBox recurOptions = new VBox(10);
        recurOptions.setAlignment(Pos.CENTER);
        recurOptions.setVisible(false); 

        ComboBox<String> intervalBox = new ComboBox<>();
        intervalBox.getItems().addAll("Daily (1d)", "Weekly (1w)", "Monthly (1m)");
        intervalBox.setValue("Weekly (1w)"); 

        TextField repeatCountInput = new TextField("0");
        repeatCountInput.setPromptText("Repeat Times (e.g. 5)");
        repeatCountInput.setMaxWidth(200);
        
        recurOptions.getChildren().addAll(new Label("Frequency:"), intervalBox, new Label("Repeat Counts:"), repeatCountInput);

        recurCheckBox.setOnAction(e -> {
            recurOptions.setVisible(recurCheckBox.isSelected());
        });

        Label statusLabel = new Label("");
        Button saveBtn = new Button("Save Event");
        saveBtn.setStyle("-fx-background-color: #00c3ff; -fx-text-fill: white; -fx-font-weight: bold;");

        saveBtn.setOnAction(e -> {
            try {
                LocalDate date = dateInput.getValue();
                LocalTime startT = LocalTime.parse(startTimeInput.getText());
                LocalTime endT = LocalTime.parse(endTimeInput.getText());
                LocalDateTime startDT = LocalDateTime.of(date, startT);
                LocalDateTime endDT = LocalDateTime.of(date, endT);

                // --- UPDATED: Use EventManager for ID ---
                int newId = eventManager.getNextEventId();

                Event newEvent = new Event(
                    newId,
                    nameInput.getText(),
                    descInput.getText(),
                    startDT,
                    endDT
                );

                // --- UPDATED: Use ConflictService ---
                boolean hasConflict = ConflictService.isClashing(newEvent, eventManager.getAllEvent());
                
                if (hasConflict) {
                    statusLabel.setText("Time Conflict. You have another event at the same time.");
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return; 
                }

                if (recurCheckBox.isSelected()) {
                    String selectedStr = intervalBox.getValue();
                    String code = selectedStr.substring(selectedStr.indexOf("(") + 1, selectedStr.indexOf(")")); 
                    int count = Integer.parseInt(repeatCountInput.getText());

                    // --- UPDATED: Create RecurrentEvent and Save to BOTH managers ---
                    RecurrentEvent re = new RecurrentEvent(newEvent, code, count, null);
                    
                    eventManager.addEvent(re);             // Saves to event.csv
                    recurrenceManager.addRecurrentEvent(re); // Saves to recurrent.csv
                    
                    statusLabel.setText("Recurring Event Saved!");
                } else {
                    eventManager.addEvent(newEvent);
                    statusLabel.setText("Event Saved!");
                }
                
                statusLabel.setStyle("-fx-text-fill: green;");
                nameInput.clear(); 
                descInput.clear();

            } catch (Exception ex) {
                ex.printStackTrace(); // Helpful for debugging
                statusLabel.setText("Error: Check time format (HH:mm) or numbers.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        formLayout.getChildren().addAll(titleLabel, nameInput, descInput, dateInput, timeBox, recurCheckBox, recurOptions, saveBtn, statusLabel);
        return formLayout;
    }

    private VBox createViewEventsPage() {
        VBox eventslayout = new VBox(15);
        eventslayout.setPadding(new Insets(30));
        eventslayout.setAlignment(Pos.CENTER);
        eventslayout.setStyle("-fx-background-color: #f0f8ff;");

        Label listLabel = new Label("Scheduled Events");
        listLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search by Title...");
        searchInput.setMaxWidth(200);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #00c3ff; -fx-text-fill: white; -fx-font-weight: bold;");

        Button clearBtn = new Button("Reset");
        clearBtn.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-font-weight: bold;");

        searchBox.getChildren().addAll(searchInput, searchBtn, clearBtn);

        ListView<String> eventList = new ListView<>();
        eventList.setMaxWidth(400);
        eventList.setMaxHeight(300);

        // --- UPDATED: Use EventManager ---
        refreshEventList(eventList, eventManager.getAllEvent());

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: red;");

        Button deleteBtn = new Button("Delete Selected Event");
        deleteBtn.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;");
        
        searchBtn.setOnAction(e -> {
            String query = searchInput.getText();
            // --- UPDATED: Use SearchService ---
            List<Event> results = SearchService.searchByTitle(eventManager.getAllEvent(), query);
            refreshEventList(eventList, results);
        });

        clearBtn.setOnAction(e -> {
            searchInput.clear();
            refreshEventList(eventList, eventManager.getAllEvent());
        });

        deleteBtn.setOnAction(e -> {
            String selectedItem = eventList.getSelectionModel().getSelectedItem();
            
            if (selectedItem != null && !selectedItem.equals("No events found.")) {
                try {
                    int endIndex = selectedItem.indexOf("]");
                    String idStr = selectedItem.substring(1, endIndex);
                    int idToDelete = Integer.parseInt(idStr);

                    // --- UPDATED: Use EventManager delete ---
                    eventManager.deleteEvent(idToDelete);
                    
                    searchInput.clear();
                    refreshEventList(eventList, eventManager.getAllEvent());
                    statusLabel.setText("Event deleted.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    
                } catch (Exception ex) {
                    statusLabel.setText("Error deleting event.");
                }
            } else {
                statusLabel.setText("Please select an event to delete.");
                statusLabel.setStyle("-fx-text-fill: orange;");
            }
        });

        eventslayout.getChildren().addAll(listLabel, searchBox, eventList, deleteBtn, statusLabel);
        return eventslayout;
    }

    private void refreshEventList(ListView<String> list, List<Event> data) {
        list.getItems().clear();
        
        if (data.isEmpty()) {
            list.getItems().add("No events found.");
        } else {
            for (Event e : data) {
                String displayStr = String.format("[%d] %s (%s @ %s - %s)", 
                    e.getId(), 
                    e.getTitle(), 
                    e.getstartDateTime().toLocalDate(),
                    e.getstartDateTime().toLocalTime(),
                    e.getendDateTime().toLocalTime()
                );
                list.getItems().add(displayStr);
            }
        }
    }

    private VBox createSettingsPage(Stage stage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f8ff;");

        Label titleLabel = new Label("Data Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label descLabel = new Label("Backup your event data to a folder or restore from a previous save.");
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: grey;");

        Button backupBtn = new Button("Backup Data");
        backupBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        backupBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Folder to Save Backup");
            File selectedDirectory = chooser.showDialog(stage);

            if (selectedDirectory != null) {
                try {
                    // --- UPDATED: Use BackupService ---
                    BackupService.backup(selectedDirectory.getAbsolutePath());
                    statusLabel.setText("Backup Success: " + selectedDirectory.getName());
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } catch (IOException ex) {
                    statusLabel.setText("Backup Failed: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });

        Button restoreBtn = new Button("Restore Data");
        restoreBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        restoreBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Backup Folder");
            File selectedDirectory = chooser.showDialog(stage);

            if (selectedDirectory != null) {
                try {
                    // --- UPDATED: Use BackupService ---
                    BackupService.restore(selectedDirectory.getAbsolutePath());
                    
                    // Reload data from file after restore
                    eventManager.loadEvents(); 
                    recurrenceManager.loadRecurrentEvents(eventManager.getAllEvent());
                    
                    statusLabel.setText("Restore Success! Please restart app or navigate away.");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } catch (IOException ex) {
                    statusLabel.setText("Restore Failed: Files not found.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });

        layout.getChildren().addAll(titleLabel, descLabel, statusLabel, backupBtn, restoreBtn);
        return layout;
    }

    private VBox createStatsPage() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f8ff;");

        Label titleLabel = new Label("Event Statistics");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        List<Event> allEvents = eventManager.getAllEvent();

        // --- UPDATED: Use StatsService methods ---
        Label totalLabel = new Label("Total Events Tracked: " + statsService.getTotalEventCount(allEvents));
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        String busiestDay = statsService.getBusiestDayOfWeek(allEvents);
        Label busyLabel = new Label("Busiest Day: " + busiestDay);
        busyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        
        // Added extra stat just because we have the service now
        Label avgDurationLabel = new Label("Avg Duration: " + String.format("%.0f min", statsService.getAverageEventDuration(allEvents)));
        avgDurationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");

        Button refreshBtn = new Button("Refresh Stats");
        refreshBtn.setStyle("-fx-background-color: #00c3ff; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> {
            menuPane.setCenter(createStatsPage()); 
        });

        layout.getChildren().addAll(titleLabel, totalLabel, busyLabel, avgDurationLabel, refreshBtn);
        return layout;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

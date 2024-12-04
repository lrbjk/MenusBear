import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

// import java.time.Duration as JavaDuration;
import java.time.temporal.ChronoUnit;

public class PomodoroClock extends Application {

    private boolean running = false;
    private boolean paused = false;
    private long startTime;
    private long remainingTime = 25 * 60; // Default to 25 minutes
    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pomodoro Clock");
        primaryStage.initStyle(StageStyle.UNDECORATED); // Remove default controls

        // Create a VBox to hold all components
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // Create a GridPane for the main controls
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        // Text field to display remaining time
        Label timeLabel = new Label(formatTime(remainingTime));
        timeLabel.setFont(timeLabel.getFont().font(24));
        grid.add(timeLabel, 0, 0, 2, 1);

        // Buttons for start, pause, reset, and set time
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");
        TextField setTimeField = new TextField("MM:SS");
        Button setTimeButton = new Button("Set Time");

        // Add action listeners to buttons
        startButton.setOnAction(e -> startTimer());
        pauseButton.setOnAction(e -> pauseTimer());
        resetButton.setOnAction(e -> resetTimer());
        setTimeButton.setOnAction(e -> {
            try {
                String[] parts = setTimeField.getText().split(":");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                setRemainingTime(minutes * 60 + seconds);
            } catch (Exception ex) {
                // Handle invalid input
                System.out.println("Invalid time format");
            }
        });

        // Add buttons to grid
        grid.add(startButton, 0, 1);
        grid.add(pauseButton, 1, 1);
        grid.add(resetButton, 0, 2, 2, 1);
        grid.add(new Label("Set Time: "), 0, 3);
        grid.add(setTimeField, 1, 3);
        grid.add(setTimeButton, 2, 3);

        // Add shortcut buttons
        HBox shortcutBox = new HBox(10);
        shortcutBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            int timeInMinutes = (i + 1) * 5; // 5, 10, 15, 20, 25 minutes
            Button shortcutButton = new Button(String.format("%d:%02d", timeInMinutes / 60, timeInMinutes % 60));
            shortcutButton.setOnAction(e -> setRemainingTime(timeInMinutes * 60));
            shortcutBox.getChildren().add(shortcutButton);
        }

        // Custom minimize and close buttons
        Button minimizeButton = new Button("-");
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));
        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> primaryStage.close());

        // Add custom buttons to the top
        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.getChildren().addAll(minimizeButton, closeButton);

        // Add everything to the root VBox
        root.getChildren().addAll(topBox, grid, shortcutBox);

        // Create the scene and set it to the stage
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the timeline for the timer
        initializeTimeline();
    }

    private void initializeTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimer() {
        if (!running) {
            running = true;
            paused = false;
            startTime = System.currentTimeMillis() - (remainingTime * 1000);
            timeline.play();
        }
    }

    private void pauseTimer() {
        if (running && !paused) {
            paused = true;
            timeline.pause();
        } else if (paused) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            remainingTime = (remainingTime * 1000 - elapsedTime) / 1000;
            paused = false;
            timeline.play();
        }
    }

    private void resetTimer() {
        running = false;
        paused = false;
        remainingTime = 25 * 60; // Reset to default 25 minutes
        timeline.stop();
        updateTimeDisplay();
    }

    private void setRemainingTime(long timeInSeconds) {
        remainingTime = timeInSeconds;
        resetTimer(); // Stop the timer and update the display, but don't start it automatically
        updateTimeDisplay();
    }

    private void updateTime() {
        if (running && !paused) {
            remainingTime--;
            if (remainingTime <= 0) {
                resetTimer(); // Timer finished, reset it
            } else {
                updateTimeDisplay();
            }
        }
    }

    private void updateTimeDisplay() {
        Label timeLabel = (Label) ((GridPane) ((VBox) ((Stage) getCurrentWindow()).getScene().getRoot()).getChildren().get(1)).getChildren().get(0);
        timeLabel.setText(formatTime(remainingTime));
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
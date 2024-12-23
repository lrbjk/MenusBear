package app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TodoList extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    private TextArea taskInput;
    private ListView<Task> taskList;
    private ObservableList<Task> tasks;
    private final String FILE_PATH = "tasks.txt"; // 使用文本文件保存任务
    private CheckBox showCompletedCheckBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("todo.png")));

        // Remove window borders and make the window transparent
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        BorderPane borderPane = new BorderPane();

        // Set a background color or image
        borderPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-image: url('" + getClass().getResource("background.jpg") + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-color: #e2d3bc;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: lightgray;" +
                        "-fx-border-width: 2;"
        );

        // Custom control buttons for window actions
        VBox controlButtons = new VBox(10);
        controlButtons.setPadding(new Insets(10));

        Button minimizeButton = new Button();
        Button maximizeButton = new Button();
        Button closeButton = new Button();

        // Load icons
        minimizeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("minimize-icon.png"))));
        maximizeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("maximize-icon.png"))));
        closeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("close-icon.png"))));


        minimizeButton.getStyleClass().add("minimize-button");
        maximizeButton.getStyleClass().add("maximize-button");
        closeButton.getStyleClass().add("close-button");

        closeButton.setOnAction(event -> primaryStage.close());
        minimizeButton.setOnAction(event -> primaryStage.setIconified(true));
        maximizeButton.setOnAction(event -> primaryStage.setMaximized(!primaryStage.isMaximized()));

        controlButtons.getChildren().addAll(closeButton, maximizeButton, minimizeButton);

        // Add drag functionality to the entire borderPane
        borderPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        borderPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // Text area for task input
        taskInput = new TextArea();
        taskInput.setPromptText("输入新的任务");
        taskInput.setWrapText(true);
        taskInput.setMinHeight(60);

        Button addButton = new Button("添加");
        addButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        addButton.setOnAction(e -> addTask());
        addButton.setDefaultButton(true);

        Button removeButton = new Button("删除");
        removeButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        removeButton.setOnAction(e -> removeTask());

        showCompletedCheckBox = new CheckBox("显示已完成任务");
        showCompletedCheckBox.setSelected(true);
        showCompletedCheckBox.setOnAction(e -> filterTasks());

        tasks = FXCollections.observableArrayList();
        taskList = new ListView<>(tasks);
        taskList.setCellFactory(lv -> new TaskCell()); // Custom cell to display tasks

        // Handle keyboard events for task list
        taskList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                removeTask();
            }
        });

        // Handle double-click to edit task
        taskList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editTask();
            }
        });
        // Button layout for add, remove and checkbox
        HBox buttonLayout = new HBox(15, addButton, removeButton, showCompletedCheckBox);
        buttonLayout.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");  // Optional: Style for button layout

        // Layout for task input and buttons
        VBox inputLayout = new VBox(15, taskInput, buttonLayout);
        inputLayout.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");  // Optional: Style for input layout
        inputLayout.setPadding(new Insets(10));  // Padding for input layout

        // Layout for task list
        VBox taskLayout = new VBox(15, taskList);
        taskLayout.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10;"); // Style for task list
        taskLayout.setPrefHeight(300);  // Limit height of task list

        // Main layout containing input and task list sections
        VBox layout = new VBox(20, inputLayout, taskLayout);
        layout.setPadding(new Insets(10, 10, 10, 155));  // Keep original padding (left padding modified)

        // Load tasks from file
        loadTasks();

        // Scene setup
        Scene scene = new Scene(borderPane, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        // Set layout into BorderPane's center
        borderPane.setLeft(controlButtons);
        borderPane.setCenter(layout);

        // Show scene
        primaryStage.setScene(scene);
        primaryStage.setUserData(this);
        primaryStage.show();

        // Save tasks when closing the application
        primaryStage.setOnCloseRequest(event -> saveTasks());
    }

    private void addTask() {
        String taskContent = taskInput.getText().trim(); // Remove whitespace
        if (!taskContent.isEmpty()) {
            Optional<Task> existingTask = tasks.stream().filter(task -> task.getContent().equals(taskContent)).findFirst();
            if (existingTask.isPresent()) {
                showAlert("该任务已存在，请输入不同的任务。");
            } else {
                Task newTask = new Task(taskContent);
                tasks.add(newTask);
                taskInput.clear();
                showAlert("任务已成功添加！");
            }
        }
    }

    private void removeTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除该任务吗？");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    tasks.remove(selectedIndex);
                }
            });
        }
    }

    private void editTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectedTask = taskList.getItems().get(selectedIndex);
            taskInput.setText(selectedTask.getContent());
            taskList.getItems().remove(selectedIndex); // Remove the original task
        }
    }

    private void filterTasks() {
        // Filter tasks based on checkbox status
        if (showCompletedCheckBox.isSelected()) {
            taskList.setItems(tasks);
        } else {
            taskList.setItems(tasks.filtered(task -> !task.isCompleted()));
        }
    }

    private void loadTasks() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
                for (String line : lines) {
                    String[] parts = line.split(" \\| "); // Assume task content and time are separated by '|'
                    if (parts.length == 3) {
                        Task task = new Task(parts[0], parts[1], Boolean.parseBoolean(parts[2]));
                        tasks.add(task);
                    }
                }
            }
        } catch (IOException e) {
            showAlert("加载任务失败: " + e.getMessage());
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                writer.write(task.getContent() + " | " + task.getCreationTime() + " | " + task.isCompleted());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("保存任务失败: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Define task class
    public static class Task {
        private String content;
        private String creationTime;
        private boolean completed; // Add completion status

        public Task(String content) {
            this.content = content;
            this.creationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.completed = false; // Default to not completed
        }

        public Task(String content, String creationTime, boolean completed) {
            this.content = content;
            this.creationTime = creationTime;
            this.completed = completed;
        }

        public String getContent() {
            return content;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void toggleCompleted() {
            completed = !completed; // Toggle completion status
        }

        @Override
        public String toString() {
            return (completed ? "[√] " : "[ ] ") + content + " (创建时间: " + creationTime + ")"; // Display task status and time
        }
    }

    // Custom cell
    private static class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                // 创建复选框和标签
                CheckBox completedCheckbox = new CheckBox();
                completedCheckbox.setSelected(item.isCompleted());
                completedCheckbox.setOnAction(event -> {
                    // 更新任务完成状态
                    item.toggleCompleted();

                    // 获取列表视图，刷新数据
                    ListView<Task> listView = getListView();
                    if (listView != null) {
                        listView.refresh();
                    }

                    // 调用过滤任务逻辑（避免直接依赖主类）
                    TodoList parentApp = (TodoList) getScene().getUserData();
                    if (parentApp != null) {
                        parentApp.filterTasks();
                    }
                });

                // 显示任务内容
                Label taskLabel = new Label(item.toString());

                // 创建布局并设置图形
                HBox taskBox = new HBox(10, completedCheckbox, taskLabel);
                setGraphic(taskBox);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
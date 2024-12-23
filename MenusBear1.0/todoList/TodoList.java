package todoList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TodoList extends Application {

    private TextArea taskInput;
    private ListView<Task> taskList;
    private ObservableList<Task> tasks;
    private final String FILE_PATH = "tasks.txt"; // 使用文本文件保存任务
    private CheckBox showCompletedCheckBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Todo List");

        // 设置默认窗口大小
        primaryStage.setWidth(600); // 设置宽度
        primaryStage.setHeight(400); // 设置高度

        taskInput = new TextArea();
        taskInput.setPromptText("输入新的任务");
        taskInput.setWrapText(true);
        taskInput.setMinHeight(60);

        Button addButton = new Button("添加");
        addButton.setOnAction(e -> addTask());
        addButton.setDefaultButton(true);

        Button removeButton = new Button("删除");
        removeButton.setOnAction(e -> removeTask());

        showCompletedCheckBox = new CheckBox("显示已完成任务");
        showCompletedCheckBox.setSelected(true);
        showCompletedCheckBox.setOnAction(e -> filterTasks());

        tasks = FXCollections.observableArrayList();
        taskList = new ListView<>(tasks);
        taskList.setCellFactory(lv -> new TaskCell()); // 自定义单元格显示

        // 处理任务列表的键盘事件
        taskList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                removeTask();
            }
        });

        // 处理双击编辑任务
        taskList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editTask();
            }
        });

        HBox buttonLayout = new HBox(10, addButton, removeButton, showCompletedCheckBox);
        VBox layout = new VBox(10, taskInput, buttonLayout, taskList);
        layout.setPadding(new Insets(10));

        // 加载任务
        loadTasks();

        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // 在关闭应用时保存任务
        primaryStage.setOnCloseRequest(event -> saveTasks());
    }

    private void addTask() {
        String taskContent = taskInput.getText().trim(); // 去除前后空格
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
            taskList.getItems().remove(selectedIndex); // 删除原有任务
        }
    }

    private void filterTasks() {
        // 根据复选框状态过滤任务
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
                    String[] parts = line.split(" \\| "); // 假设任务内容和时间用 '|' 分隔
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

    // 定义任务类
    public static class Task {
        private String content;
        private String creationTime;
        private boolean completed; // 添加完成状态

        public Task(String content) {
            this.content = content;
            this.creationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.completed = false; // 默认未完成
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
            completed = !completed; // 切换完成状态
        }

        @Override
        public String toString() {
            return (completed ? "[x] " : "[ ] ") + content + " (创建时间: " + creationTime + ")"; // 用来表示未完成和已完成的任务
        }
    }

    // 自定义单元格
    private static class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.toString()); // 显示任务的内容和创建时间

                // 处理单击事件以切换完成状态
                setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        item.toggleCompleted(); // 切换完成状态
                        setText(item.toString()); // 更新显示
                    }
                });
            }
        }
    }
}

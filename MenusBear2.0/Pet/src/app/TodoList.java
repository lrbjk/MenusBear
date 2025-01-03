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
import javafx.scene.layout.*;
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

/**
 * TodoList 应用程序主类，负责创建和管理任务列表窗口及相关功能。
 */
public class TodoList extends Application {

    // 偏移量，用于窗口拖动时的位移计算
    private double xOffset = 0;
    private double yOffset = 0;

    // 小熊窗口相对于主窗口的位置偏移量
    private double bearOffsetX = -50;
    private double bearOffsetY = -50;

    // UI 组件
    private TextArea taskInput;
    private ListView<Task> taskList;
    private ObservableList<Task> tasks;
    private final String FILE_PATH = "tasks.txt"; // 使用文本文件保存任务
    private CheckBox showCompletedCheckBox;

    @Override
    public void start(Stage primaryStage) {
        // --------------------- 小熊舞台（Bear Stage） ---------------------
        // 创建一个透明的舞台用于显示小熊
        Stage bearStage = new Stage(StageStyle.TRANSPARENT);

        // 设置 Bear 窗口窗口的图标
        bearStage.getIcons().add(new Image(getClass().getResourceAsStream("todo.png")));
        bearStage.setTitle("Todolist");

        // 加载并设置小熊图片
        ImageView bearView = new ImageView(new Image(getClass().getResourceAsStream("Window.png")));
        bearView.setFitWidth(100); // 设置小熊图片宽度
        bearView.setPreserveRatio(true); // 保持宽高比

        // 创建小熊窗口的根布局，并设置透明背景
        StackPane bearRoot = new StackPane(bearView);
        bearRoot.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // 创建透明场景并设置给小熊窗口
        Scene bearScene = new Scene(bearRoot, Color.TRANSPARENT);
        bearStage.setScene(bearScene);

        // 设置小熊窗口的初始位置
        bearStage.setX(primaryStage.getX() + bearOffsetX);
        bearStage.setY(primaryStage.getY() + bearOffsetY);

        // --------------------- 主窗口（Primary Stage） ---------------------
        // 设置主窗口的图标
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("chat.png")));

        // 设置主窗口样式为无边框和透明
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(true); // 允许主窗口可调整大小

        // --------------------- BorderPane 布局 ---------------------
        // 创建主窗口的 BorderPane 布局
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle(
                "-fx-background-color: #e2d3bc;" + // 背景颜色
                        "-fx-background-image: url('" + getClass().getResource("background.jpg") + "');" + // 背景图片
                        "-fx-background-size: cover;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: lightgray;" +
                        "-fx-border-width: 2;"
        );

        // --------------------- 控制按钮区域 ---------------------
        // 左侧的控制按钮（最小化、最大化、关闭）
        VBox controlButtons = new VBox(10);
        controlButtons.setPadding(new Insets(10));

        // 创建控制按钮
        Button minimizeButton = new Button();
        Button maximizeButton = new Button();
        Button closeButton = new Button();

        // 加载按钮图标
        minimizeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("minimize-icon.png"))));
        maximizeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("maximize-icon.png"))));
        closeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("close-icon.png"))));

        // 添加 CSS 样式类
        minimizeButton.getStyleClass().add("minimize-button");
        maximizeButton.getStyleClass().add("maximize-button");
        closeButton.getStyleClass().add("close-button");

        // 设置按钮点击事件
        minimizeButton.setOnAction(event -> primaryStage.setIconified(true));
        closeButton.setOnAction(event -> {
            primaryStage.close();
            bearStage.hide();
        });

        // 将按钮添加到 VBox 中
        controlButtons.getChildren().addAll(closeButton, maximizeButton, minimizeButton);

        // --------------------- 窗口拖动功能 ---------------------
        // 设置 BorderPane 的鼠标按下事件，用于记录偏移量
        borderPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // 设置 BorderPane 的鼠标拖动事件，用于移动窗口
        borderPane.setOnMouseDragged(event -> {
            if (!primaryStage.isMaximized()) { // 仅在未最大化时允许拖动
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);

                // 拖动时显示小熊窗口
                if (!bearStage.isShowing()) {
                    bearStage.show();
                }
            }
        });

        // --------------------- 任务输入区域 ---------------------
        // 创建任务输入区域
        taskInput = new TextArea();
        taskInput.setPromptText("Enter a new task");
        taskInput.setWrapText(true);
        taskInput.setMinHeight(60);
        taskInput.setStyle("-fx-prompt-text-fill: #a57a52;");

        // 创建添加任务按钮
        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        addButton.setOnAction(e -> addTask());
        addButton.setDefaultButton(true);

        // 创建删除任务按钮
        Button removeButton = new Button("Delete");
        removeButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        removeButton.setOnAction(event -> {
            removeTask();
            event.consume(); // 阻止事件进一步传播，防止干扰
        });

        // 创建显示已完成任务的复选框
        showCompletedCheckBox = new CheckBox("Show Completed Tasks");
        showCompletedCheckBox.setSelected(true);
        showCompletedCheckBox.setOnAction(e -> filterTasks());

        // 初始化任务列表
        tasks = FXCollections.observableArrayList();
        taskList = new ListView<>(tasks);
        taskList.setCellFactory(lv -> new TaskCell()); // 使用自定义单元格显示任务

        // 处理任务列表的键盘事件
        taskList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                removeTask();
            }
        });

        // 处理任务列表的双击事件以编辑任务
        taskList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editTask();
            }
        });

        // 创建按钮布局（添加、删除、复选框）
        HBox buttonLayout = new HBox(15, addButton, removeButton, showCompletedCheckBox);
        buttonLayout.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;"); // 可选：按钮布局样式

        // 创建任务输入和按钮的布局
        VBox inputLayout = new VBox(15, taskInput, buttonLayout);
        inputLayout.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;"); // 可选：输入布局样式
        inputLayout.setPadding(new Insets(10)); // 输入布局的内边距

        // 创建任务列表的布局
        VBox taskLayout = new VBox(15, taskList);
        taskLayout.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-radius: 10;"); // 任务列表样式
        taskLayout.setPrefHeight(300); // 限制任务列表的高度

        // 创建主布局，包含输入和任务列表部分
        VBox layout = new VBox(20, inputLayout, taskLayout);
        layout.setPadding(new Insets(10, 10, 10, 155)); // 保持原始的内边距（左侧内边距修改）

        // 从文件加载任务
        loadTasks();

        // 设置主场景
        Scene scene = new Scene(borderPane, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        // 将布局添加到 BorderPane 的中心
        borderPane.setLeft(controlButtons);
        borderPane.setCenter(layout);

        // 显示主场景
        primaryStage.setScene(scene);
        primaryStage.setUserData(this); // 设置用户数据以便在单元格中访问
        primaryStage.show();

        // --------------------- 位置同步与最大化处理 ---------------------
        // 监听主窗口的 X 属性变化，调整小熊窗口的位置
        primaryStage.xProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) { // 未最大化时同步位置
                bearStage.setX(newVal.doubleValue() + bearOffsetX);
            } else {
                // 最大化时将小熊窗口置于左上角
                bearStage.setX(0 + bearOffsetX);
            }
        });

        // 监听主窗口的 Y 属性变化，调整小熊窗口的位置
        primaryStage.yProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) { // 未最大化时同步位置
                bearStage.setY(newVal.doubleValue() + bearOffsetY);
            } else {
                // 最大化时将小熊窗口置于左上角
                bearStage.setY(0 + bearOffsetY);
            }
        });

        // 监听主窗口的最小化属性变化，隐藏或显示小熊窗口
        primaryStage.iconifiedProperty().addListener((obs, wasMinimized, isNowMinimized) -> {
            if (isNowMinimized) {
                bearStage.hide();
            } else {
                bearStage.show();
                // 恢复位置同步
                if (!primaryStage.isMaximized()) {
                    bearStage.setX(primaryStage.getX() + bearOffsetX);
                    bearStage.setY(primaryStage.getY() + bearOffsetY);
                } else {
                    bearStage.setX(0 + bearOffsetX);
                    bearStage.setY(0 + bearOffsetY);
                }
            }
        });

        // 确保主窗口在小熊窗口之上
        primaryStage.show();

        // 监听主窗口获得焦点时，确保其在前端
        primaryStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                primaryStage.toFront();
            }
        });

        // 在关闭应用程序时保存任务
        primaryStage.setOnCloseRequest(event -> saveTasks());
    }

    /**
     * 添加新任务到任务列表。
     */
    private void addTask() {
        String taskContent = taskInput.getText().trim(); // 去除首尾空格
        if (!taskContent.isEmpty()) {
            // 检查任务是否已存在
            Optional<Task> existingTask = tasks.stream().filter(task -> task.getContent().equals(taskContent)).findFirst();
            if (existingTask.isPresent()) {
                showAlert("This task already exists. Please enter a different task."); // 显示提示信息
            } else {
                Task newTask = new Task(taskContent);
                tasks.add(newTask);
                taskInput.clear();
                showAlert("Task successfully added!"); // 显示成功信息
            }
        }
    }

    /**
     * 删除选中的任务。
     */
    private void removeTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Alert alert = createStyledAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this task?");

            // 如果 createStyledAlert() 返回的弹窗里，第一个按钮就是 "Click" 按钮
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && "Click".equals(result.get().getText())) {
                tasks.remove(selectedIndex);
            }
        }
    }

    /**
     * 编辑选中的任务。
     */
    private void editTask() {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectedTask = taskList.getItems().get(selectedIndex);
            taskInput.setText(selectedTask.getContent());
            taskList.getItems().remove(selectedIndex); // 移除原始任务
        }
    }

    /**
     * 根据复选框状态过滤任务列表，显示或隐藏已完成的任务。
     */
    private void filterTasks() {
        // 根据复选框状态过滤任务
        if (showCompletedCheckBox.isSelected()) {
            taskList.setItems(tasks);
        } else {
            taskList.setItems(tasks.filtered(task -> !task.isCompleted()));
        }
    }

    /**
     * 从文件中加载任务。
     */
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
            showAlert("Failed to load tasks: " + e.getMessage());
        }
    }

    /**
     * 将当前任务列表保存到文件。
     */
    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                writer.write(task.getContent() + " | " + task.getCreationTime() + " | " + task.isCompleted());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Failed to save tasks: " + e.getMessage());
        }
    }

    /**
     * 显示提示对话框。
     *
     * @param message 要显示的消息
     */
    private void showAlert(String message) {
        Alert alert = createStyledAlert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    /**
     * 创建一个带有自定义样式的 Alert 对话框。
     *
     * @param alertType Alert 类型
     * @param message   要显示的消息
     * @return 自定义的 Alert 对象
     */
    private Alert createStyledAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Todo List");          // 弹窗标题
        alert.setHeaderText(null);            // 不显示头部
        alert.setContentText(message);        // 显示的内容

        // 使用英文按钮
        if (alertType == Alert.AlertType.CONFIRMATION) {
            ButtonType ClickButton = new ButtonType("Click", ButtonBar.ButtonData.OK_DONE);
            ButtonType BackButton = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(ClickButton, BackButton);
        } else if (alertType == Alert.AlertType.INFORMATION) {
            ButtonType okButton = new ButtonType("Click", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
        }

        // 设置弹窗图标
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("todo.png")));

        // 对 DialogPane 进行样式设定
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #e2d3bc;" +   // 背景颜色
                        "-fx-border-color: #e2d3bc;" +       // 边框颜色
                        "-fx-border-width: 2;" +
                        "-fx-font-family: 'Roboto';" +       // 字体
                        "-fx-font-size: 12px;"
        );

        return alert;
    }

    /**
     * 自定义任务单元格，用于在 ListView 中显示任务内容和完成状态。
     */
    private static class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                // 创建复选框并设置其状态
                CheckBox completedCheckbox = new CheckBox();
                completedCheckbox.setSelected(item.isCompleted());
                completedCheckbox.setOnAction(event -> {
                    // 更新任务完成状态
                    item.toggleCompleted();

                    // 获取列表视图并刷新数据
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

                // 创建水平布局并设置图形
                HBox taskBox = new HBox(10, completedCheckbox, taskLabel);
                setGraphic(taskBox);
            }
        }
    }

    /**
     * 任务类，表示一个待办任务。
     */
    public static class Task {
        private String content;
        private String creationTime;
        private boolean completed; // 任务完成状态

        /**
         * 构造一个新的任务。
         *
         * @param content 任务内容
         */
        public Task(String content) {
            this.content = content;
            this.creationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.completed = false; // 默认未完成
        }

        /**
         * 构造一个已存在的任务。
         *
         * @param content      任务内容
         * @param creationTime 创建时间
         * @param completed    完成状态
         */
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

        /**
         * 切换任务的完成状态。
         */
        public void toggleCompleted() {
            completed = !completed;
        }

        @Override
        public String toString() {
            return (completed ? "[√] " : "[ ] ") + content + " (Creation Date: " + creationTime + ")"; // 显示任务状态和时间
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

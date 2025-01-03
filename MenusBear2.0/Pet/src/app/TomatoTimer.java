package app;

import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Timer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * TomatoTimer 应用程序主类，实现番茄钟功能。
 */
public class TomatoTimer extends Application {

    // 偏移量，用于窗口拖动时的位移计算
    private double xOffset = 0;
    private double yOffset = 0;

    // 小熊窗口相对于主窗口的位置偏移量
    private double bearOffsetX = -50;
    private double bearOffsetY = -50;

    // UI 组件
    private Button chatArea;
    private Timer timer;
    private Stage primaryStage; // 主舞台

    // 计时器变量
    private int minutes;
    private int seconds;
    private int recMinutes = 1; // 默认1分钟

    // 媒体播放器，用于播放计时结束声音
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage; // 初始化成员变量

        // --------------------- 小熊舞台（Bear Stage） ---------------------
        // 创建一个透明的舞台用于显示小熊
        Stage bearStage = new Stage(StageStyle.TRANSPARENT);

        // 设置 Bear 窗口窗口的图标
        bearStage.getIcons().add(new Image(getClass().getResourceAsStream("tomato.png")));
        bearStage.setTitle("TomatoTimer");

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
        controlButtons.getStyleClass().add("control-buttons");

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

        // --------------------- 计时显示区域 ---------------------
        // 创建计时显示按钮
        chatArea = new Button();
        minutes = 1;
        seconds = 0;
        chatArea.setText(String.format("%02d:%02d", minutes, seconds));
        chatArea.setFont(new Font(95));
        chatArea.setDisable(false);
        chatArea.getStyleClass().add("chat-area");
        chatArea.setBackground(null);
        chatArea.setBorder(
                new Border(
                        new BorderStroke(Color.BLACK,
                                BorderStrokeStyle.SOLID,
                                new CornerRadii(15),
                                new BorderWidths(5)
                        )
                )
        );

        // --------------------- 控制按钮区域 ---------------------
        // 创建 Start、Pause、Reset 和 Set 按钮
        Button startButton = new Button("Start");
        startButton.getStyleClass().add("send-button");
        startButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        startButton.setPrefHeight(30);
        startButton.setPrefWidth(90);
        startButton.setPadding(new Insets(5)); // 设置内边距

        Button pauseButton = new Button("Pause");
        pauseButton.getStyleClass().add("send-button");
        pauseButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        pauseButton.setPrefHeight(30);
        pauseButton.setPrefWidth(90);
        pauseButton.setPadding(new Insets(5)); // 设置内边距

        Button resetButton = new Button("Reset");
        resetButton.getStyleClass().add("send-button");
        resetButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        resetButton.setPrefHeight(30);
        resetButton.setPrefWidth(90);
        resetButton.setPadding(new Insets(5)); // 设置内边距

        Button setButton = new Button("Set");
        setButton.getStyleClass().add("send-button");
        setButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        setButton.setPrefHeight(30);
        setButton.setPrefWidth(90);
        setButton.setPadding(new Insets(5)); // 设置内边距
        setButton.setOnAction(event -> showSetWindow());

        // 创建快速设置按钮（05, 15, 30, 45, 60 分钟）
        Button[] timeButtons = new Button[5];
        timeButtons[0] = new Button("05");
        timeButtons[1] = new Button("15");
        timeButtons[2] = new Button("30");
        timeButtons[3] = new Button("45");
        timeButtons[4] = new Button("60");

        for (int i = 0; i < timeButtons.length; i++) {
            timeButtons[i].setStyle("-fx-background-color: #5a362b;" +
                    "-fx-text-fill: white;" +
                    "-fx-border-radius: 50%;" +
                    "-fx-background-radius: 50%;" +
                    "-fx-padding: 14px 16px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-alignment: center;"
            );
        }

        // 初始化计时器（每秒触发一次）
        timer = new Timer(1000, new TimerListener());

        // 设置快速按钮的点击事件
        timeButtons[0].setOnAction(event -> clearTime(5));
        timeButtons[1].setOnAction(event -> clearTime(15));
        timeButtons[2].setOnAction(event -> clearTime(30));
        timeButtons[3].setOnAction(event -> clearTime(45));
        timeButtons[4].setOnAction(event -> clearTime(60));

        // 设置 Start、Pause、Reset 按钮的点击事件
        startButton.setOnAction(event -> {
            timer.start();
            // mediaPlayer.play(); // 可选：播放声音
        });

        pauseButton.setOnAction(event -> {
            timer.stop();
        });

        resetButton.setOnAction(event -> {
            timer.stop();
            clearTime();
        });

        // 创建按钮布局（Start、Pause、Reset、Set）
        HBox forSetButton = new HBox();
        forSetButton.getChildren().addAll(startButton, pauseButton, resetButton, setButton);
        forSetButton.setSpacing(15);

        // 创建快速设置按钮的布局
        HBox forQuickButton = new HBox();
        forQuickButton.getChildren().addAll(timeButtons);
        forQuickButton.setAlignment(Pos.CENTER);
        forQuickButton.setSpacing(20);

        // 创建 GridPane 布局并添加按钮
        GridPane inputBox = new GridPane();
        inputBox.setVgap(60);
        ColumnConstraints emptyColumn = new ColumnConstraints(50, 200, Double.MAX_VALUE);
        inputBox.getColumnConstraints().add(emptyColumn);
        inputBox.add(forSetButton, 1, 1);
        inputBox.add(forQuickButton, 1, 0);
        inputBox.setPadding(new Insets(10));

        // 将计时显示按钮和控制按钮添加到 BorderPane
        borderPane.setRight(chatArea);
        borderPane.setBottom(inputBox);
        borderPane.setLeft(controlButtons);

        // --------------------- 媒体播放器初始化 ---------------------
        // 加载并初始化媒体播放器
        URL soundURL = getClass().getResource("Boom!.wav");
        if (soundURL != null) {
            Media sound = new Media(soundURL.toExternalForm());
            mediaPlayer = new MediaPlayer(sound);
        } else {
            showAlert("Sound file not found.");
        }

        // --------------------- 设置计时器 ---------------------
        // 设置计时器的启动逻辑
        startButton.setOnAction(event -> {
            timer.start();
            // mediaPlayer.play(); // 可选：播放声音
        });

        // 设置 Pause 按钮的停止逻辑
        pauseButton.setOnAction(event -> {
            timer.stop();
        });

        // 设置 Reset 按钮的重置逻辑
        resetButton.setOnAction(event -> {
            timer.stop();
            clearTime();
        });

        // --------------------- 场景设置 ---------------------
        // 创建主场景并设置透明背景
        Scene scene = new Scene(borderPane, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        // 设置主场景
        primaryStage.setScene(scene);
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
    }

    /**
     * 清除计时器时间并重置显示。
     */
    private void clearTime() {
        minutes = recMinutes;
        seconds = 0;
        chatArea.setText(String.format("%02d:%02d", minutes, seconds));
    }

    /**
     * 根据指定的分钟数清除并设置计时器时间。
     *
     * @param rec 要设置的分钟数
     */
    private void clearTime(int rec) {
        recMinutes = rec;
        minutes = recMinutes;
        seconds = 0;
        chatArea.setText(String.format("%02d:%02d", minutes, seconds));
    }

    /**
     * 显示设置窗口，允许用户自定义计时器时间。
     */
    private void showSetWindow() {
        // 新建一个 Stage 作为弹窗
        Stage setStage = new Stage();
        setStage.getIcons().add(new Image(getClass().getResourceAsStream("tomato.png")));

        // 设置窗口样式为透明
        setStage.initStyle(StageStyle.TRANSPARENT);

        // 指定主窗口为设置窗口的所有者，并使设置窗口模态
        setStage.initOwner(primaryStage);
        setStage.initModality(Modality.WINDOW_MODAL);

        // 使用 BorderPane 作为根节点
        BorderPane setPane = new BorderPane();
        setPane.setStyle(
                "-fx-background-color: #e2d3bc;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #f5f1f9;" +
                        "-fx-border-width: 2;"
        );
        setPane.setPadding(new Insets(20));

        // 添加窗口拖动功能
        setPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        setPane.setOnMouseDragged(event -> {
            setStage.setX(event.getScreenX() - xOffset);
            setStage.setY(event.getScreenY() - yOffset);
        });

        // 中间内容放一个 VBox，包含说明文字、Spinner 以及 OK/Cancel 按钮
        VBox centerVBox = new VBox(20);
        centerVBox.setAlignment(Pos.CENTER);

        // 创建 Spinner 选择分钟数
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 60, recMinutes);
        minuteSpinner.setEditable(true);
        minuteSpinner.setStyle("-fx-pref-height: 8;");

        // 创建标签
        javafx.scene.control.Label label = new javafx.scene.control.Label("Set Minutes:");
        label.setFont(new Font(16));
        label.setTextFill(Color.web("#5a362b"));

        // 创建按钮布局（OK 和 Cancel）
        HBox buttonBox = new HBox(18);
        buttonBox.setAlignment(Pos.CENTER);

        // 创建 OK 按钮
        Button ClickButton = new Button("Click");
        ClickButton.setStyle("-fx-background-color: #5a362b; -fx-text-fill: white;");
        ClickButton.setOnAction(evt -> {
            recMinutes = minuteSpinner.getValue();
            minutes = recMinutes;
            seconds = 0;
            chatArea.setText(String.format("%02d:%02d", minutes, seconds));
            setStage.close();
        });

        // 创建 Cancel 按钮
        Button BackButton = new Button("Back");
        BackButton.setStyle("-fx-background-color: #5a362b; -fx-text-fill: white;");
        BackButton.setOnAction(evt -> setStage.close());

        // 将按钮添加到按钮布局中
        buttonBox.getChildren().addAll(ClickButton, BackButton);

        // 将标签、Spinner 和按钮添加到 VBox 中
        centerVBox.getChildren().addAll(label, minuteSpinner, buttonBox);
        setPane.setCenter(centerVBox);

        // 创建设置窗口的场景并设置透明背景
        Scene setScene = new Scene(setPane, 200, 150);
        setScene.setFill(Color.TRANSPARENT);
        setStage.setScene(setScene);
        setStage.show();
    }

    /**
     * 定义计时器的监听器，每秒更新一次计时显示。
     */
    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            Platform.runLater(() -> {
                if (minutes == 0 && seconds == 0) {
                    mediaPlayer.play(); // 播放计时结束声音
                    timer.stop(); // 停止计时器
                    boom();// 显示弹窗

                } else if (seconds == 0) {
                    minutes--;
                    seconds = 59;
                } else {
                    seconds--;
                }
                chatArea.setText(String.format("%02d:%02d", minutes, seconds));
            });
        }
    }

    /**
     * 定义计时器的弹窗样式。
     */
    private void boom() {
        // 创建一个信息类型的 Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Boom！");          // 弹窗标题
        alert.setHeaderText(null);         // 不显示头部
        alert.setContentText("Time Up！");    // 显示的内容，可以根据需要修改

        // 使用英文按钮
        ButtonType ClickButton = new ButtonType("Click", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(ClickButton);

        // 设置弹窗图标
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("tomato.png")));

        // 对 DialogPane 进行样式设定
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #e2d3bc;" +   // 背景颜色
                        "-fx-border-color: #e2d3bc;" +       // 边框颜色
                        "-fx-border-width: 2;" +
                        "-fx-font-family: 'Roboto';" +        // 字体
                        "-fx-font-size: 12px;"
        );

        // 显示弹窗并等待用户响应
        alert.showAndWait();
    }

    /**
     * 显示提示对话框。
     *
     * @param message 要显示的消息
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Tomato Timer");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package app;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Chat 应用程序主类，负责创建和管理主窗口及相关功能。
 */
public class Chat extends Application {
    // 偏移量，用于窗口拖动时的位移计算
    private double xOffset = 0;
    private double yOffset = 0;

    // Bear 窗口相对于主窗口的位置偏移量
    private double bearOffsetX = -50;
    private double bearOffsetY = -50;

    @Override
    public void start(Stage primaryStage) {
        // --------------------- Bear Stage ---------------------
        // 创建一个透明的 Bear 窗口
        Stage bearStage = new Stage(StageStyle.TRANSPARENT);

        // 设置 Bear 窗口窗口的图标
        bearStage.getIcons().add(new Image(getClass().getResourceAsStream("chat.png")));
        bearStage.setTitle("Chat");

        // 加载并设置 Bear 图片
        ImageView bearView = new ImageView(new Image(getClass().getResourceAsStream("Window.png")));
        bearView.setFitWidth(100); // 设置 Bear 图片宽度
        bearView.setPreserveRatio(true); // 保持宽高比

        // 创建 Bear 窗口的根布局，并设置透明背景
        StackPane bearRoot = new StackPane(bearView);
        bearRoot.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // 创建透明场景并设置给 Bear 窗口
        Scene bearScene = new Scene(bearRoot, Color.TRANSPARENT);
        bearStage.setScene(bearScene);

        // 设置 Bear 窗口的初始位置
        bearStage.setX(primaryStage.getX() + bearOffsetX);
        bearStage.setY(primaryStage.getY() + bearOffsetY);

        // --------------------- Primary Stage ---------------------
        // 设置主窗口的图标
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("chat.png")));

        // 设置主窗口样式为无边框和透明
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(true); // 允许主窗口可调整大小

        // --------------------- BorderPane Layout ---------------------
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

        // 添加CSS样式类
        minimizeButton.getStyleClass().add("minimize-button");
        maximizeButton.getStyleClass().add("maximize-button");
        closeButton.getStyleClass().add("close-button");

        // 设置按钮点击事件
        minimizeButton.setOnAction(event -> primaryStage.setIconified(true));
        maximizeButton.setOnAction(event -> primaryStage.setMaximized(!primaryStage.isMaximized()));
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

                // 拖动时显示 Bear 窗口
                if (!bearStage.isShowing()) {
                    bearStage.show();
                }
            }
        });

        // --------------------- 聊天区域 ---------------------
        // 创建不可编辑的聊天显示区域
        TextArea chatArea = new TextArea();
        chatArea.setStyle("-fx-font-family: Arial; -fx-font-size: 12px;");
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.getStyleClass().add("chat-area");

        // 将聊天区域放入 StackPane 并设置样式
        StackPane chatBox = new StackPane(chatArea);
        chatBox.getStyleClass().add("chat-box");
        chatBox.setPadding(new Insets(10));
        chatBox.setPrefWidth(380); // 可根据需要调整或移除固定宽度

        // --------------------- 输入区域 ---------------------
        // 创建输入字段
        TextField inputField = new TextField();
        inputField.setPromptText("Enter number to get instructions");
        inputField.getStyleClass().add("input-field");
        inputField.setPrefHeight(30);
        inputField.setStyle("-fx-prompt-text-fill: #a57a52;");

        // 创建发送按钮
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        sendButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        sendButton.setPrefHeight(30);

        // 创建帮助按钮
        Button helpButton = new Button("Back");
        helpButton.getStyleClass().add("input-field");
        helpButton.setStyle("-fx-background-color: #a57a52; -fx-text-fill: white;");
        helpButton.setPrefHeight(30);

        // 设置帮助按钮的点击事件，模拟发送“Help”
        helpButton.setOnAction(event -> {
            inputField.setText("Help");
            sendButton.fire();
        });

        // 绑定输入字段的宽度
        inputField.prefWidthProperty().bind(borderPane.widthProperty().multiply(0.75));

        // 设置发送按钮的点击事件
        sendButton.setOnAction(event -> {
            String userInput = inputField.getText();
            if (!userInput.trim().isEmpty()) {
                chatArea.appendText("You: " + userInput + "\n");
                inputField.clear();
                String reply = getAutoReply(userInput);
                chatArea.clear();
                chatArea.appendText("Menus Bear: " + reply + "\n");
            }
        });

        // 创建输入区域的 HBox 布局
        HBox inputBox = new HBox(10, inputField, sendButton, helpButton);
        inputBox.setPadding(new Insets(10));
        inputBox.getStyleClass().add("input-box");

        // 将各部分添加到 BorderPane
        borderPane.setLeft(controlButtons);
        borderPane.setRight(chatBox);
        borderPane.setBottom(inputBox);

        // --------------------- 主场景设置 ---------------------
        // 创建主场景并设置透明背景
        Scene mainScene = new Scene(borderPane, 600, 400);
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);

        // --------------------- 位置同步与最大化处理 ---------------------
        // 监听主窗口的X属性变化，调整 Bear 窗口的位置
        primaryStage.xProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) { // 未最大化时同步位置
                bearStage.setX(newVal.doubleValue() + bearOffsetX);
            } else {
                // 最大化时将 Bear 窗口置于左上角
                bearStage.setX(0 + bearOffsetX);
            }
        });

        // 监听主窗口的Y属性变化，调整 Bear 窗口的位置
        primaryStage.yProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryStage.isMaximized()) { // 未最大化时同步位置
                bearStage.setY(newVal.doubleValue() + bearOffsetY);
            } else {
                // 最大化时将 Bear 窗口置于左上角
                bearStage.setY(0 + bearOffsetY);
            }
        });

        // 监听主窗口的最大化属性变化，调整 Bear 窗口的位置
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isNowMaximized) {
                // 最大化时将 Bear 窗口置于左上角
                bearStage.setX(0 + bearOffsetX);
                bearStage.setY(0 + bearOffsetY);
            } else {
                // 取消最大化时恢复 Bear 窗口的位置
                bearStage.setX(primaryStage.getX() + bearOffsetX);
                bearStage.setY(primaryStage.getY() + bearOffsetY);
            }
        });

        // 监听主窗口的最小化属性变化，隐藏或显示 Bear 窗口
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

        // 确保主窗口在 Bear 窗口之上
        primaryStage.show();

        // 当主窗口获得焦点时，确保其在前端
        primaryStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                primaryStage.toFront();
            }
        });

        // 自动发送“Help”信息
        inputField.setText("Help");
        sendButton.fire();
    }

    /**
     * 根据用户输入生成自动回复内容。
     *
     * @param input 用户输入的字符串
     * @return 自动回复的字符串
     */
    private String getAutoReply(String input) {
        switch (input) {
            case "Help":
                return "Please select the number of the option to get more information:\n\n" +
                        "1. Initial State and Activation\n" +
                        "2. Bear Interaction Features\n" +
                        "3. Bear Button Operations\n" +
                        "4. Other Information";
            case "1":
                return "Initial State and Activation:\n\n" +
                        "   1. Initial State: When the software is opened, the bear appears in a sleeping state, stationary.\n\n" +
                        "   2. Activate Bear: Click or drag the bear to activate and enter the interactive state.";
            case "2":
                return "Bear Interaction Features:\n\n" +
                        "   1. Drag the Bear: Freely drag the bear on the desktop to move it to your desired position.\n\n" +
                        "   2. Trigger Animations: Click different parts of the bear to trigger specific actions:\n" +
                        "       Foot Click - Left Foot/Right Foot: Display the 'Sit Down and Rest' animation.\n" +
                        "       Hand Click - Left Hand/Right Hand: Trigger the 'Wave' animation.\n" +
                        "       Eye Click - Left Eye/Right Eye: Trigger the 'Reading' animation.\n" +
                        "       Ear Click - Left Ear: Random expression pack; Right Ear: Random expression.";
            case "3":
                return "Bear Button Operations:\n\n" +
                        "   1. Chat Button: Introduces how to use the bear and its features.\n\n" +
                        "   2. TomatoTimer Button: Opens the timer with preset time options: 15, 30, 45, 60.\n" +
                        "       Start Button: Start the timer.\n" +
                        "       Pause Button: Pause the current timer.\n" +
                        "       Reset Button: Reset the timer time.\n" +
                        "       Set Time Button: Customize the timer length, from 1 to 120 minutes.\n\n" +
                        "   3. Todolist Button: Opens the task list interface, where you can add, check, and delete tasks.\n\n" +
                        "   4. Return to Sleep State Button: The bear returns to the sleep state, waiting for the next activation.";
            case "4":
                return "Other Information:\n\nPlease ensure to follow all operation guidelines during use to ensure the best experience with the bear desktop pet.";
            default:
                return "Sorry, I don't understand. Please enter a valid number or type 'Back' to view the usage guide.";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

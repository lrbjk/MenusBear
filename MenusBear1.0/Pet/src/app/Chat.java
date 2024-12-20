package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Chat extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
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


        // Custom control buttons
        VBox controlButtons = new VBox(10);
        controlButtons.setPadding(new Insets(10));
        controlButtons.getStyleClass().add("control-buttons");

        Button minimizeButton = new Button("-");
        Button maximizeButton = new Button("[]");
        Button closeButton = new Button("X");

        // Set buttons to be smaller and square-shaped
        minimizeButton.setPrefSize(20, 20);
        maximizeButton.setPrefSize(20, 20);
        closeButton.setPrefSize(20, 20);

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

        // Chat area (right side, occupying 75% of the width)
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.getStyleClass().add("chat-area");

        StackPane chatBox = new StackPane(chatArea);
        chatBox.getStyleClass().add("chat-box");
        chatBox.setPadding(new Insets(10));
        chatBox.setPrefWidth(380);

        // Input field and buttons (bottom)
        TextField inputField = new TextField();
        inputField.setPromptText("输入 '帮助' 获取使用说明");
        inputField.getStyleClass().add("input-field");
        inputField.setPrefHeight(30); // Reduce input field height

        Button sendButton = new Button("发送");
        sendButton.getStyleClass().add("send-button");
        sendButton.setPrefHeight(30); // Match input field height

        // Set input field width to 80% of the window width
        inputField.prefWidthProperty().bind(borderPane.widthProperty().multiply(0.8));

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

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setPadding(new Insets(10));
        inputBox.getStyleClass().add("input-box");

        // Assemble layout
        borderPane.setRight(chatBox); // Chat box on the right
        borderPane.setBottom(inputBox); // Input box at the bottom
        borderPane.setLeft(controlButtons); // Control buttons on the left

        // Main scene
        Scene scene = new Scene(borderPane, 600, 400); // Window size: 800x600
        scene.setFill(Color.TRANSPARENT); // Set the scene background to transparent

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getAutoReply(String input) {
        if (input.contains("帮助")) {
            return "请输入以下指令\n1. 检查我的日程 \n2. 我的信息 \n3. 可以帮我添加一个五分钟的番茄钟计时吗 \n4. 使用指南";
        } else if (input.contains("检查我的日程")) {
            return  "\n"+
                    "8:00-10:00 CS260 OVERWATCH \n" +
                    "\n"+
                    "10:00-12:00 CS110 Team Project \n" +
                    "\n"+
                    "14:00-15:40 CS330 Software design \n" +
                    "\n"+
                    "8:00-10:00 CS420 StoneHeart \n";
        } else if (input.contains("我的信息")) {
            return  "\n"+
                    "Project: Menus(Mus) Bear \n" +
                    "\n"+
                    "Group name: Menus Bear developers \n" +
                    "\n"+
                    "MU Email: XinHang.hu.1950@mumail.ie \n" +
                    "\n"+
                    "Status: I have girl friend \n";}
            else if (input.contains("可以帮我添加一个五分钟的番茄钟计时吗")) {
                return  "\n"+
                        "已经为您添加了五分钟的番茄钟计时了哦，欧尼酱 \n";
        }
            else if (input.contains("使用指南")) {
            return  "\n"+
                    "番茄钟可以为在您学习的过程中计时，做到限时训练 \n"+
                    "\n"+
                    "To-do List 可以有效的帮您管理待办事项 \n"
                    ;
        }
            else {
            return "抱歉，我不明白您的意思。";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

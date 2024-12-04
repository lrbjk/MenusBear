package application;

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

public class template extends Application {

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
                        "-fx-background-image: url('" + getClass().getResource("background.png") + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-color: #e2d3bc;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: lightgray;" +
                        "-fx-border-width: 2;"
        );


        // 这个是自定义三个窗口控制 owo
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

        //回复框
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.getStyleClass().add("chat-area");

        StackPane chatBox = new StackPane(chatArea);
        chatBox.getStyleClass().add("chat-box");
        chatBox.setPadding(new Insets(10));
        chatBox.setPrefWidth(380);


        TextField inputField = new TextField();
        inputField.setPromptText("输入(帮助)获取使用提示...");
        inputField.getStyleClass().add("input-field");
        inputField.setPrefHeight(30);

        Button sendButton = new Button("发送");
        sendButton.getStyleClass().add("send-button");
        sendButton.setPrefHeight(30);


        inputField.prefWidthProperty().bind(borderPane.widthProperty().multiply(0.8));

        sendButton.setOnAction(event -> {
            String userInput = inputField.getText();
            //从这里开始是对话的逻辑
            if (!userInput.trim().isEmpty()) {
                chatArea.appendText("You: " + userInput + "\n");
                inputField.clear();
                String reply = getAutoReply(userInput);
                chatArea.appendText("Bear: " + reply + "\n");
            }
        });

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setPadding(new Insets(10));
        inputBox.getStyleClass().add("input-box");

        // Assemble layout
        borderPane.setRight(chatBox);
        borderPane.setBottom(inputBox);
        borderPane.setLeft(controlButtons);

        // 窗口的参数
        Scene scene = new Scene(borderPane, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //对话的内容
    private String getAutoReply(String input) {
        if (input.contains("帮助")) {
            return "命令\n1. 帮助信息\n2. 使用指南";
        } else if (input.contains("使用教程")) {
            return "这是使用教程的内容。";
        } else if (input.contains("设计理念")) {
            return "这是设计理念的内容。";
        } else {
            return "抱歉，我不明白您的意思。";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

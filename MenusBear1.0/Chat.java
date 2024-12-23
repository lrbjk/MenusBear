import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;

public class Chat extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        // Remove window borders
        primaryStage.initStyle(StageStyle.UNDECORATED);

        BorderPane borderPane = new BorderPane();

        // Custom title bar with buttons
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("title-bar");

        Button minimizeButton = new Button("-");
        Button maximizeButton = new Button("[ ]");
        Button closeButton = new Button("X");

        closeButton.setOnAction(event -> primaryStage.close());
        minimizeButton.setOnAction(event -> primaryStage.setIconified(true));
        
        // Toggle maximized state
        maximizeButton.setOnAction(event -> {
            primaryStage.setMaximized(!primaryStage.isMaximized());
        });

        titleBar.getChildren().addAll(minimizeButton, maximizeButton, closeButton);

        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // Chat area
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.getStyleClass().add("chat-area");

        // Input field and buttons
        TextField inputField = new TextField();
        inputField.setPromptText("输入您的消息...");
        inputField.getStyleClass().add("input-field");

        Button sendButton = new Button("发送");
        sendButton.getStyleClass().add("action-button");
        
        // Action to send message
        sendButton.setOnAction(event -> {
            String userInput = inputField.getText();
            if (!userInput.trim().isEmpty()) {
                chatArea.appendText("You: " + userInput + "\n");
                inputField.clear();
                String reply = getAutoReply(userInput);
                chatArea.appendText("Bear: " + reply + "\n");
            }
        });

        // Clear chat button
        Button clearButton = new Button("清空");
        clearButton.getStyleClass().add("action-button");
        clearButton.setOnAction(event -> chatArea.clear());

        // Additional buttons for predefined text
        Button helloButton = new Button("帮助");
        Button askQuestionButton = new Button("使用教程");
        Button noPlayButton = new Button("设计理念");

        helloButton.setOnAction(event -> inputField.setText("帮助"));
        askQuestionButton.setOnAction(event -> inputField.setText("使用教程"));
        noPlayButton.setOnAction(event -> inputField.setText("设计理念"));

        // Arrange predefined buttons
        HBox predefinedButtonsBox = new HBox(10, helloButton, askQuestionButton, noPlayButton);
        predefinedButtonsBox.setPadding(new Insets(10));

        // Arrange action buttons in a separate row
        HBox actionButtonsBox = new HBox(10, sendButton, clearButton);
        actionButtonsBox.setPadding(new Insets(10));

        // Arrange input field and buttons vertically
        VBox inputBox = new VBox(10, inputField, predefinedButtonsBox, actionButtonsBox);
        inputBox.setPadding(new Insets(10));

        // Main layout
        VBox contentBox = new VBox(10, chatArea, inputBox);
        contentBox.setPadding(new Insets(10));
        contentBox.getStyleClass().add("content-box");

        borderPane.setTop(titleBar);
        borderPane.setCenter(contentBox);

        Scene scene = new Scene(borderPane, 600, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String getAutoReply(String input) {
        if (input.contains("帮助")) {
            return "命令\n1.\n2.\n";
        } else if (input.contains("使用教程")) {
            return "命令\n1.\n2.\n";
        } else if (input.contains("设计理念")) {
            return "Overwatch!!!!!!!!!!!!!!!";
        } else {
            return "抱歉，不玩守望先锋别问我";
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
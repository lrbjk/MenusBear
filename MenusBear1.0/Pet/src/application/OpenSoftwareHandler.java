package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class OpenSoftwareHandler implements EventHandler<ActionEvent> {

    private String softwareName;

    public OpenSoftwareHandler(String softwareName) {
        this.softwareName = softwareName;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            if ("todo".equalsIgnoreCase(softwareName)) {
                // 打开 TodoList 应用
                TodoList todoList = new TodoList();
                Stage stage = new Stage();
                todoList.start(stage);
            } else if ("chat".equalsIgnoreCase(softwareName)) {
                // 打开 Chat 应用
                Chat chat = new Chat();
                Stage stage = new Stage();
                chat.start(stage);
            } else if ("tomato".equalsIgnoreCase(softwareName)) {
                // 打开 Tomato 应用
                template tomatoTimer = new template();
                Stage stage = new Stage();
                tomatoTimer.start(stage);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package app;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * OpenSoftwareHandler 类负责处理打开不同软件应用的事件。
 * 根据传入的软件名称，启动相应的应用程序。
 */
public class OpenSoftwareHandler implements EventHandler<ActionEvent> {

    private String softwareName; // 要打开的软件名称
    private EventListener eventListener; // 引用 EventListener（未使用，建议移除或使用）

    /**
     * 构造方法，初始化软件名称。
     *
     * @param softwareName 要打开的软件名称，如 "todo", "chat", "tomato"
     */
    public OpenSoftwareHandler(String softwareName) {
        this.softwareName = softwareName;
    }

    /**
     * 处理 ActionEvent，根据软件名称启动对应的应用程序。
     *
     * @param event 触发的 ActionEvent
     */
    @Override
    public void handle(ActionEvent event) {
        try {
            Stage stage = new Stage(); // 创建新的舞台用于启动应用

            switch (softwareName.toLowerCase()) {
                case "todo":
                    // 打开 TodoList 应用
                    TodoList todoList = new TodoList();
                    todoList.start(stage);
                    break;

                case "chat":
                    // 打开 Chat 应用
                    Chat chat = new Chat();
                    chat.start(stage);
                    break;

                case "tomato":
                    // 打开 TomatoTimer 应用
                    TomatoTimer tomatoTimer = new TomatoTimer();
                    tomatoTimer.start(stage);
                    break;

                default:
                    System.out.println("未知的软件名称: " + softwareName);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 EventListener（如果需要使用）。
     *
     * @param eventListener 要设置的 EventListener
     */
    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}

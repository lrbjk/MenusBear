package application;
    
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
   private static ImageView imageView;
   EventListener listen;
   VBox messageBox;
   int petID = 1; // 宠物的 ID
   double xOffset = 0; 
   double yOffset = 0;
   
   public void start(Stage primaryStage) {
      try {
         /*
          * 加载 GIF 动画文件
          */
         Image image = new Image(this.getClass().getResourceAsStream("/biu/biu0.gif"));
         imageView = new ImageView(image); 
         imageView.setX(0); 
         imageView.setY(0);
         imageView.setLayoutX(0);
         imageView.setLayoutY(50);
         
         // 设置显示区域的高度和宽度
         imageView.setFitHeight(150); 
         imageView.setFitWidth(150); 
         // 设置事件监听
         listen = new EventListener(imageView , petID);
         imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);
         
         imageView.setPreserveRatio(true); // 保持宽高比
         imageView.setStyle("-fx-background:transparent;"); // 设置背景透明

         UI ui = new UI(imageView, petID, listen, primaryStage);
         ui.addMessageBox("欢迎来到我的宠物程序~");

         AnchorPane pane = new AnchorPane(ui.getMessageBox(), ui.getImageView());
         
         pane.setStyle("-fx-background:transparent;");
         // 拖动窗口事件
         Timeline idleTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            // 在这里处理待机状态，比如隐藏窗口或显示待机画面等
             System.out.println("进入待机状态");
            listen.mainimg(1,14);}));
             idleTimer.play(); // 开始计时
         pane.setOnMousePressed(event -> {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
        resetIdleTimer(idleTimer);
     });
     pane.setOnMouseDragged(event -> {
        primaryStage.setX(event.getScreenX() - xOffset);
        primaryStage.setY(event.getScreenY() - yOffset);
     });
    
     
     Scene scene = new Scene(pane, 400, 400);
     scene.setFill(null);
     scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
     
     primaryStage.setScene(scene);
     // 设置窗口初始位置
     primaryStage.setX(850);
     primaryStage.setY(400);
     primaryStage.setAlwaysOnTop(true); // 总是位于最上层
     // 设置窗口图标
     primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
     // 设置窗口风格为透明
     primaryStage.initStyle(StageStyle.TRANSPARENT);
     
     /*
      * 在关闭请求时执行特定操作
      * event.consume() 防止窗口立即关闭
     */
     primaryStage.setOnCloseRequest(event -> {
         event.consume();
         ui.end();
     });
     primaryStage.show();
     
     ui.setTray(primaryStage); // 设置托盘
     Thread thread = new Thread(ui);
     thread.start();
   } catch(Exception e) {
     e.printStackTrace();
   }
 }
 private void resetIdleTimer(Timeline idleTimer) {
   if (idleTimer.getStatus() == Timeline.Status.RUNNING) {
       idleTimer.stop(); // 停止计时器
   }
   idleTimer.playFromStart(); // 从头开始计时
   System.out.println("退出待机状态");
   listen.mainimg(1,15); // 显示主画面
}
 public static void main(String[] args) {
   launch(args);
 }
}


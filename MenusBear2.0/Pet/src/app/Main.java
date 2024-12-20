package app;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class Main extends Application{
	VBox messageBox;
	Image image;
	EventListener listen;
	double xOffset = 0;
	double yOffset = 0;
	private static ImageView imageView;

	public void start(Stage primaryStage) {
		try {
			image = new Image(this.getClass().getResourceAsStream("/beer/MB0.gif"));
			imageView = new ImageView(image);
			imageView.setX(40);
			imageView.setY(0);
			imageView.setLayoutX(0);
			imageView.setLayoutY(50);

			//设置图片显示的大小
			imageView.setFitHeight(150);
			imageView.setFitWidth(150);
			imageView.setPreserveRatio(true);
			imageView.setStyle("-fx-background:transparent;");

			// 初始化 UI 和 EventListener
			UI ui = new UI(imageView, null, primaryStage);
			listen = new EventListener(imageView, ui); // 将 UI 传递给 EventListener
			ui.setEventListener(listen); // 将 listen 设置到 UI 中

			// 添加点击事件
			imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);

			//Chat Button
			Button chat = new Button();
			chat.setLayoutX(10);
			chat.setLayoutY(200);
			Image icon1 = new Image(this.getClass().getResourceAsStream("chat.png"));
			ImageView iconView1 = new ImageView(icon1);
			iconView1.setFitWidth(20);
			iconView1.setFitHeight(20);
			iconView1.setStyle("-fx-background-color: transparent;"); // 设置ImageView背景透明
			chat.setGraphic(iconView1);
			// 在打开 Chat 时播放 GIF
			chat.setOnAction(event -> {
				// 播放MB100.gif
				listen.loadImg(0,1f);
				// 打开 Chat 应用
				Chat chatApp = new Chat();
				Stage stage = new Stage();
				chatApp.start(stage);
				ui.addMessageBox("小熊帮助！");
			});

			//Tomato Button
			Button tomato = new Button();
			tomato.setLayoutX(70);
			tomato.setLayoutY(200);
			Image icon2 = new Image(this.getClass().getResourceAsStream("tomato.png"));
			ImageView iconView2 = new ImageView(icon2);
			iconView2.setFitWidth(20);
			iconView2.setFitHeight(20);
			iconView2.setStyle("-fx-background-color: transparent;"); // 设置ImageView背景透明
			tomato.setGraphic(iconView2);
			tomato.setOnAction(event -> {
				listen.loadImg(0,1f);
				TomatoTimer tomatoApp = new TomatoTimer();
				Stage stage = new Stage();
				tomatoApp.start(stage);
				ui.addMessageBox("开始计时咯");
			});

			//Todo Button
			Button todo = new Button();
			todo.setLayoutX(130);
			todo.setLayoutY(200);
			Image icon3 = new Image(this.getClass().getResourceAsStream("todo.png"));
			ImageView iconView3 = new ImageView(icon3);
			iconView3.setFitWidth(20);
			iconView3.setFitHeight(20);
			iconView3.setStyle("-fx-background-color: transparent;"); // 设置ImageView背景透明
			todo.setGraphic(iconView3);
			todo.setOnAction(event -> {
				listen.loadImg(0,1f);
				TodoList todoApp = new TodoList();
				Stage stage = new Stage();
				todoApp.start(stage);
				ui.addMessageBox("让小熊看看");
			});

			//Convert Button
			Button convert = new Button();
			convert.setLayoutX(190);
			convert.setLayoutY(200);
			Image icon4 = new Image(this.getClass().getResourceAsStream("convert.png"));
			ImageView iconView4 = new ImageView(icon4);
			iconView4.setFitWidth(20);
			iconView4.setFitHeight(20);
			iconView4.setStyle("-fx-background-color: transparent;"); // 设置ImageView背景透明
			convert.setGraphic(iconView4);

			AnchorPane pane = new AnchorPane(ui.getMessageBox(),ui.getImageView(),chat,tomato,todo,convert);

			pane.setStyle("-fx-background:transparent;");

			//使窗体能拖动。先获取按下鼠标时的坐标p1，再将窗体坐标设为p1加拖动的位移量

			listen = new EventListener(imageView,ui);
			imageView.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED,event -> {
				listen.handle(event);

				double x = event.getX();
				double y = event.getY();
			});

			pane.setOnMousePressed(event -> {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			});

			pane.setOnMouseDragged(event -> {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			});

			Scene scene = new Scene(pane,400,400);
			scene.setFill(null);

			primaryStage.setScene(scene);

			//设置窗体的初始位置
			primaryStage.setX(850);
			primaryStage.setY(400);
			primaryStage.setAlwaysOnTop(true);//窗口总显示在最前

			//修改任务栏图标
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));

			//下句隐藏任务栏图标，但javafx的stage.initStyle(Style)只能有一个起效，只好作罢
			primaryStage.initStyle(StageStyle.TRANSPARENT);//背景透明


			primaryStage.setOnCloseRequest( event ->{event.consume(); ui.end(); pane.getChildren().removeAll(chat, tomato, todo, convert);});

			primaryStage.show();

			ui.setTray(primaryStage);//添加系统托盘
			Thread thread = new Thread(String.valueOf(ui));
			thread.start();

			pane.getChildren().removeAll(chat, tomato, todo, convert);

			// 监听 isAwake 变化
			ui.isAwake().addListener((observable, oldValue, newValue) -> {
				if (!newValue) {
					// 如果 isAwake 为 true，更新 AnchorPane，添加按钮
					if (!pane.getChildren().contains(chat)) {
						pane.getChildren().add(chat);
					}
					if (!pane.getChildren().contains(tomato)) {
						pane.getChildren().add(tomato);
					}
					if (!pane.getChildren().contains(todo)) {
						pane.getChildren().add(todo);
					}
					if (!pane.getChildren().contains(convert)) {
						pane.getChildren().add(convert);
					}
					// 设置 convert 按钮事件逻辑
					convert.setOnAction(event -> {
						// 检查宠物是否为唤醒状态
						if (ui.isAwake().get()) {
							ui.setPetState("Sleep"); // 切换到休眠状态
							ui.isAwake().set(false); // 更新状态
						} else {
							// 使用 toggleAwakeState 来处理逻辑，但忽略默认的 else 分支
							ui.customToggleAwakeState(); // 替换默认 else 行为
						}
					});
				} else {
					// 如果 isAwake 为 false，移除按钮
					pane.getChildren().removeAll(chat, tomato, todo, convert);
				}
			});

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}

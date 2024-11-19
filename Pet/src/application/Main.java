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
import javafx.scene.control.Button;


public class Main extends Application {
	
	private static ImageView imageView;
	EventListener listen;
	VBox messageBox;
	int petID = 1;//宠物ID。罗小黑=0，比丢=1
	double xOffset = 0; 
	double yOffset = 0;
	Image image;
	public void start(Stage primaryStage) {
		try {
			/*
			 * 创建初始的图
			 * 加载相对路径的图片要用class.getResource，不然运行jar包时会报错：找不到文件路径！
			 * 路径中第一个“/”是必需的，它表示类的根目录，类文件夹在此项目中与lxh和biu在同一级
			 */
			image = new Image(this.getClass().getResourceAsStream("/biu/biu0.gif"));
	      	imageView = new ImageView(image); 
	      	imageView.setX(40); 
	      	imageView.setY(0);
	      	imageView.setLayoutX(0);
	      	imageView.setLayoutY(50);
	      	
	      	//设置图片显示的大小
	      	imageView.setFitHeight(150); 
	      	imageView.setFitWidth(150); 
	      	
	      	//添加图片的点击事件
	      	listen = new EventListener(imageView , petID);
	      	imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);
	      	
	      	//Chat Button
            Button chat = new Button();
            chat.setLayoutX(0); 
            chat.setLayoutY(10); 
            Image icon1 = new Image(this.getClass().getResourceAsStream("chat.png")); 
            ImageView iconView1 = new ImageView(icon1); 
            iconView1.setFitWidth(20); 
            iconView1.setFitHeight(20); 
            chat.setGraphic(iconView1);
            chat.setOnAction(new OpenSoftwareHandler("chat"));
            
            //Tomato Button
            Button tomato = new Button();
            tomato.setLayoutX(0); 
            tomato.setLayoutY(70); 
            Image icon2 = new Image(this.getClass().getResourceAsStream("tomato.png")); 
            ImageView iconView2 = new ImageView(icon2); 
            iconView2.setFitWidth(20); 
            iconView2.setFitHeight(20); 
            tomato.setGraphic(iconView2);
            tomato.setOnAction(new OpenSoftwareHandler("tomato"));
            
            //Todo Button
            Button todo = new Button();
            todo.setLayoutX(0); 
            todo.setLayoutY(130); 
            Image icon3 = new Image(this.getClass().getResourceAsStream("todo.png")); 
            ImageView iconView3 = new ImageView(icon3); 
            iconView3.setFitWidth(20); 
            iconView3.setFitHeight(20); 
            todo.setGraphic(iconView3);
            todo.setOnAction(new OpenSoftwareHandler("todo"));
            
            //Convert Button
            Button convert = new Button();
            convert.setLayoutX(0); 
            convert.setLayoutY(190); 
            Image icon4 = new Image(this.getClass().getResourceAsStream("convert.png")); 
            ImageView iconView4 = new ImageView(icon4); 
            iconView4.setFitWidth(20); 
            iconView4.setFitHeight(20); 
            convert.setGraphic(iconView4);
            
	      	imageView.setPreserveRatio(true); //保留 width：height的比例
	      	imageView.setStyle("-fx-background:transparent;");//容器背景设为透明
	      	
	      	UI ui = new UI(imageView, petID, listen,primaryStage);
	      	ui.addMessageBox("你好吖~");
	      	
			AnchorPane pane = new AnchorPane(ui.getMessageBox(),ui.getImageView(),chat,tomato,todo,convert);
			
			pane.setStyle("-fx-background:transparent;");
			
			//使窗体能拖动。先获取按下鼠标时的坐标p1，再将窗体坐标设为p1加拖动的位移量
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
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());		
			primaryStage.setScene(scene);
			
			//设置窗体的初始位置
			primaryStage.setX(850);
			primaryStage.setY(400);
			primaryStage.setAlwaysOnTop(true);//窗口总显示在最前
			
			//修改任务栏图标
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			
			//下句隐藏任务栏图标，但javafx的stage.initStyle(Style)只能有一个起效，只好作罢
            //stage.initStyle(StageStyle.UTILITY);
			primaryStage.initStyle(StageStyle.TRANSPARENT);//背景透明
			
			/*
			 * 点击任务栏的“关闭窗口”时，播放告别动画，同时使托盘的图标也关闭.
			 * event.consume()是必需的，这样才能真正阻止Window Close事件的默认处理。
			 * 如果仅仅使用System.exit(0);则不需要event.consume();
			*/
			primaryStage.setOnCloseRequest( event ->{event.consume(); ui.end();});
			primaryStage.show();
			
			ui.setTray(primaryStage);//添加系统托盘
			Thread thread = new Thread(ui);
			thread.start();
			
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
						System.out.println("按钮被点击, 当前状态: " + ui.isAwake().get());
						// 检查宠物是否为唤醒状态
						if (ui.isAwake().get()) {
							ui.setPetState("Sleep"); // 切换到休眠状态
							ui.isAwake().set(false); // 更新状态
						} else {
							System.out.println("切换到待机状态...");
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

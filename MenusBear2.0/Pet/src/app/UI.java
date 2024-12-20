package app;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UI  {

	private Stage primaryStage;
	private CheckboxMenuItem itemSay;
	private BooleanProperty isAwake = new SimpleBooleanProperty(false); // 用于跟踪当前状态（待机/唤醒）
	private VBox messageBox;
	private ImageView imageView;
	private EventListener listen;
	private Random random = new Random(); // 用于生成随机数

	public UI(ImageView view, EventListener el, Stage s) {
		imageView = view;
		listen = el;
		primaryStage = s;
		// 初始化聊天气泡容器
		initializeMessageBox();
		// 初始化时显示待机状态的图像
		setPetState("待机");
		// 为imageView添加点击事件，点击时切换到“唤醒”状态
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> toggleAwakeState());
		imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> Dragged());
		imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> handleDragEnd());
	}

	// 初始化聊天气泡容器
	private void initializeMessageBox() {
		messageBox = new VBox();
		messageBox.setAlignment(Pos.BOTTOM_CENTER);
		messageBox.setStyle("-fx-background:transparent;");
		messageBox.setLayoutX(70);
		messageBox.setLayoutY(0);
		messageBox.setVisible(false); // 默认隐藏
	}

	// 切换宠物的状态：点击后由待机到唤醒
	private void toggleAwakeState() {
		if (isAwake.get()) {
			setPetState("唤醒"); // 切换到唤醒
		}
		new Timeline(new KeyFrame(
				Duration.seconds(2f),
				ae -> toIdle()))
				.play();
	}

	public void setEventListener(EventListener listen) {
		this.listen = listen;
	}

	// 自定的状态切换方法:由唤醒到待机
	public void customToggleAwakeState() {
		if (!isAwake.get()) {
			setPetState("待机"); // 自定义待机逻辑
		}
	}

	private void Dragged() {
		setPetState("Dragged");
		addMessageBox("啊啊啊!!!"); // 在拖动时显示聊天气泡
	}

	private void handleDragEnd() {
		setPetState("ReturnedToStand");
	}

	private void toIdle() {
		setPetState("ReturnedToStand");
	}

	public BooleanProperty isAwake() {
		return isAwake;
	}

	// 根据状态设置宠物图片（待机或唤醒）
	public void setPetState(String state) {
		String imageFileName;

		if (state.equals("待机")) {
			imageFileName = "/beer/Sleep.gif"; // 根据petID选择对应的待机图
			isAwake.set(!isAwake().get()); // 切换状态
			addMessageBox("睡觉觉~"); // 在拖动时显示聊天气泡
		} else if (state.equals("唤醒")) {
			imageFileName = "/beer/WakeUp.gif"; // 根据petID选择对应的唤醒图
			addMessageBox("你好吖!"); // 在拖动时显示聊天气泡
			isAwake.set(!isAwake().get()); // 切换状态
		} else if (state.equals("Dragged")) {
			imageFileName = "/beer/Drag.gif";
		} else if (state.equals("ReturnedToStand")) {
			imageFileName = "/beer/MB0.gif";
		} else {
			imageFileName = "";
		}

		// 加载并显示新的图像
		Image newImage = new Image(this.getClass().getResourceAsStream(imageFileName));
		imageView.setImage(newImage);
	}

	// 添加系统托盘
	public void setTray(Stage stage) {
		SystemTray tray = SystemTray.getSystemTray();
		BufferedImage image;// 托盘图标
		try {
			// 为托盘添加一个右键弹出菜单
			PopupMenu popMenu = new PopupMenu();
			popMenu.setFont(new Font("Roboto", Font.PLAIN, 20));

			MenuItem itemShow = new MenuItem("Show");
			itemShow.addActionListener(e -> Platform.runLater(() -> stage.show()));

			MenuItem itemHide = new MenuItem("Hide");
			// 要先setImplicitExit(false)，否则stage.hide()会直接关闭stage
			itemHide.addActionListener(e -> {
				Platform.setImplicitExit(false);
				Platform.runLater(() -> stage.hide());
			});

			MenuItem itemExit = new MenuItem("Exit");
			itemExit.addActionListener(e -> end());

			popMenu.add(itemShow);
			popMenu.add(itemHide);
			popMenu.add(itemExit);
			// 设置托盘图标
			image = ImageIO.read(getClass().getResourceAsStream("Icon.png"));
			TrayIcon trayIcon = new TrayIcon(image, "桌面宠物", popMenu);
			trayIcon.setToolTip("桌面宠物");
			trayIcon.setImageAutoSize(true); // 自动调整图片大小。这步很重要，不然显示的是空白
			tray.add(trayIcon);
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}
	}

	// 退出程序时展示动画
	void end() {
		if (listen != null) { // 检查 listen 是否为 null
			listen.mainimg(99); // 播放宠物的告别动画
		}
		double time = 2f;

		// 动画结束后执行退出
		Platform.runLater(() -> setMsg("Bye~"));
		new Timeline(new KeyFrame(
				Duration.seconds(time),
				ae -> System.exit(0)))
				.play();
	}

	// 动态添加聊天气泡
	public void addMessageBox(String message) {
		Label bubble = new Label(message);
		bubble.setPrefWidth(100); // 设置气泡宽度
		bubble.setStyle("-fx-background-color:#654219; -fx-background-radius: 8px;");
		bubble.setPadding(new Insets(5));
		bubble.setTextFill(Color.WHITE);
		bubble.setAlignment(Pos.CENTER); // 设置文本居中
		bubble.setWrapText(true); // 自动换行
		Polygon triangle = new Polygon(
				0.0, 0.0,
				10.0, 10.0,
				20.0, 0.0
		);
		triangle.setFill(Color.web("654219"));

		messageBox.getChildren().setAll(bubble, triangle); // 更新气泡内容
		messageBox.setVisible(true); // 显示气泡

		// 设置气泡的显示时间
		new Timeline(new KeyFrame(
				Duration.seconds(3),
				ae -> messageBox.setVisible(false)
		)).play();
	}

	public void setMsg(String msg) {
		Label lbl = (Label) messageBox.getChildren().get(0);
		lbl.setText(msg);
		messageBox.setVisible(true);
		// 设置气泡的显示时间
		new Timeline(new KeyFrame(
				Duration.seconds(3),
				ae -> messageBox.setVisible(false)))
				.play();
	}

	public ImageView getImageView() {
		return imageView;
	}

	public VBox getMessageBox() {
		return messageBox;
	}

	// 显示随机表情 GIF 和聊天气泡
	public void showRandomEmoji(String ear) {
		int emojiNumber;
		String chatMessage = "";

		if ("leftEar".equals(ear)) {
			emojiNumber = random.nextInt(4) + 1; // 1 到 4
			if(emojiNumber==1){
				chatMessage = "舒服!";
			}else if(emojiNumber==2){
				chatMessage = "摸哪呢!";
			}else if(emojiNumber==3){
				chatMessage = "虾头!";
			}else{
				chatMessage = "那里不可以!";
			}
		} else if ("rightEar".equals(ear)) {
			emojiNumber = random.nextInt(4) + 4; // 4 到 7
			if(emojiNumber==5){
				chatMessage = "吃掉你!";
			}else if(emojiNumber==6){
				chatMessage = "开心开心!";
			}else if(emojiNumber==7){
				chatMessage = "怎么啦？";
			}else{
				chatMessage = "那里不可以!";
			}
		} else {
			return; // 无效参数，忽略
		}

		String emojiFileName = "/beer/EMJ" + emojiNumber + ".gif";
		Image emojiImage = new Image(getClass().getResourceAsStream(emojiFileName));
		ImageView emojiView = new ImageView(emojiImage);
		emojiView.setFitWidth(100);
		emojiView.setFitHeight(100);
		emojiView.setPreserveRatio(true);

		// 创建 Popup
		Popup popup = new Popup();
		popup.getContent().add(emojiView);

		// 计算 Popup 位置，根据点击的耳朵调整位置
		double popupX;
		double popupY;
		if ("leftEar".equals(ear)) {
			popupX = primaryStage.getX() + imageView.getLayoutX() - 50; // 左耳附近
			popupY = primaryStage.getY() + imageView.getLayoutY() - 50;
		} else { // right
			popupX = primaryStage.getX() + imageView.getLayoutX() + imageView.getFitWidth() + 35; // 右耳附近
			popupY = primaryStage.getY() + imageView.getLayoutY() - 50 ;;
		}

		popup.show(primaryStage, popupX, popupY);

		// 显示聊天气泡
		addMessageBox(chatMessage);

		// 设置 Popup 自动隐藏
		new Timeline(new KeyFrame(
				Duration.seconds(2), // 显示 2 秒
				ae -> popup.hide()
		)).play();
	}
}


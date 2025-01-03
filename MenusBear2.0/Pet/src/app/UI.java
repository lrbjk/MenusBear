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

/**
 * UI 类负责管理宠物的用户界面，包括状态切换、消息显示、系统托盘等功能。
 */
public class UI {

	private Stage primaryStage; // 主舞台
	private BooleanProperty isAwake = new SimpleBooleanProperty(false); // 用于跟踪当前状态（待机/唤醒）
	private VBox messageBox; // 消息框容器
	private ImageView imageView; // 宠物的图片视图
	private EventListener listen; // 事件监听器
	private Random random = new Random(); // 用于生成随机数

	/**
	 * 构造方法，初始化 UI 组件和事件处理。
	 *
	 * @param view 图像视图
	 * @param el   事件监听器
	 * @param s    主舞台
	 */
	public UI(ImageView view, EventListener el, Stage s) {
		imageView = view;
		listen = el;
		primaryStage = s;
		// 初始化聊天气泡容器
		initializeMessageBox();
		// 初始化时显示待机状态的图像
		setPetState("待机");
		// 为 imageView 添加点击事件，点击时切换到“唤醒”状态
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> toggleAwakeState());
		// 添加拖动事件处理
		imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> handleDragged());
		imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> handleDragEnd());
	}

	/**
	 * 初始化聊天气泡容器。
	 */
	private void initializeMessageBox() {
		messageBox = new VBox();
		messageBox.setAlignment(Pos.BOTTOM_CENTER);
		messageBox.setStyle("-fx-background:transparent;");
		messageBox.setLayoutX(70);
		messageBox.setLayoutY(0);
		messageBox.setVisible(false); // 默认隐藏
	}

	/**
	 * 切换宠物的状态：点击后由待机到唤醒。
	 * 如果已经是唤醒状态，则延迟2秒后切换回待机状态。
	 */
	private void toggleAwakeState() {
		if (isAwake.get()) {
			setPetState("唤醒"); // 切换到唤醒
		}
		// 创建一个持续2秒的时间轴，结束后切换到待机状态
		new Timeline(new KeyFrame(
				Duration.seconds(2),
				ae -> toIdle()))
				.play();
	}

	/**
	 * 设置事件监听器。
	 *
	 * @param listen 事件监听器
	 */
	public void setEventListener(EventListener listen) {
		this.listen = listen;
	}

	/**
	 * 自定义的状态切换方法：由唤醒到待机。
	 */
	public void customToggleAwakeState() {
		if (!isAwake.get()) {
			setPetState("待机"); // 切换到待机状态
		}
	}

	/**
	 * 处理拖动事件，切换到拖动状态并显示消息气泡。
	 */
	private void handleDragged() {
		setPetState("Dragged");
		addMessageBox("啊啊啊!!!"); // 在拖动时显示聊天气泡
	}

	/**
	 * 处理拖动结束事件，切换回待机状态。
	 */
	private void handleDragEnd() {
		setPetState("ReturnedToStand");
	}

	/**
	 * 切换到待机状态。
	 */
	private void toIdle() {
		setPetState("ReturnedToStand");
	}

	/**
	 * 获取 isAwake 属性，用于绑定和监听。
	 *
	 * @return BooleanProperty 表示宠物是否处于唤醒状态
	 */
	public BooleanProperty isAwake() {
		return isAwake;
	}

	/**
	 * 根据状态设置宠物图片（待机、唤醒、拖动或返回待机）。
	 *
	 * @param state 宠物的目标状态
	 */
	public void setPetState(String state) {
		String imageFileName;

		switch (state) {
			case "待机":
				imageFileName = "/beer/Sleep.gif"; // 待机图像
				isAwake.set(!isAwake.get()); // 切换状态
				addMessageBox("睡觉觉~(～﹃～)"); // 显示消息气泡
				break;
			case "唤醒":
				imageFileName = "/beer/WakeUp.gif"; // 唤醒图像
				addMessageBox("你好吖!"); // 显示消息气泡
				isAwake.set(!isAwake.get()); // 切换状态
				break;
			case "Dragged":
				imageFileName = "/beer/Drag.gif"; // 拖动图像
				break;
			case "ReturnedToStand":
				imageFileName = "/beer/MB0.gif"; // 返回待机图像
				break;
			default:
				imageFileName = "";
				break;
		}

		// 加载并显示新的图像
		if (!imageFileName.isEmpty()) {
			Image newImage = new Image(this.getClass().getResourceAsStream(imageFileName));
			imageView.setImage(newImage);
		}
	}

	/**
	 * 添加系统托盘图标，并设置右键菜单。
	 *
	 * @param stage 主舞台
	 */
	public void setTray(Stage stage) {
		// 检查系统是否支持托盘
		if (!SystemTray.isSupported()) {
			System.out.println("系统不支持系统托盘");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();
		BufferedImage trayImage;

		try {
			// 创建右键弹出菜单
			PopupMenu popMenu = new PopupMenu();
			popMenu.setFont(new Font("Roboto", Font.PLAIN, 20));

			// “显示”菜单项
			MenuItem itemShow = new MenuItem("Show");
			itemShow.addActionListener(e -> Platform.runLater(() -> stage.show()));

			// “隐藏”菜单项
			MenuItem itemHide = new MenuItem("Hide");
			// 先设置 Platform.setImplicitExit(false)，否则 stage.hide() 会直接关闭应用
			itemHide.addActionListener(e -> {
				Platform.setImplicitExit(false);
				Platform.runLater(() -> stage.hide());
			});

			// “退出”菜单项
			MenuItem itemExit = new MenuItem("Exit");
			itemExit.addActionListener(e -> end());

			// 添加菜单项到弹出菜单
			popMenu.add(itemShow);
			popMenu.add(itemHide);
			popMenu.add(itemExit);

			// 加载托盘图标
			trayImage = ImageIO.read(getClass().getResourceAsStream("Icon.png"));
			TrayIcon trayIcon = new TrayIcon(trayImage, "桌面宠物", popMenu);
			trayIcon.setToolTip("桌面宠物");
			trayIcon.setImageAutoSize(true); // 自动调整图标大小

			// 将托盘图标添加到系统托盘
			tray.add(trayIcon);
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出程序时展示动画，并在动画结束后关闭应用。
	 */
	void end() {
		if (listen != null) { // 检查 listen 是否为 null
			listen.mainimg(99); // 播放宠物的告别动画
		}
		double time = 2; // 动画持续时间（秒）

		// 创建一个持续指定时间的时间轴，结束后退出程序
		new Timeline(new KeyFrame(
				Duration.seconds(time),
				ae -> System.exit(0)))
				.play();
	}

	/**
	 * 动态添加聊天气泡。
	 *
	 * @param message 要显示的消息内容
	 */
	public void addMessageBox(String message) {
		// 创建消息气泡标签
		Label bubble = new Label(message);
		bubble.setPrefWidth(100); // 设置气泡宽度
		bubble.setStyle("-fx-background-color:#654219; -fx-background-radius: 8px;");
		bubble.setPadding(new Insets(5));
		bubble.setTextFill(Color.WHITE);
		bubble.setAlignment(Pos.CENTER); // 设置文本居中
		bubble.setWrapText(true); // 自动换行

		// 创建气泡的三角形部分
		Polygon triangle = new Polygon(
				0.0, 0.0,
				10.0, 10.0,
				20.0, 0.0
		);
		triangle.setFill(Color.web("#654219"));

		// 更新消息框内容
		messageBox.getChildren().setAll(bubble, triangle);
		messageBox.setVisible(true); // 显示气泡

		// 设置气泡的显示时间为3秒，3秒后隐藏气泡
		new Timeline(new KeyFrame(
				Duration.seconds(3),
				ae -> messageBox.setVisible(false)
		)).play();
	}

	/**
	 * 获取宠物的图片视图。
	 *
	 * @return ImageView 宠物的图片视图
	 */
	public ImageView getImageView() {
		return imageView;
	}

	/**
	 * 获取消息框容器。
	 *
	 * @return VBox 消息框容器
	 */
	public VBox getMessageBox() {
		return messageBox;
	}

	/**
	 * 显示随机表情 GIF 和聊天气泡。
	 *
	 * @param ear 表示点击的是哪一只耳朵（"leftEar" 或 "rightEar"）
	 */
	public void showRandomEmoji(String ear) {
		int emojiNumber;
		String chatMessage = "";

		// 根据点击的耳朵决定使用的表情
		if ("leftEar".equals(ear)) {
			emojiNumber = random.nextInt(4) + 1; // 1 到 4
			switch (emojiNumber) {
				case 1:
					chatMessage = "舒hu(*￣▽￣*)";
					break;
				case 2:
					chatMessage = "摸哪呢！￣へ￣";
					break;
				case 3:
					chatMessage = "虾头(ˉ▽ˉ；)...";
					break;
				case 4:
					chatMessage = "别瞎摸!(=ˍ=)";
					break;
			}
		} else if ("rightEar".equals(ear)) {
			emojiNumber = random.nextInt(3) + 5; // 5 到 7
			switch (emojiNumber) {
				case 5:
					chatMessage = "吃掉你(╯▔皿▔)╯!";
					break;
				case 6:
					chatMessage = "开心!(*^_^*)";
					break;
				case 7:
					chatMessage = "咋啦？(●'◡'●)";
					break;
			}
		} else {
			return; // 无效参数，忽略
		}

		String emojiFileName = "/beer/EMJ" + emojiNumber + ".gif"; // 表情 GIF 文件名
		Image emojiImage = new Image(getClass().getResourceAsStream(emojiFileName));
		ImageView emojiView = new ImageView(emojiImage);
		emojiView.setFitWidth(100);
		emojiView.setFitHeight(100);
		emojiView.setPreserveRatio(true);

		// 创建 Popup 显示表情
		Popup popup = new Popup();
		popup.getContent().add(emojiView);

		// 计算 Popup 显示的位置
		double popupX;
		double popupY;
		if ("leftEar".equals(ear)) {
			popupX = primaryStage.getX() + imageView.getLayoutX() - 50; // 左耳附近
			popupY = primaryStage.getY() + imageView.getLayoutY() - 50;
		} else { // rightEar
			popupX = primaryStage.getX() + imageView.getLayoutX() + imageView.getFitWidth() + 35; // 右耳附近
			popupY = primaryStage.getY() + imageView.getLayoutY() - 50;
		}

		// 显示 Popup
		popup.show(primaryStage, popupX, popupY);

		// 显示聊天气泡
		addMessageBox(chatMessage);

		// 设置 Popup 自动隐藏，显示2秒后隐藏
		new Timeline(new KeyFrame(
				Duration.seconds(2),
				ae -> popup.hide()
		)).play();
	}
}

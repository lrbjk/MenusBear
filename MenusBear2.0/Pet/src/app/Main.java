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

/**
 * 主应用程序类，继承自JavaFX的Application类。
 */
public class Main extends Application {
	// 消息框容器
	private VBox messageBox;
	// 应用程序使用的图片
	private Image image;
	// 事件监听器
	private EventListener listen;
	// 窗口拖动时的偏移量
	private double xOffset = 0;
	private double yOffset = 0;
	// 图片视图
	private static ImageView imageView;

	/**
	 * 应用程序启动方法。
	 *
	 * @param primaryStage 主舞台
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// 加载初始图片
			image = new Image(this.getClass().getResourceAsStream("/beer/MB0.gif"));
			imageView = new ImageView(image);
			imageView.setX(40);
			imageView.setY(0);
			imageView.setLayoutX(0);
			imageView.setLayoutY(50);

			// 设置图片显示的大小并保持比例
			imageView.setFitHeight(150);
			imageView.setFitWidth(150);
			imageView.setPreserveRatio(true);
			imageView.setStyle("-fx-background:transparent;");

			// 初始化 UI 和事件监听器
			UI ui = new UI(imageView, null, primaryStage);
			listen = new EventListener(imageView, ui); // 将 UI 传递给事件监听器
			ui.setEventListener(listen); // 将事件监听器设置到 UI 中

			// 添加图片点击事件处理
			imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);

			// 创建并配置 Chat 按钮
			Button chat = createButton("chat.png", 10, 200, "小熊帮助！", () -> {
				listen.loadImg(0, 1f); // 播放 GIF
				Chat chatApp = new Chat();
				Stage stage = new Stage();
				chatApp.start(stage); // 打开 Chat 应用
			});

			// 创建并配置 Tomato 按钮
			Button tomato = createButton("tomato.png", 70, 200, "开始计时咯", () -> {
				listen.loadImg(0, 1f); // 播放 GIF
				TomatoTimer tomatoApp = new TomatoTimer();
				Stage stage = new Stage();
				tomatoApp.start(stage); // 打开 TomatoTimer 应用
			});

			// 创建并配置 Todo 按钮
			Button todo = createButton("todo.png", 130, 200, "让小熊看看", () -> {
				listen.loadImg(0, 1f); // 播放 GIF
				TodoList todoApp = new TodoList();
				Stage stage = new Stage();
				todoApp.start(stage); // 打开 TodoList 应用
			});

			// 创建并配置 Convert 按钮
			Button convert = createButton("convert.png", 190, 200, null, null);

			// 创建根布局，并添加所有组件
			AnchorPane pane = new AnchorPane(ui.getMessageBox(), ui.getImageView(), chat, tomato, todo, convert);
			pane.setStyle("-fx-background:transparent;");

			// 使窗体可拖动
			setupWindowDrag(pane, primaryStage);

			// 创建场景并设置透明背景
			Scene scene = new Scene(pane, 400, 400);
			scene.setFill(null);

			primaryStage.setScene(scene);

			// 设置窗体的初始位置和其他属性
			primaryStage.setX(850);
			primaryStage.setY(400);
			primaryStage.setAlwaysOnTop(true); // 窗口总在最前
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png"))); // 设置任务栏图标
			primaryStage.initStyle(StageStyle.TRANSPARENT); // 窗口背景透明

			// 设置关闭请求事件，防止窗口关闭时退出应用
			primaryStage.setOnCloseRequest(event -> {
				event.consume(); // 消费事件，防止默认关闭行为
				ui.end(); // 执行 UI 结束逻辑
				pane.getChildren().removeAll(chat, tomato, todo, convert); // 移除所有按钮
			});

			primaryStage.show(); // 显示主舞台

			ui.setTray(primaryStage); // 添加系统托盘图标

			// 启动 UI 相关的线程
			Thread thread = new Thread(String.valueOf(ui));
			thread.start();

			// 初始时移除所有按钮
			pane.getChildren().removeAll(chat, tomato, todo, convert);

			// 监听 isAwake 属性的变化，动态添加或移除按钮
			ui.isAwake().addListener((observable, oldValue, newValue) -> {
				if (!newValue) {
					// 如果 isAwake 为 false，添加按钮
					addButtonsIfAbsent(pane, chat, tomato, todo, convert);
					setupConvertButtonAction(ui, convert); // 设置 Convert 按钮的事件逻辑
				} else {
					// 如果 isAwake 为 true，移除按钮
					pane.getChildren().removeAll(chat, tomato, todo, convert);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个带图标和事件的按钮。
	 *
	 * @param iconPath 图标路径
	 * @param x        按钮的X坐标
	 * @param y        按钮的Y坐标
	 * @param message  按钮点击后显示的消息（可为null）
	 * @param action   按钮点击后执行的操作（可为null）
	 * @return 配置好的按钮
	 */
	private Button createButton(String iconPath, double x, double y, String message, Runnable action) {
		Button button = new Button();
		button.setLayoutX(x);
		button.setLayoutY(y);

		// 加载并设置图标
		Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
		ImageView iconView = new ImageView(icon);
		iconView.setFitWidth(20);
		iconView.setFitHeight(20);
		iconView.setStyle("-fx-background-color: transparent;"); // 设置 ImageView 背景透明
		button.setGraphic(iconView);

		// 设置按钮点击事件
		if (action != null) {
			button.setOnAction(event -> {
				if (message != null) {
					// 如果有消息，添加到消息框
					UI ui = getUI(); // 假设有方法获取 UI 实例
					if (ui != null) {
						ui.addMessageBox(message);
					}
				}
				action.run(); // 执行动作
			});
		}

		return button;
	}

	/**
	 * 设置窗口拖动功能。
	 *
	 * @param pane         要添加拖动功能的根布局
	 * @param primaryStage 主舞台
	 */
	private void setupWindowDrag(AnchorPane pane, Stage primaryStage) {
		pane.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});

		pane.setOnMouseDragged(event -> {
			primaryStage.setX(event.getScreenX() - xOffset);
			primaryStage.setY(event.getScreenY() - yOffset);
		});
	}

	/**
	 * 添加按钮到 pane 中，如果它们尚未存在。
	 *
	 * @param pane    根布局
	 * @param buttons 要添加的按钮
	 */
	private void addButtonsIfAbsent(AnchorPane pane, Button... buttons) {
		for (Button button : buttons) {
			if (!pane.getChildren().contains(button)) {
				pane.getChildren().add(button);
			}
		}
	}

	/**
	 * 设置 Convert 按钮的点击事件逻辑。
	 *
	 * @param ui      UI 实例
	 * @param convert Convert 按钮
	 */
	private void setupConvertButtonAction(UI ui, Button convert) {
		convert.setOnAction(event -> {
			if (ui.isAwake().get()) {
				ui.setPetState("Sleep"); // 切换到休眠状态
				ui.isAwake().set(false); // 更新状态
			} else {
				ui.customToggleAwakeState(); // 切换唤醒状态
			}
		});
	}

	/**
	 * 获取当前的 UI 实例。
	 * （假设存在此方法，根据实际情况实现）
	 *
	 * @return UI 实例
	 */
	private UI getUI() {
		// 实现获取 UI 实例的逻辑
		return null; // 需要根据实际情况修改
	}

	/**
	 * 主方法，启动 JavaFX 应用。
	 *
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		launch(args);
	}
}

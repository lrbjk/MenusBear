package app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * EventListener 类负责处理与宠物图片相关的鼠标事件，
 * 根据点击位置触发不同的动画和消息。
 */
public class EventListener implements EventHandler<MouseEvent> {
	private ImageView imageView; // 宠物的图片视图
	private UI ui; // 引用 UI 对象，用于更新界面和显示消息
	private int gifID = 0; // 当前显示的动画编号，0 表示无动画
	private double time = 0; // 动画持续时间（秒）

	/**
	 * 构造方法，初始化 ImageView 和 UI 对象。
	 *
	 * @param imgView 宠物的图片视图
	 * @param ui      UI 对象
	 */
	public EventListener(ImageView imgView, UI ui) {
		this.imageView = imgView;
		this.ui = ui;
	}

	/**
	 * 处理鼠标点击事件，根据点击位置触发相应的动画和消息。
	 *
	 * @param e 鼠标事件
	 */
	@Override
	public void handle(MouseEvent e) {
		// 如果当前有动画正在播放，则不处理新的点击事件
		if (gifID != 0) return;

		double x = e.getX(); // 获取点击的 X 坐标
		double y = e.getY(); // 获取点击的 Y 坐标

		beerBehavior(x, y); // 根据点击位置触发行为
		loadImg(gifID, time); // 加载并显示对应的动画
	}

	/**
	 * 根据点击的坐标位置设置动画编号和持续时间，并显示相应的消息。
	 *
	 * @param x 点击的 X 坐标
	 * @param y 点击的 Y 坐标
	 */
	public void beerBehavior(double x, double y) {
		// 判断点击区域并设置对应的动画和消息
		if (((x > 0 && x < 90) || (x > 150 && x < 200)) && y > 100 && y < 125) {
			gifID = 1;
			time = 1.7;
			ui.addMessageBox("嗨~~");
		} else if (((x > 130 && x < 150) || (x > 90 && x < 110)) && y > 125 && y < 135) {
			gifID = 2;
			time = 1.7;
			ui.addMessageBox("休息一下~");
		} else if (((x > 135 && x < 145) || (x > 95 && x < 100)) && y > 35 && y < 50) {
			gifID = 3;
			time = 1.7;
			ui.addMessageBox("开始读书！");
		} else if ((x > 0 && x < 90) && y < 20) {
			// 点击左耳，显示随机表情
			ui.showRandomEmoji("leftEar");
			time = 2;
		} else if ((x > 150 && x < 200) && y < 20) {
			// 点击右耳，显示随机表情
			ui.showRandomEmoji("rightEar");
			time = 2;
		}
	}

	/**
	 * 根据 gifID 加载对应的动画图片，并在指定时间后切换回主图。
	 *
	 * @param gifID 动画编号，0 表示无动画
	 * @param time  动画持续时间（秒）
	 */
	public void loadImg(int gifID, double time) {
		this.gifID = gifID;
		this.time = time;

		if (gifID != 0) {
			// 加载对应的动画图片
			Image newImage = new Image(this.getClass().getResourceAsStream("/beer/MB" + gifID + ".gif"));
			imageView.setImage(newImage);

			// 创建一个持续指定时间的时间轴，结束后切换回主图
			new Timeline(new KeyFrame(Duration.seconds(time), ae -> mainimg(0))).play();
		}
	}

	/**
	 * 切换回主图，并重置动画编号。
	 *
	 * @param key 主图的编号，0 表示默认主图
	 */
	public void mainimg(int key) {
		// 加载主图
		Image newImage = new Image(this.getClass().getResourceAsStream("/beer/MB" + key + ".gif"));
		imageView.setImage(newImage);

		// 重置动画编号，表示动画已完成
		if (key == 0) gifID = 0;
	}
}

package app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class EventListener implements javafx.event.EventHandler<MouseEvent> {
	private ImageView imageView;
	private UI ui; // 新增：引用UI对象
	int gifID = 0; // 图片编号
	double time =0;
	public EventListener(ImageView imgView, UI ui) {
		this.imageView = imgView;
		this.ui = ui; // 接收UI对象
	}

	@Override
	public void handle(MouseEvent e) {
		if (gifID != 0) return; // 如果动作没做完，就不允许再做新的动作
		double x = e.getX();
		double y = e.getY();
		beerBehavior(x, y); // 触发行为
		loadImg(gifID,time); // 显示图片
	}

	public void beerBehavior(double x, double y) {

		if (((x > 0 && x < 90) || (x > 150 && x < 200)) && y > 100 && y < 125) {
			gifID = 1;
			time = 1.5;
			ui.addMessageBox("嗨~~");
		}else if(((x > 130 && x < 150)||(x > 90 && x < 110)) && y > 125 && y < 135){
			gifID = 2;
			time = 1.5;
			ui.addMessageBox("休息一下~");
		} else if (((x > 140 && x < 145)||(x > 95 && x < 100)) && y > 35 && y < 50) {
			gifID = 3;
			time = 1.5;
			ui.addMessageBox("开始读书！");
		} else if ((x > 0 && x < 90) && y < 20) {
			ui.showRandomEmoji("leftEar");
			time = 2;
		} else if ((x > 150 && x < 200) && y < 20) {
			ui.showRandomEmoji("rightEar");
			time = 2;
		}
	}

	// 点击部位后加载图片
	public void loadImg(int gifID,double time) {
		this.gifID = gifID;
		this.time = time;
		if (gifID != 0) {
			Image newImage = new Image(this.getClass().getResourceAsStream("/beer/MB" + gifID + ".gif"));
			imageView.setImage(newImage);
			// 中断动图的播放，切换至主图
			new Timeline(new KeyFrame(Duration.seconds(time), ae -> mainimg(0))).play();
		}
	}

	// 主图，负责等待时和退出时的动作
	public void mainimg(int key) {
		Image newImage = new Image(this.getClass().getResourceAsStream("/beer/MB" + key + ".gif"));
		imageView.setImage(newImage);
		if (key == 0) gifID = 0;
	}
}

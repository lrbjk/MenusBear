package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class EventListener implements EventHandler<MouseEvent> {
	private ImageView imageView;
	int gifID = 0;//图片编号
	int petID = 0;//宠物ID
	double time = 3;//播放动画的时间
	public EventListener(ImageView imgView, int pet) {
		imageView=imgView;
		petID = pet;
	}
	public void handle(MouseEvent e) {
		if(gifID!=0) return;	//如果动作没做完，就不允许再做新的动作
		double x = e.getX();
		double y = e.getY();
//		System.out.println(x+" "+y);//测试眼睛等部位的位置
		//选择动作
		if(petID == 1) lxhBehavior(x,y);
		else biuBehavior(x,y);
		loadImg(petID,gifID,time);//显示图片
	}
	//罗小黑的动作
	public void lxhBehavior(double x,double y) {
		System.out.println(x +"-lrbsb-"+ y );
		if(x > 70 && x < 90 && y > 14 && y < 24){
			gifID = 2;
			time = 1;
		}
		else if(x > 140 && x < 164 && y > 14 && y < 24){
			gifID = 3;
			time = 1;
		}
		else if( x > 78 && x < 162 && y > 40 && y < 80){
			gifID = 5;
			time = 1;
		}
		else if( x > 94 && x < 146 && y > 88 && y < 115){
			gifID = 1;
			time = 1;
		}
		else if( x > 78 && x < 89 && y > 91 && y < 102){
			gifID = 4;
			time = 1;
		}
		else if( x > 146 && x < 153 && y > 91 && y < 102){
			gifID = 6;
			time = 1;
		}
		else {
			gifID = 0;
		}
	}
	//比丢的动作
	public void biuBehavior(double x,double y) {
		System.out.println(x +"-lrbsb-"+ y );
		if(x > 70 && x < 90 && y > 14 && y < 24){
			gifID = 2;
			time = 1;
		}
		else if(x > 140 && x < 164 && y > 14 && y < 24){
			gifID = 3;
			time = 1;
		}
		else if( x > 150 && x < 162 && y > 10 && y < 20){
			gifID = 5;
			time = 1;
		}
		else if( x > 94 && x < 146 && y > 88 && y < 115){
			gifID = 1;
			time = 1;
		}
		else if( x > 78 && x < 89 && y > 91 && y < 102){
			gifID = 4;
			time = 1;
		}
		else if( x > 146 && x < 153 && y > 91 && y < 102){
			gifID = 6;
			time = 1;
		}
		else {
			gifID = 0;
		}
	}
	//点击部位后加载图片
	public void loadImg(int petID,int gifID, double time) {
		this.gifID = gifID;
		if(gifID!=0) {
			Image newimage;
		if(petID==1)
			newimage = new Image(this.getClass().getResourceAsStream("/lxh/MB"+gifID+".gif"));
		else 
			newimage = new Image(this.getClass().getResourceAsStream("/biu/MB"+gifID+".gif"));
		System.out.println(petID + " lrb");
		System.out.println(newimage);
		imageView.setImage(newimage);
		//中断动图的播放，切换至主图
		new Timeline(new KeyFrame(Duration.seconds(time), ae ->mainimg(this.petID,0))).play();
		}
    }
	//主图，负责等待时和退出时的动作
	public void mainimg(int pet,int key) {
		Image newimage;
		if(pet==0)
			newimage = new Image(this.getClass().getResourceAsStream("/lxh/MB"+key+".gif"));
		else
			newimage = new Image(this.getClass().getResourceAsStream("/biu/MB"+key+".gif"));
		imageView.setImage(newimage);
		//这里是为了保证能做出新的动作，对应于handle方法的if(gifID!=0) return;
		//同时也是为了做其他动作时不被“自行走动”和“自娱自乐”打断
		if(key == 0) gifID=0;
    }
	

}

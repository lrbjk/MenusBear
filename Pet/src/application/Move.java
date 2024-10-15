package application;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Move extends Thread{
	private long time;
	private ImageView imageView;
	private int direID;
    double x;
    double maxx;
    double width;
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    Stage stage;
	private EventListener listen;
	private int petID;
	boolean exit;
	
	public Move(long time, ImageView imgView, int dire, Stage primaryStage, EventListener el) {
		this.time = time;
		imageView = imgView;
		direID = dire;
		stage = primaryStage;
		listen = el;
		petID = listen.petID;//??petID?????Move???petID
	}
	
	public void run() {
		//????????
		/*???listen.petID????????????????int petID = listen.petID;
		 *???????????????��?????��??????????petID???????????listen.petID???????????????
		 *?????????listen.mainimg(petID,0)?????????????��??????????????petID??????petID??
		 */
		imageView.addEventHandler(MouseEvent.MOUSE_PRESSED,
				e ->{exit = true;listen.mainimg(listen.petID, 0);});
		while(!exit) {
			//???petID!=listen.petID????????��????????????????????
			if(petID!=listen.petID) {
				exit=true;
				return;
			}
		    width = imageView.getBoundsInLocal().getMaxX();
		    x = stage.getX();
		    maxx = screenBounds.getMaxX();
			double speed=15;
	        if(x+speed+width >= maxx | x-speed<=0 | time<=0) {
	        	this.interrupt();
	        	listen.mainimg(listen.petID, 0);
	        	return;
	        }
	        if(direID == 0) {	//??????
	        	stage.setX(x-speed);
	        }
	        else if(direID == 1) {	//??????
	        	stage.setX(x+speed);
	        }
	        time -= 300;
	        try {
			     Thread.sleep(300);
			    } catch (InterruptedException e) {    
			     e.printStackTrace();
			    }
		}
	}
}

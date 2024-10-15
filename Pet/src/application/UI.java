package application;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UI implements Runnable {
	private ImageView imageView;
	private int petID;
	private EventListener listen;
	private VBox messageBox;
	private CheckboxMenuItem itemWalkable;
	private CheckboxMenuItem autoPlay;
	private CheckboxMenuItem itemSay;
	private MenuItem itemSwitch;
	private Stage primaryStage;
	Thread thread;
	double x;
	String[] lxhStrings= {
			"�����ġ�����",
			"����������~",
			"����С��ս�ǡ���ô��û����",
			"��ʦ����",
			"���������˿���������ڰ��Ҵ��è����"
	};
	String[] biuStrings = {
			"��Զ�������",
			"biu~",
			"����С�Ǽ�",
			"�ȶ���ô�ɰ�����ô�����۸��ȶ�"
	};
	public UI(ImageView view, int pet, EventListener el, Stage s) {
		imageView = view;
		petID = pet;
		listen = el;
		primaryStage = s;
	}
	
	//����ϵͳ����
	public void setTray(Stage stage) {
        SystemTray tray = SystemTray.getSystemTray();
        BufferedImage image;//����ͼ��
		try {
			// Ϊ��������һ���Ҽ������˵�
			PopupMenu popMenu = new PopupMenu();
			popMenu.setFont(new Font("΢���ź�", Font.PLAIN,18));
			
			itemSwitch = new MenuItem("�л�����");
			itemSwitch.addActionListener(e -> switchPet());
			
			itemWalkable = new CheckboxMenuItem("�����߶�");
			autoPlay = new CheckboxMenuItem("��������");
			itemSay = new CheckboxMenuItem("������");
			//��"�����߶�"��"��������"��"������"����ͬʱ��Ч
			itemWalkable.addItemListener(il -> {
				if(itemWalkable.getState()) { 
					autoPlay.setEnabled(false);
					itemSay.setEnabled(false);
				}
				else {
					autoPlay.setEnabled(true);
					itemSay.setEnabled(true);
				}
			});
			autoPlay.addItemListener(il -> {
				if(autoPlay.getState()) { 
					itemWalkable.setEnabled(false);
					itemSay.setEnabled(false);
				}
				else {
					itemWalkable.setEnabled(true);
					itemSay.setEnabled(true);
				}
			});
			itemSay.addItemListener(il -> {
				if(itemSay.getState()) { 
					itemWalkable.setEnabled(false);
					autoPlay.setEnabled(false);
				}
				else {
					itemWalkable.setEnabled(true);
					autoPlay.setEnabled(true);
				}
			});
			
			MenuItem itemShow = new MenuItem("��ʾ");
			itemShow.addActionListener(e -> Platform.runLater(() -> stage.show()));
			
			MenuItem itemHide = new MenuItem("����");
			//Ҫ��setImplicitExit(false)������stage.hide()��ֱ�ӹر�stage
			//stage.hide()��ͬ��stage.close()
			itemHide.addActionListener(e ->{Platform.setImplicitExit(false);
				Platform.runLater(() -> stage.hide());});
			
			MenuItem itemExit = new MenuItem("�˳�");
			itemExit.addActionListener(e -> end());
			
			popMenu.add(itemSwitch);
			popMenu.addSeparator();
			popMenu.add(itemWalkable);
			popMenu.add(autoPlay);
			popMenu.add(itemSay);
			popMenu.addSeparator();
			popMenu.add(itemShow);
			popMenu.add(itemHide);
			popMenu.add(itemExit);
			//��������ͼ��
			image = ImageIO.read(getClass().getResourceAsStream("icon.png"));
			TrayIcon trayIcon = new TrayIcon(image, "�������", popMenu);
	        trayIcon.setToolTip("�������");
	        trayIcon.setImageAutoSize(true);//�Զ�����ͼƬ��С���ⲽ����Ҫ����Ȼ��ʾ���ǿհ�
	        tray.add(trayIcon);
		} catch (IOException | AWTException e) {
			e.printStackTrace();
		}
	}
	
	//�л�����
	private void switchPet() {
		imageView.removeEventHandler(MouseEvent.MOUSE_CLICKED, listen);//�Ƴ�ԭ������¼�
		//�л�����ID
		if(petID == 0) { 
			petID = 1; //�л��ɱȶ�
			imageView.setFitHeight(150);
			imageView.setFitWidth(150);
		}
		else { 
			petID = 0; //�л�����С��
			imageView.setFitHeight(200);
			imageView.setFitWidth(200);
		}
//		listen = new EventListener(imageView,petID);
		/*
		 *�޸�listen.petID��Ϊ���޸�bug: ��������������֮һʱ����л����ͼƬ���л��������ﶯ������ֹͣ
		 *�Ҷ�����ɺ�ָ�����ͼ������һ�����ֱ����һ������ִ�вű�������
		 *ԭ���������������ܵ���listen.loadimg()ʱ���ݵ��Ǿ�petID��
		 */
		listen.petID = petID;
		listen.mainimg(petID,0);//�л����ó������ͼ��ͼƬ���Ϊ0��
		//��Ϊlisten�����ˣ�����Ҫ�������ӵ���¼�
		imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);
	}
	//�˳�����ʱչʾ����
	void end() {
		listen.mainimg(petID,99);//���ų���ĸ�𶯻������������Ϊ99��ͼƬ
		double time;
		//��С�ڵĸ�𶯻�1.5�룬�ȶ���3��
		if(petID == 0) time = 1.5;
		else time = 3;
		//Ҫ��Platform.runLater����Ȼ�ᱨ��Not on FX application thread;
		Platform.runLater(() ->setMsg("�ټ�~"));
		//����������ִ���˳�
		new Timeline(new KeyFrame(
			     Duration.seconds(time), 
			     ae ->System.exit(0)))
			    .play();
	}
	//������������
	public void addMessageBox(String message) {
		Label bubble = new Label(message);
		//�������ݵĿ��ȡ����û����䣬�ͻ�������ݶ���������Ӧ����
		bubble.setPrefWidth(100);
        bubble.setWrapText(true);//�Զ�����
        bubble.setStyle("-fx-background-color: DarkTurquoise; -fx-background-radius: 8px;");
        bubble.setPadding(new Insets(7));//��ǩ���ڱ߾�Ŀ���
        bubble.setFont(new javafx.scene.text.Font(14));
        Polygon triangle = new Polygon(
        		0.0, 0.0,
        		8.0, 10.0, 
        		16.0, 0.0);//�ֱ��������������������X��Y
        triangle.setFill(Color.DARKTURQUOISE);
        messageBox = new VBox();
//      VBox.setMargin(triangle, new Insets(0, 50, 0, 0));//���������ε�λ�ã�Ĭ�Ͼ���
        messageBox.getChildren().addAll(bubble, triangle);
        messageBox.setAlignment(Pos.BOTTOM_CENTER);
      	messageBox.setStyle("-fx-background:transparent;");
        //��������ڸ�������λ��
        messageBox.setLayoutX(0);
      	messageBox.setLayoutY(0);
      	messageBox.setVisible(true);
      	//�������ݵ���ʾʱ��
      	new Timeline(new KeyFrame(
			     Duration.seconds(8), 
			     ae ->{messageBox.setVisible(false);}))
			    .play();
	}
	
//�ö��߳���ʵ�� �������ʱ����ִ�С��Զ����ߡ����������֡���������Ĺ���
	public void run() {
		while(true) {
			Random rand = new Random();
			//��������Զ��¼����������ü��Ϊ9~24�롣Ҫע�����ʱ���������˶������ŵ�ʱ��
			long time = (rand.nextInt(15)+10)*1000;
			System.out.println("Waiting time:"+time);
			if(itemWalkable.getState() & listen.gifID == 0) {
				walk();
			}
			else if(autoPlay.getState() & listen.gifID == 0) {
				play();
			}
			else if(itemSay.getState() & listen.gifID == 0) {
				//���ѡ��Ҫ˵�Ļ�����ΪĿǰֻ������������Կ�������Ŀ�����
				String str = (petID == 0) ? lxhStrings[rand.nextInt(5)]:biuStrings[rand.nextInt(4)];
				Platform.runLater(() ->setMsg(str));
			}
			try {
				Thread.sleep(time);
			    } catch (InterruptedException e) {    
			     e.printStackTrace();
			    }
		} 
	}
	/*
	 * ִ��"������"�Ĺ��ܡ����ڳ����Ϸ���ʾ�Ի�����
	 * ��Ĭ�Ͽ����ǿ��ǵ��û����ܲ��뱻����
	 */
	public void setMsg(String msg) {
		
		Label lbl = (Label) messageBox.getChildren().get(0);
      	lbl.setText(msg);
      	messageBox.setVisible(true);
      	//�������ݵ���ʾʱ��
      	new Timeline(new KeyFrame(
			     Duration.seconds(4), 
			     ae ->{messageBox.setVisible(false);}))
			    .play();
	}
	
	/*
	 * ִ��"�����߶�"�Ĺ��ܡ�����ˮƽ�������߶�
	 * ��Ĭ�Ͽ����ǿ��ǵ��û�����ֻ����ﰲ������
	 */
	void walk(){
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		x = primaryStage.getX();//stage�����Ե����
		double maxx = screenBounds.getMaxX();//��ȡ��Ļ�Ĵ�С
		double width = imageView.getBoundsInLocal().getWidth();//��ȡimageView�Ŀ��ȣ�Ҳ��ʹ��.getMaxX();
		Random rand = new Random();
		double speed=10;//ÿ���ƶ��ľ���
		//�����Ҫ������Ļ��Ե��ͣ��
        if(x+speed+width >= maxx | x-speed<=0)
        	return;
        //��������ƶ���ʱ�䣬��λ΢��ms
		long time = (rand.nextInt(4)+3)*1000;
		System.out.println("Walking time:"+time);
		int direID = rand.nextInt(2);//�����������0Ϊ��1Ϊ��
		//�л�����Ӧ���������ͼ
		Image newimage;
		if(petID == 0)
			newimage = new Image(this.getClass().getResourceAsStream("/lxh/��С��w"+direID+".gif"));
		else {
			newimage = new Image(this.getClass().getResourceAsStream("/biu/biuw"+direID+".gif"));
		}
		imageView.setImage(newimage);
		//�ƶ�
		Move move = new Move(time, imageView, direID, primaryStage, listen);
		thread = new Thread(move);
		thread.start();
	}
	/*
	 * ִ��"��������"�Ĺ��ܡ�������ʱ���������
	 * �����Ͳ����ܲ�λ���������ƣ�Ҳ�����ó����Եô���
	 * ��Ĭ�Ͽ����ǿ��ǵ��û�����ֻ����ﰲ������
	 */
	void play() {
		Random rand = new Random();
		int gifID;
		double time = 4;
		//gifID�Ǹ���ͼƬ�ļ�������;δ�����ͼƬ�����趨�Ķ���������ȷ����
		if(petID == 0) {
			gifID = rand.nextInt(7)+5;
		}
		else
			gifID = rand.nextInt(7)+7;
		listen.loadImg(petID, gifID, time);
	}
	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public VBox getMessageBox() {
		return messageBox;
	}

	public void setMessageBox(VBox messageBox) {
		this.messageBox = messageBox;
	}
}

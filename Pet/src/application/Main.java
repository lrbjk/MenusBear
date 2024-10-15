package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	private static ImageView imageView;
	EventListener listen;
	VBox messageBox;
	int petID = 1;//����ID����С��=0���ȶ�=1
	double xOffset = 0; 
	double yOffset = 0;
	
	public void start(Stage primaryStage) {
		try {
			/*
			 * ������ʼ��ͼ
			 * �������·����ͼƬҪ��class.getResource����Ȼ����jar��ʱ�ᱨ�����Ҳ����ļ�·����
			 * ·���е�һ����/���Ǳ���ģ�����ʾ��ĸ�Ŀ¼�����ļ����ڴ���Ŀ����lxh��biu��ͬһ��
			 */
			Image image = new Image(this.getClass().getResourceAsStream("/biu/biu0.gif"));
	      	imageView = new ImageView(image); 
	      	imageView.setX(0); 
	      	imageView.setY(0);
	      	imageView.setLayoutX(0);
	      	imageView.setLayoutY(50);
	      	//����ͼƬ��ʾ�Ĵ�С
	      	imageView.setFitHeight(150); 
	      	imageView.setFitWidth(150); 
	      	//����ͼƬ�ĵ���¼�??
	      	listen = new EventListener(imageView , petID);
	      	imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen);
	      	
	      	imageView.setPreserveRatio(true); //���� width��height�ı���
	      	imageView.setStyle("-fx-background:transparent;");//����������Ϊ͸��
	      	
	      	UI ui = new UI(imageView, petID, listen,primaryStage);
	      	ui.addMessageBox("���߹~");
	      	
			AnchorPane pane = new AnchorPane(ui.getMessageBox(),ui.getImageView());
			
			pane.setStyle("-fx-background:transparent;");
			//ʹ�������϶����Ȼ�ȡ�������ʱ������p1���ٽ�����������Ϊp1���϶���λ����
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
			//���ô���ĳ�ʼλ��??
			primaryStage.setX(850);
			primaryStage.setY(400);
			primaryStage.setAlwaysOnTop(true);//��������ʾ����ǰ
			//�޸�������ͼ��
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			//�¾�����������ͼ�꣬��javafx��stage.initStyle(Style)ֻ����һ����Ч��ֻ������
//			stage.initStyle(StageStyle.UTILITY);
			primaryStage.initStyle(StageStyle.TRANSPARENT);//����͸��
			/*
			 * ����������ġ��رմ��ڡ�ʱ�����Ÿ�𶯻���ͬʱʹ���̵�ͼ��Ҳ�ر�.
			 * event.consume()�Ǳ���ģ���������������ֹWindow Close�¼���Ĭ�ϴ�����
			 * �������ʹ��System.exit(0);����Ҫevent.consume();
			*/
			primaryStage.setOnCloseRequest( event ->{event.consume(); ui.end();});
			primaryStage.show();
			
			ui.setTray(primaryStage);//����ϵͳ����
			Thread thread = new Thread(ui);
			thread.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}

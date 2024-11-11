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
         "欢迎来到喵星球",
         "喵~",
         "小猫要不要更多的吃的",
         "主人快来",
         "我爱你们永远不会腻"
   };
   String[] biuStrings = {
         "自动行走",
         "biu~",
         "超可爱",
         "怎么又要睡觉了呢"
   };
   
   public UI(ImageView view, int pet, EventListener el, Stage s) {
      imageView = view;
      petID = pet;
      listen = el;
      primaryStage = s;
   }
   
   // 创建系统托盘
   public void setTray(Stage stage) {
       SystemTray tray = SystemTray.getSystemTray();
       BufferedImage image; // 托盘图标
      try {
         // 为托盘创建一个弹出菜单
         PopupMenu popMenu = new PopupMenu();
         popMenu.setFont(new Font("微软雅黑", Font.PLAIN,18));
         
         itemSwitch = new MenuItem("切换宠物");
         itemSwitch.addActionListener(e -> switchPet());
         
         itemWalkable = new CheckboxMenuItem("可行走");
         autoPlay = new CheckboxMenuItem("自动播放");
         itemSay = new CheckboxMenuItem("说话");
         // "可行走"、"自动播放"和"说话"同时不生效
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
         
         MenuItem itemShow = new MenuItem("显示");
         itemShow.addActionListener(e -> Platform.runLater(() -> stage.show()));
         
         MenuItem itemHide = new MenuItem("隐藏");
         // 需要setImplicitExit(false)否则stage.hide()会直接关闭stage
         itemHide.addActionListener(e ->{Platform.setImplicitExit(false);
            Platform.runLater(() -> stage.hide());});
         
         MenuItem itemExit = new MenuItem("退出");
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
         // 加载托盘图标
         image = ImageIO.read(getClass().getResourceAsStream("icon.png"));
         TrayIcon trayIcon = new TrayIcon(image, "喵星球", popMenu);
           trayIcon.setToolTip("喵星球");
           trayIcon.setImageAutoSize(true); // 自动调整图标大小
           tray.add(trayIcon);
      } catch (IOException | AWTException e) {
         e.printStackTrace();
      }
   }
   
   // 切换宠物
   private void switchPet() {
      imageView.removeEventHandler(MouseEvent.MOUSE_CLICKED, listen); // 移除原有的事件监听
      // 切换宠物ID
      if(petID == 0) { 
         petID = 1; // 切换到小猫
         imageView.setFitHeight(150);
         imageView.setFitWidth(150);
      }
      else { 
         petID = 0; // 切换到小狗
         imageView.setFitHeight(200);
         imageView.setFitWidth(200);
      }
      listen.petID = petID;
      listen.mainimg(petID, 0); // 切换宠物的图片
      imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listen); // 重新添加事件监听
   }
   
   // 退出时显示的图片
   void end() {
      listen.mainimg(petID, 99); // 显示告别的图片
      double time;
      // 小狗告别时间为1.5秒，小猫为3秒
      if(petID == 0) time = 1.5;
      else time = 3;
      Platform.runLater(() -> setMsg("再见~"));
      // 等待后执行退出
      new Timeline(new KeyFrame(Duration.seconds(time), ae -> System.exit(0))).play();
   }
   
   // 添加消息框
   public void addMessageBox(String message) {
      Label bubble = new Label(message);
      bubble.setPrefWidth(100);
      bubble.setWrapText(true); // 自动换行
      bubble.setStyle("-fx-background-color: DarkTurquoise; -fx-background-radius: 8px;");
      bubble.setPadding(new Insets(7)); // 文字与边缘的间距
      bubble.setFont(new javafx.scene.text.Font(14));
      Polygon triangle = new Polygon(0.0, 0.0, 8.0, 10.0, 16.0, 0.0); // 话泡的指向三角形
      triangle.setFill(Color.DARKTURQUOISE);
      messageBox = new VBox();
      messageBox.getChildren().addAll(bubble, triangle);
      messageBox.setAlignment(Pos.BOTTOM_CENTER);
      messageBox.setStyle("-fx-background:transparent;");
      messageBox.setLayoutX(0);
      messageBox.setLayoutY(0);
      messageBox.setVisible(true);
      // 消息显示持续时间
      new Timeline(new KeyFrame(Duration.seconds(8), ae -> { messageBox.setVisible(false); })).play();
   }
   
// 线程实现 自动输入状态
   public void run() {
      while(true) {
         Random rand = new Random();
         long time = (rand.nextInt(15)+10) * 1000;
         System.out.println("Waiting time:" + time);
         if(itemWalkable.getState() && listen.gifID == 0) {
            walk();
         }
         else if(autoPlay.getState() && listen.gifID == 0) {
            play();
         }
         else if(itemSay.getState() && listen.gifID == 0) {
            String str = (petID == 0) ? lxhStrings[rand.nextInt(5)] : biuStrings[rand.nextInt(4)];
            Platform.runLater(() -> setMsg(str));
         }
         try {
            Thread.sleep(time);
         } catch (InterruptedException e) {    
            e.printStackTrace();
         }
      }
   }
   
   public void setMsg(String msg) {
      Label lbl = (Label) messageBox.getChildren().get(0);
      lbl.setText(msg);
      messageBox.setVisible(true);
      new Timeline(new KeyFrame(Duration.seconds(4), ae -> { messageBox.setVisible(false); })).play();
   }
   
   void walk() {
      Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
      x = primaryStage.getX(); // 获取stage的位置
      double maxx = screenBounds.getMaxX(); // 获取屏幕的宽度
      double width = imageView.getBoundsInLocal().getWidth(); 
      Random rand = new Random();
      double speed = 10; // 每次移动的速度
      // 当需要移动超出边界时
      if(x + speed + width >= maxx || x - speed <= 0) return;
      // 获取随机移动时间
      long time = (rand.nextInt(4) + 3) * 1000;
      System.out.println("Walking time:" + time);
      int direID = rand.nextInt(2); // 随机方向 0为左 1为右
      Image newimage;
      if(petID == 0)
         newimage = new Image(this.getClass().getResourceAsStream("/lxh/小狗walk" + direID + ".gif"));
      else {
         newimage = new Image(this.getClass().getResourceAsStream("/biu/biuw" + direID + ".gif"));
      }
      imageView.setImage(newimage);
      Move move = new Move(time, imageView, direID, primaryStage, listen);
      thread = new Thread(move);
      thread.start();
   }
   
   void play() {
      Random rand = new Random();
      int gifID;
      double time = 4;
      if(petID == 0) {
         gifID = rand.nextInt(7) + 5;
      }
      else
         gifID = rand.nextInt(7) + 7;
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


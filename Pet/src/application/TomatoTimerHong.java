package application;
import javax.swing.*;

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.layout.Background;
// import javafx.scene.layout.BackgroundImage;
// import javafx.scene.layout.BackgroundPosition;
// import javafx.scene.layout.BackgroundRepeat;
// import javafx.scene.layout.BackgroundSize;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class TomatoTimerHong{
    JFrame window1;
    JPanel forImage;
    ImageIcon icon;
    JLabel backgroundLabel;
    JPanel forContent;
    JPanel forText;
    JLabel label;
    JButton startButton;
    JButton stopButton;
    JButton resetButton;
    JButton setButton;
    JPanel imPanel;
    Boolean isCN = true;
    
    int recMinutes;
    int minutes; // 创建一个整型变量，用于存储分钟数
    int seconds; // 创建一个整型变量，用于存储秒数
    Timer timer;// 创建一个Timer对象，用于计时

    public TomatoTimerHong(boolean tempIsCN, int timeSet){
        //创建窗口
        String title = "番茄时钟";
        isCN = tempIsCN;
        recMinutes = timeSet;
        if(!tempIsCN)   title = "Tomato Timer";
        window1 = new JFrame(title);

        //初始化
        minutes = timeSet; // 初始化分钟数为25
        seconds = 0; // 初始化秒数为0
        // timer = new Timer(1000, new TimerListener()); // 创建一个间隔为1000ms的Timer对象，并为其添加ActionListener

        //一个用来放背景图片的Panel
        forImage = new JPanel();

        //加载图片
        // Image image = new ImageIcon("testBackground.jpg").getImage();
        // ImageIcon labIma = new ImageIcon("testBackground.jpg");
        icon = new ImageIcon("testBackground.png");

        //背景层内容
        backgroundLabel = new JLabel(icon);
        backgroundLabel.setBounds(0,0,400,200);
        // icon.setImage(icon.getImage().getScaledInstance(400, 300, Image.SCALE_DEFAULT));
        // forImage.setBounds(0,0);
        window1.getLayeredPane().add(backgroundLabel, Integer.valueOf(Integer.MIN_VALUE));
        window1.setResizable(false);

        //一个用来放按钮的Panel
        forContent = new JPanel(new GridLayout(1, 4));
        //一个用来放文字的Panel
        forText = new JPanel();
        forText.setOpaque(false);
        forText.setBackground(new Color(255,255,255,0));

        //文字
        label = new JLabel(minutes+":00", SwingConstants.CENTER); // 创建一个初始值为“25:00”的JLabel对象
        label.setFont(new Font("Times New Roman", Font.BOLD, 100));
        label.setForeground(Color.white);

        //按钮们
        String a = "开始";
        if(!tempIsCN)   a = "Begin";
        startButton = new JButton(a);
        a = "暂停";
        if(!tempIsCN)   a = "Pause";
        stopButton = new JButton(a);
        a = "重置";
        if(!tempIsCN)   a = "Reset";
        resetButton = new JButton(a);
        a = "设置时间";
        if(!tempIsCN)   a = "Options";
        setButton = new JButton(a);

        startButton.setBorderPainted(false); //设为边界透明
        stopButton.setBorderPainted(false); //设为边界透明
        resetButton.setBorderPainted(false); //设为边界透明
        setButton.setBorderPainted(false); //设为边界透明

        // startButton.setBorder(BorderFactory.createRaisedBevelBorder());

        startButton.setBackground(Color.white);
        stopButton.setBackground(Color.white);
        resetButton.setBackground(Color.white);
        setButton.setBackground(Color.white);

        startButton.addActionListener(new StartButtonListener()); // 为startButton添加ActionListener
        stopButton.addActionListener(new StopButtonListener()); // 为stopButton添加ActionListener
        resetButton.addActionListener(new ResetButtonListener()); // 为resetButton添加ActionListener
        setButton.addActionListener(new SetTimeButtonListener()); // 为setTimeButton添加ActionListener

        timer = new Timer(1000, new TimerListener()); // 创建一个间隔为1000ms的Timer对象，并为其添加ActionListener
 
        //在Panel中添加文字和按钮
        forText.add(label, BorderLayout.CENTER);
        forContent.add(startButton);
        forContent.add(stopButton);
        forContent.add(resetButton);
        forContent.add(setButton);

        //修改非背景Panel的透明度（好像没用）
        // window1.getContentPane().setOpaque(false);
        imPanel = (JPanel)window1.getContentPane();
        imPanel.setOpaque(false);
        forContent.setOpaque(false);
        forText.setOpaque(false);

        //把Panel添加到Frame上
        window1.add(forImage, BorderLayout.CENTER);
        window1.add(forContent, BorderLayout.SOUTH);
        window1.add(forText, BorderLayout.CENTER);
        

        //LayeredPane安排叠加顺序
        // layeredPane.add(forImage,JLayeredPane.DEFAULT_LAYER);
        // layeredPane.add(forContent,JLayeredPane.MODAL_LAYER);
        // layeredPane.add(forText,JLayeredPane.MODAL_LAYER);

        // window1.add(layeredPane);

        //距离屏幕左边100个像素，上边100个像素，窗口的宽是300，高是300
		window1.setBounds(100,100,400,200);    //设置窗口1在屏幕上的位置及大小

		window1.setVisible(true);    //设置窗口可见
		window1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    //释放当前窗口
    }

    public void start() { // 启动番茄工作法计时器
        window1.setVisible(true); // 将窗口设置为可见
    }

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = "TomatoConfig.txt";
        try (Scanner sc = new Scanner(new FileReader(fileName))){
            Boolean tempIsCN = false;
            if(sc.nextLine().equals("CN")){
                tempIsCN = true;
            }
            int timeSet = Integer.parseInt(sc.nextLine());
            TomatoTimerHong tomatoTimerHong = new TomatoTimerHong(tempIsCN, timeSet);
		    tomatoTimerHong.start();
        }
	}

    private class StartButtonListener implements ActionListener { // 开始按钮的ActionListener
        public void actionPerformed(ActionEvent e) {
            timer.start(); // 启动计时器
        }
    }

    private class StopButtonListener implements ActionListener { // 停止按钮的ActionListener
        public void actionPerformed(ActionEvent e) {
            timer.stop(); // 停止计时器
        }
    }

    private class ResetButtonListener implements ActionListener { // 重置按钮的ActionListener
        public void actionPerformed(ActionEvent e) {
            timer.stop(); // 停止计时器
            minutes = recMinutes; // 将分钟数重置为25
            seconds = 0; // 将秒数重置为0
            label.setText(String.format("%02d:%02d", minutes, seconds)); // 更新label的文本
        }
    }

    private class SetTimeButtonListener implements ActionListener { // 设置时间按钮的ActionListener
        public void actionPerformed(ActionEvent e) {
            String str = "Enter time in minutes (max 60):";
            if(isCN)    str = "请输入时间（不超过60分钟）";
            String input = JOptionPane.showInputDialog(window1, str); // 弹出一个对话框，提示用户输入分钟数
            try {
                int newMinutes = Integer.parseInt(input); // 将用户输入的字符串转换为整型
                if (newMinutes > 0 && newMinutes <= 60) { // 如果输入的分钟数在1到60之间
                    minutes = newMinutes; // 更新分钟数
                    recMinutes = newMinutes;
                    seconds = 0; // 将秒数重置为0
                    label.setText(String.format("%02d:%02d", minutes, seconds)); // 更新label的文本
                } else { // 如果输入的分钟数不在1到60之间
                    str = "Invalid input. Please enter an integer between 1 and 60.";
                    if(isCN)    str = "无效输入，请输入1-60之间的数字";
                    JOptionPane.showMessageDialog(window1, str); // 弹出一个对话框，提示用户输入1到60之间的数字
                }
            } catch (NumberFormatException ex) { // 如果用户输入的不是数字
                str = "Invalid input. Please enter an integer between 1 and 60.";
                    if(isCN)    str = "无效输入，请输入1-60之间的数字";
                JOptionPane.showMessageDialog(window1, str); // 弹出一个对话框，提示用户输入1到60之间的数字
            }
        }
    }

    private class TimerListener implements ActionListener { // 计时器的ActionListener
        public void actionPerformed(ActionEvent e) {
            if (minutes == 0 && seconds == 0) { // 如果时间到了
                timer.stop(); // 停止计时器
                String str = "Times Up!";
                if(isCN)    str = "时间到！";
                JOptionPane.showMessageDialog(window1, str); // 弹出一个对话框，提示用户时间到了
            } else if (seconds == 0) { // 如果秒数为0
                minutes--; // 分钟数减1
                seconds = 59; // 秒数重置为59
            } else { // 如果秒数不为0
                seconds--; // 秒数减1
            }
            label.setText(String.format("%02d:%02d", minutes, seconds)); // 更新label的文本
        }
    }
 } 
 
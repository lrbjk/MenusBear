import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class TomatoTimer {
    private JFrame frame; // 创建一个JFrame对象，用于显示番茄工作法计时器
    private JLabel label; // 创建一个JLabel对象，用于显示计时器的时间
    private int minutes; // 创建一个整型变量，用于存储分钟数
    private int seconds; // 创建一个整型变量，用于存储秒数
    private Timer timer; // 创建一个Timer对象，用于计时

    public TomatoTimer() { // 构造函数，用于初始化番茄工作法计时器
        frame = new JFrame("番茄工作法"); // 创建一个标题为“番茄工作法”的JFrame对象
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭窗口时退出程序
        frame.setSize(300, 200); // 设置窗口大小为300x200
        frame.setLayout(new BorderLayout()); // 设置窗口布局为BorderLayout

        label = new JLabel("25:00", SwingConstants.CENTER); // 创建一个初始值为“25:00”的JLabel对象
        label.setFont(new Font("Arial", Font.BOLD, 50)); // 设置字体为Arial，加粗，大小为50
        frame.add(label, BorderLayout.CENTER); // 将label添加到窗口中央

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4)); // 创建一个GridLayout为1x4的JPanel对象
        JButton startButton = new JButton("开始"); // 创建一个文本为“开始”的JButton对象
        startButton.addActionListener(new StartButtonListener()); // 为startButton添加ActionListener
        buttonPanel.add(startButton); // 将startButton添加到buttonPanel中
        JButton stopButton = new JButton("停止"); // 创建一个文本为“停止”的JButton对象
        stopButton.addActionListener(new StopButtonListener()); // 为stopButton添加ActionListener
        buttonPanel.add(stopButton); // 将stopButton添加到buttonPanel中
        JButton resetButton = new JButton("重置"); // 创建一个文本为“重置”的JButton对象
        resetButton.addActionListener(new ResetButtonListener()); // 为resetButton添加ActionListener
        buttonPanel.add(resetButton); // 将resetButton添加到buttonPanel中
        JButton setTimeButton = new JButton("设置时间"); // 创建一个文本为“设置时间”的JButton对象
        setTimeButton.addActionListener(new SetTimeButtonListener()); // 为setTimeButton添加ActionListener
        buttonPanel.add(setTimeButton); // 将setTimeButton添加到buttonPanel中
        frame.add(buttonPanel, BorderLayout.SOUTH); // 将buttonPanel添加到窗口南侧

        minutes = 25; // 初始化分钟数为25
        seconds = 0; // 初始化秒数为0
        timer = new Timer(1000, new TimerListener()); // 创建一个间隔为1000ms的Timer对象，并为其添加ActionListener
    }

    public void start() { // 启动番茄工作法计时器
        frame.setVisible(true); // 将窗口设置为可见
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
            minutes = 25; // 将分钟数重置为25
            seconds = 0; // 将秒数重置为0
            label.setText(String.format("%02d:%02d", minutes, seconds)); // 更新label的文本
        }
    }

    private class SetTimeButtonListener implements ActionListener { // 设置时间按钮的ActionListener
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(frame, "Enter time in minutes (max 60):"); // 弹出一个对话框，提示用户输入分钟数
            try {
                int newMinutes = Integer.parseInt(input); // 将用户输入的字符串转换为整型
                if (newMinutes > 0 && newMinutes <= 60) { // 如果输入的分钟数在1到60之间
                    minutes = newMinutes; // 更新分钟数
                    seconds = 0; // 将秒数重置为0
                    label.setText(String.format("%02d:%02d", minutes, seconds)); // 更新label的文本
                } else { // 如果输入的分钟数不在1到60之间
                    JOptionPane.showMessageDialog(frame, "无效输入，请输入1-60之间的数字"); // 弹出一个对话框，提示用户输入1到60之间的数字
                }
            } catch (NumberFormatException ex) { // 如果用户输入的不是数字
                JOptionPane.showMessageDialog(frame, "无效输入，请输入1-60之间的数字"); // 弹出一个对话框，提示用户输入1到60之间的数字
            }
        }
    }

    private class TimerListener implements ActionListener { // 计时器的ActionListener
        public void actionPerformed(ActionEvent e) {
            if (minutes == 0 && seconds == 0) { // 如果时间到了
                timer.stop(); // 停止计时器
                JOptionPane.showMessageDialog(frame, "时间到!"); // 弹出一个对话框，提示用户时间到了
            } else if (seconds == 0) { // 如果秒数为0
                minutes--; // 分钟数减1
                seconds = 59; // 秒数重置为59
            } else { // 如果秒数不为0
                seconds--; // 秒数减1
            }
            label.setText(String.format("%02d:%02d", minutes, seconds)); // 更新label的文本
        }
    }

    public static void main(String[] args) { // 主函数
        TomatoTimer timer = new TomatoTimer(); // 创建一个TomatoTimer对象
        timer.start(); // 启动番茄工作法计时器
    }
}



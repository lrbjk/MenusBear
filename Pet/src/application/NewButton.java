package application;

/*NewButton类，继承JButton类重写用于绘制按钮形状的函数*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NewButton extends JButton
{
   private String s;
   private int sizeX;
   private int sizeY;

   public NewButton(String s ,int sizeX,int sizeY)    //传递图片引用
    {
       super(s);
       this.sizeX = sizeX;
       this.sizeY = sizeY;
       setContentAreaFilled(false);
    }
    
   protected void paintComponent(Graphics g)    //绘制按钮内容
    {
       g.setColor(new Color(31,171,137));
       g.fillRoundRect(0,0,getSize().width-1,getSize().height-1,15,15);        //绘制一个圆角矩形getSize()为获取组件的大小
       //g.drawImage(img, 0,0,50, 40, null);      //除了形状外还可以为按钮绘制一个图片来美化按钮  
       super.paintComponent(g);	//使用父类函数绘制一个焦点框
    }
    
   protected void paintBorder(Graphics g)   //绘制按钮边框
    {
       g.drawRoundRect(0,0,getSize().width-1,getSize().height-1,15,15);
    }
}




/*
 * 昊：新的类（管理事件），可以通过菜单的按钮打开对应程序
 * 黎乐：美术需求表
 */
package com.sxt;

import javazoom.jl.decoder.JavaLayerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MyFrame extends JFrame implements KeyListener,Runnable {
    //用于存储所有的背景
    private List<BackGround> allBg = new ArrayList<>();
    //用于存储当前的背景
    private BackGround nowBg = new BackGround();
    //用于双缓存
    private Image offScreenImage = null;
    //马里奥对象
    private Mario mario = new Mario();
    //定义一个线程对象,用于实现马里奥的运动
    private Thread thread = new Thread(this);

    public MyFrame() {
        //设置窗口的大小为800 * 600
        this.setSize(800,600);
        //设置窗口居中显示
        this.setLocationRelativeTo(null);
        //设置窗口的可见性
        this.setVisible(true);
        //设置点击窗口上的关闭键,结束程序
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口大小不可变
        this.setResizable(false);
        //向窗口对象添加键盘监听器
        this.addKeyListener(this);
        //设置窗口名称
        this.setTitle("尚学堂超级玛丽");
        //初始化图片
        StaticValue.init();
        //初始化马里奥
        mario = new Mario(10,355);
        //创建全部的场景
        for (int i = 1;i <= 3;i++) {
            allBg.add(new BackGround(i, i == 3 ? true : false));
        }
        //将第一个场景设置为当前场景
        nowBg = allBg.get(0);
        mario.setBackGround(nowBg);
        //绘制图像
        repaint();
        thread.start();
        try {
            new Music();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = createImage(800,600);
        }

        Graphics graphics = offScreenImage.getGraphics();
        graphics.fillRect(0,0,800,600);

        //绘制背景
        graphics.drawImage(nowBg.getBgImage(),0,0,this);

        //绘制敌人
        for (Enemy e : nowBg.getEnemyList()) {
            graphics.drawImage(e.getShow(),e.getX(),e.getY(),this);
        }

        //绘制障碍物
        for (Obstacle ob : nowBg.getObstacleList()) {
            graphics.drawImage(ob.getShow(),ob.getX(),ob.getY(),this);
        }

        //绘制城堡
        graphics.drawImage(nowBg.getTower(),620,270,this);

        //绘制旗杆
        graphics.drawImage(nowBg.getGan(),500,220,this);

        //绘制马里奥
        graphics.drawImage(mario.getShow(),mario.getX(),mario.getY(),this);

        //添加分数
        Color c = graphics.getColor();
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("黑体",Font.BOLD,25));
        graphics.drawString("当前的分数为: " + mario.getScore(),300,100);
        graphics.setColor(c);

        //将图像绘制到窗口中
        g.drawImage(offScreenImage,0,0,this);
    }

    public static void main(String[] args) {
        MyFrame myFrame = new MyFrame();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
    //当键盘按下按键时调用
    @Override
    public void keyPressed(KeyEvent e) {
        //向右移动
        if (e.getKeyCode() == 39) {
            mario.rightMove();
        }
        //向左移动
        if (e.getKeyCode() == 37) {
            mario.leftMove();
        }
        //跳跃
        if (e.getKeyCode() == 38) {
            mario.jump();
        }
    }
    //当键盘松开按键时调用
    @Override
    public void keyReleased(KeyEvent e) {
        //想左停止
        if (e.getKeyCode() == 37) {
            mario.leftStop();
        }
        //向右停止
        if (e.getKeyCode() == 39) {
            mario.rightStop();
        }
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(50);

                if (mario.getX() >= 775) {
                    nowBg = allBg.get(nowBg.getSort());
                    mario.setBackGround(nowBg);
                    mario.setX(10);
                    mario.setY(355);
                }

                //判断马里奥是否死亡
                if (mario.isDeath()) {
                    JOptionPane.showMessageDialog(this,"马里奥死亡!!!");
                    System.exit(0);
                }

                //判断游戏是否结束
                if (mario.isOK()) {
                    JOptionPane.showMessageDialog(this,"恭喜你!成功通关了");
                    System.exit(0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

package Test;
 
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
 
/*
 * ly  2014-11-20
 * 该类实时发送截屏消失，多线程实现，不包含鼠标信息，且没有做对每个Client做优化处理
 */
public  class SendScreenImg extends Thread
{
    public static int SERVERPORT=8004;
    private ServerSocket serverSocket;
    private Robot robot;
    public  Dimension screen;
    public Rectangle rect ;
    private Socket socket; 
     
    public static void main(String args[])
    {
        new SendScreenImg(SERVERPORT).start();
    }
     
    //构造方法  开启套接字连接      机器人robot   获取屏幕大小
    public SendScreenImg(int SERVERPORT)
    {
        try {
            serverSocket = new ServerSocket(SERVERPORT);
            serverSocket.setSoTimeout(864000000);
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        screen = Toolkit.getDefaultToolkit().getScreenSize();  //获取主屏幕的大小
        rect = new Rectangle(screen);                          //构造屏幕大小的矩形
    }
     
    @Override
    public void run() 
    {
        //实时等待接收截屏消息
        while(true)
        {
            try{
                socket = serverSocket.accept();
                System.out.println("学生端口已经连接");
                ZipOutputStream zip = new ZipOutputStream(new DataOutputStream(socket.getOutputStream()));
                zip.setLevel(9);     //设置压缩级别
                 
                BufferedImage img = robot.createScreenCapture(rect);
                try{
                BufferedImage cursor= ImageIO.read(new File("src/img/fac.png"));  //把鼠标加载到缓存中
                Point p= MouseInfo.getPointerInfo().getLocation();               //获取鼠标坐标
                img.createGraphics().drawImage(cursor, p.x, p.y, null);
                }catch(Exception e){
                	System.out.println(e.getMessage());
                }
                zip.putNextEntry(new ZipEntry("test.jpg"));
                ImageIO.write(img, "jpg", zip);
                if(zip!=null)zip.close();
                System.out.println("Client正在实时连接");
                
            } catch (IOException ioe) {
            	System.out.println("连接断开");
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {e.printStackTrace();}
                }
            }
        }
    }
}
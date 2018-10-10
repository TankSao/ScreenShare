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
 * ����ʵʱ���ͽ�����ʧ�����߳�ʵ�֣������������Ϣ����û������ÿ��Client���Ż�����
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
     
    //���췽��  �����׽�������      ������robot   ��ȡ��Ļ��С
    public SendScreenImg(int SERVERPORT)
    {
        try {
            serverSocket = new ServerSocket(SERVERPORT);
            serverSocket.setSoTimeout(864000000);
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
        }
        screen = Toolkit.getDefaultToolkit().getScreenSize();  //��ȡ����Ļ�Ĵ�С
        rect = new Rectangle(screen);                          //������Ļ��С�ľ���
    }
     
    @Override
    public void run() 
    {
        //ʵʱ�ȴ����ս�����Ϣ
        while(true)
        {
            try{
                socket = serverSocket.accept();
                System.out.println("ѧ���˿��Ѿ�����");
                ZipOutputStream zip = new ZipOutputStream(new DataOutputStream(socket.getOutputStream()));
                zip.setLevel(9);     //����ѹ������
                 
                BufferedImage img = robot.createScreenCapture(rect);
                try{
                BufferedImage cursor= ImageIO.read(new File("src/img/fac.png"));  //�������ص�������
                Point p= MouseInfo.getPointerInfo().getLocation();               //��ȡ�������
                img.createGraphics().drawImage(cursor, p.x, p.y, null);
                }catch(Exception e){
                	System.out.println(e.getMessage());
                }
                zip.putNextEntry(new ZipEntry("test.jpg"));
                ImageIO.write(img, "jpg", zip);
                if(zip!=null)zip.close();
                System.out.println("Client����ʵʱ����");
                
            } catch (IOException ioe) {
            	System.out.println("���ӶϿ�");
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
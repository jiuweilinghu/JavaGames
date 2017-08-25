import java.awt.*;
import javax.swing.*;

public class Bullet {
    private static final int BULLET_SPEED_Y = 60;//子弹的y轴速度
    public int positionX = 655;          //子弹的坐标(x,y)
    public int positionY = 655;        //初始时子弹在屏幕外面
    private Image[] image = new Image[4];//子弹图片数组
    private int frameID = 0;          //当前帧的ID

    public Bullet() {
        for (int i = 0; i < 4; i++) {
            java.net.URL imgURL = Bullet.class.getResource("/images/bullet_" + i + ".png");
            ImageIcon imgIcon = new ImageIcon(imgURL);
            image[i] = imgIcon.getImage();
        }
    }

    /**
     * 初始化坐标
     **/
    public void init(int x, int y) {
        positionX = x;
        positionY = y;
    }

    /**
     * 绘制子弹
     **/
    public void drawBullet(Graphics g, JPanel i) {
        g.drawImage(image[frameID++], positionX, positionY, i);
        if (frameID == 4) {
            frameID = 0;
        }
    }

    /**
     * 更新子弹坐标点
     **/
    public void updateBullet() {
        positionY -= BULLET_SPEED_Y;//这里只更新y轴坐标
    }
}

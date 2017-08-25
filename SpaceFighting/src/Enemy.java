import java.awt.*;
import javax.swing.*;

public class Enemy {
    /**
     * 敌机的存活状态
     **/
    public static final int ENEMY_ALIVE = 0;
    /**
     * 敌机的死亡状态
     **/
    public static final int ENEMY_DEATH = 1;
    /**
     * 敌机的y轴速度
     **/
    static final int ENEMY_SPEED_Y = 20;
    /**
     * 敌机的位置坐标(x,y)
     **/
    public int posX = 0;
    public int posY = 0;
    /**
     * 敌机的状态
     **/
    public int state = ENEMY_ALIVE;   //一开始为存活状态
    /**
     * 敌机爆炸图片数组
     **/
    private Image[] enemyExplore = new Image[4];
    /**
     * 当前帧的ID
     **/
    public int enemyFrameID = 0;

    public Enemy() {
        for (int i = 0; i < 4; i++) {
            java.net.URL imgURL = Enemy.class.getResource("/images/bomb" + i + ".png");
            ImageIcon img = new ImageIcon(imgURL);
            enemyExplore[i] = img.getImage();
        }
    }

    /**
     * 初始化坐标
     **/
    public void init(int x, int y) {
        posX = x;
        posY = y;
        state = ENEMY_ALIVE;
        enemyFrameID = 0;
    }

    /**
     * 绘制敌机
     **/
    public void drawEnemy(Graphics g, JPanel i) {
        //当敌机状态为死亡且死亡动画播放完毕
        if (state == ENEMY_DEATH && enemyFrameID < 4) {
            g.drawImage(enemyExplore[enemyFrameID++], posX, posY, i);
            return;
        } else {
            //当敌机状态为存活
            java.net.URL imgURL = Enemy.class.getResource("/images/enemy.png");
            ImageIcon img = new ImageIcon(imgURL);
            Image pic = img.getImage();
            g.drawImage(pic, posX, posY, i);
        }
    }

    /**
     * 更新敌机坐标
     **/
    public void updateEnemy() {
        posY += ENEMY_SPEED_Y;
    }
}


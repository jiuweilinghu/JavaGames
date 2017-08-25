import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    /**
     * 屏幕的宽和高
     **/
    private int screenWidth = 400;
    private int screenHeight = 654;
    /**
     * 游戏状态:
     * gameState=0   游戏结束
     * gameState=-1  游戏暂停
     * gameState=1   游戏进行
     **/
    private int gameState = 1;
    /**
     * 游戏背景资源
     **/
    private Image background0 = null;
    private Image background1 = null;
    private Image gameOver = null;
    private Image gamePause = null;
    /**
     * 记录两张背景滚动时的y坐标
     **/
    private int bgPosY0 = 0;
    private int bgPosY1 = 0;
    /**
     * 子弹对象的数量
     **/
    final static int BULLET_COUNT = 8;
    /**
     * 飞机移动的步长
     **/
    final static int PLANE_STEP_X = 15;
    final static int PLANE_STEP_Y = 20;
    /**
     * 发射一颗子弹
     **/
    final static int SHOT_TIME = 500;
    /**
     * 敌机的数量
     **/
    final static int ENEMY_COUNT = 4;
    /**
     * 敌机偏移量
     **/
    final static int ENEMY_OFF = 100;
    /**
     * 游戏主线程
     **/
    private Thread mainThread = null;
    /**
     * 飞机在屏幕中的坐标
     **/
    public int planePosX = 0;
    public int planePosY = 0;
    /**
     * 敌机对象数组
     **/
    Enemy[] enemyArray = null;
    /**
     * 子弹对象数组
     **/
    Bullet[] bulletArray = null;
    /**
     * 初始化发射子弹ID
     **/
    public int shotID = 0;
    /**
     * 上一颗子弹发射时间
     **/
    public Long shotTime = 0L;
    /**
     * 玩家飞机的图片
     **/
    Image airplane = null;


    /**
     * init()方法，初始化各种对象，
     * 包括两张背景图片background0和background1，
     * 通过两张背景图片的切换实现游戏背景动态移动效果。
     * 初始化玩家飞机坐标。
     * 创建敌机对象数组enemyArray[]
     * 创建子弹类对象数组bulletArray[]
     **/
    private void init() {
        /**游戏背景**/
        java.net.URL imgURL;
        ImageIcon img;

        imgURL = GamePanel.class.getResource("/images/background0.png");
        img = new ImageIcon(imgURL);
        background0 = img.getImage();

        imgURL = GamePanel.class.getResource("/images/background1.png");
        img = new ImageIcon(imgURL);
        background1 = img.getImage();

        imgURL = GamePanel.class.getResource("/images/gameOver.png");
        img = new ImageIcon(imgURL);
        gameOver = img.getImage();

        imgURL = GamePanel.class.getResource("/images/pause.png");
        img = new ImageIcon(imgURL);
        gamePause = img.getImage();


        /**第一张图片紧贴在屏幕(0,0,)处，第二张在第一张上方**/
        bgPosY0 = 0;
        bgPosY1 = -screenHeight;
        /**初始化玩家飞机坐标**/
        planePosX = 200;
        planePosY = 500;
        /**初始化玩家飞机图片**/
        imgURL = GamePanel.class.getResource("/images/fox.png");
        img = new ImageIcon(imgURL);
        airplane = img.getImage();
        /**创建敌机对象**/
        enemyArray = new Enemy[ENEMY_COUNT];
        for (int i = 0; i < ENEMY_COUNT; i++) {
            enemyArray[i] = new Enemy();
            enemyArray[i].init(i * ENEMY_OFF, (i % 2) * 50);
        }
        /**创建子弹对象**/
        bulletArray = new Bullet[BULLET_COUNT];
        for (int i = 0; i < BULLET_COUNT; i++) {
            bulletArray[i] = new Bullet();
        }
        shotTime = System.currentTimeMillis();
    }


    /**
     * Draw()方法绘制游戏界面，包括背景、敌我飞机、子弹，更新游戏逻辑
     **/
    protected void Draw() {
        repaint();//重绘游戏界面
        updateBg();//更新游戏逻辑
    }


    /**
     * 重写paint()方法，绘制游戏地图，绘制玩家飞机，
     * 绘制所有子弹的动画及所有敌方飞机的动画
     **/

    public void paint(Graphics g) {
        if (gameState == 1) { //正常游戏
            /**绘制游戏地图**/
            g.drawImage(background0, 0, bgPosY0, this);
            g.drawImage(background1, 0, bgPosY1, this);
            /**绘制玩家飞机动画**/
            g.drawImage(airplane, planePosX, planePosY, this);
            /**绘制子弹动画**/
            for (int i = 0; i < BULLET_COUNT; i++) {
                bulletArray[i].drawBullet(g, this);
            }
            /**绘制敌机动画**/
            for (int i = 0; i < ENEMY_COUNT; i++) {
                enemyArray[i].drawEnemy(g, this);
            }
        } else if (gameState == 0) {//游戏结束
            g.drawImage(gameOver, 0, 0, this);
        } else {//游戏暂停
            g.drawImage(gamePause, 0, 0, this);
        }
    }

    /**
     * updateBg()方法更新游戏逻辑。
     * 更新游戏背景图片位置，实现向下滚动效果
     * 更新子弹位置
     * 更新敌机位置
     * 敌机死亡则重置敌机坐标位置
     * 敌机超过屏幕还未死亡则游戏结束
     * 添加一发子弹并初始化其位置坐标
     * 最后调用Collision()方法检测子弹与敌机的碰撞
     **/

    private void updateBg() {
        /**更新背景图片，实现向下滚动**/
        bgPosY0 += 2;
        bgPosY1 += 2;
        if (bgPosY0 == screenHeight) {
            bgPosY0 = 0;
            bgPosY1 = -screenHeight;
        }
        if (bgPosY1 == screenHeight) {
            bgPosY1 = -screenHeight;
        }
        /**更新子弹位置**/
        for (int i = 0; i < BULLET_COUNT; i++) {
            bulletArray[i].updateBullet();
        }
        /**更新敌机位置**/
        for (int i = 0; i < ENEMY_COUNT; i++) {
            enemyArray[i].updateEnemy();
            /**敌机死亡则重置坐标**/
            if (enemyArray[i].state == Enemy.ENEMY_DEATH
                    && enemyArray[i].enemyFrameID == 4) {
                enemyArray[i].init(getRandom(0, ENEMY_COUNT * ENEMY_OFF - 100), 0);
            } else if (enemyArray[i].posY >= screenHeight) {
                gameState = 0;
            }
        }
        /**根据时间初始化将要发射的子弹位置在玩家飞机前方**/
        if (shotID < BULLET_COUNT) {
            long now = System.currentTimeMillis();
            if (now - shotTime >= SHOT_TIME) {
                //每过一定时间发射一颗子弹，此子弹位置在玩家飞机前方
                bulletArray[shotID].init(planePosX + 35, planePosY - 40);
                shotTime = now;
                shotID++;
            }
        } else {
            shotID = 0;
        }
        Collision();//子弹与飞机的碰撞检测
    }

    /**
     * Collision()方法检测子弹与敌机的碰撞
     **/
    public void Collision() {
        for (int i = 0; i < BULLET_COUNT; i++) {
            for (int j = 0; j < ENEMY_COUNT; j++) {
                if (bulletArray[i].positionX >= enemyArray[j].posX
                        && bulletArray[i].positionX <= enemyArray[j].posX + 98
                        && bulletArray[i].positionY <= enemyArray[j].posY
                        && bulletArray[i].positionY >= enemyArray[j].posY - 124) {
                    enemyArray[j].state = Enemy.ENEMY_DEATH;
                }
            }
        }
    }

    /**
     * getRandom()方法返回（bottom,top）区间的一个随机数
     **/
    private int getRandom(int bottom, int top) {
        return ((Math.abs(new Random().nextInt()) % (top - bottom)) + bottom);
    }


    /**
     * 构造方法，设置游戏屏幕区域大小，
     * 调用init()方法初始化各种对象，最后启动游戏线程
     **/
    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        //设置焦点在本窗体并赋予监听对象
        setFocusable(true);
        addKeyListener(this);
        init();
        mainThread = new Thread(this);   //线程实例
        /**启动游戏线程**/
        mainThread.start();
        setVisible(true);
    }

    /**
     * run()方法，适当延时后调用Draw()方法刷新游戏屏幕
     **/
    public void run() {
        while (true) {
            /**刷新屏幕**/
            Draw();
            //延时
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        //假如向上键被按下
        if (key == KeyEvent.VK_UP) {
            planePosY -= PLANE_STEP_Y;
            if (planePosY < 0) {   //超出上边界
                planePosY = 0;
            }
        }
        //假如向下键被按下
        if (key == KeyEvent.VK_DOWN) {
            planePosY += PLANE_STEP_Y;
            if (planePosY > screenHeight - 124) {   //超出下边界
                planePosY = screenHeight - 124;
            }
        }
        //假如向左键被按下
        if (key == KeyEvent.VK_LEFT) {
            planePosX -= PLANE_STEP_X;
            if (planePosX < 0) {   //超出左边界
                planePosX = 0;
            }
        }
        //假如向右键被按下
        if (key == KeyEvent.VK_RIGHT) {
            planePosX += PLANE_STEP_X;
            if (planePosX > screenWidth - 100) {   //超出右边界
                planePosX = screenWidth - 100;
            }
        }
        //假如空格键被按下
        if (key == KeyEvent.VK_SPACE) {
            //游戏暂停
            gameState = -gameState;
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }
}
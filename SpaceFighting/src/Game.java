import javax.swing.JFrame ;
import java.awt.Container ;

public class Game extends JFrame {
    public Game (){
        setTitle("SpaceFighting") ;   //窗体标题
        setSize(200,654) ;
        //获得自定义面板实例
        GamePanel panel =new GamePanel() ;
        Container contentPane=getContentPane() ;
        contentPane .add(panel);
        pack();
    }
    public static void main(String[] args){
        Game game=new Game() ;
        //设定允许窗体关闭操作
        game.setDefaultCloseOperation(JFrame .EXIT_ON_CLOSE );
        //设置窗体初始位置，null为居中
        game.setLocationRelativeTo(null);
        //显示窗体
        game.setVisible(true) ;
    }
}

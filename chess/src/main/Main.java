package main;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        GamePanel gp = new GamePanel();
        window.add(gp); 
        window.pack();

        window.setVisible(true);
        window.setLocationRelativeTo(null);
        
        gp.launchGame();
    }
}

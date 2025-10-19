import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minigame extends JPanel implements ActionListener, KeyListener {
    static final int MINIGAME_WIDTH = 960;
    static final int MINIGAME_HEIGHT = 960;
    static final int MILLISECONDS_PER_FRAME = 20;

    int speed;

    Timer mainGame;
    Timer timer;

    Minigame(Timer mainGame, int speed, String name) { // Timer reference is needed to restart the main game.
        setPreferredSize(new Dimension(MINIGAME_WIDTH, MINIGAME_HEIGHT));

        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Needed to end program
        frame.add(this); // Connects the JPanel and JFrame
        frame.setResizable(false);
        frame.pack(); // Sets the size of the frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.mainGame = mainGame;
        this.speed = speed;

        timer = new Timer(MILLISECONDS_PER_FRAME, this);
        timer.start();
    }

    void ResumeGame() {
        JFrame minigameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        minigameFrame.dispose();
        timer.stop(); // Finish this minigame.
        mainGame.start(); // Start the main game again.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ResumeGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
}

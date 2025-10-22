import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minigame extends JPanel implements ActionListener, KeyListener {
    static final int MINIGAME_WIDTH = 960;
    static final int MINIGAME_HEIGHT = 960;
    static final int MILLISECONDS_PER_FRAME = 20;

    int speed;

    KeysOfSurvival mainGame;
    Timer timer;

    JProgressBar progressBar;

    JFrame frame;

    Minigame(KeysOfSurvival mainGame, int speed, String name, int time) { // Timer reference is needed to restart the main game.
        setPreferredSize(new Dimension(MINIGAME_WIDTH, MINIGAME_HEIGHT));

        frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Needed to end program
        frame.add(this); // Connects the JPanel and JFrame
        frame.setResizable(false);
        frame.pack(); // Sets the size of the frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        progressBar = new JProgressBar(0, time);
        progressBar.setValue(time);
        progressBar.setPreferredSize(new Dimension(MINIGAME_WIDTH, 20));
        add(progressBar, BorderLayout.NORTH);

        this.mainGame = mainGame;
        this.speed = speed;

        timer = new Timer(MILLISECONDS_PER_FRAME, this);
        timer.start();
    }

    void resumeGame() {
        JFrame minigameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        minigameFrame.dispose();
        timer.stop(); // Finish this minigame.
        mainGame.timer.start(); // Start the main game again.
    }

    void failGame() {
        JFrame minigameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        minigameFrame.dispose();
        timer.stop(); // Finish this minigame.
        mainGame.gameOver();
    }

    void progress() {
        progressBar.setValue(progressBar.getValue() - MILLISECONDS_PER_FRAME);
        if (progressBar.getValue() == 0) {
            failGame();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        progress();
    }
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JumpOver extends Minigame {
    Image background = new ImageIcon("Images/Minigame/Background1.png").getImage();
    Image player = new ImageIcon("Images/Minigame/Player1.png").getImage();
    Image[] zombie = {
        new ImageIcon("Images/Minigame/Zombie1.png").getImage(),
        new ImageIcon("Images/Minigame/Zombie2.png").getImage(),
        new ImageIcon("Images/Minigame/Zombie3.png").getImage()};
    
    int zombieFrame = 0;
    int zombieX = MINIGAME_WIDTH; // Zombie starts at the very right.

    static final int PLAYER_WIDTH = 80;
    static final int PLAYER_HEIGHT = 280;
    static final int PLAYER_X = 80;
    static final int PLAYER_Y = 560;
    static final int ZOMBIE_WIDTH = 152;
    static final int ZOMBIE_HEIGHT = 264;
    static final int ZOMBIE_Y = 580;
    
    int playerAltitude = 0;
    int playerVelocity = 0;
    static final int JUMP_FORCE = 40;
    static final int GRAVITY = 2;
    boolean jumping = false;

    JumpOver(KeysOfSurvival mainGame, int speed) {
        super(mainGame, speed, "⚠️ Jump Over!", 25000 / speed);
        frame.addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Initial painting
        g.drawImage(background, 0, 0, MINIGAME_WIDTH, MINIGAME_HEIGHT, this);
        g.drawImage(player, PLAYER_X, PLAYER_Y - playerAltitude, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        g.drawImage(zombie[zombieFrame / 2], zombieX, ZOMBIE_Y, ZOMBIE_WIDTH, ZOMBIE_HEIGHT, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e); // Progress bar

        // The following if-statement checks if the player touches the zombie.
        // Constants are added to shrink the zombie hitbox.
        if (zombieX <= PLAYER_X + PLAYER_WIDTH - 10
            && zombieX >= PLAYER_X - ZOMBIE_WIDTH + 20
            && playerAltitude <= PLAYER_Y - ZOMBIE_Y + PLAYER_HEIGHT - 10) {
            timer.stop();
            failGame();
        }

        zombieX -= speed;
        zombieFrame++;
        zombieFrame %= 6;

        if (zombieX < -ZOMBIE_WIDTH) {
            resumeGame();
        }

        if (jumping) {
            playerAltitude += playerVelocity;
            playerVelocity -= GRAVITY;
            if (playerAltitude <= 0) {
                playerAltitude = 0;
                playerVelocity = 0;
                jumping = false;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!jumping) {
            playerVelocity = JUMP_FORCE;
            jumping = true;
        }
    }
}

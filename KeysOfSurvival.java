import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

/**
 * 🗝️ KEYS OF SURVIVAL 🧟
 * 
 * This is the main game.
 * A detailing of features can be be found in the Readme.md file.
 * 
 * Usage:
 * - The game is an endless runner. It never ends unless the player hits an obstacle.
 * - The game gradually increases in speed. Details are in Countdowns.java.
 * - There are 3-5 lanes, depending on what was chosen in the main menu of Main.java.
 * - The player may go to adjacent lanes using the left and right arrow keys.
 * - There are three obstacle types:
 *   - Keys:    The player is not stopped by these, but rather collects these.
 *   - Doors:   If the player has a key of the matching color, his/her score increases.
 *              Otherwise, the player has to play a minigame.
 *   - Zombies: The player is stopped by this obstacle unless he dodges it in a minigame
 * - There are 4 different colors of keys and doors in the start.
 * - Gradually more colors are added until there are 8 colors.
 * - To pause, press P or Escape. 
 */
public class KeysOfSurvival extends JPanel implements ActionListener, KeyListener {
    private final KeysOfSurvival thisGame = this; // Reference to this game
    private final Random random = new Random();

    public static int NUMBER_OF_LANES;
    static final int FRAME_WIDTH = 600;
    static final int FRAME_HEIGHT = 960;
    static final int PLAYER_WIDTH = 150;
    static final int PLAYER_HEIGHT = 150;
    static final int PLAYER_Y = 600;
    static final int MILLISECONDS_PER_FRAME = 20;
    static final int NUMBER_OF_COLORS = 8;
    int currentNumberOfColors = 4;
    int speed = 10;
    static int currentLane;

    public static boolean isMuted = false;

    private final Countdowns countdowns = new Countdowns();
    static final int PLAYER_ANIMATION_DURATION = 4; // 0.08 seconds at starting speed
    static int playerAnimationCountdown = PLAYER_ANIMATION_DURATION;
    static int playerAnimationFrame = 0;
    static final int NUMBER_OF_PLAYER_SPRITES = new File("Images/Player").listFiles().length;

    // Images
    private Image backgroundImage;
    private int backgroundPosition = 0;
    private final Image[] playerImage = new Image[NUMBER_OF_PLAYER_SPRITES];
    private Image peopleIcon;
    private Image heartIcon;
    private Image zombieImage;
    private final Image[] doorImages = new Image[NUMBER_OF_COLORS]; // Closed door images
    private final Image[] doorImages2 = new Image[NUMBER_OF_COLORS]; // Opened door images
    private final Image[] keyImages = new Image[NUMBER_OF_COLORS];
    private final Image[] keyIcons = new Image[NUMBER_OF_COLORS];

    // Colors
    static final String[] COLOR_NAMES = 
        {"Red",
        "Green",
        "Blue",
        "Yellow",
        "Brown",
        "Orange",
        "White",
        "Icy"};
    static final Color[] COLORS = 
        {Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        new Color(102, 51, 0),
        Color.ORANGE,
        Color.WHITE,
        new Color(51, 204, 255)};

    private final ArrayDeque<Obstacle> obstacles = new ArrayDeque<>();
    private final int[] currentKeys = new int[NUMBER_OF_COLORS]; // Array of how many keys collected
    private int score = 0;
    private int health = 0;
    private int doorsOpened = 0;

    public javax.swing.Timer timer;
    private final JLabel pauseMessage; // Appears when game is paused.

    // 🪟 New taskbar + "SPEED UP!" variables
    private static final int TASKBAR_HEIGHT = 80;
    private long speedUpMessageTime = 0;
    private static final int SPEEDUP_DISPLAY_DURATION = 1000; // ms

    /** Constructor without mute. */
    public KeysOfSurvival(int numberOfLanes) {
        // Create the frame
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        JFrame frame = new JFrame("Keys of Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addKeyListener(this);

        NUMBER_OF_LANES = numberOfLanes;
        currentLane = numberOfLanes / 2; // Player starts at middle lane
        loadImages(numberOfLanes);

        pauseMessage = new JLabel("PAUSED", SwingConstants.CENTER);
        pauseMessage.setForeground(Color.WHITE);
        pauseMessage.setFont(new Font("Consolas", Font.BOLD, 64));
        pauseMessage.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

        timer = new javax.swing.Timer(MILLISECONDS_PER_FRAME, this);
        timer.start();
    }

    /** Constructor with mute option. */
    public KeysOfSurvival(int lanes, boolean isMuted) {
        this(lanes);
        this.isMuted = isMuted;
    }

    /** Load all game images. */
    private void loadImages(int numberOfLanes) {
        // Load player sprites
        for (int i = 0; i < NUMBER_OF_PLAYER_SPRITES; i++) {
            playerImage[i] = new ImageIcon("Images/Player/" + (i + 1) + ".png").getImage();
        }
        backgroundImage =
            new ImageIcon("Images/Backgrounds/Background" + numberOfLanes + ".png").getImage();
        zombieImage = new ImageIcon("Images/Zombie.png").getImage();
        peopleIcon = new ImageIcon("Images/Icons/People.png").getImage();
        heartIcon = new ImageIcon("Images/Icons/Heart.png").getImage();
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            doorImages[i] = new ImageIcon("Images/Doors/" + COLOR_NAMES[i] + ".png").getImage();
            doorImages2[i] = new ImageIcon("Images/Doors/" + COLOR_NAMES[i] + "2.png").getImage();
            keyImages[i] = new ImageIcon("Images/Keys/" + COLOR_NAMES[i] + ".png").getImage();
            keyIcons[i] = new ImageIcon("Images/Icons/Keys/" + COLOR_NAMES[i] + ".png").getImage();
        }
    }

    /** Play a sound if not muted */
    private void playSound(String soundFile) {
        if (isMuted) {
            return;
        }
        try {
            AudioInputStream audioInput =
                AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (Exception e) {
            System.out.println(soundFile + " couldn't play.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font(g2d.getFont().getFontName(), Font.PLAIN, 20));

        // Background
        g2d.drawImage(backgroundImage, 0, backgroundPosition, FRAME_WIDTH, FRAME_HEIGHT, this);
        g2d.drawImage(
            backgroundImage, 0, backgroundPosition - FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT, this);

        boolean playerDrawn = false;

        // Obstacles and player
        for (Obstacle obstacle : obstacles) {
            if (obstacle.y > PLAYER_Y && !playerDrawn) {
                playerDrawn = true;
                g2d.drawImage(playerImage[playerAnimationFrame],
                    ((FRAME_WIDTH / NUMBER_OF_LANES * (2 * currentLane + 1) - PLAYER_WIDTH)) / 2,
                    PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, this);
            }

            Image image = (obstacle instanceof Door)
                ? (((Door) obstacle).opened 
                    ? doorImages2[obstacle.color]
                    : doorImages[obstacle.color])
                : (obstacle instanceof Key ? keyImages[obstacle.color] : zombieImage);

            if (!(obstacle instanceof Key && ((Key) obstacle).obtained)) {
                g2d.drawImage(image,
                    ((FRAME_WIDTH / NUMBER_OF_LANES * (2 * obstacle.lane + 1) - PLAYER_WIDTH)) / 2,
                    obstacle.y, PLAYER_WIDTH, PLAYER_HEIGHT, this);
            }
        }

        if (!playerDrawn) {
            g2d.drawImage(playerImage[playerAnimationFrame],
                    ((FRAME_WIDTH / NUMBER_OF_LANES * (2 * currentLane + 1) - PLAYER_WIDTH)) / 2,
                    PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        }

        // 🖤 Gradient overlay for top UI
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 0, 0, 180),
                0, TASKBAR_HEIGHT, new Color(0, 0, 0, 0));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, FRAME_WIDTH, TASKBAR_HEIGHT);

        // UI: Score
        g2d.setColor(Color.WHITE);
        g2d.drawImage(peopleIcon, 20, 12, 24, 24, this);
        g2d.drawString("People saved: " + score, 52, 32);

        // UI: Hearts
        int heartY = 36;
        if (health > 8) {
            g2d.drawImage(heartIcon, 20, heartY, 24, 24, this);
            g2d.drawString("× " + health, 50, heartY + 18);
        } else {
            for (int i = 0; i < health; i++) {
                g2d.drawImage(heartIcon, 20 + 28 * i, heartY, 24, 24, this);
            }
        }

        // UI: Keys
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            if (currentKeys[i] > 0) {
                int x = FRAME_WIDTH - 210 + 50 * (i % 4);
                int y = 10 + 50 * (i / 4);
                g2d.drawImage(keyIcons[i], x, y, 40, 20, this);
                if (currentKeys[i] > 1) {
                    g2d.setColor(COLORS[i]);
                    g2d.drawString("" + currentKeys[i], x + 15, y + 40);
                }
            }
        }

        // ⚡ SPEED UP! Fade + Zoom + Glow effect
        long elapsed = System.currentTimeMillis() - speedUpMessageTime;
        if (elapsed < SPEEDUP_DISPLAY_DURATION) {
            float progress = (float) elapsed / SPEEDUP_DISPLAY_DURATION;
            int alpha = (int) (255 * (1 - progress)); // fade alpha 255 → 0
            alpha = Math.max(0, Math.min(alpha, 255));

            // Smooth zoom (slightly pulse in)
            float scale = 1.0f + 0.3f * (1 - progress); // start bigger, shrink down
            Font originalFont = g2d.getFont();
            Font bigFont = new Font("Consolas", Font.BOLD, 72); // larger, bolder
            g2d.setFont(bigFont.deriveFont(AffineTransform.getScaleInstance(scale, scale)));

            int textX = FRAME_WIDTH / 2 - 180;
            int textY = FRAME_HEIGHT / 2;

            // Glow layer (soft yellow outer glow)
            for (int i = 4; i > 0; i--) {
                g2d.setColor(new Color(255, 220, 100, Math.max(0, alpha - i * 40)));
                g2d.drawString("SPEED UP!", textX - i, textY - i);
                g2d.drawString("SPEED UP!", textX + i, textY + i);
            }

            // Main text (white core)
            g2d.setColor(new Color(255, 255, 255, alpha));
            g2d.drawString("SPEED UP!", textX, textY);

            g2d.setFont(originalFont);
        }

    }

    /** Spawn a door, key, or zombie */
    void spawnDoor() {
        obstacles.addFirst(new Door(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT)); 
    }
    void spawnKey() {
        obstacles.addFirst(new Key(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT));
    }
    void spawnZombie() {
        obstacles.addFirst(new Zombie(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT));
    }

    /** Game loop */
    @Override
    public void actionPerformed(ActionEvent e) {
        byte countdownAction = countdowns.countdown(speed);

        if (countdownAction >= 16) {
            countdownAction -= 16;
            if (currentNumberOfColors < 8) {
                currentNumberOfColors++;
            }
        }
        if (countdownAction >= 8) {
            countdownAction -= 8;
            if (speed < 50) {
                speed += 5;
                speedUpMessageTime = System.currentTimeMillis(); // Trigger "SPEED UP!"
            }
        }
        if (countdownAction >= 4) { 
            countdownAction -= 4;
            spawnZombie();
        }
        if (countdownAction >= 2) {
            countdownAction -= 2;
            spawnKey();
        }
        if (countdownAction >= 1) {
            countdownAction -= 1;
            spawnDoor();
        }

        playerAnimationCountdown -= speed / 10;
        if (playerAnimationCountdown < 1) {
            playerAnimationCountdown += PLAYER_ANIMATION_DURATION;
            playerAnimationFrame = (playerAnimationFrame + 1) % NUMBER_OF_PLAYER_SPRITES;
        }

        backgroundPosition = (backgroundPosition + speed) % FRAME_HEIGHT;
        obstacles.forEach(Obstacle::move);
        if (!obstacles.isEmpty() && obstacles.peekLast().y > FRAME_HEIGHT) {
            obstacles.removeLast();
        }

        repaint();
    }

    /** Handle player input */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && currentLane != 0) {
            currentLane--;
            playSound("Sounds/MoveLeftRight.wav");
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentLane != NUMBER_OF_LANES - 1) {
            currentLane++;
            playSound("Sounds/MoveLeftRight.wav");
        }
        if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (timer.isRunning()) {
                add(pauseMessage); repaint(); timer.stop();
            }
            else {
                remove(pauseMessage); timer.start();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}

    @Override public void keyTyped(KeyEvent e) {}

    /** Base obstacle class */
    class Obstacle {
        int lane;
        int y;
        int color;
        boolean passed = false;

        Obstacle(int lane, int y) {
            this.lane = lane; this.y = y;
        }

        void move() {
            y += speed;
        }
    }

    /** Door obstacle */
    class Door extends Obstacle {
        boolean opened = false;
        
        Door (int lane, int y) {
            super(lane, y);
            color = random.nextInt(currentNumberOfColors);
        }

        @Override
        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    if (currentKeys[color] == 0) {
                        playSound("/Sounds/HitObstacle.wav");
                        timer.stop();
                        int minigameInt = random.nextInt(4);
                        switch (minigameInt) {
                            case 1 -> new ConnectWires(thisGame, speed);
                            case 2 -> new ZombieTicTacToe(thisGame, speed);
                            case 3 -> new ZombieFruitSequenceMini(thisGame, speed);
                            default -> new TapMatchFruitsMini(thisGame, speed);
                        }
                    } else {
                        opened = true;
                        playSound("/Sounds/DoorOpens.wav");
                        currentKeys[color]--;
                        score++;
                        doorsOpened++;
                        if (doorsOpened % 5 == 0) {
                            health++;
                        }
                    }
                }
                passed = true;
            }
        }
    }

    /** Key obstacle */
    class Key extends Obstacle {
        boolean obtained = false;
        Key (int lane, int y) {
            super(lane, y);
            color = random.nextInt(currentNumberOfColors);
        }

        @Override
        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    currentKeys[color]++;
                    obtained = true;
                    playSound("/Sounds/KeyCollected.wav");
                }
                passed = true;
            }
        }
    }

    /** Zombie obstacle */
    class Zombie extends Obstacle {
        Zombie(int lane, int y) {
            super(lane, y);
        }

        @Override
        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    playSound("/Sounds/HitObstacle.wav");
                    timer.stop();
                    new JumpOver(thisGame, speed);
                }
                passed = true;
            }
        }
    }

    /** Handle game over */
    void gameOver() {
        if (health >= 3) {
            int option = JOptionPane.showConfirmDialog(this,
                    "You hit an obstacle.\nUse 3 hearts to continue?",
                    "Continue?", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                health -= 3;
                timer.start();
                return;
            }
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Game Over!\nScore: " + score + "\nGo to main menu?",
                "Game Over!", JOptionPane.YES_NO_OPTION);
        JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        gameFrame.dispose();
        if (option == JOptionPane.YES_OPTION) {
            new Main();
        } else {
            System.exit(0);
        }
    }
}

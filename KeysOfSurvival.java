import java.awt.*;       
import java.awt.event.*; 
import java.io.File;     
import java.util.*;      
import javax.swing.*;
import javax.sound.sampled.*;

public class KeysOfSurvival extends JPanel implements ActionListener, KeyListener {
    private final KeysOfSurvival thisGame = this;
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

    public static boolean isMuted = false; // Mute flag

    private final Countdowns countdowns = new Countdowns();
    static final int PLAYER_ANIMATION_DURATION = 4;
    static int playerAnimationCountdown = PLAYER_ANIMATION_DURATION;
    static int playerAnimationFrame = 0;
    static final int NUMBER_OF_PLAYER_SPRITES = new File("Images/Player").listFiles().length;

    private Image backgroundImage;
    private int backgroundPosition = 0; 
    private final Image[] playerImage = new Image[NUMBER_OF_PLAYER_SPRITES];
    private Image peopleIcon;
    private Image heartIcon;
    private Image zombieImage;
    private final Image[] doorImages = new Image[NUMBER_OF_COLORS];
    private final Image[] doorImages2 = new Image[NUMBER_OF_COLORS];
    private final Image[] keyImages = new Image[NUMBER_OF_COLORS];
    private final Image[] keyIcons = new Image[NUMBER_OF_COLORS];

    static final String[] COLOR_NAMES = {"Red","Green","Blue","Yellow","Brown","Orange","White","Icy"};
    static final Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, new Color(102,51,0), Color.ORANGE, Color.WHITE, new Color(51,204,255)};

    private final ArrayDeque<Obstacle> obstacles = new ArrayDeque<>();
    private final int[] currentKeys = new int[NUMBER_OF_COLORS];
    private int score = 0;
    private int health = 0;
    private int doorsOpened = 0;

    private javax.swing.Timer timer;
    private final JLabel pauseMessage;

    /** Constructor without mute */
    public KeysOfSurvival(int numberOfLanes) {
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
        currentLane = numberOfLanes / 2;
        loadImages(numberOfLanes);

        timer = new javax.swing.Timer(MILLISECONDS_PER_FRAME, this);
        timer.start();

        pauseMessage = new JLabel("PAUSED", SwingConstants.CENTER);
        pauseMessage.setForeground(Color.WHITE);
        pauseMessage.setFont(new Font("Consolas", Font.BOLD, 64));
        pauseMessage.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
    }

    /** Constructor with mute option */
    public KeysOfSurvival(int lanes, boolean isMuted) {
        this(lanes);
        this.isMuted = isMuted;
    }

    /** Load all game images */
    private void loadImages(int numberOfLanes) {
        for (int i = 0; i < NUMBER_OF_PLAYER_SPRITES; i++) {
            playerImage[i] = new ImageIcon("Images/Player/" + (i+1) + ".png").getImage();
        }
        backgroundImage = new ImageIcon("Images/Backgrounds/Background" + numberOfLanes + ".png").getImage();
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
        if (isMuted) return;
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
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
        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 20));

        // Draw background
        g.drawImage(backgroundImage, 0, backgroundPosition, FRAME_WIDTH, FRAME_HEIGHT, this);
        g.drawImage(backgroundImage, 0, backgroundPosition - FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT, this);

        boolean playerDrawn = false;

        // Draw obstacles and player
        for (Obstacle obstacle : obstacles) {
            if (obstacle.y > PLAYER_Y && !playerDrawn) {
                playerDrawn = true;
                g.drawImage(playerImage[playerAnimationFrame],
                        ((FRAME_WIDTH/NUMBER_OF_LANES*(2*currentLane+1)-PLAYER_WIDTH))/2,
                        PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, this);
            }
            Image image = (obstacle instanceof Door) ? 
                    (((Door) obstacle).opened ? doorImages2[obstacle.color] : doorImages[obstacle.color]) :
                    (obstacle instanceof Key ? keyImages[obstacle.color] : zombieImage);
            if (!(obstacle instanceof Key && ((Key) obstacle).obtained)) {
                g.drawImage(image,
                        ((FRAME_WIDTH/NUMBER_OF_LANES*(2*obstacle.lane+1)-PLAYER_WIDTH))/2,
                        obstacle.y, PLAYER_WIDTH, PLAYER_HEIGHT, this);
            }
        }

        if (!playerDrawn) {
            g.drawImage(playerImage[playerAnimationFrame],
                    ((FRAME_WIDTH/NUMBER_OF_LANES*(2*currentLane+1)-PLAYER_WIDTH))/2,
                    PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        }

        // Draw score, health, and keys
        g.setColor(Color.WHITE);
        g.drawImage(peopleIcon, 20, 12, 20, 20, this);
        g.drawString("People saved: " + score, 42, 28);

        if (health > 8) {
            g.drawImage(heartIcon, 20, 30, 20, 20, this);
            g.drawString("× " + health, 46, 46);
        } else {
            for (int i = 0; i < health; i++) {
                g.drawImage(heartIcon, 20 + 30*i, 30, 20, 20, this);
            }
        }

        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            if (currentKeys[i] > 0) {
                g.drawImage(keyIcons[i], FRAME_WIDTH-210 + 50*(i%4), 10+50*(i/4), 40, 20, this);
                if (currentKeys[i] > 1) {
                    g.setColor(COLORS[i]);
                    g.drawString("" + currentKeys[i], FRAME_WIDTH-205 + 50*(i%4), 50 + 50*(i/4));
                }
            }
        }
    }

    /** Spawn a door, key, or zombie */
    void spawnDoor() { obstacles.addFirst(new Door(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT)); }
    void spawnKey() { obstacles.addFirst(new Key(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT)); }
    void spawnZombie() { obstacles.addFirst(new Zombie(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT)); }

    /** Game loop executed every timer tick */
    @Override
    public void actionPerformed(ActionEvent e) {
        byte countdownAction = countdowns.countdown(speed);

        if (countdownAction >= 16) { countdownAction -= 16; if (currentNumberOfColors < 8) currentNumberOfColors++; }
        if (countdownAction >= 8)  { countdownAction -= 8; if (speed < 50) speed += 5; }
        if (countdownAction >= 4)  { countdownAction -= 4; spawnZombie(); }
        if (countdownAction >= 2)  { countdownAction -= 2; spawnKey(); }
        if (countdownAction >= 1)  { countdownAction -= 1; spawnDoor(); }

        playerAnimationCountdown -= speed/10;
        if (playerAnimationCountdown < 1) {
            playerAnimationCountdown += PLAYER_ANIMATION_DURATION;
            playerAnimationFrame = (playerAnimationFrame + 1) % NUMBER_OF_PLAYER_SPRITES;
        }

        backgroundPosition = (backgroundPosition + speed) % FRAME_HEIGHT;
        obstacles.forEach(Obstacle::move);
        if (!obstacles.isEmpty() && obstacles.peekLast().y > FRAME_HEIGHT) obstacles.removeLast();

        repaint();
    }

    /** Handle player input */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && currentLane != 0) { currentLane--; playSound("Sounds/MoveLeftRight.wav"); }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentLane != NUMBER_OF_LANES-1) { currentLane++; playSound("Sounds/MoveLeftRight.wav"); }
        if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (timer.isRunning()) { add(pauseMessage); repaint(); timer.stop(); }
            else { remove(pauseMessage); timer.start(); }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    /** Base obstacle class */
    class Obstacle {
        int lane, y, color = 0;
        boolean passed = false;

        Obstacle(int lane, int y) { this.lane = lane; this.y = y; }
        void move() { y += speed; }
    }

    /** Door obstacle */
    class Door extends Obstacle {
        boolean opened = false;
        Door(int lane, int y) { super(lane,y); color = random.nextInt(currentNumberOfColors); }

        @Override
        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    if (currentKeys[color] == 0) {
                        playSound("/Sounds/HitObstacle.wav");
                        timer.stop();
                        int minigameInt = random.nextInt(4);
                        switch(minigameInt) {
                            case 1 -> new ConnectWires(thisGame, speed);
                            case 2 -> new ZombieTicTacToe(thisGame, speed);
                            case 3 -> new ZombieFruitSequenceMini(thisGame, speed);
                            default -> new TapMatchFruitsMini(thisGame, speed);
                        }
                    } else {
                        opened = true;
                        playSound("/Sounds/DoorOpens.wav");
                        currentKeys[color]--; score++; doorsOpened++;
                        if (doorsOpened % 5 == 0) health++;
                    }
                }
                passed = true;
            }
        }
    }

    /** Key obstacle */
    class Key extends Obstacle {
        boolean obtained = false;
        Key(int lane, int y) { super(lane, y); color = random.nextInt(currentNumberOfColors); }

        @Override
        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) { currentKeys[color]++; obtained = true; playSound("/Sounds/KeyCollected.wav"); }
                passed = true;
            }
        }
    }

    /** Zombie obstacle */
    class Zombie extends Obstacle {
        Zombie(int lane, int y) { super(lane, y); }

        @Override
        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) { playSound("/Sounds/HitObstacle.wav"); timer.stop(); new JumpOver(thisGame, speed); }
                passed = true;
            }
        }
    }

    /** Handle game over sequence */
    void gameOver() {
        if (health >= 3) {
            int option = JOptionPane.showConfirmDialog(this,
                    "You hit an obstacle.\nUse 3 hearts to continue?",
                    "Continue?", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) { health -= 3; timer.start(); return; }
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Game Over!\nScore: " + score + "\nGo to main menu?",
                "Game Over!", JOptionPane.YES_NO_OPTION);
        JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        gameFrame.dispose();
        if (option == JOptionPane.YES_OPTION) new Main();
        else System.exit(0);
    }
}

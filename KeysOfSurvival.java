import java.awt.*;       // For Color, Image, Dimension
import java.awt.event.*; // For ActionListener, KeyListener
import java.io.File;     // For File
import java.util.*;      // For Random, ArrayList
import javax.swing.*;

public class KeysOfSurvival extends JPanel implements ActionListener, KeyListener {
    JPanel panel = this; // Reference to the game panel
    Random random = new Random(); // Used for randomly generating obstacles.

    public static int NUMBER_OF_LANES = 3;
    // These are the number of lanes that are taken into account for the game.
    
    static int currentLane = NUMBER_OF_LANES / 2;
    // The player starts in the middle lane
    
    static final int FRAME_WIDTH = 600;
    static final int FRAME_HEIGHT = 960;
    // These are the frame dimensions.

    static final int PLAYER_WIDTH = 150;
    static final int PLAYER_HEIGHT = 150;
    // These are the dimensions of the player character.

    static final int PLAYER_Y = 600;
    // The vertical position in which the player is rendered

    static final int MILLISECONDS_PER_FRAME = 20;
    // This is the length of each interval processed by the game in milliseconds.

    static int speed = 10; 
    // A measure of how fast the game runs.

    static final int NUMBER_OF_COLORS = 4;
    // This is the number of different door colors used in the game.

    int doorSpawnCooldown = 600;
    int keySpawnCooldown = 300;
    int zombieSpawnCooldown = 450 + 300 * random.nextInt(8);
    
    static final int SPEED_UP_COUNTDOWN_TIME = 600; // One minute roughly
    int speedUpCountdown = SPEED_UP_COUNTDOWN_TIME;
    
    static final int PLAYER_ANIMATION_DURATION = 4;
    static int playerAnimationCountdown = PLAYER_ANIMATION_DURATION;
    static int playerAnimationFrame = 0;
    
    static final int NUMBER_OF_PLAYER_SPRITES = new File("Images/Player").listFiles().length;

    Image backgroundImage;
    int backgroundPosition = 0; // Controls movement of background image 
    Image[] playerImage = new Image[NUMBER_OF_PLAYER_SPRITES];
    Image peopleIcon;
    Image heartIcon;
    Image zombieImage;
    Image[] doorImages = new Image[NUMBER_OF_COLORS];
    Image[] keyImages = new Image[NUMBER_OF_COLORS];
    Image[] keyIcons = new Image[NUMBER_OF_COLORS];

    static final String[] COLOR_NAMES = {"Red", "Green", "Blue", "Yellow"}; // Names of colors used
    static final Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}; // Colors used

    javax.swing.Timer timer;
    
    ArrayDeque<Obstacle> obstacles = new ArrayDeque<Obstacle>();
    int[] currentKeys = new int[NUMBER_OF_COLORS]; // Number of keys of each color

    int score = 0;
    int health = 0;
    int doorsOpened = 0;

    KeysOfSurvival() {
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        JFrame frame = new JFrame("Keys of Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Needed to end program
        frame.add(this); // Connects the JPanel and JFrame
        frame.setResizable(false);
        frame.pack(); // Sets the size of the frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addKeyListener(this);

        loadImages();
        timer = new javax.swing.Timer(MILLISECONDS_PER_FRAME, this);
        timer.start();
    }

    void loadImages() {
        for (int i = 0; i < NUMBER_OF_PLAYER_SPRITES; i++) {
            playerImage[i] = new ImageIcon("Images/Player/" + (i + 1) + ".png").getImage();
        }

        backgroundImage = new ImageIcon("Images/Background.png").getImage();
        zombieImage = new ImageIcon("Images/Zombie.png").getImage();
        peopleIcon = new ImageIcon("Images/Icons/People.png").getImage();
        heartIcon = new ImageIcon("Images/Icons/Heart.png").getImage();

        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            doorImages[i] = new ImageIcon("Images/Doors/" + COLOR_NAMES[i] + ".png").getImage();
            keyImages[i] = new ImageIcon("Images/Keys/" + COLOR_NAMES[i] + ".png").getImage();
            keyIcons[i] = new ImageIcon("Images/Icons/Keys/" + COLOR_NAMES[i] + ".png").getImage();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Initial painting
        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 20)); // Increase font size

        // Draw background
        // Background needs to be drawn twice to give an "endless background" illusion .
        g.drawImage(backgroundImage, 0, backgroundPosition, FRAME_WIDTH, FRAME_HEIGHT, this);
        g.drawImage(backgroundImage,
            0, backgroundPosition - FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT, this);
        
        boolean playerDrawn = false;

        // Draw obstacles and player
        for (Obstacle obstacle : obstacles) {
            if (obstacle.y > PLAYER_Y && !playerDrawn) {
                playerDrawn = true;
                // Draw player
                g.drawImage(playerImage[playerAnimationFrame],
                    ((FRAME_WIDTH / NUMBER_OF_LANES * (2 * currentLane + 1) - PLAYER_WIDTH)) / 2,
                    PLAYER_Y,
                    PLAYER_WIDTH,
                    PLAYER_HEIGHT,
                    this);
            }
            Image image = zombieImage;
            if (obstacle instanceof Door) {
                image = doorImages[obstacle.color];
            } else if (obstacle instanceof Key) {
                image = keyImages[obstacle.color];
            }
            if (!(obstacle instanceof Key && ((Key) obstacle).obtained)) {
                // Keys that are obtained are not drawn.
                g.drawImage(image,
                    ((FRAME_WIDTH / NUMBER_OF_LANES * (2 * obstacle.lane + 1) - PLAYER_WIDTH)) / 2,
                    obstacle.y,
                    PLAYER_WIDTH,
                    PLAYER_HEIGHT, // For now, obstacles have identical dimensions to player.
                    this);
            }
        }

        if (!playerDrawn) {
            // Draw player
            g.drawImage(playerImage[playerAnimationFrame],
                ((FRAME_WIDTH / NUMBER_OF_LANES * (2 * currentLane + 1) - PLAYER_WIDTH)) / 2,
                PLAYER_Y,
                PLAYER_WIDTH,
                PLAYER_HEIGHT,
                this);
        }
        
        // Write score
        g.setColor(Color.WHITE);
        g.drawImage(peopleIcon, 20, 12, 20, 20, this);
        g.drawString("People saved: " + score, 42, 28);

        // Show health
        if (health > 8) { // If there are too many hearts, show them differently
            g.drawImage(heartIcon, 20, 30, 20, 20, this);
            g.drawString("× " + health, 46, 46);
        } else {
            for (int i = 0; i < health; i++) {
                g.drawImage(heartIcon, 20 + 30 * i, 30, 20, 20, this);
            }
        }

        // Show keys
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            if (currentKeys[i] > 0) {
                g.drawImage(keyIcons[i], FRAME_WIDTH - 210 + 50 * i, 10, 40, 20, this);
                if (currentKeys[i] > 1) {
                    g.setColor(COLORS[i]);
                    g.drawString("" + currentKeys[i], FRAME_WIDTH - 205 + 50 * i, 50);
                }
            }
        }
    }

    void spawnDoor() {
        obstacles.addFirst(new Door(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT));
    }

    void spawnKey() {
        obstacles.addFirst(new Key(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT));
    }

    void spawnZombie() {
        obstacles.addFirst(new Zombie(random.nextInt(NUMBER_OF_LANES), -PLAYER_HEIGHT));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Countdown for each obstacle type. If countdown reaches 0, spawn that obstacle.
        doorSpawnCooldown -= speed;
        if (doorSpawnCooldown < 1) {
            doorSpawnCooldown += 600;
            spawnDoor();
        }
        
        keySpawnCooldown -= speed;
        if (keySpawnCooldown < 1) {
            keySpawnCooldown += 600;
            spawnKey();
        }

        zombieSpawnCooldown -= speed;
        if (zombieSpawnCooldown < 1) {
            zombieSpawnCooldown += 300 + 300 * random.nextInt(8);
            spawnZombie();
        }

        // Countdown for speeding up. Speed is capped at 50
        speedUpCountdown -= 1;
        if (!(speed >= 50) && speedUpCountdown < 1) {
            speedUpCountdown += SPEED_UP_COUNTDOWN_TIME;
        }

        playerAnimationCountdown -= 1;
        if (playerAnimationCountdown < 1) {
            playerAnimationCountdown += PLAYER_ANIMATION_DURATION;
            playerAnimationFrame = (playerAnimationFrame + 1) % NUMBER_OF_PLAYER_SPRITES;
        }

        // Move background down
        backgroundPosition += speed;
        backgroundPosition %= FRAME_HEIGHT;
        // Move obstacles down
        for (Obstacle obstacle : obstacles) {
            obstacle.move();
        }
        // Remove any obstacle beyond the screen
        if (obstacles.peekLast() != null && obstacles.peekLast().y > FRAME_HEIGHT) {
            obstacles.removeLast();
        }
                
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && currentLane != 0) {
            currentLane--;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && currentLane != NUMBER_OF_LANES - 1) {
            currentLane++;
        }
    }

    public void keyReleased(KeyEvent e) {
        // Nothing to do
    }

    public void keyTyped(KeyEvent e) {
        // Nothing to do
    }

    class Obstacle {
        int lane; // The lane in which this obstacle spawns, indexed from 0 from left to right
        int y; // The distance of this obstacle from the top edge of the frame
        int color = 0;
        boolean passed = false; // Whether the player passed this obstacle

        /**
         * Obstacles are any objects other than the player.
         * 
         * @param lane is the lane in which the object must spawn.
         * @param y is its y-position, or how far it is from the top of the frame.
         * @param speed is its speed.
         */
        Obstacle(int lane, int y) {
            this.lane = lane;
            this.y = y;
        }

        void move() {
            y += speed;
        }
    }

    class Door extends Obstacle {
        Door(int lane, int y) {
            super(lane, y);
            color = random.nextInt(NUMBER_OF_COLORS);
        }

        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    if (currentKeys[color] == 0) {
                        gameOver();
                    } else {
                        currentKeys[color]--;
                        score++;
                        doorsOpened++;
                        if (doorsOpened % 5 == 0) {
                            health += 1;
                        }
                    }
                }
                passed = true;
            }
        }
    }

    class Key extends Obstacle {
        boolean obtained = false;

        Key(int lane, int y) {
            super(lane, y);
            color = random.nextInt(NUMBER_OF_COLORS);
        }

        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    currentKeys[color]++;
                    obtained = true;
                }
                passed = true;
            }
        }
    }

    class Zombie extends Obstacle {
        Zombie(int lane, int y) {
            super(lane, y);
        }

        void move() {
            super.move();
            if (y > PLAYER_Y && !passed) {
                if (currentLane == lane) {
                    gameOver();
                }
                passed = true;
            }
        }
    }

    void gameOver() {
        if (health >= 3) {
            int option = JOptionPane.showConfirmDialog(this,
                "You hit an obstacle.\nDo you want to use three hearts to continue?",
                "Continue?",
                JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                health -= 3;
                return;
            }
        }
        timer.stop();
        JOptionPane.showMessageDialog(panel, "Game Over!\nYour Score: " + score);
        System.exit(0);
    }
}
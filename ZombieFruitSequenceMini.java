import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * ZombieFruitSequenceMini is a minigame where the player must repeat an increasing sequence
 * of fruit-button highlights to collect fruits while avoiding losing lives to zombies.
 */
public class ZombieFruitSequenceMini extends Minigame {
    private JButton[] buttons = new JButton[4]; // 2x2 grid
    private String[] fruits = {"🍎", "🍌", "🍇", "🍒"}; // Emoji fruits
    private Color[] baseColors = {new Color(60, 20, 20), new Color(90, 60, 20),
        new Color(30, 30, 60), new Color(60, 20, 60)};
    private Color[] highlightColors = {new Color(255, 100, 100), new Color(255, 220, 100),
        new Color(150, 150, 255), new Color(255, 100, 255)};

    private List<Integer> sequence = new ArrayList<>();
    private int currentStep = 0;
    private int lives = 3;
    private int fruitsCollected = 0;
    private final int winTarget = 5;
    private Random random = new Random();
    private JLabel statusLabel;
    private boolean acceptingInput = false;

    /**
     * Creates a new Zombie Fruit Sequence minigame.
     * @param mainGame the main game instance
     * @param speed the speed factor affecting game duration
     */
    public ZombieFruitSequenceMini(KeysOfSurvival mainGame, int speed) {
        super(mainGame, speed, "☣ Zombie Fruit Sequence ☣", 30000 / speed);
        timer.stop();
        setLayout(null);
        setBackground(new Color(15, 18, 15));

        // Title
        JLabel title = new JLabel("☣ Collect the Fruits! ☣", SwingConstants.CENTER);
        title.setFont(new Font("Stencil", Font.BOLD, 36));
        title.setForeground(new Color(200, 50, 50));
        title.setBounds(0, 30, MINIGAME_WIDTH, 50);
        add(title);

        // Status
        statusLabel = new JLabel("Lives: " + lives + " | Fruits collected: " + fruitsCollected,
            SwingConstants.CENTER);
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 26));
        statusLabel.setBounds(0, 90, MINIGAME_WIDTH, 40);
        add(statusLabel);

        // Board
        JPanel board = new JPanel(new GridLayout(2, 2, 20, 20));
        board.setBounds(280, 180, 400, 400);
        board.setBackground(new Color(20, 25, 20));
        add(board);

        for (int i = 0; i < 4; i++) {
            JButton btn = new JButton(fruits[i]);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
            btn.setBackground(baseColors[i]);
            btn.setFocusPainted(false);
            final int index = i;
            btn.addActionListener(e -> handlePlayerInput(index));
            buttons[i] = btn;
            board.add(btn);
        }

        startNewRound();
    }

    /**
     * Starts a new round by adding to the sequence and displaying it.
     */
    private void startNewRound() {
        acceptingInput = false;
        sequence.add(random.nextInt(4)); // add a new fruit
        currentStep = 0;
        statusLabel.setText("Lives: " + lives + " | Fruits collected: " + fruitsCollected);
        Timer timer = new Timer(600, null);
        final int[] step = {0};
        timer.addActionListener(e -> {
            if (step[0] < sequence.size()) {
                highlightButton(sequence.get(step[0]));
                step[0]++;
            } else {
                timer.stop();
                acceptingInput = true;
                statusLabel.setText("Lives: " + lives + " | Repeat the sequence!");
            }
        });
        timer.start();
    }

    /* The button chosen should be highlighted for UX, 
    so the player understands and remembers the sequence 
    */
    private void highlightButton(int index) {
        JButton btn = buttons[index];
        Color original = btn.getBackground();
        btn.setBackground(highlightColors[index]);
        Timer t = new Timer(500, e -> btn.setBackground(original));
        t.setRepeats(false);
        t.start();
    }

    /**
     * Handles player input when a fruit button is pressed.
     * @param index the index of the button pressed
     */
    private void handlePlayerInput(int index) {
        if (!acceptingInput) {
            return;
        }

        if (sequence.get(currentStep) == index) {
            currentStep++;
            if (currentStep == sequence.size()) {
                fruitsCollected++;
                if (fruitsCollected >= winTarget) {
                    JOptionPane.showMessageDialog(this,
                        "🎉 Mission Complete! You collected all fruits safely! ☣");
                    resumeGame(); // Inform main game that minigame succeeded
                } else {
                    Timer t = new Timer(500, e -> startNewRound());
                    t.setRepeats(false);
                    t.start();
                }
            }
        } else {
            lives--;
            acceptingInput = false;
            if (lives > 0) {
                JOptionPane.showMessageDialog(this, "Wrong fruit! Lives remaining: " + lives);
                Timer t = new Timer(500, e -> startNewRound());
                t.setRepeats(false);
                t.start();
            } else {
                JOptionPane.showMessageDialog(this, "No lives left! Zombies ate all the fruits! ☣");
                failGame(); // Inform main game that minigame failed
            }
        }
    }
}

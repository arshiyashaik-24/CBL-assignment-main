import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class ZombieFruitSequence extends JFrame {
    private JButton[] buttons = new JButton[4]; // 2x2 grid
    private String[] fruits = {"🍎", "🍌", "🍇", "🍒"}; // Emoji fruits
    private Color[] baseColors = {new Color(60, 20, 20), new Color(90, 60, 20),
            new Color(30, 30, 60), new Color(60, 20, 60)}; // dark zombie-style colors
    private Color[] highlightColors = {new Color(255, 100, 100), new Color(255, 220, 100),
            new Color(150, 150, 255), new Color(255, 100, 255)}; // bright for visibility

    private List<Integer> sequence = new ArrayList<>();
    private int currentStep = 0;
    private int lives = 3;
    private int fruitsCollected = 0; // New win condition counter
    private final int WIN_TARGET = 6; // Collect 10 fruits to win
    private Random random = new Random();
    private JLabel statusLabel;
    private boolean acceptingInput = false;

    public ZombieFruitSequence() {
        setTitle("☣ Zombie Fruit Sequence ☣");
        setSize(960, 960);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(15, 18, 15));

        // Title
        JLabel title = new JLabel("☣ Collect the Fruits! ☣", SwingConstants.CENTER);
        title.setFont(new Font("Stencil", Font.BOLD, 36));
        title.setForeground(new Color(200, 50, 50));
        title.setBounds(0, 30, 960, 50);
        add(title);

        // Status
        statusLabel = new JLabel("Lives: " + lives + " | Fruits collected: " + fruitsCollected, SwingConstants.CENTER);
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 26));
        statusLabel.setBounds(0, 90, 960, 40);
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

        setVisible(true);
        startNewRound();
    }

    private void startNewRound() {
        acceptingInput = false;
        sequence.add(random.nextInt(4)); // Add a new fruit to sequence each round
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

    private void highlightButton(int index) {
        JButton btn = buttons[index];
        Color original = btn.getBackground();
        btn.setBackground(highlightColors[index]);
        Timer t = new Timer(500, e -> btn.setBackground(original));
        t.setRepeats(false);
        t.start();
    }

    private void handlePlayerInput(int index) {
        if (!acceptingInput) return;

        if (sequence.get(currentStep) == index) {
            currentStep++;
            if (currentStep == sequence.size()) {
                fruitsCollected++; // Increment fruits collected for winning
                if (fruitsCollected >= WIN_TARGET) {
                    JOptionPane.showMessageDialog(this, "🎉 Mission Complete! You collected all fruits safely! ☣");
                    dispose();
                } else {
                    // Next round after short delay
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
                dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ZombieFruitSequence::new);
    }
}

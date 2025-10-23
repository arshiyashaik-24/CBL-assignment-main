import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class TapMatchFruitsMini extends Minigame {
    private JButton[][] gridButtons = new JButton[3][3];
    private String[] fruits = {"🍼", "🥛", "🧃", "🫗"};
    private Color[] baseColors = {new Color(60, 20, 20), new Color(90, 60, 20),
        new Color(30, 30, 60), new Color(60, 20, 60)};
    private List<JButton> selected = new ArrayList<>();
    private int score = 0;
    private int lives = 3;
    private final int winTarget = 3; // Target score
    private JLabel statusLabel;
    private Random random = new Random();

    public TapMatchFruitsMini(KeysOfSurvival mainGame, int speed) {
        super(mainGame, speed, "☣ Tap Match Fluids ☣", 30000 / speed);
        timer.stop();
        setLayout(null);
        setBackground(new Color(15, 18, 15));

        // Title
        JLabel title = new JLabel("Tap Match Fluids!", SwingConstants.CENTER);
        title.setFont(new Font("Stencil", Font.BOLD, 36));
        title.setForeground(new Color(200, 50, 50));
        title.setBounds(0, 30, MINIGAME_WIDTH, 50);
        add(title);

        // Status
        statusLabel = new JLabel("Score: 0 | Lives: 3", SwingConstants.CENTER);
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 26));
        statusLabel.setBounds(0, 90, MINIGAME_WIDTH, 40);
        add(statusLabel);

        // Board
        JPanel board = new JPanel(new GridLayout(3, 3, 10, 10));
        board.setBounds(260, 180, 440, 440);
        board.setBackground(new Color(20, 25, 20));
        add(board);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton(randomFruit());
                btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
                btn.setBackground(baseColors[random.nextInt(baseColors.length)]);
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setFocusPainted(false);
                btn.addActionListener(e -> selectTile(btn));
                gridButtons[i][j] = btn;
                board.add(btn);
            }
        }
    }

    private String randomFruit() {
        return fruits[random.nextInt(fruits.length)];
    }

    private void selectTile(JButton btn) {
        if (selected.contains(btn)) {
            return;
        }

        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        selected.add(btn);

        if (selected.size() == 3) {
            checkMatch();
        }
    }

    private void checkMatch() {
        String first = selected.get(0).getText();
        boolean allMatch = true;
        for (JButton b : selected) {
            if (!b.getText().equals(first)) {
                allMatch = false;
                break;
            }
        }

        if (allMatch) {
            score++;
            for (JButton b : selected) {
                b.setText(randomFruit());
                b.setBorder(null);
                b.setBackground(baseColors[random.nextInt(baseColors.length)]);
            }
            if (score >= winTarget) {
                JOptionPane.showMessageDialog(this,
                    "🎉 Mission Complete! Collected enough fluids!");
                resumeGame();
                return;
            }
        } else {
            lives--;
            for (JButton b : selected) {
                b.setBorder(null);
            }
            if (lives <= 0) {
                JOptionPane.showMessageDialog(this,
                    "No lives left! Zombies drank all the fluids!");
                failGame();
                return;
            }
        }
        selected.clear();
        statusLabel.setText("Score: " + score + " | Lives: " + lives);
    }
}

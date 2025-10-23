import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * ZombieTicTacToe is a minigame where the player competes against a zombie AI 
 * in a game of Tic-Tac-Toe.
 * The player has limited lives and must win rounds to survive.
 */
public class ZombieTicTacToe extends Minigame {
    private JButton[] buttons = new JButton[9];
    private boolean playerTurn = true;
    private JLabel statusLabel;
    private Random random = new Random();
    private int lives = 3;

    /**
     * Creates the Zombie Tic-Tac-Toe minigame UI and initializes game state.
     *
     * @param mainGame Reference to the main KeysOfSurvival game controller.
     * @param speed    Speed modifier used to scale the round timeout.
     */
    public ZombieTicTacToe(KeysOfSurvival mainGame, int speed) {
        super(mainGame, speed, "☣ Zombie Tic-Tac-Toe ☣", 20000 / speed);
        setLayout(null);
        setBackground(new Color(15, 18, 15));

        // Title with glow effect
        JLabel title = new JLabel("☣ Zombie Tic-Tac-Toe ☣", SwingConstants.CENTER);
        title.setFont(new Font("Stencil", Font.BOLD, 38));
        title.setForeground(new Color(200, 30, 30));
        title.setBounds(0, 30, MINIGAME_WIDTH, 50);
        add(title);

        // Status label
        statusLabel = new JLabel("Survivor's Turn (X) — Lives: " + lives, SwingConstants.CENTER);
        statusLabel.setForeground(new Color(180, 180, 180));
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 26));
        statusLabel.setBounds(0, 90, MINIGAME_WIDTH, 40);
        add(statusLabel);

        // Board panel
        JPanel board = new JPanel(new GridLayout(3, 3, 8, 8));
        board.setBounds(280, 150, 400, 400);
        board.setBackground(new Color(20, 25, 20));
        add(board);

        // Buttons with rounded corners and flat look
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial Black", Font.BOLD, 72));
            buttons[i].setBackground(new Color(30, 35, 30));
            buttons[i].setForeground(Color.GREEN);
            buttons[i].setFocusPainted(false);
            buttons[i].setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true));
            final int index = i;
            buttons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (buttons[index].isEnabled() && playerTurn) {
                        buttons[index].setBackground(new Color(50, 70, 50));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (buttons[index].isEnabled() && playerTurn) {
                        buttons[index].setBackground(new Color(30, 35, 30));
                    }
                }
            });
            buttons[i].addActionListener(this);
            board.add(buttons[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!playerTurn) {
            return;
        }
        JButton btn = (JButton) e.getSource();
        if (!btn.getText().equals("")) {
            return;
        }

        // Player move
        btn.setText("X");
        btn.setForeground(new Color(0, 255, 0));
        btn.setFont(new Font("Arial Black", Font.BOLD, 72));

        if (checkWinner()) {
            return;
        }

        playerTurn = false;
        statusLabel.setText("Zombie's Turn (O) — Lives: " + lives);

        // Delay zombie move
        Timer timer = new Timer(400, evt -> {
            if (!isGameOver()) {
                zombieMove();
            }
            ((Timer) evt.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void zombieMove() {
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                emptyCells.add(i);
            }
        }
        if (emptyCells.isEmpty()) {
            return;
        }

        int move = findBestMove();
        buttons[move].setText("O");
        buttons[move].setForeground(new Color(200, 40, 40));
        buttons[move].setFont(new Font("Arial Black", Font.BOLD, 72));

        if (!checkWinner()) {
            playerTurn = true;
            statusLabel.setText("Survivor's Turn (X) — Lives: " + lives);
        }
    }

    private int findBestMove() {
        // Try to win
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                if (isWinningCombo("O")) {
                    buttons[i].setText("");
                    return i;
                }
                buttons[i].setText("");
            }
        }
        // Block player
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("X");
                if (isWinningCombo("X")) {
                    buttons[i].setText("");
                    return i;
                }
                buttons[i].setText("");
            }
        }
        // Random
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                empty.add(i);
            }
        }
        return empty.get(random.nextInt(empty.size()));
    }

    private boolean checkWinner() {
        if (isWinningCombo("X")) {
            return endRoundDialog("Survivors Win! ⚡", true);
        }
        if (isWinningCombo("O")) {
            return endRoundDialog("Zombies Win! ☣", false);
        }

        boolean draw = true;
        for (JButton b : buttons) {
            if (b.getText().equals("")) {
                draw = false;
            }
        }
        if (draw) {
            return endRoundDialog("It's a Draw! Everyone's Infected ☠", false);
        }

        return false;
    }

    private boolean isWinningCombo(String symbol) {
        int[][] patterns = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        for (int[] p : patterns) {
            if (buttons[p[0]].getText().equals(symbol)
                && buttons[p[1]].getText().equals(symbol)
                && buttons[p[2]].getText().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    private int[] getWinningPattern(String symbol) {
        int[][] patterns = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        for (int[] p : patterns) {
            if (buttons[p[0]].getText().equals(symbol)
                && buttons[p[1]].getText().equals(symbol)
                && buttons[p[2]].getText().equals(symbol)) {
                return p;
            }
        }
        return new int[0];
    }

    private boolean endRoundDialog(String message, boolean playerWon) {
        if (playerWon) {
            highlightWinner(getWinningPattern("X"));
        } else if (!message.contains("Draw")) {
            highlightWinner(getWinningPattern("O"));
        }

        statusLabel.setText(message);
        endGame(playerWon ? new Color(0, 100, 0) : new Color(100, 0, 0));

        JOptionPane.showMessageDialog(this, message);

        if (!playerWon && !message.contains("Draw")) {
            lives--;
            if (lives > 0) {
                JOptionPane.showMessageDialog(this,
                    "You have " + lives + " lives remaining! Try again!");
                restartRound();
            } else {
                failGame();
            }
        } else {
            resumeGame();
        }
        return true;
    }

    private void restartRound() {
        for (JButton b : buttons) {
            b.setText("");
            b.setEnabled(true);
            b.setBackground(new Color(30, 35, 30));
        }
        playerTurn = true;
        statusLabel.setText("Survivor's Turn (X) — Lives: " + lives);
    }

    private void highlightWinner(int[] pattern) {
        for (int i : pattern) {
            buttons[i].setBackground(new Color(120, 20, 20));
        }
        disableBoard();
    }

    private void disableBoard() {
        for (JButton b : buttons) {
            b.setEnabled(false);
        }
    }

    private void endGame(Color color) {
        for (JButton b : buttons) {
            b.setBackground(color.darker());
        }
        disableBoard();
    }

    private boolean isGameOver() {
        String text = statusLabel.getText();
        return text.contains("Win") || text.contains("Draw");
    }
}

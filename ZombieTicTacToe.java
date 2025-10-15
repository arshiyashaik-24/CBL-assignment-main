import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ZombieTicTacToe extends JFrame {

    private static final int SIZE = 3;
    private JButton[][] cells = new JButton[SIZE][SIZE];
    private char[][] board = new char[SIZE][SIZE];
    private boolean humanTurn = true;
    private char human = 'X';
    private char ai = 'O';
    private boolean gameOver = false;
    private int lives = 3;

    private JLabel statusLabel;
    private JLabel livesLabel;
    private JComboBox<String> difficultyBox;
    private JComboBox<String> sideBox;

    public ZombieTicTacToe() {
        setTitle("🧟 Zombie Apocalypse Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.black);

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(Color.black);
        topPanel.setForeground(Color.white);

        statusLabel = new JLabel("Welcome to the ruined city. Claim a cell!");
        statusLabel.setForeground(Color.green);

        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        // === BOARD PANEL ===
        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE, 5, 5));
        boardPanel.setBackground(Color.black);

        Font font = new Font("Arial", Font.BOLD, 72);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new JButton("");
                cells[i][j].setFont(font);
                cells[i][j].setFocusPainted(false);
                cells[i][j].setBackground(new Color(35, 35, 35));
                cells[i][j].setForeground(Color.white);
                final int r = i, c = j;
                cells[i][j].addActionListener(e -> handleMove(r, c));
                boardPanel.add(cells[i][j]);
                board[i][j] = ' ';
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.black);

        difficultyBox = new JComboBox<>(new String[]{"Normal", "Hard"});
        sideBox = new JComboBox<>(new String[]{"Survivor (X)", "Zombie (O)"});
        JButton restartBtn = new JButton("Restart Game");

        livesLabel = new JLabel("❤️❤️❤️");
        livesLabel.setForeground(Color.red);
        livesLabel.setFont(new Font("Arial", Font.BOLD, 20));

        bottomPanel.add(new JLabel("Difficulty:"));
        bottomPanel.add(difficultyBox);
        bottomPanel.add(new JLabel("Your side:"));
        bottomPanel.add(sideBox);
        bottomPanel.add(livesLabel);
        bottomPanel.add(restartBtn);

        restartBtn.addActionListener(e -> resetFullGame());
        add(bottomPanel, BorderLayout.SOUTH);

        // === Initialize window ===
        setSize(450, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // === HANDLE PLAYER MOVE ===
    private void handleMove(int r, int c) {
        if (gameOver || !humanTurn || board[r][c] != ' ') return;

        board[r][c] = human;
        cells[r][c].setText(String.valueOf(human));
        cells[r][c].setForeground(human == 'X' ? Color.cyan : Color.pink);
        humanTurn = false;

        char winner = checkWinner(board);
        if (winner != ' ') {
            endRound(winner);
            return;
        } else if (isBoardFull(board)) {
            endRound('D');
            return;
        }

        // Delay AI move for 0.7 sec
        new javax.swing.Timer(700, e -> {
            aiMove();
            humanTurn = true;
            char w = checkWinner(board);
            if (w != ' ') endRound(w);
            else if (isBoardFull(board)) endRound('D');
            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }

    // === AI MOVE ===
    private void aiMove() {
        if (gameOver) return;
        int[] move = (difficultyBox.getSelectedItem().equals("Hard")) ?
                findBestMove(board, ai) : randomMove();
        board[move[0]][move[1]] = ai;
        cells[move[0]][move[1]].setText(String.valueOf(ai));
        cells[move[0]][move[1]].setForeground(ai == 'X' ? Color.cyan : Color.pink);
    }

    private int[] randomMove() {
        java.util.List<int[]> empty = new java.util.ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (board[r][c] == ' ')
                    empty.add(new int[]{r, c});
        return empty.get((int) (Math.random() * empty.size()));
    }

    // === MINIMAX AI ===
    private int[] findBestMove(char[][] board, char player) {
        int bestScore = (player == ai) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[] move = {-1, -1};

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == ' ') {
                    board[r][c] = player;
                    int score = minimax(board, 0, player == human);
                    board[r][c] = ' ';
                    if (player == ai) {
                        if (score > bestScore) {
                            bestScore = score;
                            move = new int[]{r, c};
                        }
                    } else {
                        if (score < bestScore) {
                            bestScore = score;
                            move = new int[]{r, c};
                        }
                    }
                }
            }
        }
        return move;
    }

    private int minimax(char[][] b, int depth, boolean isHumanTurn) {
        char winner = checkWinner(b);
        if (winner == ai) return 10 - depth;
        if (winner == human) return depth - 10;
        if (isBoardFull(b)) return 0;

        int best = isHumanTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (b[r][c] == ' ') {
                    b[r][c] = isHumanTurn ? human : ai;
                    int score = minimax(b, depth + 1, !isHumanTurn);
                    b[r][c] = ' ';
                    if (isHumanTurn)
                        best = Math.min(best, score);
                    else
                        best = Math.max(best, score);
                }
            }
        }
        return best;
    }

    // === ROUND END ===
    private void endRound(char winner) {
        gameOver = true;

        if (winner == human) {
            statusLabel.setText("🎉 You conquered the zombies!");
            statusLabel.setForeground(Color.cyan);
        } else if (winner == ai) {
            lives--;
            updateLives();
            if (lives > 0) {
                statusLabel.setText("💀 You lost this round... prepare again!");
                statusLabel.setForeground(Color.red);
                // reset board for next round
                new javax.swing.Timer(1500, e -> {
                    resetBoardOnly();
                    ((javax.swing.Timer) e.getSource()).stop();
                }).start();
            } else {
                statusLabel.setText("☠️ Zombies overran your base. You lose!");
                statusLabel.setForeground(Color.red);
            }
        } else {
            statusLabel.setText("😐 Draw. The city still stands...");
            statusLabel.setForeground(Color.gray);
            new javax.swing.Timer(1500, e -> {
                resetBoardOnly();
                ((javax.swing.Timer) e.getSource()).stop();
            }).start();
        }
    }

    // === RESET JUST THE BOARD ===
    private void resetBoardOnly() {
        gameOver = false;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = ' ';
                cells[r][c].setText("");
                cells[r][c].setEnabled(true);
                cells[r][c].setBackground(new Color(35, 35, 35));
            }
        statusLabel.setText("Your move...");
        statusLabel.setForeground(Color.green);
    }

    // === FULL RESTART (hearts + side) ===
    private void resetFullGame() {
        String side = (String) sideBox.getSelectedItem();
        human = side.startsWith("Survivor") ? 'X' : 'O';
        ai = (human == 'X') ? 'O' : 'X';
        lives = 3;
        updateLives();
        resetBoardOnly();
    }

    private void updateLives() {
        if (lives == 3) livesLabel.setText("❤️❤️❤️");
        else if (lives == 2) livesLabel.setText("❤️❤️🖤");
        else if (lives == 1) livesLabel.setText("❤️🖤🖤");
        else livesLabel.setText("🖤🖤🖤");
    }

    // === CHECK WINNER ===
    private char checkWinner(char[][] b) {
        for (int i = 0; i < SIZE; i++) {
            if (b[i][0] != ' ' && b[i][0] == b[i][1] && b[i][1] == b[i][2]) return b[i][0];
            if (b[0][i] != ' ' && b[0][i] == b[1][i] && b[1][i] == b[2][i]) return b[0][i];
        }
        if (b[0][0] != ' ' && b[0][0] == b[1][1] && b[1][1] == b[2][2]) return b[0][0];
        if (b[0][2] != ' ' && b[0][2] == b[1][1] && b[1][1] == b[2][0]) return b[0][2];
        return ' ';
    }

    private boolean isBoardFull(char[][] b) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (b[i][j] == ' ') return false;
        return true;
    }

    // === MAIN ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ZombieTicTacToe::new);
    }
}

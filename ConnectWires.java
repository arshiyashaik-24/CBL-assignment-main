import java.awt.*;
import java.util.*;
import javax.swing.*;

public class ConnectWires extends Minigame {
    private JButton[] leftBtns = new JButton[4];
    private JButton[] rightBtns = new JButton[4];
    private Color[] colors = {Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW};
    private int selectedLeft = -1;
    private int lives = 3;
    private int matches = 0;

    private java.util.List<Connection> connections = new ArrayList<>();
    private JLabel livesLabel;
    private Font uiFont = new Font("Consolas", Font.BOLD, 16);

    // Store each button's color separately (no LineBorder casting)
    private Map<JButton, Color> buttonColors = new HashMap<>();

    ConnectWires(KeysOfSurvival mainGame, int speed) {
        super(mainGame, speed, "⚡ Connect the Wires! — Cyber Grid", 150000 / speed);
        
        setPreferredSize(new Dimension(520, 420));
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addKeyListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setLayout(null);
        setBackground(new Color(15, 15, 20));

        progressBar.setBounds(0, 10, 520, 20);

        // Lives label
        livesLabel = new JLabel("❤ Lives: " + lives);
        livesLabel.setForeground(Color.WHITE);
        livesLabel.setFont(uiFont);
        livesLabel.setBounds(15, 30, 200, 30);
        add(livesLabel);

        // Title label
        JLabel title = new JLabel("CONNECT THE WIRES!");
        title.setForeground(new Color(80, 200, 255));
        title.setFont(new Font("Consolas", Font.BOLD, 18));
        title.setBounds(150, 30, 300, 30);
        add(title);

        // Create left buttons
        for (int i = 0; i < 4; i++) {
            JButton btn = makeButton(colors[i]);
            final int index = i;
            btn.addActionListener(e -> selectLeft(index));
            leftBtns[i] = btn;
            add(btn);
            buttonColors.put(btn, colors[i]);
        }

        // Create right buttons
        for (int i = 0; i < 4; i++) {
            JButton btn = makeButton(colors[i]);
            final int index = i;
            btn.addActionListener(e -> selectRight(index));
            rightBtns[i] = btn;
            add(btn);
            buttonColors.put(btn, colors[i]);
        }

        shuffleAndPlace();
    }

    private JButton makeButton(Color borderColor) {
        JButton btn = new JButton(" ");
        btn.setBackground(new Color(25, 25, 30));
        btn.setFocusPainted(false);
        btn.setFont(uiFont);
        btn.setBorder(BorderFactory.createLineBorder(borderColor, 3, true));
        return btn;
    }

    private void shuffleAndPlace() {
        java.util.List<Integer> positions = Arrays.asList(80, 150, 220, 290);
        Collections.shuffle(positions);
        java.util.List<Integer> rightPositions = new ArrayList<>(positions);
        Collections.shuffle(rightPositions);

        for (int i = 0; i < 4; i++) {
            leftBtns[i].setBounds(70, positions.get(i), 60, 40);
            rightBtns[i].setBounds(380, rightPositions.get(i), 60, 40);
        }
    }

    private void selectLeft(int index) {
        selectedLeft = index;
        for (JButton btn : leftBtns) {
            btn.setBorder(BorderFactory.createLineBorder(buttonColors.get(btn), 3, true));
        }
        leftBtns[index].setBorder(BorderFactory.createLineBorder(Color.WHITE, 4, true));
    }

    private void selectRight(int index) {
        if (selectedLeft == -1) {
            return;
        }

        JButton left = leftBtns[selectedLeft];
        JButton right = rightBtns[index];
        Color leftColor = buttonColors.get(left);
        Color rightColor = buttonColors.get(right);

        // Calculate centers (relative to the panel)
        Point leftCenter = SwingUtilities.convertPoint(left.getParent(),
                left.getX() + left.getWidth(), left.getY() + left.getHeight() / 2, this);
        Point rightCenter = SwingUtilities.convertPoint(right.getParent(),
                right.getX(), right.getY() + right.getHeight() / 2, this);

        if (leftColor.equals(rightColor)) {
            connections.add(new Connection(leftCenter, rightCenter, leftColor));
            repaint();
            left.setEnabled(false);
            right.setEnabled(false);
            left.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            right.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            matches++;
            if (matches == 4) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "⚡ All wires connected! Power restored!");
                frame.dispose();
                resumeGame();
            }
        } else {
            lives--;
            connections.add(new Connection(leftCenter, rightCenter, Color.RED));
            repaint();
            livesLabel.setText("❤ Lives: " + lives);
            JOptionPane.showMessageDialog(this, "Wrong wire! Lives left: " + lives);
            if (lives <= 0) {
                JOptionPane.showMessageDialog(this, "💥 Short circuit! Game Over.");
                frame.dispose();
                failGame();
            }
        }

        selectedLeft = -1;
        for (JButton btn : leftBtns) {
            if (btn.isEnabled()) {
                btn.setBorder(BorderFactory.createLineBorder(buttonColors.get(btn), 3, true));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Connection c : connections) {
            // Glow effect — multiple strokes with fading opacity
            for (int glow = 12; glow >= 1; glow -= 3) {
                float alpha = (float) glow / 20f;
                g2.setColor(new Color(c.color.getRed(),
                    c.color.getGreen(),
                    c.color.getBlue(),
                    (int) (alpha * 255))
                );
                g2.setStroke(new BasicStroke(glow, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(c.start.x, c.start.y, c.end.x, c.end.y);
            }
            // Core bright line
            g2.setColor(c.color);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(c.start.x, c.start.y, c.end.x, c.end.y);
        }
    }

    static class Connection {
        Point start;
        Point end;
        Color color;
        
        Connection(Point s, Point e, Color c) {
            start = s;
            end = e;
            color = c;
        }
    }
}
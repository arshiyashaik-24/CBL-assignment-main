import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JPanel {
    private float fadeOpacity = 0f;
    private float glowPhase = 0f; 
    private JFrame frame;
    private JButton startButton;
    private JComboBox<Integer> laneSelector;
    private JLabel laneLabel;
    private Image backgroundImage;

    private int frameHeight = 960;

    public static void main(String[] args) {
        new Main();
    }

    private void startFadeIn() {
        Timer fadeTimer = new Timer(25, e -> {
            fadeOpacity += 0.03f;
            if (fadeOpacity >= 1f) {
                fadeOpacity = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        fadeTimer.start();
    }

    private void startGlowAnimation() {
        Timer glowTimer = new Timer(50, e -> {
            glowPhase += 0.15f;
            repaint();
        });
        glowTimer.start();
    }

    public Main() {
        // Frame setup
        frame = new JFrame("Keys of Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, frameHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Load your title screen background image
        backgroundImage = new ImageIcon("Images/MenuBackground.png").getImage();
        setLayout(null);

        // START button — transparent + over player's hands area
        startButton = new JButton("     ");
        startButton.setFont(new Font("Press Start 2P", Font.BOLD, 30)); // use a pixel font if available
        startButton.setForeground(Color.WHITE);
        startButton.setBounds(190, frameHeight - 360, 220, 80); // position to match hands area

        // Transparency 
        startButton.setContentAreaFilled(false);
        startButton.setOpaque(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(startButton);

        // LANE SELECTOR SECTION
        laneLabel = new JLabel("LANES");
        laneLabel.setFont(new Font("Minecraftia", Font.BOLD, 20)); // pixel-style font
        laneLabel.setForeground(new Color(230, 230, 210)); // bone-white to match selector text
        laneLabel.setBounds(210, frameHeight - 200, 150, 30);
        add(laneLabel);

        laneSelector = new JComboBox<>(new Integer[]{3, 4, 5});
        laneSelector.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 210), 2)); // bone-white border
        laneSelector.setFont(new Font("Press Start 2P", Font.PLAIN, 18)); // pixel-style font
        laneSelector.setForeground(new Color(230, 230, 210));
        laneSelector.setBackground(new Color(50, 30, 20));
        laneSelector.setFocusable(false);
        laneSelector.setBounds(300, frameHeight - 200, 80, 35);
        add(laneSelector);

        // Button action
        startButton.addActionListener(e -> startGame());

        frame.add(this);
        frame.setVisible(true);

        // Start animations
        startFadeIn();
        startGlowAnimation();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Fade effect
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeOpacity));

        // Background
        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // rusty plate behind lane selector
        g2.setColor(new Color(50, 30, 20, 200)); 
        g2.fillRoundRect(175, frameHeight - 220, 235, 70, 20, 20);

        // Pulsing glow around start button
        int glowAlpha = (int) (100 + 80 * Math.sin(glowPhase));
        g2.setColor(new Color(255, 180, 150, glowAlpha));
        g2.setStroke(new BasicStroke(5f));
        g2.drawRoundRect(175, frameHeight - 220, 235, 70, 20, 20);

        g2.setComposite(original);
        g2.dispose();
    }

    private void startGame() {
        int chosenLanes = (int) laneSelector.getSelectedItem();
        KeysOfSurvival.NUMBER_OF_LANES = chosenLanes;
        frame.dispose(); // close menu
        new KeysOfSurvival(chosenLanes);
    }
}

import java.awt.*;
import javax.swing.*;

/**
 * Main menu for Keys of Survival with start button, lane selector, and toggleable mute button.
 */
public class Main extends JPanel {
    private float fadeOpacity = 0f;
    private float glowPhase = 0f;
    private JFrame frame;
    private JButton startButton;
    private JButton muteButton;
    private JComboBox<Integer> laneSelector;
    private JLabel laneLabel;
    private Image backgroundImage;

    private int buttonY;
    private int selectorY;

    private final ImageIcon muteIcon;
    private final ImageIcon unmuteIcon;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        setPreferredSize(new Dimension(600, 960));
        frame = new JFrame("Keys of Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        backgroundImage = new ImageIcon("Images/MenuBackground.png").getImage();
        setLayout(null);

        buttonY = (frame.getContentPane().getSize().height * 13) / 20;
        selectorY = (frame.getContentPane().getSize().height * 17) / 20;

        // START button
        startButton = new JButton();
        startButton.setBounds(190, buttonY, 220, 80);
        startButton.setContentAreaFilled(false);
        startButton.setOpaque(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(startButton);

        // LANE selector
        laneLabel = new JLabel("LANES");
        laneLabel.setFont(new Font("Minecraftia", Font.BOLD, 20));
        laneLabel.setForeground(new Color(230, 230, 210));
        laneLabel.setBounds(210, selectorY, 150, 30);
        add(laneLabel);

        laneSelector = new JComboBox<>(new Integer[]{3, 4, 5});
        laneSelector.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 210), 2));
        laneSelector.setFont(new Font("Press Start 2P", Font.PLAIN, 18));
        laneSelector.setForeground(new Color(230, 230, 210));
        laneSelector.setBackground(new Color(50, 30, 20));
        laneSelector.setFocusable(false);
        laneSelector.setBounds(300, selectorY, 80, 35);
        add(laneSelector);

        // Load and scale mute icons
        int buttonSize = 50; // size of mute button
        ImageIcon rawMute = new ImageIcon("Images/muteButton_muted.png");
        ImageIcon rawUnmute = new ImageIcon("Images/muteButton.png");
        Image muteImg = rawMute.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        Image unmuteImg = rawUnmute.getImage().getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
        muteIcon = new ImageIcon(muteImg);
        unmuteIcon = new ImageIcon(unmuteImg);

        // Mute button at top-right corner
        muteButton = new JButton(KeysOfSurvival.isMuted ? muteIcon : unmuteIcon);
        muteButton.setBounds(getWidth() - buttonSize - 10, 10, buttonSize, buttonSize);
        muteButton.setContentAreaFilled(false);
        muteButton.setOpaque(false);
        muteButton.setBorderPainted(false);
        muteButton.setFocusPainted(false);
        muteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(muteButton);

        // Toggle mute on click
        muteButton.addActionListener(e -> {
            KeysOfSurvival.isMuted = !KeysOfSurvival.isMuted;
            muteButton.setIcon(KeysOfSurvival.isMuted ? muteIcon : unmuteIcon);
        });

        // Start game button
        startButton.addActionListener(e -> startGame());

        // Start animations
        startFadeIn();
        startGlowAnimation();
    }

    private void startFadeIn() {
        Timer fadeTimer = new Timer(25, e -> {
            fadeOpacity += 0.03f;
            if (fadeOpacity >= 1f) { fadeOpacity = 1f; ((Timer) e.getSource()).stop(); }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeOpacity));

        g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Rusty plate behind lane selector
        g2.setColor(new Color(50, 30, 20, 200));
        g2.fillRoundRect(175, selectorY - 20, 235, 70, 20, 20);

        // Pulsing glow around start button
        int glowAlpha = (int) (100 + 80 * Math.sin(glowPhase));
        g2.setColor(new Color(255, 180, 150, glowAlpha));
        g2.setStroke(new BasicStroke(5f));
        g2.drawRoundRect(175, selectorY - 20, 235, 70, 20, 20);

        g2.setComposite(original);
        g2.dispose();
    }

    private void startGame() {
        int chosenLanes = (int) laneSelector.getSelectedItem();
        frame.dispose();
        new KeysOfSurvival(chosenLanes, KeysOfSurvival.isMuted);
    }
}

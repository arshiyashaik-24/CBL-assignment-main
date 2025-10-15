import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JPanel {
    private JFrame frame;
    private JButton startButton;
    private JComboBox<Integer> laneSelector;
    private JLabel titleLabel, laneLabel;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        // Frame setup
        frame = new JFrame("Keys of Survival");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 960);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Panel setup
        setBackground(Color.BLACK);
        setLayout(null);

        // Title
        titleLabel = new JLabel("KEYS OF SURVIVAL");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 150, 600, 100);
        add(titleLabel);

        // Start button
        startButton = new JButton("START");
        startButton.setFont(new Font("Arial Black", Font.BOLD, 40));
        startButton.setFocusPainted(false);
        startButton.setBounds(150, 400, 300, 100);
        startButton.setBackground(new Color(255, 80, 80));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(startButton);

        // Lane label
        laneLabel = new JLabel("Number of Lanes:");
        laneLabel.setFont(new Font("Arial", Font.BOLD, 22));
        laneLabel.setForeground(Color.WHITE);
        laneLabel.setBounds(150, 550, 250, 30);
        add(laneLabel);

        // Lane selector
        Integer[] lanes = { 3, 4, 5 };
        laneSelector = new JComboBox<>(lanes);
        laneSelector.setFont(new Font("Arial", Font.PLAIN, 22));
        laneSelector.setBounds(370, 550, 60, 35);
        laneSelector.setBackground(Color.WHITE);
        add(laneSelector);

        // Button action
        startButton.addActionListener(e -> startGame());

        // Add panel and show frame
        frame.add(this);
        frame.setVisible(true);
    }

    private void startGame() {
        int chosenLanes = (int) laneSelector.getSelectedItem();
        frame.dispose(); // close the menu window

        // make NUMBER_OF_LANES adjustable in the game
        KeysOfSurvival.NUMBER_OF_LANES = chosenLanes;
        new KeysOfSurvival();
    }
}

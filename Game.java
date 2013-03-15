import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Game implements Runnable {
    private TetrisField field;
    private JLabel      scoreLabel;
    private JButton     pause;
    private JButton     reset;
    private JButton     instructionButton;
    private JTextArea   instructions;
    private JFrame      frame;

    @Override
    public void run() {
        scoreLabel = new JLabel("  Score: 0  ");
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        scoreLabel.setVerticalAlignment(SwingConstants.CENTER);
        scoreLabel.setFont(new Font(null, 1, 20));

        pause = new JButton("  Pause  "); //Not paused
        pause.setFont(new Font(null, 1, 20));
        pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                field.pause();
                field.requestFocusInWindow();
            }
        });

        reset = new JButton("Reset");

        instructionButton = new JButton("Instructions");
        instructionButton.setHorizontalAlignment(SwingConstants.RIGHT);
        instructionButton.setVerticalAlignment(SwingConstants.CENTER);
        instructionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleInstructionText();
            }
        });

        // Top-level frame
        frame = new JFrame("Tetreife");
        frame.setLayout(new BorderLayout());
        frame.setLocation(300, 300);

        // Main playing area
        field = new TetrisField(this);
        frame.add(field, BorderLayout.CENTER);

        frame.add(createTopMenu(), BorderLayout.NORTH);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game running
        field.restart();
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    public JButton getPause() {
        return pause;
    }

    public JButton getReset() {
        return reset;
    }

    public JButton getInstructions() {
        return instructionButton;
    }

    public void toggleInstructionText() {
        if (instructions == null) {
            instructions = createInstructionArea();
            frame.add(instructions, BorderLayout.EAST);
        } else {
            frame.remove(instructions);
            instructions = null;
        }
        frame.pack();
        field.requestFocus();
    }

    public JTextArea createInstructionArea() {
        try {
            JTextArea instructions = new JTextArea();
            FileReader in = new FileReader("instructions.txt");
            instructions.read(in, null);
            return instructions;
        } catch (IOException e) {
            // Won't throw because it exists
            System.out.println("instructions.txt doesn't exist");
            return new JTextArea();
        }
    }

    public JComponent createTopMenu() {
        JPanel topMenu = new JPanel();
        topMenu.setLayout(new GridLayout(3, 1));
        topMenu.add(scoreLabel);
        topMenu.add(pause);
        topMenu.add(reset);
        topMenu.add(instructionButton);

        return topMenu;
    }

    /*
     * Get the game started!
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }

}

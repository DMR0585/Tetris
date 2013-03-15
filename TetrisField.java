import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class TetrisField extends JComponent {
    private Block[][] field;
    private Tetromino block;
    private Tetromino nextBlock;
    private boolean   switched;

    private boolean   rotateNextTick;
    private boolean   isPaused;
    private boolean   hasStarted;
    private int       score;
    private int       scoreThreshold;

    private JLabel    scoreLabel;
    private JButton   pause;
    private JButton   reset;

    private int       move_interval  = 250; // Milliseconds between updating player's movements
    private Timer     move_timer;          // Each time timer fires we animate one step
    private int       fall_interval  = 750; // Milliseconds between moving the block down one step
    private Timer     fall_timer;          // Each time timer fires blocks fall one step.

    final int         FIELDWIDTH     = 12;
    final int         FIELDHEIGHT    = 24;
    final int         blockDimension = 20;

    final int         FALL_SPEED     = 1;  // How fast do the blocks fall (4?)
    final int         MOVE_SPEED     = 1;  // How fast do the blocks move L/R

    public TetrisField(Game g) {
        block = randomBlock();
        block = block.setCs((FIELDWIDTH / 2) - 1, 0);
        nextBlock = randomBlock();
        switched = false;
        field = new Block[FIELDWIDTH][FIELDHEIGHT];
        rotateNextTick = false;
        score = 0;
        scoreThreshold = 1500;

        hasStarted = true;

        scoreLabel = g.getScoreLabel();

        pause = g.getPause();

        reset = g.getReset();
        reset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                restart();
            }
        });

        move_timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move_tick();
            }
        });
        move_timer.setDelay(move_interval);
        move_timer.start();

        fall_timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fall_tick();
            }
        });
        fall_timer.setDelay(fall_interval);
        fall_timer.start();

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setFocusable(true);
        addNewKLS();
    }

    // Removes current key listener(s) and adds a new one for the current block.
    public void addNewKLS() {
        // Remove all old KeyListeners
        KeyListener[] kls = this.getKeyListeners();
        if (kls.length != 0) {
            for (int i = 0; i < kls.length; i++)
                removeKeyListener(kls[i]);
        }

        // Add a new one to the current block
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    // Left moves the current block left (if possible)
                    block.setDirection(-MOVE_SPEED);
                    move_timer.restart();
                    move_timer.setDelay(move_interval);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    // Right moves the current block right (if possible)
                    block.setDirection(MOVE_SPEED);
                    move_timer.restart();
                    move_timer.setDelay(move_interval);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // DOWN makes the block fall faster
                    fall_timer.setDelay(fall_interval / 4);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // UP drops the block
                    if (!isPaused) {
                        while (!intersectsV() && block.allCanFall())
                            block.allFall();
                        List<Block> bList = block.getBlockList();
                        for (Block b : bList) {
                            field[b.getX()][b.getY()] = b;
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    // Space rotates the block on the next move_tick
                    if (!isPaused) rotateNextTick = true;
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    // P pauses the game (as does the pause button)
                    pause();
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    switchBlock();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Stops moving if the left or right arrows were released
                if (e.getKeyCode() == KeyEvent.VK_LEFT
                        || e.getKeyCode() == KeyEvent.VK_RIGHT) block
                        .setDirection(0);
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                // Falls at normal speed if the down arrow key was released
                    fall_timer.setDelay(fall_interval);
            }
        });
    }

    // Returns a Tetromino with a different shape, depending on a randomly
    // generated number
    public Tetromino randomBlock() {
        Shape s = Shape.NONE;
        int random = (int) (7 * Math.random());

        if (random == 0) {
            s = Shape.LEFTL;
        }
        if (random == 1) {
            s = Shape.RIGHTL;
        }
        if (random == 2) {
            s = Shape.LEFTZZ;
        }
        if (random == 3) {
            s = Shape.RIGHTZZ;
        }
        if (random == 4) {
            s = Shape.LINE;
        }
        if (random == 5) {
            s = Shape.SQUARE;
        }
        if (random == 6) {
            s = Shape.T;
        }

        return new Tetromino(s, 0, 0, 0, 0, FALL_SPEED);
    }

    // Switches the current Tetromino with the next one
    void switchBlock() {
        if (!switched) {
            Tetromino tempBlock = block;
            nextBlock = nextBlock.setCs(block.getRX(), block.getRY());
            block = nextBlock;
            nextBlock = tempBlock;
            nextBlock = nextBlock.setCs(0, 0);
            switched = true;
        }
    }

    // Updates the vertical position of the block by oen timestep, or adds the
    // block to the current field if it can't fall
    void fall_tick() {
        if (!hasStarted) return; // Do nothing if it hasn't started
        if (intersectsV() || !block.allCanFall()) {
            // if it shouldn't fall (intersects or at the bottom), add the
            // component blocks of the Tetromino to the field
            List<Block> bList = block.getBlockList();
            for (Block b : bList) {
                field[b.getX()][b.getY()] = b;
            }
            // and create a new block, shifting the nextBlock (which will be the
            // current block) back up and resetting its fall speed.

            block = nextBlock;
            block = block.setCs((FIELDWIDTH / 2) - 1, 0);
            nextBlock = randomBlock();
            switched = false;
            addNewKLS();
        } else {
            // Otherwise, make the Tetromino fall
            block.allFall();
        }

        // Check each row and if it's full, clear it
        for (int j = FIELDHEIGHT - 1; j >= 0; j--) {
            boolean fullRow = true;
            for (int i = 0; i < FIELDWIDTH; i++) {
                fullRow = fullRow && (field[i][j] != null);
            }
            if (fullRow) clearRow(j);
        }
        repaint();
    }

    /* Moves all rows above the given row down, sets the top row to null, and 
     * increments the score.*/
    public void clearRow(int row) {
        for (int i = 0; i < FIELDWIDTH; i++) {
            for (int j = row; j > 0; j--) {
                field[i][j] = field[i][j - 1];
                if (field[i][j - 1] != null) field[i][j - 1].fall(FALL_SPEED);

            }

        }
        score += 100;
        scoreLabel.setText("   Score: " + String.valueOf(score) + "   ");
        if (score >= scoreThreshold) {
            scoreThreshold += score;
            if (fall_interval > 25) fall_interval -= 25;
            if (move_interval > 25) move_interval -= 25;
        }
        repaint();
    }

    // Checks if the Tetromino intersects anything or if it is above the bottom
    public boolean intersectsV() {
        // Check for intersection
        List<Block> bList = block.getBlockList();
        for (int i = 0; i < FIELDWIDTH; i++) {
            for (int j = 0; j < FIELDHEIGHT; j++) {
                for (Block b : bList) {
                    if (b.intersects(field[i][j]) == Intersection.VERTICAL) { return true; }
                }
            }

        }
        return false;
    }

    /** Update the game one timestep and moving currently falling Tetromino if 
     *  it should be moved. */
    void move_tick() {
        if (isPaused || !hasStarted) return;

        // Checks if the block can move and if not, sets its "velocity" to 0
        boolean intersects = false;
        List<Block> bList = block.getBlockList();
        for (int i = 0; i < FIELDWIDTH; i++) {
            for (int j = 0; j < FIELDHEIGHT; j++) {
                for (Block b : bList) {
                    if (b.intersects(field[i][j]) == Intersection.HORIZONTAL) {
                        intersects = true;
                        break;
                    }
                }
            }

        }
        if (intersects) block.setDirection(0);

        // Things to do always, convenient to include them in move_timer
        block.allMove(); // Move (with vX = 0 if L/R arrows aren't pressed)
        if (rotateNextTick) { // Rotate once
            rotateNextTick = false;
            block.rotate();
            if (intersectsV()) {
                // if rotating it causes it to intersect, rotate it back
                block.rotate();
                block.rotate();
                block.rotate();
            }
        }

        // Check to see if there is anything in the top row
        boolean inTopRow = false;
        for (int i = 0; i < FIELDWIDTH; i++) {
            if (field[i][0] != null) {
                inTopRow = true;
                break;
            }
        }
        // if there is something in the top, but it can't fall...game over
        if (inTopRow && (intersectsV())) gameOver();
        repaint();
    }

    // Resets / starts the game
    public void restart() {
        if (isPaused) pause();
        reset();

        hasStarted = true;
        fall_timer.start();
        move_timer.start();
    }

    /** Set the state of the state of the game to its initial value and 
    prepare the game for keyboard input. */
    public void reset() {
        field = new Block[FIELDWIDTH][FIELDHEIGHT];
        block = randomBlock();
        block = block.setCs((FIELDWIDTH / 2) - 1, 0);
        nextBlock = randomBlock();
        switched = false;
        requestFocusInWindow();
        score = 0;
        scoreLabel.setText("   Score: " + String.valueOf(score) + "   ");
        reset.setVisible(false);
    }

    // Pauses the game
    public void pause() {
        if (!hasStarted) return;
        isPaused = !isPaused;
        if (isPaused) { // If paused, stop the timer and display "Resume"
            move_timer.stop();
            fall_timer.stop();
            pause.setText("Resume");
        } else { // If not paused, restart the timer and display "Pause"
            move_timer.start();
            fall_timer.start();
            pause.setText("Pause");
        }
        repaint();

    }

    // Ends the game
    public void gameOver() {
        // Fills the board with black
        for (int i = 0; i < FIELDWIDTH; i++) {
            for (int j = 0; j < FIELDHEIGHT; j++) {
                field[i][j] = new Block(i, j, Color.BLACK);
            }
        }
        nextBlock = null;

        reset.setVisible(true);
        hasStarted = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Paint background, border

        // Draw the grid
        for (int i = 0; i <= FIELDWIDTH; i++) {
            g.drawLine(i * blockDimension, 0, i * blockDimension, FIELDHEIGHT
                    * blockDimension);
        }
        for (int j = 0; j <= FIELDHEIGHT; j++) {
            g.drawLine(0, j * blockDimension, FIELDWIDTH * blockDimension, j
                    * blockDimension);
        }

        // Draw the current Tetromino
        block.draw(g);
        // Draw blocks in the field
        for (int i = 0; i < FIELDWIDTH; i++) {
            for (int j = 0; j < FIELDHEIGHT; j++) {
                if (field[i][j] != null) field[i][j].draw(g);
            }
        }

        // Draw the next block by translating the graphics context and
        // moving it vertically to make room for the text
        if (nextBlock != null) {
            g.translate(((FIELDWIDTH + 1) * blockDimension), (FIELDHEIGHT / 2)
                    * blockDimension);
            g.drawString("Next block:", 0, 0);
            g.translate(0, blockDimension);
            nextBlock.draw(g);
            // now translate it back
            g.translate(-(FIELDWIDTH + 1) * blockDimension,
                    -((FIELDHEIGHT / 2) + 1) * blockDimension);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((FIELDWIDTH + 6) * blockDimension, FIELDHEIGHT
                * blockDimension);
    }
}

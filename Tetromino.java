import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

/*
 * I traverse the blocks in the linked list by going to adjacent blocks first,
 * and if there are two options, go in the direction of the one you can continue
 * longest or in the same direction you were already going.
 * 
 */

public class Tetromino {

    private List<Block> blocks;
    private Shape       shape;
    private int         orientation;

    // The upper left corner of the Tetromino relative to field[0][0]
    // -- if rotated in a way that doesn't have a block in the upper left,
    // corresponds to the lower left
    private int         rx;
    private int         ry;

    // The x and y velocity of the Tetromino
    private int         vX;
    private int         vY;

    private int         rightBound  = 11; // Maximum permissible x, y values.
    private int         bottomBound = 23;
    private int         sideLength  = 20;

    public Tetromino(Shape s, int o, int x, int y, int velocityX, int velocityY) {
        blocks = new LinkedList<Block>();
        orientation = 0; // Starting orientation

        this.shape = s;
        this.rx = x;
        this.ry = y;
        this.vX = velocityX;
        this.vY = velocityY;

        switch (s) {
        // Adds blocks the the list in the right order and position
        // to display the given shape (with a unique color per shape)
        case RIGHTL: {
            for (int i = 0; i < 3; i++) {
                blocks.add(new Block(rx, ry + i, Color.ORANGE));
            }
            blocks.add(new Block(rx + 1, ry + 2, Color.ORANGE));
            break;
        }
        case LEFTL: {
            for (int i = 0; i < 3; i++) {
                blocks.add(new Block(rx + 1, ry + i, Color.BLUE));
            }
            blocks.add(new Block(rx, ry + 2, Color.BLUE));
            break;
        }
        case LEFTZZ: {
            blocks.add(new Block(rx, ry, Color.GREEN));
            blocks.add(new Block(rx + 1, ry, Color.GREEN));
            blocks.add(new Block(rx + 1, ry + 1, Color.GREEN));
            blocks.add(new Block(rx + 2, ry + 1, Color.GREEN));
            break;
        }
        case RIGHTZZ: {
            blocks.add(new Block(rx, ry + 1, Color.RED));
            blocks.add(new Block(rx + 1, ry + 1, Color.RED));
            blocks.add(new Block(rx + 1, ry, Color.RED));
            blocks.add(new Block(rx + 2, ry, Color.RED));
            break;
        }
        case LINE: {
            for (int i = 0; i < 4; i++)
                blocks.add(new Block(rx + i, ry, Color.CYAN));
            break;
        }
        case SQUARE: {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    blocks.add(new Block(rx + i, ry + j, Color.YELLOW));
                }
            }
            break;
        }
        case T: {
            for (int i = 0; i < 3; i++) {
                blocks.add(new Block(rx + i, ry, Color.MAGENTA));
            }
            blocks.add(new Block(rx + 1, ry + 1, Color.MAGENTA));
        }
        }
    }

    public void rotate() {
        // The four blocks in the Tetromino to rotate
        // Rotates CLOCKWISE
        Block b0 = blocks.get(0);
        Block b1 = blocks.get(1);
        Block b2 = blocks.get(2);
        Block b3 = blocks.get(3);

        switch (shape) {
        case LEFTL: {
            switch (orientation) {
            case 0: {
                clip(2, 1);
                b0.setXY(rx, ry);
                b1.setXY(rx, ry + 1);
                b2.setXY(rx + 1, ry + 1);
                b3.setXY(rx + 2, ry + 1);
                orientation++;
                break;
            }
            case 1: {
                clip(1, 2);
                b1.setXY(rx, ry + 1);
                b2.setXY(rx, ry + 2);
                b3.setXY(rx + 1, ry);
                orientation++;
                break;
            }
            case 2: {
                clip(2, 1);
                b1.setXY(rx + 1, ry);
                b2.setXY(rx + 2, ry);
                b3.setXY(rx + 2, ry + 1);
                orientation++;
                break;
            }
            case 3: {
                clip(1, 2);
                b0.setXY(rx + 1, ry);
                b1.setXY(rx + 1, ry + 1);
                b2.setXY(rx + 1, ry + 2);
                b3.setXY(rx, ry + 2);
                orientation = 0;
                break;
            }
            }
            break;
        }
        case RIGHTL: {
            switch (orientation) {
            case 0: {
                clip(2, 1);
                b1.setXY(rx + 1, ry);
                b2.setXY(rx + 2, ry);
                b3.setXY(rx, ry + 1);
                orientation++;
                break;
            }
            case 1: {
                clip(1, 2);
                b1.setXY(rx + 1, ry);
                b2.setXY(rx + 1, ry + 1);
                b3.setXY(rx + 1, ry + 2);
                orientation++;
                break;
            }
            case 2: {
                clip(2, 1);
                b0.setXY(rx, ry + 1);
                b1.setXY(rx + 1, ry + 1);
                b2.setXY(rx + 2, ry + 1);
                b3.setXY(rx + 2, ry);
                orientation++;
                break;
            }
            case 3: {
                clip(1, 2);
                b0.setXY(rx, ry);
                b1.setXY(rx, ry + 1);
                b2.setXY(rx, ry + 2);
                b3.setXY(rx + 1, ry + 2);
                orientation = 0;
                break;
            }
            }
            break;
        }
        case LEFTZZ: {
            switch (orientation) {
            case 0: {
                clip(1, 2);
                b0.setXY(rx + 1, ry);
                b1.setXY(rx + 1, ry + 1);
                b2.setXY(rx, ry + 1);
                b3.setXY(rx, ry + 2);
                orientation++;
                break;
            }
            case 1: {
                clip(2, 1);
                b0.setXY(rx, ry);
                b1.setXY(rx + 1, ry);
                b2.setXY(rx + 1, ry + 1);
                b3.setXY(rx + 2, ry + 1);
                orientation--;
                break;
            }
            }
            break;
        }
        case RIGHTZZ: {
            switch (orientation) {
            case 0: {
                clip(1, 2);
                b0.setXY(rx, ry);
                b1.setXY(rx, ry + 1);
                b2.setXY(rx + 1, ry + 1);
                b3.setXY(rx + 1, ry + 2);
                orientation++;
                break;
            }
            case 1: {
                clip(2, 1);
                b0.setXY(rx, ry + 1);
                b1.setXY(rx + 1, ry + 1);
                b2.setXY(rx + 1, ry);
                b3.setXY(rx + 2, ry);
                orientation--;
                break;
            }
            }
            break;
        }
        case LINE: {
            switch (orientation) {
            case 0: {
                clip(0, 3);
                b1.setXY(rx, ry + 1);
                b2.setXY(rx, ry + 2);
                b3.setXY(rx, ry + 3);
                orientation++;
                break;
            }
            case 1: {
                clip(3, 0);
                b1.setXY(rx + 1, ry);
                b2.setXY(rx + 2, ry);
                b3.setXY(rx + 3, ry);
                orientation--;
                break;
            }
            }
            break;
        }
        case T: {
            switch (orientation) {
            case 0: {
                clip(1, 2);
                b0.setXY(rx + 1, ry);
                b1.setXY(rx + 1, ry + 1);
                b2.setXY(rx + 1, ry + 2);
                b3.setXY(rx, ry + 1);
                orientation++;
                break;
            }
            case 1: {
                clip(2, 1);
                b0.setXY(rx, ry + 1);
                b1.setXY(rx + 1, ry + 1);
                b2.setXY(rx + 2, ry + 1);
                b3.setXY(rx + 1, ry);
                orientation++;
                break;
            }
            case 2: {
                clip(1, 2);
                b0.setXY(rx, ry);
                b1.setXY(rx, ry + 1);
                b2.setXY(rx, ry + 2);
                b3.setXY(rx + 1, ry + 1);
                orientation++;
                break;
            }
            case 3: {
                clip(2, 1);
                b1.setXY(rx + 1, ry);
                b2.setXY(rx + 2, ry);
                b3.setXY(rx + 1, ry + 1);
                orientation = 0;
                break;
            }
            }
        }
        }
    }

    public int getRX() {
        return rx;
    }

    public int getRY() {
        return ry;
    }

    public List<Block> getBlockList() {
        return blocks;
    }

    public Shape getShape() {
        return shape;
    }

    public void center() {
        rx = (((rightBound + 1) / 2) * sideLength);
        ry = 0;
    }

    public Tetromino setCs(int x, int y) {
        rx = x;
        ry = y;
        return new Tetromino(shape, orientation, rx, ry, vX, vY);
    }

    public void setDirection(int d) {
        if (d > 0) vX = 1;
        else if (d < 0) vX = -1;
        else
            vX = 0;
    }

    public void clip(int xShift, int yShift) {
        // if rotated, makes sure the Tetromino doesn't go off the screen
        while (rx + xShift > rightBound) {
            for (Block b : blocks)
                b.move(-1);
            rx--;
        }
        while (ry + yShift > bottomBound) {
            for (Block b : blocks)
                b.fall(-1);
            ry--;
        }

    }

    // Move the Tetromino at the given velocity by moving all of its component
    // blocks
    public void allMove() {
        boolean canMove = true;
        for (Block b : blocks) {
            if (vX > 0) canMove = (canMove && b.canMoveR());
            if (vX < 0) canMove = (canMove && b.canMoveL());
        }
        if (canMove) {
            for (Block b : blocks) {
                b.move(vX);
            }
            rx += vX;
        }
    }

    // True all of the component blocks are above the bottomBound
    public boolean allCanFall() {
        boolean canFall = true;
        for (Block b : blocks) {
            canFall = canFall && b.canFall();
        }
        return canFall;
    }

    // Moves all of the blocks vertically by their velocity
    public void allFall() {
        for (Block b : blocks) {
            b.fall(vY);
        }
        ry += vY;
    }

    /**
    * Should be overridden to allow game objects to change velocity or 
    * direction.  See Ball.java for an example.
    */

    /**
     * Should be overridden to provide game-object specific drawing.
     * 
     * @param g
     *      The <code>Graphics</code> context used for drawing the object.
     */

    public void draw(Graphics g) {
        for (Block b : blocks) {
            b.draw(g);
        }
    }
}

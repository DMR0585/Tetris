import java.awt.Color;
import java.awt.Graphics;

public class Block {
    private int   x;               // x and y indices of the block
    private int   y;

    private int   rightBound  = 11; // Maximum permissible x, y values.
    private int   bottomBound = 23;

    private int   sideLength  = 20;
    private Color color;

    public Block(int rx, int ry, Color c) {
        x = rx;
        y = ry;
        color = c;
    }

    public Color getColor() {
        return color;
    }

    public void setXY(int nx, int ny) {
        x = nx;
        y = ny;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean canMoveR() {
        return (x < rightBound);
    }

    public boolean canMoveL() {
        return (x > 0);
    }

    // Move the object at the given velocity
    public void move(int vX) {
        x += vX;
    }

    public boolean canFall() {
        return (y < bottomBound);
    }

    public void fall(int vY) {
        y += vY;
    }

    public Intersection intersects(Block other) {
        if (other == null) return Intersection.NONE;
        else if ((y + 1 == other.y) && (x == other.x)) return Intersection.VERTICAL;
        else if (((x + 1 == other.x) || (x - 1 == other.x)) && y == other.y) return Intersection.HORIZONTAL;
        else
            return Intersection.NONE;
    }

    /**
     * Draws the block and a black border around it
     * 
     * @param g
     *      The <code>Graphics</code> context used for drawing the object.
     */

    public void draw(Graphics g) {
        // Draw the block
        int rx = x * sideLength;
        int ry = y * sideLength;
        g.setColor(color);
        g.fillRect(x * sideLength, y * sideLength, sideLength, sideLength);
        // Draw the border
        g.setColor(Color.BLACK);
        g.drawLine(rx, ry, rx + sideLength, ry);
        g.drawLine(rx, ry, rx, ry + sideLength);
        g.drawLine(rx + sideLength, ry, rx + sideLength, ry + sideLength);
        g.drawLine(rx, ry + sideLength, rx + sideLength, ry + sideLength);
    }
}

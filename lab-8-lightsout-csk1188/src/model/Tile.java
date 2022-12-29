package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an individual tile. You will only use the getters for x,y, and on. Everything else will be accessed through the model.
 */
public class Tile {
    private final int x;
    private final int y;
    private final Board board;
    private boolean on;

    Tile(int x, int y, Board board, boolean on) {
        this.x = x;
        this.y = y;
        this.board = board;
        this.on = on;
    }

    /**
     * Test if the tile is on.
     * @return True iff the tile is on
     */
    public boolean isOn() {
        return on;
    }

    void setOn(boolean on) {
        // System.out.printf("Setting:%s to %b%n",this,on);
        this.on = on;
    }

    /**
     * Get the X coordinate (starting at 0) of this tile
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y coordinate (starting at 0) of this tile
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    void toggle() {
        on = !on;
        for (Tile t : getNeighbors()) {
            t.on = !t.on;
        }
    }

    Set<Tile> getNeighbors() {
        Set<Tile> ret = new HashSet<Tile>();
        //loops would be even messier
        if (x > 0)
            ret.add(board.getTile(x - 1, y));
        if (x < Board.BOARD_SIZE - 1)
            ret.add(board.getTile(x + 1, y));
        if (y > 0)
            ret.add(board.getTile(x, y - 1));
        if (y < Board.BOARD_SIZE - 1)
            ret.add(board.getTile(x, y + 1));

        return ret;
    }

    //Equality/hash is based on x and y but not board
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x && y == tile.y && on == tile.on;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, on);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", on=" + on +
                '}';
    }

}

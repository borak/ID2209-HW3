package se.kth.id2209.hw3;

import java.io.Serializable;

/**
 *
 * @author Kim
 */
public class Pos implements Serializable {
    int x, y;

    public Pos() {}
    
    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "["+x+","+y+"]";
    }
}

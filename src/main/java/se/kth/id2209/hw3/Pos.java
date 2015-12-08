package se.kth.id2209.hw3;

import java.io.Serializable;

/**
 * A position as a x- and y-coordinate.
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
    
    @Override
    public boolean equals(Object o) {
        if(((Pos)o).x==x && ((Pos)o).y==y) {
            return true;
        }
        return false;
    }
}

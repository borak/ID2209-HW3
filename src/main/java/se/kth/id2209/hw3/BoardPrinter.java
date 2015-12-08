package se.kth.id2209.hw3;

import java.util.ArrayList;
import java.util.List;

/**
 * A shared memory board printer only for visualisation and not for actual
 * board representation. Used mostly for visualisation during development.
 * @author Kim
 */
public class BoardPrinter {
    static List<Pos> posList = new ArrayList();
    static int N; 
    
    synchronized static void printBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------");
        sb.append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                boolean gotMatch = false;
                for(Pos pos : posList) {
                    if(pos.x == i && pos.y == j) {
                        sb.append("X");
                        gotMatch = true;
                        break;
                    }
                }
                if(!gotMatch) {
                    sb.append("0");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        sb.append("---------------------------------------------");
        System.out.println(sb.toString());
    }
}

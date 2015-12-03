package se.kth.id2209.hw3;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kim
 */
public class BoardPrinter {
    static List<int[]> posList = new ArrayList();
    static int N; 
    
    static void printBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------");
        sb.append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                boolean gotMatch = false;
                for(int[] pos : posList) {
                    if(pos[0] == i && pos[1] == j) {
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

package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 * @author Kim
 */
public class QueenAgent extends Agent {

    private final int ID = UniqueQueenIdGiver.createUniqueId();
    private final static String DF_TYPE = "Queen";
    private final static int SEARCH_FOR_QUEENAGENTS_DELAY = 3000;
    AID predecessor = null, successor = null;
    int N = 0;
    private Random random = new Random();
    int[] myPos = null;
    List<int[]> posList = new ArrayList();

    @Override
    public void setup() {
        super.setup();

        DFUtilities.registerService(this, DF_TYPE);

        SequentialBehaviour seq = new SequentialBehaviour();
        seq.addSubBehaviour(new WakerBehaviour(this, SEARCH_FOR_QUEENAGENTS_DELAY) {

            @Override
            public void onWake() {
                AID[] queens = DFUtilities.searchAllDF(QueenAgent.this, "Queen");
                N = queens.length;
                if (ID == UniqueQueenIdGiver.FIRST_ID) {
                    int[] pos = generateRandomPosition();
                    myPos = pos;
                    successor = getSuccessor(queens);
                    System.out.println("ADDING POSITION: "+getLocalName()
                        +"'s pos = " + pos[0] + ", " + pos[1]);
                    BoardPrinter.posList.add(pos);
                } else {
                    predecessor = getPredecessor(queens);
                    successor = getSuccessor(queens);
                    System.out.println(QueenAgent.this.getAID().getLocalName() + " pred=" + predecessor.getLocalName());
                    addBehaviour(new ProposePositionBehaviour(QueenAgent.this));
                }
            }

            private AID getPredecessor(AID[] queens) {
                for (int i = 0; i < queens.length; i++) {
                    int id = Integer.parseInt(
                            queens[i].getLocalName().substring("Queen".length()));
                    if (id == QueenAgent.this.ID - 1) {
                        return queens[i];
                    }
                }
                return null;
            }
            
            private AID getSuccessor(AID[] queens) {
                for (int i = 0; i < queens.length; i++) {
                    int id = Integer.parseInt(
                            queens[i].getLocalName().substring("Queen".length()));
                    if (id == QueenAgent.this.ID + 1) {
                        return queens[i];
                    }
                }
                return null;
            }
        });
        seq.addSubBehaviour(new ListenerBehaviour(this));
        addBehaviour(seq);
    }

    int[] generateRandomPosition() {
        int x = getId() - 1;
        int y = random.nextInt(N);

        return new int[]{x, y};
    }

    /**
     * A way to generate a new position that sticks to a single row per queen.
     * Requires that mySearchPos is updated everytime a position is colliding
     */
    /*int[] generateNewPosition() {
        int x = random.nextInt(N-1);
        int y = random.nextInt(N-1);

        return new int[]{x, y};
    }*/

    // NOT TESTED
    // jämför endast med sin egen pos
    boolean isColliding(int[] pos) {
        if (pos == null) {
            System.out.println("Comparing to pos=null");
            return true;
        }
        //System.out.println("Comparing to pos=" + pos[0] + "," + pos[1] + " STRAIGHT");

        //Straight
        if (pos[0] == myPos[0] || pos[1] == myPos[1]) {
            return true;
        }
        //System.out.println("Comparing to pos=" + pos[0] + "," + pos[1] + " DIAG");
       
        // Check diagonal in all four directions
        for (int h = 1; h < N - Math.max(myPos[0], myPos[1]); h++) {
            if (myPos[0] + h == pos[0] && myPos[1] + h == pos[1]) {
                return true;
            } else if (myPos[0] - h == pos[0] && myPos[1] - h == pos[1]) {
                return true;
            } else if (myPos[0] - h == pos[0] && myPos[1] + h == pos[1]) {
                return true;
            } else if (myPos[0] + h == pos[0] && myPos[1] - h == pos[1]) {
                return true;
            }
        }
        //System.out.println("Comparing to pos=" + pos[0] + "," + pos[1] + " SUCCESS");
        return false;
    }

    /*void printBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------");
        sb.append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(board[i][j] + " ");
            }
            sb.append("\n");
        }
        sb.append("---------------------------------------------");
        System.out.println(sb.toString());
    }*/

    public int getId() {
        return Integer.parseInt(getAID().getLocalName().substring("Queen".length()));
    }

    boolean gotPos() {
        return myPos != null;
    }

    AID getPredecessor() {
        return predecessor;
    }
    
    AID getSuccessor() {
        return successor;
    }
}

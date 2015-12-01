package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import java.util.Random;

/**
 *
 * @author Kim
 */
public class QueenAgent extends Agent {

    private final int ID = UniqueQueenIdGiver.createUniqueId();
    private final static String DF_TYPE = "Queen";
    private final static int SEARCH_FOR_QUEENAGENTS_DELAY = 3000;
    AID[] queens = null;
    AID predecessor = null;
    int N = 0;
    int[][] board = null;
    private Random random = new Random();
    boolean gotPos = false;
    int[] myPos = null;

    @Override
    public void setup() {
        super.setup();

        DFUtilities.registerService(this, DF_TYPE);

        SequentialBehaviour seq = new SequentialBehaviour();
        seq.addSubBehaviour(new WakerBehaviour(this, SEARCH_FOR_QUEENAGENTS_DELAY) {

            @Override
            public void onWake() {
                queens = DFUtilities.searchAllDF(QueenAgent.this, "Queen");
                N = queens.length;
                board = initBoard();
                if (ID == UniqueQueenIdGiver.FIRST_ID) {
                    int[] pos = generateRandomPosition();
                    board[pos[0]][pos[1]] = ID;
                    gotPos = true;
                    myPos = pos;
                    printBoard();
                    int[] cpos = getANonCollidingPos();
                    System.out.println("CPOS = "+ cpos[0]+","+cpos[1]);
                } else {
                    predecessor = getPredecessor (queens); 
                    System.out.println(QueenAgent.this.getAID().getLocalName() + " pred="+predecessor.getLocalName());
                    addBehaviour(new ProposePositionBehaviour(QueenAgent.this));
                }
            }

            private AID getPredecessor (AID[] queens) {
                for(int i=0; i<queens.length; i++) {
                    int id = Integer.parseInt(
                        queens[i].getLocalName().substring("Queen".length()));
                    if(id == QueenAgent.this.ID - 1) {
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
        int x = random.nextInt(N);
        int y = random.nextInt(N);
    
        return new int[]{x, y};
    }

    private int[][] initBoard() {
        int[][] board = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = 0;
            }
        }
        return board;
    }

    // NOT TESTED
    boolean isColliding(int[] pos) {
        if(pos == null) {
            System.out.println("Comparing to pos=null");
            return true;
        }
        System.out.println("Comparing to pos="+pos[0]+","+pos[1]);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(board[i][j] == 0) {
                    // Same pos
                    if(i == pos[0] && j == pos[1]) {
                        System.out.println("Same pos: i="+i+",j="+j);
                        return true;
                    }
                    //Check straight left/right & up/down
                    for (int h = 1; h < N; h++) {
                        if(i+h == pos[0]) {
                            System.out.println("Straight1: i="+i+",j="+j + " colliding with " + i+h + "," +pos[0]);
                            return true;
                        } else if(i-h == pos[0]) {
                            System.out.println("Straight2: i="+i+",j="+j);
                            return true;
                        } else if(j+h == pos[1]) {
                            System.out.println("Straight3: i="+i+",j="+j);
                            return true;
                        } else if(j-h == pos[1]) {
                            System.out.println("Straight4: i="+i+",j="+j);
                            return true;
                        }
                    }
                    
                    // Check diagonal in all four directions
                    for (int h = 1; h < N - Math.max(i, j); h++) {
                        if(i+h == pos[0] && j+h == pos[1]) {
                            System.out.println("Diagonal1: i="+i+",j="+j);
                            return true;
                        } else if(i-h == pos[0] && j-h == pos[1]) {
                            System.out.println("Diagonal2: i="+i+",j="+j);
                            return true;
                        } else if(i-h == pos[0] && j+h == pos[1]) {
                            System.out.println("Diagonal3: i="+i+",j="+j);
                            return true;
                        } else if(i+h == pos[0] && j-h == pos[1]) {
                            System.out.println("Diagonal4: i="+i+",j="+j);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    int[] getANonCollidingPos() {
        int[] pos = new int[2];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(board[i][j] == 0) {
                    pos[0] = i;
                    pos[1] = j;
                    
                    //Check straight left/right & up/down
                    boolean shouldContinue = false;
                    for (int h = 1; h < N; h++) {
                        if(i+h == pos[0] || i-h == pos[0]
                                || j+h == pos[1] ||j-h == pos[1]) {
                            shouldContinue = true;
                        }
                    }
                    if(shouldContinue) {
                        System.out.println("getNonCollidingPos Straight: i="+i+",j="+j);
                        continue;
                    }
                    
                    // Check diagonal in all four directions
                    for (int h = 1; h < N - Math.max(i, j); h++) {
                        if(i+h == pos[0] && j+h == pos[1]) {
                            shouldContinue = true;
                        } else if(i-h == pos[0] && j-h == pos[1]) {
                            shouldContinue = true;
                        } else if(i-h == pos[0] && j+h == pos[1]) {
                            shouldContinue = true;
                        } else if(i+h == pos[0] && j-h == pos[1]) {
                            shouldContinue = true;
                        }
                    }
                    if(shouldContinue) {
                        System.out.println("getNonCollidingPos Diagonal: i="+i+",j="+j);
                        continue;
                    }
                    
                    return pos;
                }
            }
        }
        return null;
    }

    void addPos(int[] pos, AID aid) {
        board[pos[0]][pos[1]] = Integer.parseInt(
                aid.getLocalName().substring("Queen".length()));
        printBoard();
    }
    
    void printBoard() {
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
    }
}

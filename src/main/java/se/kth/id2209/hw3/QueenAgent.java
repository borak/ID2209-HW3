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
    private final String DF_NAME = "Queen" + ID;
    private final static int SEARCH_FOR_QUEENAGENTS_DELAY = 3000;
    AID[] queens = null;
    AID predecessor = null;
    static int N = 0;
    int[][] board = null;
    private Random random = new Random();
    boolean gotPos = false;

    @Override
    public void setup() {
        super.setup();

        DFUtilities.registerService(this, DF_NAME);

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
                } else {
                    predecessor = queens[ID - 1];
                    addBehaviour(new ProposePositionBehaviour(QueenAgent.this));
                }
            }
        });
        seq.addSubBehaviour(new ListenerBehaviour(this));
        addBehaviour(seq);
    }

    int[] generateRandomPosition() {
        int x = random.nextInt(N) + 1;
        int y = random.nextInt(N) + 1;
    
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
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(board[i][j] != 0) {
                    // Same pos
                    if(i == pos[0] || j == pos[1]) {
                        return true;
                    }
                    //Check straight left/right & up/down
                    for (int h = 1; h < N; h++) {
                        if(i+h == pos[0]) {
                            return true;
                        } else if(i-h == pos[0]) {
                            return true;
                        } else if(j+h == pos[1]) {
                            return true;
                        } else if(j-h == pos[1]) {
                            return true;
                        }
                    }
                    
                    // Check diagonal in all four directions
                    for (int h = 1; h < N - Math.max(i, j); h++) {
                        if(i+h == pos[0] && j+h == pos[1]) {
                            return true;
                        } else if(i-h == pos[0] && j-h == pos[1]) {
                            return true;
                        } else if(i-h == pos[0] && j+h == pos[1]) {
                            return true;
                        } else if(i+h == pos[0] && j-h == pos[1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    void addPos(int[] pos, AID aid) {
        board[pos[0]][pos[1]] = Integer.parseInt(
                aid.getLocalName().substring("Queen".length()));
    }
}

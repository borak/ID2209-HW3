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
    private AID[] queens = null;
    AID predecessor = null;
    static int N = 0;
    private static int[][] board = null;
    private Random random = new Random();

    @Override
    public void setup() {
        super.setup();

        DFUtilities.registerService(this, DF_NAME);

        SequentialBehaviour seq = new SequentialBehaviour();
        seq.addSubBehaviour(new WakerBehaviour(this, SEARCH_FOR_QUEENAGENTS_DELAY) {

            @Override
            public void onWake() {
                queens = DFUtilities.searchAllDF(QueenAgent.this, "Queen");
                if (ID == UniqueQueenIdGiver.FIRST_ID) {
                    N = queens.length;
                    board = new int[N][N];
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            board[i][j] = 0;
                        }
                    }

                    int x = random.nextInt(N) + 1;
                    int y = random.nextInt(N) + 1;
                    board[x][y] = ID;

                } else {
                    predecessor = queens[ID - 1];
                    addBehaviour(new ProposePositionBehaviour(QueenAgent.this));
                }
            }
        });
        seq.addSubBehaviour(new ListenerBehaviour(this));
        addBehaviour(seq);
    }

}

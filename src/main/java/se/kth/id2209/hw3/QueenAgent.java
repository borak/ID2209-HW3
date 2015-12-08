package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The queen agent represents a queen as a chess piece. The queens will
 * cooperate with eachother to achieve a possible solution to having N Queens on
 * a NxN board.
 *
 * It moves on a board either straight or diagonal. This solution is made
 * efficient by only allowing the agent to be randomized by one axel.
 *
 * (ID - 1, random 0 -> N-1), N is the number of queens.
 *
 * @author Kim
 */
public class QueenAgent extends Agent {

    private final int ID = UniqueQueenIdGiver.createUniqueId();
    private final static String DF_TYPE = "Queen";
    private final static int SEARCH_FOR_QUEENAGENTS_DELAY = 3000;
    AID predecessor = null, successor = null;
    int N = 0;
    private final Random random = new Random();
    private Pos myPos = null;
    private final List<Integer> posList = new ArrayList();
    private final Object posLock = new Object();
    private final Object myposLock = new Object();

    void addPos(int y) {
        synchronized (posLock) {
            if (!posList.contains(y)) {
                posList.add(y);
            }
            System.out.println("COLLIDED WITH " + (getId() - 1) + ", " + y);
        }
    }

    boolean containsPos(int y) {
        synchronized (posLock) {
            return posList.contains(y);
        }
    }

    List<Integer> getPosList() {
        synchronized (posLock) {
            return new ArrayList(posList);
        }

    }

    /**
     * Method for testing certain collision situations.
     */
    private void test() {
        if (ID != 1) {
            return;
        }

        Pos pos = new Pos(3, 3);

        myPos = new Pos(0, 4);
        System.out.println("0,4 && 3,3 collides? : " + isColliding(pos));

        myPos = new Pos(1, 2);
        System.out.println("1,2 && 3,3 collides? : " + isColliding(pos));

        myPos = new Pos(2, 0);
        System.out.println("2,0 && 3,3 collides? : " + isColliding(pos));
    }

    /**
     * Method for testing different predetermined scenarios.
     */
    private void testPos() {
        switch (ID) {
            case 1:
                setPos(new Pos(0, 4));
                break;
            case 2:
                setPos(new Pos(1, 2));
                break;
            case 3:
                setPos(new Pos(2, 0));
                break;
            case 4:
                setPos(new Pos(3, 3));
                break;
        }
    }

    /**
     * Registers itself on the DFService. (1) Searches for other queen agents
     * and proposes a position. (2) Starts listening for messages.
     */
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
                BoardPrinter.N = N;
                if (ID == UniqueQueenIdGiver.FIRST_ID) {
                    Pos pos = generateRandomPosition();
                    setPos(pos);
                    successor = getSuccessor(queens);
                    System.out.println(QueenAgent.this.getAID().getLocalName()
                            + " pred=null"
                            + " succ=" + successor.getLocalName());
                } else {
                    predecessor = getPredecessor(queens);
                    successor = getSuccessor(queens);
                    String sname;
                    if (successor == null) {
                        sname = "null";
                    } else {
                        sname = successor.getLocalName();
                    }
                    System.out.println(QueenAgent.this.getAID().getLocalName()
                            + " pred=" + predecessor.getLocalName()
                            + " succ=" + sname);
                    addBehaviour(new ProposePositionBehaviour(QueenAgent.this));
                }
            }

            private AID getPredecessor(AID[] queens) {
                for (AID queen : queens) {
                    int id = Integer.parseInt(queen.getLocalName().substring("Queen".length()));
                    if (id == QueenAgent.this.ID - 1) {
                        return queen;
                    }
                }
                return null;
            }

            private AID getSuccessor(AID[] queens) {
                for (AID queen : queens) {
                    int id = Integer.parseInt(queen.getLocalName().substring("Queen".length()));
                    if (id == QueenAgent.this.ID + 1) {
                        return queen;
                    }
                }
                return null;
            }
        });
        seq.addSubBehaviour(new ListenerBehaviour(this));
        addBehaviour(seq);
    }

    /**
     * Generates a random position in the row (column?) of the Queen.
     *
     * @return A new {x,y} coordinate within the board
     */
    Pos generateRandomPosition() {
        Pos pos = new Pos();
        pos.x = getId() - 1;
        pos.y = random.nextInt(N);

        return pos;
    }

    /**
     * Checks if the argument position collides with the current Quen
     *
     * @param pos The x and y coordinates to check
     * @return True if there is a collistion, false if there is no collision
     */
    boolean isColliding(Pos pos) {
        if (pos == null) {
            System.err.println("Comparing to pos=null");
            return true;
        }

        synchronized (myposLock) {
            if (pos.x == myPos.x || pos.y == myPos.y) {
                return true;
            }

            for (int h = 1; h < N; h++) {
                if (myPos.x + h == pos.x && myPos.y + h == pos.y) {
                    return true;
                } else if (myPos.x - h == pos.x && myPos.y - h == pos.y) {
                    return true;
                } else if (myPos.x - h == pos.x && myPos.y + h == pos.y) {
                    return true;
                } else if (myPos.x + h == pos.x && myPos.y - h == pos.y) {
                    return true;
                }
            }

            return false;
        }
    }

    public int getId() {
        return Integer.parseInt(getAID().getLocalName().substring("Queen".length()));
    }

    boolean gotPos() {
        synchronized (posLock) {
            return myPos != null;
        }
    }

    synchronized void setPos(Pos pos) {
        synchronized (posLock) {
            myPos = pos;
            System.out.println("ADDING POSITION: " + getLocalName()
                    + "'s pos = " + pos.x + ", " + pos.y);
            BoardPrinter.posList.add(pos);
            BoardPrinter.printBoard();
        }
    }

    AID getPredecessor() {
        return predecessor;
    }

    AID getSuccessor() {
        return successor;
    }

    Pos getPos() {
        synchronized (posLock) {
            return myPos;
        }
    }
}

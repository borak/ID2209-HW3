package se.kth.id2209.hw3;

/**
 *
 * @author Kim
 */
class UniqueQueenIdGiver {
    private static int counter = 0;
    static final int FIRST_ID = 1;
    
    synchronized static int createUniqueId() {
        return ++counter;
    }
}

package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

/**
 *
 * @author Kim
 */
public class QueenAgent extends Agent {
    
    private final int ID = UniqueQueenIdGiver.createUniqueId();
    private final String DF_NAME = "Queen" + ID;
    private final static int SEARCH_FOR_QUEENAGENTS_DELAY = 3000;
    private AID[] queens = null;
    
    @Override
    public void setup() {
        super.setup();
        
        DFUtilities.registerService(this, DF_NAME);
        
        addBehaviour(new WakerBehaviour(this, SEARCH_FOR_QUEENAGENTS_DELAY) {
            
            @Override
            public void onWake() {
                queens = DFUtilities.searchAllDF(QueenAgent.this, "Queen");
            }
        });
    }
    
}

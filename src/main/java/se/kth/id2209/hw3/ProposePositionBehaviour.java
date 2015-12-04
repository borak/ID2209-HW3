package se.kth.id2209.hw3;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  ProposePositionBehaviour is a OneShotBehaviour that picks a spot on the board and proposes
 *      that to the predecessor of the agent.
 *
 */
public class ProposePositionBehaviour extends OneShotBehaviour {

    private Pos pos = null;
    
    public ProposePositionBehaviour(QueenAgent agent) {
        super(agent);
        pos = agent.generateRandomPosition();
        while(agent.containsPos(pos.y)) {
            pos = agent.generateRandomPosition();
        }
        agent.addPos(pos.y);
    }
    
    public ProposePositionBehaviour(QueenAgent agent, Pos pos) {
        super(agent);
        this.pos = pos;
    }
    
    @Override
    public void action() {
        QueenAgent agent = ((QueenAgent) myAgent);
        
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setOntology(Ontologies.PROPOSE_POSITION);
        msg.addReceiver(agent.predecessor);
        
        try {
            msg.setContentObject(pos);
        } catch (IOException ex) {
            Logger.getLogger(ProposePositionBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        myAgent.send(msg);
    }
    
}

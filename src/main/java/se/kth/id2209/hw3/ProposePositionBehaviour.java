package se.kth.id2209.hw3;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kim
 */
public class ProposePositionBehaviour extends OneShotBehaviour {

    public ProposePositionBehaviour(QueenAgent agent) {
        super(agent);
    }
    
    @Override
    public void action() {
        QueenAgent agent = ((QueenAgent) myAgent);
        
        ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        msg.setOntology(Ontologies.PROPOSE_POSITION);
        msg.addReceiver(agent.predecessor);
        
        int[] pos = agent.generateRandomPosition();
        try {
            msg.setContentObject(pos);
        } catch (IOException ex) {
            Logger.getLogger(ProposePositionBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        myAgent.send(msg);
    }
    
}

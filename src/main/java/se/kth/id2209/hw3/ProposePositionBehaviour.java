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

    private int[] pos = null;
    
    public ProposePositionBehaviour(QueenAgent agent) {
        super(agent);
        pos = agent.generateRandomPosition();
        //pos = agent.getNonCollidingPos();
        if(pos == null) {
            System.out.println("ProposePositionBehaviour - pos=null");
        } else {
            System.out.println("ProposePositionBehaviour - pos="+pos[0]+","+pos[1]);
        }
    }
    
    public ProposePositionBehaviour(QueenAgent agent, int[] pos) {
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

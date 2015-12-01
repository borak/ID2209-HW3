package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kim
 */
public class ListenerBehaviour extends CyclicBehaviour {

    ListenerBehaviour(QueenAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        QueenAgent agent = ((QueenAgent) myAgent);

        if (msg == null) {
            block();
            return;
        }

        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            try {
                agent.addPos((int[]) msg.getContentObject(), msg.getSender());
            } catch (UnreadableException ex) {
                Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                block();
            }
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.PROPOSE_POSITION)) {
            int[] pos = null;
            try {
                pos = (int[]) msg.getContentObject();
            } catch (UnreadableException ex) {
                Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                block();
                return;
            }

            ACLMessage reply = msg.createReply();

            if (!agent.gotPos) {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                reply.setOntology(Ontologies.NOT_READY_FOR_PROPOSAL);
            } else if (agent.isColliding(pos)) {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                reply.setOntology(Ontologies.POSITION_COLLIDING);
            } else {
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                for (AID aid : agent.queens) {
                    reply.addReceiver(aid);
                }
            }
            agent.send(msg);
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.NOT_READY_FOR_PROPOSAL)) {

        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.POSITION_COLLIDING)) {

        }
    }

}

package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ProposeInitiator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ListenerBehaviour handles the communication between Queens. It recieves
 * and forwards position proposals, rejections, accepts etc
 *
 */
public class ListenerBehaviour extends CyclicBehaviour {

    ListenerBehaviour(final QueenAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        final ACLMessage msg = myAgent.receive();
        final QueenAgent agent = ((QueenAgent) myAgent);

        if (msg == null) {
            block();
            return;
        }
        
        Pos pos = null;
            try {
                pos = (Pos) msg.getContentObject();
            } catch (UnreadableException ex) {
                ex.printStackTrace();
                block();
                return;
            }

        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && 
                !agent.gotPos() && agent.getId() == pos.x + 1) {
            agent.setPos(pos);
        } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && agent.getSuccessor() != null
                && agent.gotPos()) {
            System.out.println(agent.getLocalName() + " FORWARDS TO "+agent.getSuccessor().getLocalName());
            forwardMsg(msg, agent.getSuccessor());
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.PROPOSE_POSITION)) {
            ACLMessage reply = new ACLMessage();
            try {
                reply.setContentObject(pos);
            } catch (IOException ex) {
                Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!agent.gotPos()) {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                reply.setOntology(Ontologies.NOT_READY_FOR_PROPOSAL);
                reply.addReceiver(msg.getSender());
            } else if (agent.isColliding(pos)) {
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                reply.setOntology(Ontologies.POSITION_COLLIDING);
                reply.addReceiver(msg.getSender());
            } else if (agent.getId() != 1) {
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setOntology(Ontologies.PROPOSE_POSITION);
                reply.addReceiver(agent.getPredecessor());
            } else {
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setOntology(Ontologies.ACCEPTED_PROPOSAL);
                reply.addReceiver(agent.getSuccessor());
                System.out.println(agent.getSuccessor().getLocalName()
                        + "' is getting a proposal that was accepted by "
                        + agent.getLocalName());
            }
            agent.send(reply);
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.NOT_READY_FOR_PROPOSAL) 
                || msg.getOntology().equalsIgnoreCase(Ontologies.POSITION_COLLIDING)) {
            if(agent.gotPos() && agent.getSuccessor() == null) {
                block();
                return;
            } else if (agent.gotPos()) {
                forwardMsg(msg, agent.getSuccessor());
                return;
            } else if (agent.getPosList().size() == agent.N) {
                System.out.println("NO SOLUTION FOUND.");
                block();
                return;
            } 
            agent.addBehaviour(new ProposePositionBehaviour(agent));
        } else {
            //System.out.println(agent.getLocalName() + "STOPS SENDING");
            block();
        }
    }

    /**
     * Used to send a proposed position to the predecessor of the current agent
     *
     */
    private void forwardMsg(ACLMessage msg, AID receiver) {
        ACLMessage forwardMsg = new ACLMessage(msg.getPerformative());
        forwardMsg.setOntology(msg.getOntology());
        try {
            forwardMsg.setContentObject(msg.getContentObject());
        } catch (UnreadableException | IOException ex) {
            Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
        forwardMsg.addReceiver(receiver);
        myAgent.send(forwardMsg);
    }

}

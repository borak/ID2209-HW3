package se.kth.id2209.hw3;

import jade.core.AID;
import jade.core.Agent;
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
 *
 * @author Kim
 */
public class ListenerBehaviour extends CyclicBehaviour {

    private final static int RESEND_PROPOSAL_DELAY = 1000;

    ListenerBehaviour(QueenAgent agent) {
        super(agent);
    }

    @Override
    public void action() {
        /*try {
         Thread.sleep(500);
         } catch (InterruptedException ex) {
         Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        final ACLMessage msg = myAgent.receive();
        final QueenAgent agent = ((QueenAgent) myAgent);

        if (msg == null) {
            block();
            return;
        }
        if(agent.getId() == 3) {
            System.out.println("3333333333:"+msg.getOntology());
        }

        if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL && agent.myPos != null) {
            forwardMsg(msg, agent.getSuccessor());
        } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
            int[] pos = null;
            try {
                pos = (int[]) msg.getContentObject();
            } catch (UnreadableException ex) {
                ex.printStackTrace();
                block();
                return;
            }

            agent.myPos = pos;
            System.out.println("ADDING POSITION: "+agent.getLocalName()
                    +"'s pos = " + pos[0] + ", " + pos[1]);
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.PROPOSE_POSITION)) {
            int[] pos = null;
            try {
                pos = (int[]) msg.getContentObject();
            } catch (UnreadableException ex) {
                Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                block();
                return;
            }

            //System.out.println(agent.getLocalName() + ": got pos=" + pos[0] + ", " + pos[1]
            //        + " from " + msg.getSender().getLocalName() + ". GotPos="
            //        + agent.gotPos());

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
                        +"' is getting a proposal that was accepted by " 
                        + agent.getLocalName());
                /*agent.addBehaviour(new OneShotBehaviour() {

                    @Override
                    public void action() {
                        ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);
                        informMsg.setOntology(Ontologies.ACCEPTED_PROPOSAL);
                        for (AID aid : agent.queens) {
                            informMsg.addReceiver(aid);
                        }
                        try {
                            informMsg.setContentObject(msg.getContentObject());
                        } catch (IOException | UnreadableException ex) {
                            Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        agent.send(informMsg);
                    }
                });*/
            }
            agent.send(reply);
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.NOT_READY_FOR_PROPOSAL)) {

            System.out.println(agent.getLocalName() + " received NOT READY");

            int[] pos = null;
            try {
                pos = (int[]) msg.getContentObject();
            } catch (UnreadableException ex) {
                Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                agent.addBehaviour(new ProposePositionBehaviour(agent));
                block();
                return;
            }

            final int[] finalpos = pos;
            agent.addBehaviour(new WakerBehaviour(myAgent, RESEND_PROPOSAL_DELAY) {
                @Override
                public void onWake() {
                    agent.addBehaviour(new ProposePositionBehaviour(agent, finalpos));
                }
            });
        } else if (msg.getOntology().equalsIgnoreCase(Ontologies.POSITION_COLLIDING)) {
            if(agent.gotPos()) {
                msg.addReplyTo(agent.getSuccessor());
                agent.send(msg);
                return;
            }
            if(agent.posList.size() == agent.N) {
                System.out.println("NO SOLUTION FOUND.");
            } else {
                //System.out.println(agent.getLocalName() + " received POSITION_COLLIDING");
                agent.addBehaviour(new ProposePositionBehaviour(agent));
            }
            
        }
    }

    private void forwardMsg(ACLMessage msg, AID receiver) {
        System.out.println(myAgent.getLocalName()+" is FORWARDING to " + receiver.getLocalName());
        ACLMessage forwardMsg = new ACLMessage(msg.getPerformative());
        forwardMsg.setOntology(msg.getOntology());
        try {
            forwardMsg.setContentObject(msg.getContentObject());
        } catch (UnreadableException | IOException ex) {
            Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addReceiver(receiver);
        myAgent.send(msg);
    }
    
    /*private void handleAccept(QueenAgent agent, ACLMessage msg) {
        try {
            int[] pos = (int[]) msg.getContentObject();
            System.out.println(agent.getLocalName() + ": adding pos="
                    + pos[0] + ", " + pos[1]);
            agent.addPos(pos, msg.getSender());
        } catch (UnreadableException ex) {
            Logger.getLogger(ListenerBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            block();
        }
    }*/

}

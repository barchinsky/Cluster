/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import sockets.*;
/**
 *
 * @author Stas
 */

enum DirectionType {NONE, LEFT, RIGHT}

public abstract class Node implements APIDelegate {
    
    public Receiver receiver;
    public TaskManager taskManager;
            
    public int id;    
    
    public String host;
    public int selfPort;
    public int leftNodePort;
    public int rightNodePort;
    
    
    public Matrix B;
    public Matrix C;
    public Matrix resultVector;
    public int resultColNumber;
    public Map<Number, Matrix> calculatedVectors;
    
    public Node(int id) {
        this.id = id;
        
        SettingsManager sm = SettingsManager.getInstance();
        this.host = sm.host;
        this.selfPort = sm.portForNode(id);
        this.leftNodePort = sm.portForNode(id - 1);
        this.rightNodePort = sm.portForNode(id + 1);
        this.taskManager = new TaskManager(this);
        this.calculatedVectors = new HashMap<Number,Matrix>();
    }
    
    public void runServer() throws InterruptedException, ParseException {
        Exchanger<String> receiverToMainEx = new Exchanger<String>();
        Exchanger<String> mainToReceiverEx = new Exchanger<String>();

        Receiver rcvr = new Receiver(this.selfPort, receiverToMainEx, mainToReceiverEx);

        new Thread(rcvr).start();

        while(true){
                String inputJSON = receiverToMainEx.exchange(null);
                
                String ouputJSON = APIManager.parseJSON(inputJSON, this);

                mainToReceiverEx.exchange(ouputJSON);
        }
    }
    
    public void tryCalculateAndSend(DirectionType directionType) {
        Thread t1 = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    if (taskManager.calculateSelfVector()) {
                        System.out.println("Self vector calculated");
                        resultVector.outToLog();
                        if (directionType != DirectionType.NONE) {
                            if (directionType == DirectionType.LEFT) {
                                taskManager.sendVectorLeft(resultVector, resultColNumber);
                            } else {
                                taskManager.sendVectorRight(resultVector, resultColNumber);
                            }                            
                        } else {
                            if (taskManager.isAvailableAllVectorsForCalulation()) {
                                showResults();
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });        
        t1.start();        
    }
    
    public void showResults() {
        System.out.println("\n\n\n===============================================\nCluster calculating completed successful:");
        
        for (int i = 0; i < 4 ; i++) {
            if (i == 1) {
                double firstNumber = resultVector.items[0][0];
                System.out.print(String.valueOf(firstNumber) + "\t");
            } else {
                Integer index = new Integer(i);
                Number tmpKey = (Number)index;
                Matrix tmpMatrix = calculatedVectors.get(tmpKey);
                double tmpResult = tmpMatrix.items[0][0];
                System.out.print(String.valueOf(tmpResult) + "\t");
            }
        }
        
        System.out.println("\n\n");
    }
}

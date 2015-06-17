/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.util.Set;
import java.util.concurrent.Exchanger;
import sockets.Sender;

/**
 *
 * @author Stas
 */
public class TaskManager {
    
    public Node node;
    
    public TaskManager(Node node) {
        this.node = node;
    }
    
    public boolean calculateSelfVector() {
        if (node.B != null && node.C != null) {
            
            int colNumber = node.id - 1;
            Point p = new Point(colNumber,0);
            Size s = new Size(1,4);
            
            Matrix subMutrix = node.C.subMatrix(p, s); 
            
            Matrix vector = node.B.multiplyByMatrix(subMutrix);
            node.resultVector = vector;
            node.resultColNumber = colNumber;
            
            return true;
        }
        return false;
    }
    
    public boolean isAvailableAllVectorsForCalulation() {
        boolean isSelfExist = node.resultVector != null;
        int countOfAvailableVectors = node.calculatedVectors.size();
        if (countOfAvailableVectors == 3 && isSelfExist) {
            return true;
        }
        
        return false;
    }
    
    public void sendMatrixRight(Matrix M, MatrixType matrixType) {
        String comment = "Send Matrix " + matrixType.name() + " from node " + String.valueOf(node.id) + " to " + String.valueOf(node.id + 1);
        String json = APIManager.getJSONStringForMatrix(M, matrixType, comment);
        sendData(json, node.rightNodePort);
    }
    
    public void sendMatrixLeft(Matrix M, MatrixType matrixType) {
        String comment = "Send Matrix " + matrixType.name() + " from node " + String.valueOf(node.id) + " to " + String.valueOf(node.id - 1);
        String json = APIManager.getJSONStringForMatrix(M, matrixType, comment);
        sendData(json, node.leftNodePort);
    }
    
    public void sendVectorRight(Matrix M, int rowNumber) {
        String comment = "Send Vector for row" + String.valueOf(rowNumber) + " from node " + String.valueOf(node.id) + " to " + String.valueOf(node.id + 1);
        String json = APIManager.getJSONStringForVector(M, rowNumber, comment);
        sendData(json, node.rightNodePort);
    }
    
    public void sendVectorLeft(Matrix M, int rowNumber) {
        String comment = "Send Vector for row" + String.valueOf(rowNumber) + " from node " + String.valueOf(node.id) + " to " + String.valueOf(node.id + 1);
        String json = APIManager.getJSONStringForVector(M, rowNumber, comment);
        sendData(json, node.leftNodePort);
    }
    
    
    private void sendData(String json, int port) {
        Thread t1 = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Exchanger<String> resultEx = new Exchanger<String>(); // keep  calculation results from right node
                    Exchanger<String> dataEx = new Exchanger<String>(); // input data for right node

                    Sender toSender = new Sender(node.host, port, resultEx, dataEx,"0");

                    new Thread(toSender).start();

                    dataEx.exchange(json);

                    String responsJSON = resultEx.exchange(null);
                    APIManager.parseJSON(responsJSON, node);
                }
                catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });        
        t1.start();
    }    
}

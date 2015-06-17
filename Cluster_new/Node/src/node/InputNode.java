/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.util.concurrent.Exchanger;
import sockets.Sender;

/**
 *
 * @author Stas
 */
public class InputNode implements APIDelegate {
    
    public Sender inputDataSender;
    public Matrix MC;
    
    String host;
    int inputNodePort;
    
    public InputNode(int connectToNodeId) {        
        SettingsManager sm = SettingsManager.getInstance();
        this.host = sm.host;
        this.inputNodePort = sm.portForNode(connectToNodeId);
    }
    
    public void setupMatrix() {
        MC = new Matrix(4, 4);
        MC.setSampleDataWithNumber(1);
    }
    
    public void sendDataToCluster() {
        try {     
            Exchanger<String> resultEx = new Exchanger<String>(); // keep  calculation results from right node
            Exchanger<String> dataEx = new Exchanger<String>(); // input data for right node

            inputDataSender = new Sender(host, inputNodePort, resultEx, dataEx,"0");
            
            new Thread(inputDataSender).start();
            
            String dataJSON = APIManager.getJSONStringForMatrix(MC, MatrixType.MC, "Send MC to node 3 from InputNode");
            dataEx.exchange(dataJSON);

            String responsJSON = resultEx.exchange(null);
            APIManager.parseJSON(responsJSON, this);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
// APIDelegate methods
    @Override
    public String receivedMatrixB(Matrix B) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String receivedMatrixC(Matrix C) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String receivedVector(Matrix V, int rowIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String receivedSuccess(boolean isSuccess) {
        if (isSuccess) {
            System.out.println("Matric C sent successful");
        } else {
            System.out.println("Matric C didn't send correctly");
        }
        return null;
    }
    
    public static void main(String args[]){
        System.out.println("InputNode Started...");
        
        InputNode inputNode = new InputNode(3);
        inputNode.setupMatrix();
        inputNode.sendDataToCluster();
    }  
}

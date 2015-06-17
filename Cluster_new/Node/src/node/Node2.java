/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

// This node should receive matrix MC

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

public class Node2 extends Node {
    
    public static void main(String[] args) {
        System.out.println("Node 2 Stared...");
        
        Node2 node = new Node2(2);
        try {
            node.runServer();
        } catch (InterruptedException ex) {
            Logger.getLogger(Node1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Node1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Node2(int id) {
        super(id);
    }

    @Override
    public String receivedMatrixB(Matrix B) {
        this.B = B;
        
        taskManager.sendMatrixRight(B, MatrixType.MB);
        taskManager.sendMatrixLeft(B, MatrixType.MB);
        
        tryCalculateAndSend(DirectionType.NONE);
        
        return APIManager.getJSONStingSuccess(true, "Matrix B received on Node 2");
    }

    @Override
    public String receivedMatrixC(Matrix C) {
        this.C = C;
        taskManager.sendMatrixLeft(C, MatrixType.MC);
                
        tryCalculateAndSend(DirectionType.NONE);
        
        return APIManager.getJSONStingSuccess(true, "Matrix C received on Node 2");
    }

    @Override
    public String receivedVector(Matrix V, int colIndex) {
        System.out.println("Received Vector");
        V.outToLog();
            
        Integer intObj = new Integer(colIndex);
        Number key = (Number)intObj;
        calculatedVectors.put(key, V);
        
        if (taskManager.isAvailableAllVectorsForCalulation()) {
            showResults();            
        }
        return APIManager.getJSONStingSuccess(true, "Thansk");
    }

    @Override
    public String receivedSuccess(boolean isSuccess) {
        System.out.println("data received " + (isSuccess == true ? "YES" : "NO"));
        return null;
    }
}

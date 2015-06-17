/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stas
 */
public class Node3 extends Node {
    public static void main(String[] args) {
        System.out.println("Node 3 Stared...");
        
        Node3 node = new Node3(3);
        try {
            node.runServer();
        } catch (InterruptedException ex) {
            Logger.getLogger(Node1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Node1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Node3(int id) {
        super(id);
    }

    @Override
    public String receivedMatrixB(Matrix B) {
        this.B = B;
        taskManager.sendMatrixRight(B, MatrixType.MB);
        
        tryCalculateAndSend(DirectionType.LEFT);
        
        return APIManager.getJSONStingSuccess(true, "Matrix B received on Node 3");
    }

    @Override
    public String receivedMatrixC(Matrix C) {
        this.C = C;        
        taskManager.sendMatrixRight(C, MatrixType.MC);
        taskManager.sendMatrixLeft(C, MatrixType.MC);
        
        tryCalculateAndSend(DirectionType.LEFT);
        
        return APIManager.getJSONStingSuccess(true, "Matrix C received on Node 3");
    }

    @Override
    public String receivedVector(Matrix V, int colIndex) {
        taskManager.sendVectorLeft(V, colIndex);
        return APIManager.getJSONStingSuccess(true, "Vector received");
    }

    @Override
    public String receivedSuccess(boolean isSuccess) {
        System.out.println("data received " + (isSuccess == true ? "YES" : "NO"));
        return null;
    }
}

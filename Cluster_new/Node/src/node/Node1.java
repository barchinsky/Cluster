/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

// This node should receive matrix MB and print result of operation MA = MB * MC

import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;
import sockets.Sender;

public class Node1 extends Node {
        
    public static void main(String[] args) {
        System.out.println("Node 1 Stared...");
        
        Node1 node = new Node1(1);
        try {
            node.runServer();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } 
    }
    
    public Node1(int id) {
        super(id);
    }

    @Override
    public String receivedMatrixB(Matrix B) {
        this.B = B;
                
        tryCalculateAndSend(DirectionType.RIGHT);
        
        return APIManager.getJSONStingSuccess(true, "Matrix B received on Node 1");
    }

    @Override
    public String receivedMatrixC(Matrix C) {
        this.C = C;
        
        tryCalculateAndSend(DirectionType.RIGHT);
        
        return APIManager.getJSONStingSuccess(true, "Matrix C received on Node 1");
    }

    @Override
    public String receivedVector(Matrix V, int colIndex) {        
        System.out.println("Ololo it's inposible");
        taskManager.sendVectorLeft(V, colIndex);
        return APIManager.getJSONStingSuccess(true, "Vector received");
    }

    @Override
    public String receivedSuccess(boolean isSuccess) {
        System.out.println("data received " + (isSuccess == true ? "YES" : "NO"));
        return null;
    }
}

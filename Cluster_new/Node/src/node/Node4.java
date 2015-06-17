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
public class Node4 extends Node {
    public static void main(String[] args) {
        System.out.println("Node 4 Stared...");
        
        Node4 node = new Node4(4);
        try {
            node.runServer();
        } catch (InterruptedException ex) {
            Logger.getLogger(Node1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Node1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Node4(int id) {
        super(id);
    }

    @Override
    public String receivedMatrixB(Matrix B) {
        this.B = B;
        
        tryCalculateAndSend(DirectionType.LEFT);
        
        return APIManager.getJSONStingSuccess(true, "Matrix B received on Node 4");
    }

    @Override
    public String receivedMatrixC(Matrix C) {
        this.C = C;        
        
        tryCalculateAndSend(DirectionType.LEFT);
        
        return APIManager.getJSONStingSuccess(true, "Matrix C received on Node 4");
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

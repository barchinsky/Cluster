/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

/**
 *
 * @author Stas
 */
public interface APIDelegate {
    
    public String receivedMatrixB(Matrix B);
    public String receivedMatrixC(Matrix C);
    public String receivedVector(Matrix V, int colIndex);
    public String receivedSuccess(boolean isSuccess);
}

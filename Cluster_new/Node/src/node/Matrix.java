/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Stas
 */


class Point {
    int x;
    int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Size {
    int width;
    int height;
            
    public Size(int w, int h) {
        width = w;
        height = h;        
    }
}


public class Matrix {
    double [][]items;
    int rowCount, colCount;
    
    // JSON keys
    public static final String kRows = "rows";
    public static final String kCols = "cols";
    public static final String kItems = "items";

    Matrix(int rows, int cols) {
        rowCount = rows;
        colCount = cols;        
        items = new double[rows][cols];
    }    
    
    public void setSampleData() {
        double counter = 1;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                items[i][j] = counter++;
            }
        }
    }
    
    public void setSampleDataWithNumber(double number) {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                items[i][j] = number;
            }
        }
    }
    
    
    
    public Matrix multiplyByMatrix(Matrix B) {
        Matrix A = this;
                
        int mA = A.rowCount;
        int nA = A.colCount;
        int mB = B.rowCount;
        int nB = B.colCount;
        
        if (nA != mB) throw new RuntimeException("Illegal matrix dimensions.");

        Matrix C = new Matrix(mA,nB);
        
        for (int i = 0; i < mA; i++)
            for (int j = 0; j < nB; j++)
                for (int k = 0; k < nA; k++)
                    C.items[i][j] += (A.items[i][k] * B.items[k][j]);
        return C;   
    }
    
    public Matrix subMatrix(Point origin, Size size) {
        Matrix A = this;
                
        int nA = A.rowCount;
        int mA = A.colCount;
        
        if (mA < origin.x + size.width) throw new RuntimeException("Submatrix row outside parrent matrix.");
        if (nA < origin.y + size.height) throw new RuntimeException("Submatrix col outside parrent matrix.");
        
        int rows = size.height;
        int cols = size.width;
        Matrix B = new Matrix(rows, cols);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                B.items[i][j] = A.items[i + origin.y][j + origin.x];
            }
        }
        
        return B;        
    }
        
    public static Matrix matrixFromJSONString(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(jsonString);
        JSONObject jsonObj = (JSONObject) obj;
        
        Matrix result = Matrix.matrixFromJSON(jsonObj);
        return result;       
    }
    
    public static Matrix matrixFromJSON(JSONObject json) {
        
        int rows = ((Number)json.get(Matrix.kRows)).intValue();
        int cols = ((Number)json.get(Matrix.kCols)).intValue();
        
        Matrix m = new Matrix(rows, cols);
        
        JSONArray allItems = (JSONArray)json.get(Matrix.kItems);
        for (int i = 0; i < rows; i++) {
            JSONArray colsItems = (JSONArray)allItems.get(i);
            for (int j = 0; j < cols; j++) {
                m.items[i][j] = (double)colsItems.get(j);
            }
        }
        
        return m;
    }
    
    public static JSONObject jsonFromMatrix(Matrix A) {
        JSONObject json = new JSONObject();
        
        json.put(Matrix.kRows, A.rowCount);
        json.put(Matrix.kCols, A.colCount);
        
        JSONArray items = new JSONArray();
        
        for (int i = 0; i < A.rowCount; i++) {
            JSONArray colsItems = new JSONArray();
            for (int j = 0; j < A.colCount; j++) {
                colsItems.add(A.items[i][j]);
            }
            items.add(colsItems);
        }
        
        json.put(Matrix.kItems, items);
        
        return json;
    }
   
    void outToLog() {        
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                System.out.print( String.valueOf(items[i][j]) + "\t");
            }
            System.out.println("");
        }
    }    
}

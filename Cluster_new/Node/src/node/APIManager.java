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

enum JSONType {MATRIXB, MATRIXC, VECTOR, SUCCESS, UNKNOWN}
enum MatrixType {MB, MC }


public class APIManager {    
    private static final String kVector = "Vector";
    private static final String kColNumber = "ColNumber";
    private static final String kSuccess = "isSuccess";
    
    // common keys for any JSON
    private static final String kObject = "object";
    private static final String kComment = "comment";
    private static final String kJSONType = "type";
    
    
    public static String getJSONStringForMatrix(Matrix M, MatrixType matrixType, String comment) {
        JSONObject json = new JSONObject();
        JSONObject matrixJson = Matrix.jsonFromMatrix(M);
        
        String matrixKey = matrixType.name();        
        json.put(matrixKey, matrixJson);
        
        JSONType jt = JSONType.UNKNOWN;
        switch (matrixType) {          
            case MB: jt = JSONType.MATRIXB; break;
            case MC: jt = JSONType.MATRIXC; break;
        }
        
        return APIManager.getFinalJSON(json, comment,jt);
    }
    
    public static String getJSONStringForVector(Matrix V, int colIndex, String comment) {
        JSONObject json = new JSONObject();
        JSONObject matrixJson = Matrix.jsonFromMatrix(V);
        
        json.put(APIManager.kVector, matrixJson);
        json.put(APIManager.kColNumber, colIndex);
        
        return APIManager.getFinalJSON(json, comment, JSONType.VECTOR);
    }
    
    public static String getJSONStingSuccess(boolean isSuccess, String comment) {
        JSONObject json = new JSONObject();
                
        json.put(APIManager.kSuccess, isSuccess);
        
        return APIManager.getFinalJSON(json, comment, JSONType.SUCCESS);
    }
    
    public static String parseJSON(String jsonString, APIDelegate delegate) throws ParseException {
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(jsonString);
        JSONObject jsonObj = (JSONObject) obj;
        
        // just for test out comment to log
        String comment = (String) jsonObj.get(APIManager.kComment);
        System.out.println("JSON Received with comment: " + comment);
        
        // get main content
        JSONObject result = (JSONObject) jsonObj.get(APIManager.kObject);
        
        // get json type for parsing
        String jsonTypeStr = (String) jsonObj.get(APIManager.kJSONType);        
        JSONType jsonType = JSONType.valueOf(jsonTypeStr);
        
        String jsonResult = null;
        switch (jsonType) {
            case MATRIXB: {
                JSONObject matrixJSON = (JSONObject)result.get(MatrixType.MB.name());
                Matrix MB = Matrix.matrixFromJSON(matrixJSON);                
                jsonResult = delegate.receivedMatrixB(MB);
                break;
            }
                        
            case MATRIXC: {
                JSONObject matrixJSON = (JSONObject)result.get(MatrixType.MC.name());
                Matrix MC = Matrix.matrixFromJSON(matrixJSON);                
                jsonResult = delegate.receivedMatrixC(MC);
                break;
            }
                        
            case VECTOR: {
                JSONObject vectorJSON = (JSONObject)result.get(APIManager.kVector);
                Matrix vector = Matrix.matrixFromJSON(vectorJSON);
                int rowNumber = ((Number) result.get(APIManager.kColNumber)).intValue();
                jsonResult = delegate.receivedVector(vector, rowNumber);
                break;
            }
            
            case SUCCESS: {                
                boolean isSuccess = ((Boolean) result.get(APIManager.kSuccess)).booleanValue();
                jsonResult = delegate.receivedSuccess(isSuccess);
                break;
            }
            
            default: {
                System.out.println("UNKNOW JSON TYPE !!!!");
                break;
            }
        }
        
        return jsonResult;
    }
    
    
//    === private methods === 
    private static String getFinalJSON(JSONObject object, String comment, JSONType jt) {
        JSONObject json = new JSONObject();
        
        json.put(APIManager.kObject, object);
        json.put(APIManager.kComment, comment);
        json.put(APIManager.kJSONType, jt.name());
        
        return json.toJSONString();
    }        
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Stas
 */
public class SettingsManager {    
    String host;
    int port1, port2, port3, port4;  
        
    private static SettingsManager instance;
    
    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = SettingsManager.defaultManager();
        }
        return instance;
    }
    
    private static SettingsManager defaultManager() {
        SettingsManager manager = new SettingsManager("settings.json");
        return manager;
    }
    
    private SettingsManager(String filePath) {
        JSONParser parser = new JSONParser();
 
        try { 
            Object obj = parser.parse(new FileReader(filePath));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            this.host = (String) jsonObject.get("host");
            this.port1 = ((Number) jsonObject.get("port1")).intValue();
            this.port2 = ((Number) jsonObject.get("port2")).intValue();
            this.port3 = ((Number) jsonObject.get("port3")).intValue();
            this.port4 = ((Number) jsonObject.get("port4")).intValue(); 
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }     
    
    public int portForNode(int nodeIndex) {
        int port;
        switch (nodeIndex) {
            case 1: port = this.port1; break;
            case 2: port = this.port2; break;
            case 3: port = this.port3; break;
            case 4: port = this.port4; break;  
            default: port = 0;
        }
        return port;
    }
}

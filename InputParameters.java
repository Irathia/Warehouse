package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class InputParameters {
    private double speed; // meters per second
    private double timeOfPreparingEmptyContainer; // seconds
    private double timeOfPreparingForDelivery; //seconds
    private double timeOfRestacking; //seconds
    private double timeOfLabeling;// seconds
    
    public InputParameters() {
        speed = 0;
        timeOfPreparingEmptyContainer = 0;
        timeOfPreparingForDelivery = 0;
        timeOfLabeling = 0;
    }
    
    public double getTimeOfRestacking() {
        return timeOfRestacking;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getTimeOfContainerPreparing() {
        return timeOfPreparingEmptyContainer;
    }
    
    public double getTimeOfDeliveryPreparing() {
        return timeOfPreparingForDelivery;
    }
    
    public double getTimeOfLabeling() {
        return timeOfLabeling;
    }
    
    private double readDouble(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) { throw new IOException("Wrong format of Parameters file"); }
        String [] elements = line.split(";");
        if (elements.length < 2) { throw new IOException("Wrong format of Parameters file"); } 
        return Double.parseDouble(elements[1].replace(",", "."));
    }
    
    public void readParameters(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            speed = readDouble(br);
            speed /= 3.6;
            timeOfRestacking = readDouble(br);
            timeOfPreparingEmptyContainer = readDouble(br);
            timeOfPreparingForDelivery = readDouble(br);
            timeOfLabeling = readDouble(br);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
     }
}

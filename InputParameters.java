package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class InputParameters {
    private double speed; // meters per second
    private double timeOfPreparingEmptyContainer; // seconds
    private double timeOfPreparingForDelivery; //seconds
    private double timeOfRestacking; //seconds
    private double timeOfLabeling; // seconds
    private double truckCapacity; // liter
    private Time startOfWork;
    private Time startOfBreak;
    private Time finishOfBreak;
    
    public InputParameters() {
        speed = 0;
        timeOfPreparingEmptyContainer = 0;
        timeOfPreparingForDelivery = 0;
        timeOfLabeling = 0;
        truckCapacity = 0;
    }
    
    public double getTruckCapacity() {
        return truckCapacity;
    }
    
    public Time getStartOfWork() {
        return startOfWork;
    }
    
    public Time getStartOfBreak() {
        return startOfBreak;
    }
    
    public Time getFinishOfBreak() {
        return finishOfBreak;
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
            String line = br.readLine();
            if (line == null) { throw new IOException("Wrong format of Parameters file"); }
            String [] elements = line.split(";");
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            if (elements.length < 2) { throw new IOException("Wrong format of Parameters file"); } 
            startOfWork = new Time(formatter.parse(elements[1]).getTime());
            
            line = br.readLine();
            if (line == null) { throw new IOException("Wrong format of Parameters file"); }
            elements = line.split(";");
            if (elements.length < 2) { throw new IOException("Wrong format of Parameters file"); } 
            elements = elements[1].split("-");
            if (elements.length < 2) { throw new IOException("Wrong format of Parameters file"); } 
            startOfBreak = new Time(formatter.parse(elements[0]).getTime());
            finishOfBreak = new Time(formatter.parse(elements[1]).getTime());
            
            truckCapacity = readDouble(br);
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
        } catch (ParseException e) {
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

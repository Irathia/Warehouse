package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class InputParameters {
    private double speed; // meters per second
    private double timeOfPreparingEmptyContainer; // seconds
    private double timeOfPreparingForDelivery; //seconds
    private double timeOfBoxRestacking; //seconds
    private double timeOfUnitRestacking; //seconds
    private double timeOfLabeling; // seconds
    private double truckCapacity; // liter
    private Time startOfWork;
    private Time startOfBreak;
    private Time finishOfBreak;
    private long maximumOrderExecutionTime; // seconds
    
    public InputParameters() {
        speed = 0;
        timeOfPreparingEmptyContainer = 0;
        timeOfPreparingForDelivery = 0;
        timeOfLabeling = 0;
        truckCapacity = 0;
        maximumOrderExecutionTime = 0;
    }
    
    public long getMaximumOrderExecutionTime() {
        return maximumOrderExecutionTime;
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
    
    public double getTimeOfBoxRestacking() {
        return timeOfBoxRestacking;
    }
    
    public double getTimeOfUnitRestacking() {
        return timeOfUnitRestacking;
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
    
    private double readDouble(BufferedReader br, String filename) throws Exception {
        String line = br.readLine();
        if (line == null) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
        String [] elements = line.split(";");
        if (elements.length < 2) { throw new Exception(I18n.wrongFormatOfFile(filename)); } 
        double result = 0;
        try {
            result = Double.parseDouble(elements[1].replace(",", "."));
        }
        catch (Exception ex) {
            throw new Exception(I18n.wrongFormatOfFile(filename));
        }
        return result;
    }
    
    public void readParameters(String filename) throws Exception{
        BufferedReader br = null;
        int lineCounter = 0;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            lineCounter++;
            if (line == null) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
            String [] elements = line.split(";");
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            if (elements.length < 2) { throw new Exception(I18n.wrongFormatOfFile(filename)); } 
            try {
                startOfWork = new Time(formatter.parse(elements[1]).getTime());
            } catch (Exception ex) {
                throw new Exception(I18n.WRONG_TIME_FORMAT +  I18n.wrongCell(lineCounter, 2, filename));
            }
            
            line = br.readLine();
            lineCounter++;
            if (line == null) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
            elements = line.split(";");
            if (elements.length < 2) { throw new Exception(I18n.wrongFormatOfFile(filename)); } 
            elements = elements[1].split("-");
            if (elements.length < 2) { throw new Exception(I18n.wrongFormatOfFile(filename)); } 
            try {
                startOfBreak = new Time(formatter.parse(elements[0]).getTime());
                finishOfBreak = new Time(formatter.parse(elements[1]).getTime());
            } catch (Exception ex) {
                throw new Exception(I18n.WRONG_TIME_INTERVAL_FORMAT + I18n.wrongCell(lineCounter, 2, filename));
            }
            
            try {
                lineCounter++;
                truckCapacity = readDouble(br, filename);
                lineCounter++;
                speed = readDouble(br, filename);
                speed /= 3.6;
                lineCounter++;
                double tmp = readDouble(br, filename);
                maximumOrderExecutionTime = (long) (tmp * 1000);
                lineCounter++;
                timeOfUnitRestacking = readDouble(br, filename);
                lineCounter++;
                timeOfBoxRestacking = readDouble(br, filename);
                lineCounter++;
                timeOfPreparingEmptyContainer = readDouble(br, filename);
                lineCounter++;
                timeOfPreparingForDelivery = readDouble(br, filename);
                lineCounter++;
                timeOfLabeling = readDouble(br, filename);
            } catch (Exception ex) {
                throw new Exception(ex.getMessage() + "\n"+ I18n.wrongCell(lineCounter, 2, filename));
            }
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

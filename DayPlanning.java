package Warehouse;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class DayPlanning {
    private Vector <TruckTasks> truckTasks;
    private Orders orders;
    private ArrayList <Integer> remainingTasks;
    private String filename;
    
    public DayPlanning(String outputFilename) throws Exception {
        filename = new String (outputFilename);
        Warehouse warehouse = Warehouse.getInstance();
        try {
            warehouse.readFromFile("Warehouse.csv", "Parameters.csv");
            orders = new Orders("Goods.csv", "Orders.csv", "Items.csv");
        }
        catch (Exception ex) {
            Logger logger = Logger.getLogger("Test");
            logger.info(I18n.INPUT_ERROR_LOG);
            logger.info(ex.getMessage());
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1251"));
                writer.write(I18n.INPUT_ERROR_RESULT + ";\n");
            } catch (Exception io) {
            } finally {
               try {
                   if (writer != null) {
                       writer.close();
                   }
               } catch (Exception io) {}
            }
            throw new Exception("");
        }
        remainingTasks = new ArrayList<Integer>();
        truckTasks = new Vector<TruckTasks>();
    }
    
    private void calculateRemainingTasks() {
        int size = orders.getTasksSize(); 
        for (int i = 0; i < size; i++) {
            remainingTasks.add(i);
        }
    }
    
    private void clearAll() {
        if (truckTasks != null && truckTasks.size() != 0) {
            truckTasks.clear();
        }
        if (remainingTasks != null && remainingTasks.size() != 0) {
            remainingTasks.clear();
        }
    }
    
    /*return true if it's enough to use k trucks */
    private boolean divideTasksToKTrucks(int k) {
        if (k < 1) {
            return false;
        }
        clearAll();
        calculateRemainingTasks();
        ArrayList <Integer> availableTrucks = new ArrayList<Integer>();
        for (int j = 0; j < k; j++) {
            availableTrucks.add(j);
        }
        Time startTime = new Time (0);
        for (int i = 0; i < k; i++) {
            truckTasks.add(new TruckTasks());
            truckTasks.get(i).setTime(startTime);
            int firstTruckTask = orders.getTaskWithMaxExecTime(startTime, remainingTasks);
            if (firstTruckTask == -1) {
                return false;
            }
            truckTasks.get(i).addTask(remainingTasks.get(firstTruckTask), orders.getTasks().get(remainingTasks.get(firstTruckTask)).getExecutionTime(), orders.getTasks().get(remainingTasks.get(firstTruckTask)).getL());
            remainingTasks.remove(firstTruckTask);
        }
        for (int i = 0; i < remainingTasks.size() && availableTrucks.size() != 0; ) {
            int indexOfAvailableTruckWithMinTime = 0;
            for (int j = 1; j < availableTrucks.size(); j++) {
                if (truckTasks.get(availableTrucks.get(j)).getFinishTime().getTime() < truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).getFinishTime().getTime()) {
                    indexOfAvailableTruckWithMinTime = j;
                }
            }
            int index = orders.getTaskWithMaxExecTime(truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).getLastTask(), truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).getFinishTime(), remainingTasks);
            if (index == -1) {
                availableTrucks.remove(indexOfAvailableTruckWithMinTime);
            }
            else {
                Time execTime = new Time( orders.getTasks().get(remainingTasks.get(index)).getExecutionTime().getTime() + orders.getTimeForMovingBetweenTasks(truckTasks.get(i).getLastTask(), remainingTasks.get(index) ).getTime());
                truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).addTask(remainingTasks.get(index), execTime, orders.getTasks().get(remainingTasks.get(index)).getL() + orders.getDistanceForMovingBetweenTasks(truckTasks.get(i).getLastTask(), remainingTasks.get(index)));
                remainingTasks.remove(index);
            }
        }
        if (remainingTasks.size() == 0) {
            return true;
        }
        return false;
    }
    
    public void divideTasksToTrucks() {
        int numberOfTrucks = 1;
        while (! divideTasksToKTrucks(numberOfTrucks) && numberOfTrucks <= orders.getTasksSize()) 
        {
            numberOfTrucks++;
        }
        if (numberOfTrucks > orders.getTasksSize()) {
            truckTasks.clear();
        }
    }
    
    private void writeToLOG(){
        Logger logger = Logger.getLogger("Test");
        
        //tasks
        logger.info(I18n.TRUCKS_INFO);
        for(int i = 0; i < truckTasks.size(); i++){
            logger.info( (i+1) + " " + I18n.TRUCK + ": " + truckTasks.get(i).toString());
        }
    }
    
    public void writeIntoFile() {
        if (truckTasks.size() != 0) {
            writeToLOG();
            BufferedWriter writer = null;
            Warehouse warehouse = Warehouse.getInstance();
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1251"));
                writer.write(I18n.NECESSARY_NUMBER_OF_STOCKKEEPERS + ";" + truckTasks.size() + ";");
                for (int i = 0; i < (truckTasks.size() - 1); i++) {
                    writer.write(";");
                }
                writer.write("\n;");
                
                writer.write(I18n.NUMBER_OF_TASKS + ";");
                writer.write(I18n.TOTAL_DISTANCE + ";");
                writer.write(I18n.DISTANCE_TIME + ";");
                writer.write(I18n.ADDITIONAL_TIME + ";");
                writer.write(I18n.TOTAL_TIME + ";\n");
                
                for (int i = 0; i < truckTasks.size(); i++) {
                    writer.write((i+1) + " " + I18n.STOCKKEEPER + ";");
                    writer.write(truckTasks.get(i).getNumberOfTasks() + ";");
                    writer.write(Double.toString(truckTasks.get(i).getDistance()).replace(".", ",") + ";");
                    Long totalTrucksTime = (truckTasks.get(i).getFinishTime().getTime()) / 1000;
                    Long distanceTrucksTime = (long) Math.floor(truckTasks.get(i).getDistance() / warehouse.getSpeed());
                    if (distanceTrucksTime > totalTrucksTime) {distanceTrucksTime = totalTrucksTime;}
                    writer.write( distanceTrucksTime + ";");
                    writer.write( (totalTrucksTime - distanceTrucksTime ) + ";");
                    writer.write( totalTrucksTime + ";");
                    writer.write( "\n");
                }
            } catch (IOException ex) {
                Logger logger = Logger.getLogger("Test");
                logger.info(ex.getMessage());
            }  
            finally {
               try {
                   if (writer != null) {
                       writer.close();
                   }
               } catch (Exception ex) {}
            }
        }
        else {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1251"));
                writer.write(I18n.PLANNING_ERROR + ";");
                writer.write("\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
               try {
                   if (writer != null) {
                       writer.close();
                   }
               } catch (Exception ex) {}
            }
        }
    }
}
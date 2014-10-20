package Warehouse;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Vector;

public class DayPlanning {
    private Vector <TruckTasks> truckTasks;
    private Orders orders;
    private ArrayList <Integer> remainingTasks;
    
    public DayPlanning() {
        Warehouse warehouse = Warehouse.getInstance();
        warehouse.readFromFile("Warehouse.csv", "Parameters.csv");
        orders = new Orders("Goods.csv", "Orders.csv", "Items.csv");
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
            truckTasks.get(i).addTask(remainingTasks.get(0), orders.getTasks().get(remainingTasks.get(0)).getExecutionTime(), orders.getTasks().get(remainingTasks.get(0)).getL());
            remainingTasks.remove(0);
        }
        for (int i = 0; i < remainingTasks.size() && availableTrucks.size() != 0; ) {
            int indexOfAvailableTruckWithMinTime = 0;
            for (int j = 1; j < availableTrucks.size(); j++) {
                if (truckTasks.get(availableTrucks.get(j)).getFinishTime().getTime() < truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).getFinishTime().getTime()) {
                    indexOfAvailableTruckWithMinTime = j;
                }
            }
            int index = orders.getNearestTaskFromOrderWithMinNumber(truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).getLastTask(), truckTasks.get(availableTrucks.get(indexOfAvailableTruckWithMinTime)).getFinishTime(), remainingTasks);
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
        clearAll();
        calculateRemainingTasks();
        int size = orders.getTasksSize(); 
        Time deadlineOfLastTask = orders.getTasks().get(orders.getTasksSize() - 1).getDeadline();
        for (int i = 0; i < size && remainingTasks.size() != 0; i++) {
            if (remainingTasks.size() == 0) { break; }
            truckTasks.add(new TruckTasks());
            Time startTime = new Time (0);
            truckTasks.get(i).setTime(startTime);
            truckTasks.get(i).addTask(remainingTasks.get(0), orders.getTasks().get(remainingTasks.get(0)).getExecutionTime(), orders.getTasks().get(remainingTasks.get(0)).getL());
            remainingTasks.remove(0);
            while (remainingTasks.size() != 0 && truckTasks.get(i).getFinishTime().getTime() < deadlineOfLastTask.getTime()) {
                int index = orders.getNearestTask(truckTasks.get(i).getLastTask(), truckTasks.get(i).getFinishTime(), remainingTasks);
                if (index == -1) {
                    break;
                }
                Time execTime = new Time(orders.getTasks().get(remainingTasks.get(index)).getExecutionTime().getTime() + orders.getTimeForMovingBetweenTasks(truckTasks.get(i).getLastTask(), remainingTasks.get(index)).getTime());
                truckTasks.get(i).addTask(remainingTasks.get(index), execTime, orders.getTasks().get(remainingTasks.get(index)).getL() + orders.getDistanceForMovingBetweenTasks(truckTasks.get(i).getLastTask(), remainingTasks.get(index)));
                remainingTasks.remove(index);
            }
        }
        int numberOfTrucks = truckTasks.size();
     //   while (! divideTasksToKTrucks(numberOfTrucks)) 
     //   {
     //       numberOfTrucks++;
     //   }
    }
    
    public void writeIntoFile(String filename) {
        BufferedWriter writer = null;
        Time startTime = new Time (0);
        Warehouse warehouse = Warehouse.getInstance();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1251"));
            writer.write(I18n.NECESSARY_NUMBER_OF_STOCKKEEPERS + ";" + truckTasks.size() + ";");
            for (int i = 0; i < (truckTasks.size() - 1); i++) {
                writer.write(";");
            }
            writer.write("\n;");
            for (int i = 1; i <= truckTasks.size(); i++) {
                writer.write(i + " " + I18n.STOCKKEEPER + ";");
            }
            writer.write("\n" + I18n.NUMBER_OF_TASKS + ";");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write(truckTasks.get(i).getNumberOfTasks() + ";");
            }
            writer.write("\n" +I18n.TOTAL_DISTANCE + ";");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write(Double.toString(truckTasks.get(i).getDistance()).replace(".", ",") + ";");
            }
            
            Long[] totalTrucksTime = new Long[truckTasks.size()];
            for (int i = 0; i < truckTasks.size(); i++) {
                totalTrucksTime[i] = (truckTasks.get(i).getFinishTime().getTime() - startTime.getTime()) / 1000;
            }
            Long[] distanceTrucksTime = new Long[truckTasks.size()];
            for (int i = 0; i < truckTasks.size(); i++) {
                distanceTrucksTime[i] = (long) Math.floor(truckTasks.get(i).getDistance() / warehouse.getSpeed());
                if (distanceTrucksTime[i] > totalTrucksTime[i]) {distanceTrucksTime[i] = totalTrucksTime[i];}
            }
            
            writer.write("\n" +I18n.DISTANCE_TIME + ";");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write( distanceTrucksTime[i] + ";");
            }
            writer.write("\n" +I18n.ADDITIONAL_TIME + ";");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write( (totalTrucksTime[i] - distanceTrucksTime[i] ) + ";");
            }
            writer.write("\n" +I18n.TOTAL_TIME + ";");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write( totalTrucksTime[i] + ";");
            }
            writer.write("\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
           try {
               writer.close();
           } catch (Exception ex) {}
        }
    }
}
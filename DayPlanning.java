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
        warehouse.readFromFile("Warehouse.csv");
        orders = new Orders("Goods.csv", "Orders.csv", "Items.csv");
        int size = orders.getTasksSize(); 
        remainingTasks = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            remainingTasks.add(i);
        }
        truckTasks = new Vector<TruckTasks>();
    }
    
    void divideTasksToTrucks() {
        int size = orders.getTasksSize(); 
        Time deadlineOfLastTask = orders.getTask().get(orders.getTasksSize() - 1).getDeadline();
        for (int i = 0; i < size && remainingTasks.size() != 0; i++) {
            if (remainingTasks.size() == 0) { break; }
            truckTasks.add(new TruckTasks());
            Time startTime = new Time (orders.getOrder(0).getDeadline().getTime());
            startTime.setHours(startTime.getHours() - 1);
            truckTasks.get(i).setTime(startTime);
            truckTasks.get(i).addTask(remainingTasks.get(0), orders.getTask().get(remainingTasks.get(0)).getExecutionTime());
            remainingTasks.remove(remainingTasks.get(0));
            while (remainingTasks.size() != 0 && truckTasks.get(i).getFinishTime().getTime() < deadlineOfLastTask.getTime()) {
                int index = orders.getNearestTask(truckTasks.get(i).getLastTask(), truckTasks.get(i).getFinishTime(), remainingTasks);
                if (index == -1) {
                    break;
                }
                Time execTime = new Time(orders.getTask().get(remainingTasks.get(index)).getExecutionTime().getTime() + orders.getTimeForMovingBetweenTasks(truckTasks.get(i).getLastTask(), remainingTasks.get(index)).getTime());
                truckTasks.get(i).addTask(remainingTasks.get(index), execTime);
                remainingTasks.remove(index);
            }
        }
    }
    
    public void writeIntoFile(String filename) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
            writer.write("Number of trucks;" + truckTasks.size() + ";");
            for (int i = 0; i < (truckTasks.size() - 1); i++) {
                writer.write(";");
            }
            writer.write("\n;");
            for (int i = 1; i <= truckTasks.size(); i++) {
                writer.write(i + " truck;");
            }
            writer.write("\nNumber of tasks:;");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write(truckTasks.get(i).getNumberOfTasks() + ";");
            }
            writer.write("\nFinish time:;");
            for (int i = 0; i < truckTasks.size(); i++) {
                writer.write(truckTasks.get(i).getFinishTime() + ";");
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
package Warehouse;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Vector;

public class DayPlanning {
    Vector <TruckTasks> truckTasks;
    
    Orders orders;
    ArrayList <Integer> remainingTasks;
    
    public DayPlanning() {
        Warehouse warehouse = Warehouse.getInstance();
        warehouse.readFromFile("Warehouse.csv");
        orders = new Orders("Items.csv", "Shops.csv", "Goods.csv");
        int size = orders.getTasksSize(); 
        for (int i = 0; i < size; i++) {
            remainingTasks.add(i);
        }
    }
    
    void divideTasksToTrucks() {
        int size = orders.getTasksSize(); 
        Time deadlineOfLastTask = orders.getTask().get(orders.getTasksSize() - 1).getDeadline();
        for (int i = 0; i < size; i++) {
            if (remainingTasks.size() == 0) { break; }
            truckTasks.add(new TruckTasks());
            while (truckTasks.get(i).getTotalTime().getTime() < deadlineOfLastTask.getTime()) {
                int index = orders.getNearestTask(truckTasks.get(i).getLastTask(), truckTasks.get(i).getTotalTime(), remainingTasks);
                if (index == -1) {
                    break;
                }
                Time execTime = new Time(orders.getTask().get(index).getExecutionTime().getTime() + orders.getDistanceBetweenTasks(truckTasks.get(i).getLastTask(), index).getTime());
                truckTasks.get(i).addTask(index, execTime);
            }
        }
    }
}

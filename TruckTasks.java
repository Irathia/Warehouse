package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class TruckTasks {
    Vector <Integer> indexOfTasks;
    Time totalTime;
    
    public int getNumberOfTasks() {
        return indexOfTasks.size();
    }
    
    public int getTruckTask(int indexOfTruckTask) {
        if (indexOfTruckTask < 0 || indexOfTruckTask >= indexOfTasks.size()) {
            return -1;
        }
        return indexOfTasks.get(indexOfTruckTask);
    }
    
    public int getLastTask() {
        if (indexOfTasks.size() == 0) {
            return -1;
        }
        return indexOfTasks.get(indexOfTasks.size());
    }
    
    public void addTask (int taskIndex, Time taskTime) {
        indexOfTasks.add(taskIndex);
        totalTime.setTime(totalTime.getTime() + taskTime.getTime());
    }
    
    public Time getTotalTime () {
        return totalTime;
    }
}
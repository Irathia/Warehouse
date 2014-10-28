package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class TruckTasks {
    private Vector <Integer> indexOfTasks;
    private Time finishTime;
    private double distance;
    
    public TruckTasks() {
        indexOfTasks = new Vector<Integer>();
        finishTime = new Time (0);
        distance = 0;
    }
    
    public void setTime(Time startTime) {
        finishTime = new Time(startTime.getTime());
    }
    
    public int getNumberOfTasks() {
        return indexOfTasks.size();
    }
    
    public String toString() {
        StringBuffer strbuf = new StringBuffer("[");
        if (indexOfTasks.size() == 0) {
            strbuf.append("]");
            return strbuf.toString();
        }
        strbuf.append( (indexOfTasks.get(0) + 1) );
        for (int i = 1; i < indexOfTasks.size(); i++) {
            strbuf.append("; " + (indexOfTasks.get(i) + 1));
        }
        strbuf.append("]");
        return strbuf.toString();
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
        return indexOfTasks.get(indexOfTasks.size() - 1);
    }
    
    public void addTask(int taskIndex, Time taskTime, double taskDistance) {
        indexOfTasks.add(taskIndex);
        finishTime.setTime(finishTime.getTime() + taskTime.getTime());
        distance += taskDistance;
    }
    
    public Time getFinishTime() {
        return finishTime;
    }
    
    public double getDistance() {
        return distance;
    }
}
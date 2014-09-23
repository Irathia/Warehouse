package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Task {
	
	Vector <String> items; // is it sorted for pick up?
	int startPoint, finishPoint; // is it path?
	Time executionTime;
	
	Task()
	{
		
	};
	
	Task(Time executionTime)
	{
		this.executionTime = executionTime;
	};
	
	public final Time getExecutionTime()
	{
		return executionTime;
	};
	
	public final int getStartPoint()
	{
		return startPoint;
	};
	
	public final int getFinishPoint()
	{
		return finishPoint;
	};
	
	public final String getItem(int index)
	{
		return items.get(index);
	};
	
	public void addItem(String value)
	{
		items.add(value);
	};
	
	public void clearAll()
	{
		items.clear();
	};
}
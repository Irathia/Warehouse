package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Order {
	
	Vector <String> items;
	Time deadline;
	char deliverySide;
	Vector <Task> tasks;
	
	Order()
	{
		
	};
	
	Order(Time deadline, char deliverySide)
	{
		this.deadline = deadline;
		this.deliverySide = deliverySide;
	};
	
	public final Time getDeadline()
	{
		return deadline;
	};
	
	public final char getDeliverySide()
	{
		return deliverySide;
	};
	
	public void setDeadline(Time value)
	{
		//need check?
		deadline = value;
	};
	
	public void setDeliverySide(char value)
	{
		//need check?
		deliverySide = value;
	};
	
	public void addItem(String value)
	{
		items.add(value);
	};
	
	public final String getItem(int index)
	{
		return items.get(index);
	};
	
	public void clearAll()
	{
		items.clear();
	};
	
	public void divideOrderToTasks()
	{
		//how?
	};
	
	public final Time executionTimeOfAllTasks()
	{
		Time t = new Time(0);
		for (int i = 0; i < tasks.size(); i++)
			t.setTime(tasks.get(i).getExecutionTime().getTime()+t.getTime());
		return t;
	};

}

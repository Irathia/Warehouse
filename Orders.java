package Warehouse;

import java.util.Vector;

public class Orders {
	
	Vector <Order> orders;
	
	Orders(String filename)
	{
		readFromFile(filename);
	};
	
	private void readFromFile(String filename)
	{
		
	};
	
	public void divideOrdersToTasks()
	{
		for (int i = 0; i < orders.size(); i++)
			orders.get(i).divideOrderToTasks();
	};
	
	public final Order getOrder(int index)
	{
		return orders.get(index);
	}
}

package Warehouse;

import java.util.Vector;

public class Items {
	
	Vector <String> goods;
	
	Items(String filename)
	{
		readFromFile(filename);
	};
	
	public void readFromFile(String filename)
	{
		/*
		 * here we create Map <Shelf, string>  and after that create variable goods based on map, like index in vector is point for save
		 */
	};
	
	public final String getGoods(int index)
	{
		return goods.get(index);
	};
}

package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Items {
	
	Vector <Item> items;
	
	Items(String filename)
	{
		readFromFile(filename);
	};
	
	public void readFromFile(String filename)
	{
		/*
		 * here we create Map <Shelf, string>  and after that create variable goods based on map, like index in vector is point for save
		 */
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			while((line = br.readLine()) != null){
				String[] elements = line.split(";");
				//shelf,index,-,-,rigidity
				Item it = new Item(Integer.parseInt(elements[1]),Integer.parseInt(elements[4]));
				items.add(Warehouse.getInstance().getIndexOfShelf(elements[0]),it);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	public final Item getItems(int index)
	{
		return items.get(index);
	};

    public void addItem(Item item){
        items.add(item);
    }

    public void deleteItem(int index){
        for (int i = 0; i < items.size(); i++){
            if (items.get(i).getIndex() == index){
                items.remove(i);
                return;
            }
        }
    }

    public final int getSize(){
        return items.size();
    }
}

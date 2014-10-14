package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Items {
	
	private Vector <Item> items;
	
	Items(String filename)
	{
		items = new Vector<Item>();
		readFromFile(filename);
	};
	
	public void readFromFile(String filename)
	{
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
            line = br.readLine();
			while((line = br.readLine()) != null){
				String[] elements = line.split(";");
				if (elements.length < 5) { break; }
				//shelf,index,-,-,rigidity
				Item it = new Item(Long.parseLong(elements[1]),Integer.parseInt(elements[4]), 0);
				int index = Warehouse.getInstance().getIndexOfShelf(elements[0]);
				if ( index == -1) {
				    index = Warehouse.getInstance().getIndexOfShelf(elements[0]);
	            }
				while (index > items.size()) {
				    items.add(new Item());
				}
				if (index == items.size()) {
				    items.add(it);
	            }
				else {
				    items.set(index, it);
				}
				//items.add(it);
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
	
	public final int getShelfsIndex(long indexOfItem){
		
		for(int i = 0; i < items.size(); i++){
			if (items.get(i).getIndex() == indexOfItem){
				return i;
			}
		}
		return 0;
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

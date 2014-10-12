package Warehouse;

/**
 * Created by Irina on 28.09.2014.
 */
public class Item {
	private long index;
	private int rigidity;
    private int boxes;

    Item(){

    };

    Item(long index,int rigidity, int boxes){
        this.index = index;
        this.rigidity = rigidity;
        this.boxes = boxes;
    }

    public final long getIndex(){
        return index;
    }

    public final int getRigidity(){
        return rigidity;
    }

    public final int getBoxes() {
        return boxes;
    }

    public void setIndex(long index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }
}

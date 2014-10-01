package Warehouse;

/**
 * Created by Irina on 28.09.2014.
 */
public class Item {
	private long index;
	private int rigidity;

    Item(){

    };

    Item(long index,int rigidity){
        this.index = index;
        this.rigidity = rigidity;
    }

    public final long getIndex(){
        return index;
    }

    public final int getRigidity(){
        return rigidity;
    }

    public void setIndex(long index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }
}

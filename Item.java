package Warehouse;

/**
 * Created by Irina on 28.09.2014.
 */
public class Item {
	private long index;
	private int rigidity;
    private double volume;

    Item(){

    };

    Item(long index,int rigidity, double volume){
        this.index = index;
        this.rigidity = rigidity;
        this.volume = volume;
    }

    public final long getIndex(){
        return index;
    }

    public final int getRigidity(){
        return rigidity;
    }

    public final double getVolume() {
        return volume;
    }

    public void setIndex(long index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }

    public void setVolume (double volume){
        this.volume = volume;
    }
}

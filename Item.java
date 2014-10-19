package Warehouse;

/**
 * Created by Irina on 28.09.2014.
 */
public class Item {
	private long index;
	private int rigidity;
    private int boxes;
    private double liters;//1 box = liters

    Item(){

    };

    Item(long index,int rigidity, int boxes, int liters){
        this.index = index;
        this.rigidity = rigidity;
        this.boxes = boxes;
        this.liters = (double)liters/boxes;
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

    public final double getLiters() {
        return liters;
    }

    public void setIndex(long index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }

    public void setBoxes (int boxes){
        this.boxes = boxes;
    }
}

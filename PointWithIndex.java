package Warehouse;

public class PointWithIndex extends Point {
    private int index;
    
    public PointWithIndex(int index) {
        super();
        this.index = index;
    }
    
    public PointWithIndex(double x, double y, int index) {
        super(x,y);
        this.index = index;
    }
    
    public PointWithIndex(String x, String y) throws Exception {
        super(x,y);
        this.index = -1;
    }
    
    public PointWithIndex(Point p, int index) {
        super(p.getX(), p.getY());
        this.index = index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
}
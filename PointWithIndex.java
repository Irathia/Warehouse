
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
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
}
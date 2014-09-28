
public class PointWithName extends Point {
	private String name;
	
	public PointWithName(String name) {
		super();
		this.name = name;
	}
	
	public PointWithName(double x, double y, String name) {
		super(x,y);
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}

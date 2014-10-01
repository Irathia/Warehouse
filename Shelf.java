package Warehouse;

public class Shelf extends Rectangle {
    private String name;
    
    public Shelf(String name, double topLeftX, double topLeftY, double width, double height) {
        super(topLeftX, topLeftY, width, height);
        this.name = name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isLocatedHigherThan(Shelf sh) {
        return this.getBottomRightY() >= sh.getTopLeftY();
    }
    
    public boolean isLocatedLowerThan(Shelf sh) {
        return this.getTopLeftY() <= sh.getBottomRightY(); 
    }
    
    public boolean isShelvesFromOneRow(Shelf sh) {
        return this.getBottomRightX() == sh.getBottomRightX() && this.getTopLeftX() == sh.getTopLeftX();
    }
}

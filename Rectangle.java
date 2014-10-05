package Warehouse;

public class Rectangle {
    private Point topLeft;
    private Point bottomRight;
    
    public Rectangle(double topLeftX, double topLeftY, double width, double height) {
        topLeft = new Point(topLeftX, topLeftY);
        bottomRight = new Point (topLeftX + width, topLeftY - height);
    }
    
    public void setTopLeftX(double x) {
        this.topLeft.setX(x);
    }
    
    public void setTopLeftY(double y) {
        this.topLeft.setY(y);
    }
    
    public void setTopLeft(Point p) {
        this.topLeft.setX(p.getX());
        this.topLeft.setY(p.getY());
    }
    
    public void setBottomRightX(double x) {
        this.bottomRight.setX(x);
    }
    
    public void setBottomRightY(double y) {
        this.bottomRight.setY(y);
    }
    
    public void setBottomRight(Point p) {
        this.bottomRight.setX(p.getX());
        this.bottomRight.setY(p.getY());
    }
    
    public double getTopLeftX() {
        return this.topLeft.getX();
    }
    
    public double getTopLeftY() {
        return this.topLeft.getY();
    }
    
    public double getBottomRightX() {
        return this.bottomRight.getX();
    }
    
    public double getBottomRightY() {
        return this.bottomRight.getY();
    }
    
    public double getHeight() {
        return this.topLeft.getY() - this.bottomRight.getY();
    }
    
    public double getWidth() {
        return this.bottomRight.getX() - this.topLeft.getX();
    }
    
    public Point getTopLeft() {
        return this.topLeft;
    }
    
    public Point getBottomRight() {
        return this.bottomRight;
    }
}
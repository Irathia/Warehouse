package Warehouse;

import java.util.Collections;
import java.util.List;
import java.util.Vector;


public class PathwayStorage {
    private Vector<Pathway> pathways;
    private int numberOfShelves;
    private Direction directionOfFirstPathway;
    
    public PathwayStorage() {
        numberOfShelves = 0;
        pathways = new Vector<Pathway>();
    }
    
    public void setDirection(Direction dir) {
        directionOfFirstPathway = dir;
    }
    
    public int getNumberOfShelves() {
        return numberOfShelves;
    }
    
    public int getNumberOfShelvesInPathway(int pathwayIndex) {
        if (pathwayIndex < 0 || pathwayIndex > pathways.size()) {
            return -1;
        }
        return pathways.get(pathwayIndex).totalSize();
    }
    
    public int getNumberOfShelvesInLeftRowOfPathway(int pathwayIndex) {
        if (pathwayIndex < 0 || pathwayIndex > pathways.size()) {
            return -1;
        }
        return pathways.get(pathwayIndex).sizeOfLeftRow();
    }
    
    public int getNumberOfShelvesInRightRowOfPathway(int pathwayIndex) {
        if (pathwayIndex < 0 || pathwayIndex > pathways.size()) {
            return -1;
        }
        return pathways.get(pathwayIndex).sizeOfRightRow();
    }
    
    public int getFirstShelfIndexInPathway(int pathwayIndex) {
        if (pathwayIndex < 0 || pathwayIndex > pathways.size()) {
            return -1;
        }
        int result = 0;
        for (int i = 0; i < pathwayIndex; i++) {
            result += pathways.get(pathwayIndex).totalSize();
        }
        return result;
    }
    
    public int getLastShelfIndexInPathway(int pathwayIndex) {
        if (pathwayIndex < 0 || pathwayIndex > pathways.size()) {
            return -1;
        }
        int result = 0;
        for (int i = 0; i <= pathwayIndex; i++) {
            result += pathways.get(pathwayIndex).totalSize();
        }
        return result - 1;
    }
    
    public int getPathwayIndexByShelfIndex(int shelfIndex) {
        if (shelfIndex < 0 || shelfIndex >= numberOfShelves) {
            return -1;
        }
        int tmp = shelfIndex;
        for (int i = 0; i < pathways.size(); i++) {
            tmp -= pathways.get(i).totalSize();
            if (tmp < 0) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPathwayIndexByPoint(Point p) {
        for (int i = 0; i < pathways.size(); i++) {
            if (pathways.get(i).isLocatedInsideHorizontalBar(p)) { return i; }
        }
        return -1;
    }
    
    public int getNumberOfPathways() {
        return pathways.size();
    }
    
    public Direction getDirectionOfPathway(int pathwayIndex) {
        Direction currentDirection = directionOfFirstPathway;
        if ( pathwayIndex % 2 == 1) {
            if (directionOfFirstPathway == Direction.Down) {
                currentDirection = Direction.Up;
            }
            else {
                currentDirection = Direction.Down;    
            }
        }
        return currentDirection;
    }
    
    public boolean add(int rowIndex, Shelf newSh) {
        if (rowIndex < 0) { return false;}
        int remainder = rowIndex % 2;
        int pathwayIndex = (rowIndex - remainder) / 2;
        while (pathwayIndex >= pathways.size()) {
            pathways.add(new Pathway());
        } 
        if (remainder == 0) {
            pathways.get(pathwayIndex).addToLeftRow(newSh);
        }
        else {
            pathways.get(pathwayIndex).addToRightRow(newSh);
        }
        return true;
    }
    
    public void recountNumberOfShelves() {
        numberOfShelves = 0;
        for (int i = 0; i < pathways.size(); i++) {
            numberOfShelves += pathways.get(i).totalSize();
        }
    }
    
    public void clear() {
        pathways.clear();
    }
    
    public int getShelfIndex(String name) {
        String newName = name.substring(0, name.lastIndexOf("-"));
        int index = 0;
        int result;
        for (int i = 0; i < pathways.size(); i++) {
            result = pathways.get(i).findName(newName);
            if (result != -1) {
                return  index + result;
            }
            index += pathways.get(i).totalSize();
        }
        return -1;
    }
    
    public Point getPickupPointOfShelf(int shelfIndex) {
        if (shelfIndex < 0 || shelfIndex >= numberOfShelves) {
            return new Point(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
        }
        for (int i = 0; i < pathways.size(); i++) {
            if (shelfIndex < pathways.get(i).sizeOfLeftRow()) {
                return pathways.get(i).getPickupPointOfLeftRow(shelfIndex);
            }
            shelfIndex -= pathways.get(i).sizeOfLeftRow();
            if (shelfIndex < pathways.get(i).sizeOfRightRow()) {
                return pathways.get(i).getPickupPointOfRightRow(shelfIndex);
            }
            shelfIndex -= pathways.get(i).sizeOfRightRow();
        }
        return new Point(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
    }
    
    public int getIndexOfNearestShelfBasedOnDirection(int pathwayIndex, Point p) {
        if (pathwayIndex < 0 || pathwayIndex >= pathways.size()) {
            return -1;
        }
        Direction dir = getDirectionOfPathway(pathwayIndex);
        int resultIndex = getFirstShelfIndexInPathway(pathwayIndex);
        int sizeOfLeftRow = pathways.get(pathwayIndex).sizeOfLeftRow();
        int sizeOfRightRow = pathways.get(pathwayIndex).sizeOfRightRow();
        if (dir == Direction.Down) {
            Point highestLeft = pathways.get(pathwayIndex).getPickupPointOfLeftRow(0);
            Point highestRight = pathways.get(pathwayIndex).getPickupPointOfRightRow(0);
            if (highestLeft.getY() != highestRight.getY()) {
                return (highestLeft.getY() > highestRight.getY()) ? resultIndex: resultIndex + sizeOfLeftRow;
            }
            return (Math.abs(p.getX() - highestLeft.getX()) < Math.abs(p.getX() - highestRight.getX())) ? resultIndex: resultIndex + sizeOfLeftRow;
        }
        else {
            Point lowestLeft = pathways.get(pathwayIndex).getPickupPointOfLeftRow(sizeOfLeftRow - 1);
            Point lowestRight = pathways.get(pathwayIndex).getPickupPointOfRightRow(sizeOfRightRow - 1);
            resultIndex += sizeOfLeftRow - 1;
            if (lowestLeft.getY() != lowestLeft.getY()) {
                return (lowestLeft.getY() < lowestRight.getY()) ? resultIndex: resultIndex + sizeOfRightRow;
            }
            return (Math.abs(p.getX() - lowestLeft.getX()) < Math.abs(p.getX() - lowestRight.getX())) ? resultIndex: resultIndex + sizeOfRightRow;
        }
    }
    
    private double getHighestRoadlock(int fromPathwayIndex, char fromSide, int toPathwayIndex, char toSide) {
        if (fromPathwayIndex < 0 || toPathwayIndex < 0 || fromPathwayIndex == toPathwayIndex)
        {
            return Double.NEGATIVE_INFINITY;
        }
        double currentMax = pathways.get(fromPathwayIndex).getHighestRoadlock(fromSide);
        double tmp;
        if ( (tmp = pathways.get(toPathwayIndex).getHighestRoadlock(toSide)) > currentMax)
        {
            currentMax = tmp;
        }
        int i;
        if(fromPathwayIndex < toPathwayIndex)
        {
            for (i = fromPathwayIndex + 1; i < toPathwayIndex; i++)
            {
                tmp = pathways.get(i).getHighestRoadlock('b');
                if (tmp > currentMax) { currentMax = tmp; }
            }
        }
        else
        {
            for (i = toPathwayIndex + 1; i < fromPathwayIndex; i++)
            {
                tmp = pathways.get(i).getHighestRoadlock('b');
                if (tmp > currentMax) { currentMax = tmp; }
            }
        }
        return currentMax;
    }
    
    public double getHighestRoadlock(int fromPathwayIndex, int toPathwayIndex) {
        if(fromPathwayIndex < toPathwayIndex)
        {
            return getHighestRoadlock(fromPathwayIndex,'r',toPathwayIndex,'l');
        }
        return getHighestRoadlock(fromPathwayIndex,'l',toPathwayIndex,'r');
    }
    
    private double getLowestRoadlock(int fromPathwayIndex, char fromSide, int toPathwayIndex, char toSide) {
        if (fromPathwayIndex < 0 || toPathwayIndex < 0 || fromPathwayIndex == toPathwayIndex)
        {
            return Double.POSITIVE_INFINITY;
        }
        double currentMin = pathways.get(fromPathwayIndex).getLowestRoadlock(fromSide);
        double tmp;
        if ( (tmp = pathways.get(toPathwayIndex).getLowestRoadlock(toSide)) < currentMin)
        {
            currentMin = tmp;
        }
        int i;
        if(fromPathwayIndex < toPathwayIndex)
        {
            for (i = fromPathwayIndex + 1; i < toPathwayIndex; i++)
            {
                tmp = pathways.get(i).getHighestRoadlock('b');
                if (tmp > currentMin) { currentMin = tmp; }
            }
        }
        else
        {
            for (i = toPathwayIndex + 1; i < fromPathwayIndex; i++)
            {
                tmp = pathways.get(i).getHighestRoadlock('b');
                if (tmp > currentMin) { currentMin = tmp; }
            }
        }
        return currentMin;
    }
    
    public double getLowestRoadlock(int fromPathwayIndex, int toPathwayIndex) {
        if(fromPathwayIndex < toPathwayIndex)
        {
            return getLowestRoadlock(fromPathwayIndex,'r',toPathwayIndex,'l');
        }
        return getLowestRoadlock(fromPathwayIndex,'l',toPathwayIndex,'r');
    }
    
    public boolean sortIndexesForHeuristic (List<OrderItem> orderItems) {
        if (orderItems.size() == 0) {return true;}
        Collections.sort(orderItems);
        int currentPathwayIndex, firstElementIndex, newPathwayIndex; 
        currentPathwayIndex = getPathwayIndexByShelfIndex(orderItems.get(0).getIndex());
        firstElementIndex = 0;
        int indexOfFirstShelfInPathway;
        for (int i = 1; i < orderItems.size(); i++) {
            newPathwayIndex = getPathwayIndexByShelfIndex(orderItems.get(i).getIndex());
            if (newPathwayIndex != currentPathwayIndex) {
                indexOfFirstShelfInPathway = getFirstShelfIndexInPathway(currentPathwayIndex);
                for (int j = firstElementIndex; j < i ; j++) {
                    orderItems.get(j).setIndex(orderItems.get(j).getIndex() - indexOfFirstShelfInPathway);
                }
                Collections.sort(orderItems.subList(firstElementIndex, i), pathways.get(currentPathwayIndex));
                if (getDirectionOfPathway(currentPathwayIndex) == Direction.Up) {
                    Collections.reverse(orderItems.subList(firstElementIndex, i));
                }
                for (int j = firstElementIndex; j < i ; j++) {
                    orderItems.get(j).setIndex(orderItems.get(j).getIndex() + indexOfFirstShelfInPathway);
                }
                currentPathwayIndex = newPathwayIndex;
                firstElementIndex = i;
            } 
        }
        indexOfFirstShelfInPathway = getFirstShelfIndexInPathway(currentPathwayIndex);
        for (int j = firstElementIndex; j < orderItems.size() ; j++) {
            orderItems.get(j).setIndex(orderItems.get(j).getIndex() - indexOfFirstShelfInPathway);
        }
        Collections.sort(orderItems.subList(firstElementIndex, orderItems.size()), pathways.get(currentPathwayIndex));
        if (getDirectionOfPathway(currentPathwayIndex) == Direction.Up) {
            Collections.reverse(orderItems.subList(firstElementIndex, orderItems.size()));
        }
        for (int j = firstElementIndex; j < orderItems.size() ; j++) {
            orderItems.get(j).setIndex(orderItems.get(j).getIndex() + indexOfFirstShelfInPathway);
        }
        
        return true;
    }
}
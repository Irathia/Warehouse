package Warehouse;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        DayPlanning dayPlanning = new DayPlanning();
        dayPlanning.divideTasksToTrucks();
        dayPlanning.writeIntoFile("Result.csv");
    }
}

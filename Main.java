package Warehouse;

import java.io.IOException;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
    	//System.out.println(System.getProperty("user.dir"));
    	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    	Logger logger = Logger.getLogger("Test");
    	FileHandler fh;
    	try {
    		fh = new FileHandler("test.log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        DayPlanning dayPlanning = new DayPlanning();
        dayPlanning.divideTasksToTrucks();
        dayPlanning.writeIntoFile("Result.csv");
    }
}

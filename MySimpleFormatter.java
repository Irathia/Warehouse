package Warehouse;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class MySimpleFormatter extends SimpleFormatter {
		
		public MySimpleFormatter() {
			// TODO Auto-generated constructor stub
			super();
		}
		public String format(LogRecord record){
			 // if(record.getLevel() == Level.INFO){
			    return record.getMessage() + "\r\n";
			  //}else{
			 //   return super.format(record);
			  //}
		}
}

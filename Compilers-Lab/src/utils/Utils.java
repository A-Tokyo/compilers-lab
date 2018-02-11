package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
	
	public static String appendNewLine(String str){
		return str + System.lineSeparator();
	}
	
	public static void writeOutputFile(String fileText, String outFilePath)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFilePath));
		writer.write(fileText);
		writer.close();
	}
	
	public static String trimLastChar(String str){
		return str.substring(0, str.length()-1);
	}
}

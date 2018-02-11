package lab2;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import nfa.NFA;
import utils.Utils;

public class Lab2 {
	private static String DEFAULT_IN_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab2/test1.in";
	private static String DEFAULT_OUT_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab2/test_out1.out";
	
	public static ArrayList<String> parseFileToNFAs(String inFilePath) throws FileNotFoundException, IOException{
		BufferedReader br;
		String currentNFA = "";
		ArrayList<String> dfasToReturn = new ArrayList<String>();		
		int currLineIndex = 0;
		try {
			br = new BufferedReader(new FileReader(inFilePath));
			try {
				String currLine;
				while ( (currLine = br.readLine()) != null ) {
					currLineIndex ++;
					if(currLineIndex%7 == 0 && currLine.isEmpty() ){
						dfasToReturn.add(currentNFA.trim());
						currentNFA = "";
					} else {
						currentNFA += (currLine + System.lineSeparator());
					}
				}
				return dfasToReturn;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				br.close();
			}
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public static void runTask() throws FileNotFoundException, IOException{
		ArrayList<String> rawInputNFAs = parseFileToNFAs(DEFAULT_IN_FILEPATH);
		String resultFileText = NFA.rawInputNFAsToOutputString(rawInputNFAs);
		Utils.writeOutputFile(resultFileText, DEFAULT_OUT_FILEPATH);
	}
}
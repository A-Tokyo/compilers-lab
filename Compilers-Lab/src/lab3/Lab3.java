package lab3;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dfa.DFA;
import nfa.NFA;
//import nfa.NFA;
import utils.Utils;

public class Lab3 {
	private static String DEFAULT_IN_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab3/NFAin.in";
	private static String DEFAULT_OUT_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab3/myNFAout.out";
	
	public static ArrayList<String> parseFileToDFAs(String inFilePath) throws FileNotFoundException, IOException{
		BufferedReader br;
		String currentDFA = "";
		ArrayList<String> dfasToReturn = new ArrayList<String>();		
		int currLineIndex = 0;
		try {
			br = new BufferedReader(new FileReader(inFilePath));
			try {
				String currLine;
				while ( (currLine = br.readLine()) != null ) {
					currLineIndex ++;
					if(currLineIndex%8 == 0 && currLine.isEmpty() ){
						dfasToReturn.add(currentDFA.trim());
						currentDFA = "";
					} else {
						currentDFA += (currLine + System.lineSeparator());
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
//		ArrayList<String> rawInputFBDFAs = parseFileToDFAs(DEFAULT_IN_FILEPATH);
//		String resultFileTextFBDFAs = DFA.rawInputFBDFAsToOutputString(rawInputFBDFAs);
//		Utils.writeOutputFile(resultFileTextFBDFAs, DEFAULT_OUT_FILEPATH);
		

		ArrayList<String> rawInputFBNFAs = parseFileToDFAs(DEFAULT_IN_FILEPATH);
		String resultFileTextFBNFAs = DFA.rawInputFBDFAsToOutputString(rawInputFBNFAs);
		Utils.writeOutputFile(resultFileTextFBNFAs, DEFAULT_OUT_FILEPATH);
	}
}

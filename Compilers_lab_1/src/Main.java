import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	private static String DEFAULT_IN_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers_lab_1/src/assets/in.in";
	private static String DEFAULT_OUT_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers_lab_1/src/assets/out.out";
	
	private static String NORMAL_SEPERATOR_STRING = ",";
	private static String SECONDARY_SEPERATOR_STRING = "#";

	private static String DFA_CONSTRUCTED = "DFA constructed";
	private static String IGNORED = "Ignored";

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
					if(currLineIndex%7 == 0 && currLine.isEmpty() ){
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

	public static DFA constructDFA (String dfaStr){		
		String[] dfaState = (dfaStr.split(System.lineSeparator()));
		String [] transitions = dfaState[4].split(SECONDARY_SEPERATOR_STRING);
		String [] inputs = dfaState[5].split(SECONDARY_SEPERATOR_STRING);
		return new DFA(dfaState[0].split(NORMAL_SEPERATOR_STRING), dfaState[1].split(NORMAL_SEPERATOR_STRING), dfaState[2].split(NORMAL_SEPERATOR_STRING), dfaState[3], transitions, inputs);
	}

	public static int extractInputsLengthFromRawDFAStr(String dfaStr){
		String[] dfaState = (dfaStr.split(System.lineSeparator()));
		String [] inputs = dfaState[5].split(SECONDARY_SEPERATOR_STRING);
		return inputs.length;
	}

	private static String appendNewLine(String str){
		return str + System.lineSeparator();
	}

	private static String rawInputDFAsToOutputString(ArrayList<String> rawInputDFAs){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rawInputDFAs.size(); i++) {
			String currRawInputDFA = rawInputDFAs.get(i);
			try {
				DFA currDFA = constructDFA(currRawInputDFA);
				sb.append(appendNewLine(DFA_CONSTRUCTED));
				String [] currDFAResults = currDFA.runOnInputs();
				for (int j = 0; j < currDFAResults.length; j++) {
					sb.append(appendNewLine(currDFAResults[j]));
				}
				sb.append(appendNewLine(""));
			} catch (Error e) {
				sb.append(appendNewLine(e.getMessage()));
				for (int j = 0; j <extractInputsLengthFromRawDFAStr(currRawInputDFA); j++) {
					sb.append(appendNewLine(IGNORED));
				}
				sb.append(appendNewLine(""));
			}
		}
		return sb.toString();
	}
	
	public static void writeOutputFile(String fileText, String outFilePath)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFilePath));
		writer.write(fileText);
		writer.close();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ArrayList<String> rawInputDFAs = parseFileToDFAs(DEFAULT_IN_FILEPATH);
		String resultFileText = rawInputDFAsToOutputString(rawInputDFAs);
		writeOutputFile(resultFileText, DEFAULT_OUT_FILEPATH);
	}
}

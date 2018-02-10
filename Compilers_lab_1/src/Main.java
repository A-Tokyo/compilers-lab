import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	private static String inFilePath = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers_lab_1/src/in1.in";
	
	public static ArrayList<String> parseFileToDFAs() throws FileNotFoundException, IOException{
		BufferedReader br;
		String currentDFA = "";
		ArrayList<String> dfas = new ArrayList<String>();		
		int currLineIndex = 0;

		try {
			br = new BufferedReader(new FileReader(inFilePath));
			try {
				String currLine;
				while ( (currLine = br.readLine()) != null ) {
					currLineIndex ++;
					if(currLineIndex%7 == 0 && currLine.isEmpty() ){
						dfas.add(currentDFA.trim());
						currentDFA = "";
					} else {
						currentDFA += (currLine + System.lineSeparator());
					}
				}
				return dfas;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				br.close();
			}
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	public static DFA constructDFA (String dfaStr){		
		String[] dfaState = (dfaStr.split(System.lineSeparator()));
		String [] transitions = dfaState[4].split("#");
		String [] inputs = dfaState[5].split("#");
		return new DFA(dfaState[0].split(","), dfaState[1].split(","), dfaState[2].split(","), dfaState[3], transitions, inputs);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ArrayList<String> rawInputDFAs = parseFileToDFAs();
		for (int i = 0; i < rawInputDFAs.size(); i++) {
			try {
				DFA currDFA = constructDFA(rawInputDFAs.get(i));
				currDFA.runOnInputs();
			} catch (Error e) {
				System.out.println(e.getMessage());
			}
		}
	}
}

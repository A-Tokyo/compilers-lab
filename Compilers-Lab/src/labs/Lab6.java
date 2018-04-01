package labs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import grammar.Grammar;
import grammar.GrammarReader;
import grammar.LeftRecursionElimination;

public class Lab6 {
	private static String DEFAULT_IN_FILEPATH_PREFIX = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab6/";
	
	public static void runTask() throws IOException {
		LeftRecursionElimination leftRecursuionEliminator = new LeftRecursionElimination();
		BufferedWriter br = null;
		for (int i = 1; i < 8; i++) {
			String inFilePath = DEFAULT_IN_FILEPATH_PREFIX + "test"+i+".in";
			String outFilePath = DEFAULT_IN_FILEPATH_PREFIX + "test"+i+".out";

			Grammar inGrammar = new GrammarReader().read(inFilePath);
			Grammar outGrammar = leftRecursuionEliminator.leftRecursionEliminator(inGrammar);
			
			br = new BufferedWriter(new FileWriter(outFilePath));
			br.write(outGrammar.toString());
			br.close();
			
			System.out.println("Output Grammar with left recursion eliminated:\n" + outGrammar);			
		}
	}
}

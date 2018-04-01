package lab6;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import leftRecursionElimination.Grammar;
import leftRecursionElimination.GrammarReader;
import leftRecursionElimination.LeftRecursionElimination;

public class Lab6 {
	private static String DEFAULT_IN_FILEPATH_PREFIX = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab6/";
	
	public static void runTask() throws IOException {
		BufferedWriter br = null;
		for (int i = 1; i < 8; i++) {
			String inFilePath = DEFAULT_IN_FILEPATH_PREFIX + "test"+i+".in";
			String outFilePath = DEFAULT_IN_FILEPATH_PREFIX + "test"+i+".out";
			br = new BufferedWriter(new FileWriter(outFilePath));
			Grammar inGrammar = new GrammarReader().reader(inFilePath);
			LeftRecursionElimination leftRecursuionEliminator = new LeftRecursionElimination();
			Grammar outputGrammar = leftRecursuionEliminator.leftRecursionEliminator(inGrammar);
			String outFileText = outputGrammar.toString();
			br.write(outFileText);
			System.out.println("Output Grammar with left recursion eliminated:");
			System.out.println(outFileText);
		}
	}
}

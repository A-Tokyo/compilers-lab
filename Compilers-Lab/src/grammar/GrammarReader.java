package grammar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GrammarReader {
	private static final String GRAMMAR_RULE_SPLITTER = "\\|";

	public Grammar read(String filename) throws IOException {
		Grammar grammar = new Grammar();
		GrammarRule rule = null;
		BufferedReader br = null;
		GrammarRule currentRule = null;
		boolean isTerminal = true;
		String substring = "";
		String lineString;
		
		ArrayList<String> fileList = new ArrayList<String>();
		br = new BufferedReader(new FileReader(filename));
		while ((lineString = br.readLine()) != null) {
			fileList.add(lineString);
		}
		br.close();
		
		int currLineIndex = 0;
		while (currLineIndex < fileList.size()) {
			lineString = fileList.get(currLineIndex);
			if ((currLineIndex & 1) == 0) {
				rule = new GrammarRule(lineString);
				grammar.getNonTerminals().add(lineString);
				grammar.getRules().add(rule);
			}
			currLineIndex++;
		}

		currLineIndex = 0;
		while (currLineIndex < fileList.size()) {
			lineString = fileList.get(currLineIndex);
			if ((currLineIndex & 1) == 0) {
				for (int i = 0; i < grammar.getRules().size(); i++) {
					if (grammar.getRules().get(i).getHead().equals(lineString)) {
						currentRule = grammar.getRules().get(i);
						break;
					}
				}
			} else {
				String[] splitLineString = lineString.split(GRAMMAR_RULE_SPLITTER);

				for (int i = 0; i < splitLineString.length; i++) {
					currentRule.getBody().add(splitLineString[i]);
					for (int j = 0; j < splitLineString[i].length(); j++) {
						for (int k = 0; k < grammar.getNonTerminals().size(); k++) {
							String currentNonTerminal = grammar.getNonTerminals().get(k);
							if (currentNonTerminal.length() == 1) {
								if (currentNonTerminal.equals(splitLineString[i].charAt(j) + "")) {
									isTerminal = false;
									break;
								}
							} else {
								if (currentNonTerminal.length() <= splitLineString[i].length()) {
									int endIndex = j + currentNonTerminal.length();
									if (endIndex > splitLineString[i].length()) {
										endIndex = splitLineString[i].length();
									}
									String comparedString = splitLineString[i].substring(j, endIndex);
									if (currentNonTerminal.equals(comparedString)) {
										j = endIndex - 1;
										isTerminal = false;
										break;
									}
								}
							}
						}

						if (isTerminal) {
							substring += splitLineString[i].charAt(j);

							if (j == splitLineString[i].length() - 1 && !grammar.getTerminals().contains(substring)) {
								grammar.getTerminals().add(substring);
								isTerminal = true;
								substring = "";
							}
						} else {
							if (!substring.equals("") && !grammar.getTerminals().contains(substring)) {
								grammar.getTerminals().add(substring);
							}
							isTerminal = true;
							substring = "";
						}
					}
				}
			}
			currLineIndex++;
		}

		return grammar;
	}
}

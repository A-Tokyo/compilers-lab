package grammar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GrammarReader {
	private static final String GRAMMAR_RULE_SPLITTER = "\\|";

	public ArrayList<String> parseFile(String filepath) throws IOException{
		ArrayList<String> fileList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String brCurrLine;
		while ((brCurrLine = br.readLine()) != null) {
			fileList.add(brCurrLine);
		}
		br.close();
		return fileList;
	}

	public Grammar read(String filepath) throws IOException {
		ArrayList<String> fileList = parseFile(filepath);
		Grammar grammar = new Grammar();
		GrammarRule currentRule = null;
		boolean isTerminal = true;
		String substring = "";

		for (int currLineIndex = 0; currLineIndex < fileList.size(); currLineIndex++) {
			if ((currLineIndex & 1) == 0) {
				String currLine = fileList.get(currLineIndex);
				GrammarRule rule = new GrammarRule(currLine);
				grammar.getNonTerminals().add(currLine);
				grammar.getRules().add(rule);
			}
		}

		for (int currLineIndex = 0; currLineIndex < fileList.size(); currLineIndex++) {
			String currLine = fileList.get(currLineIndex);
			if ((currLineIndex & 1) == 0) {
				for (int i = 0; i < grammar.getRules().size(); i++) {
					if (grammar.getRules().get(i).getHead().equals(currLine)) {
						currentRule = grammar.getRules().get(i);
						break;
					}
				}
			} else {
				String[] splitLineString = currLine.split(GRAMMAR_RULE_SPLITTER);

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
		}

		return grammar;
	}
}

package grammar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GrammarReader {
	private static final String GRAMMAR_RULE_SPLITTER = "\\|";
	
	public Grammar read(String filename) throws IOException {
		Grammar grammar = new Grammar();
		GrammarRule rule = null;
		FileReader file1 = new FileReader(filename);
		FileReader file2 = new FileReader(filename);
		BufferedReader br1 = new BufferedReader(file1);
		BufferedReader br2 = new BufferedReader(file2);
		GrammarRule currentRule = null;
		boolean isTerminal = true;
		String substring = "";
		String lineString;

		int currLineIndex = 1;
		while ((lineString = br1.readLine()) != null) {
			if (currLineIndex % 2 != 0) {
				rule = new GrammarRule(lineString);
				grammar.getNonTerminals().add(lineString);
				grammar.getRules().add(rule);
			}
			currLineIndex++;
		}
		file1.close();

		currLineIndex = 1;
		while ((lineString = br2.readLine()) != null) {
			if (currLineIndex % 2 != 0) {
				for (int i = 0; i < grammar.getRules().size(); i++) {
					if (grammar.getRules().get(i).getHead().equals(lineString)) {
						currentRule = grammar.getRules().get(i);
						break;
					}
				}
			} else if (currLineIndex % 2 == 0) {
				String[] sts = lineString.split(GRAMMAR_RULE_SPLITTER);

				for (int i = 0; i < sts.length; i++) {
					currentRule.getBody().add(sts[i]);

					for (int j = 0; j < sts[i].length(); j++) {
						for (int k = 0; k < grammar.getNonTerminals().size(); k++) {
							String currentNonTerminal = grammar.getNonTerminals().get(k);
							if (currentNonTerminal.length() == 1) {
								if (currentNonTerminal.equals(sts[i].charAt(j) + "")) {
									isTerminal = false;
									break;
								}
							} else {
								if (currentNonTerminal.length() <= sts[i].length()) {
									int endIndex = j + currentNonTerminal.length();
									if (endIndex > sts[i].length()) {
										endIndex = sts[i].length();
									}
									String comparedString = sts[i].substring(j, endIndex);
									if (currentNonTerminal.equals(comparedString)) {
										j = endIndex - 1;
										isTerminal = false;
										break;
									}
								}

							}
						}

						if (isTerminal) {
							substring += sts[i].charAt(j);

							if (j == sts[i].length() - 1 && !grammar.getTerminals().contains(substring)) {
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
		file2.close();
		
		return grammar;
	}
}

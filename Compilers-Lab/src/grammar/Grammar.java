package grammar;
import java.util.ArrayList;

import utils.GRConsts;
import utils.Utils;

public class Grammar {

	private ArrayList<GrammarRule> rules;
	private ArrayList<String> terminals;
	private ArrayList<String> nonTerminals;

	public Grammar() {
		rules = new ArrayList<GrammarRule>();
		nonTerminals = new ArrayList<String>();
		terminals = new ArrayList<String>();
	}

	public ArrayList<GrammarRule> getRules() {
		return rules;
	}

	public ArrayList<String> getTerminals() {
		return terminals;
	}
	
	public ArrayList<String> getNonTerminals() {
		return nonTerminals;
	}
	
	public String toString() {
		String str = "";
		for (GrammarRule rule : this.getRules()) {
			str += Utils.appendNewLine(rule.toString());
		}
		return str;
	}
}

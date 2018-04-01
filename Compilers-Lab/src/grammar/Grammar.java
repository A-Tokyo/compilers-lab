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
			String head = rule.getHead();
			ArrayList<String> ruleBody = rule.getBody();
			str += head + GRConsts.GRAM_GOES_TO  + "[";
			for (String bodyItem : ruleBody) {
				str += bodyItem;
				if (ruleBody.indexOf(bodyItem) != ruleBody.size() - 1) {
					str += Utils.appendSpace(GRConsts.NORMAL_SEPERATOR_STRING);
				}
			}
			str += Utils.appendNewLine("]");
		}
		return str;
	}
}

package grammar;
import java.util.ArrayList;

import utils.GRConsts;

public class LeftRecursionEliminator {
	public Grammar eliminateLeftRecursion(Grammar inGrammar) {
		Grammar grammar = inGrammar;
		ArrayList<GrammarRule> rules = grammar.getRules();
		ArrayList<String> nonTerminals = grammar.getNonTerminals();

		for (int i = 1; i < rules.size(); i++) {
			GrammarRule rule = rules.get(i);
			ArrayList<String> ruleBody = rule.getBody();
			for (int j = 0; j < ruleBody.size(); j++) {
				String currentProduction = ruleBody.get(j);

				for (int k = 0; k < i; k++) {
					String currNonTerminal = nonTerminals.get(k);
					String currSubString = "";

					if (currNonTerminal.length() == 1) {
						if (currNonTerminal.equals(currentProduction.charAt(0) + "")) {
							int subStringEnd = currentProduction.length();
							currSubString = currentProduction.substring(1, subStringEnd);
						}
					} else if (currNonTerminal.length() > 1) {
						int nonTerminalEndIndex = Math.min(currNonTerminal.length(), currentProduction.length());
						if (currNonTerminal.equals(currentProduction.substring(0, nonTerminalEndIndex))) {
							currSubString = currentProduction.substring(nonTerminalEndIndex, currentProduction.length());
						}
					}

					if (currSubString.length() > 0) {
						int targetIndex = ruleBody.indexOf(currentProduction);
						String targtElement = "";
						GrammarRule matchingRule = null;

						for (GrammarRule currentRule : rules) {
							if (currentRule.getHead().equals(currNonTerminal)) {
								matchingRule = currentRule;
								break;
							}
						}

						int startIndex = matchingRule.getBody().size() - 1;
						ruleBody.remove(targetIndex);

						for (int l = startIndex; l >= 0; l--) {
							String bodyElement = matchingRule.getBody().get(l);
							targtElement = bodyElement + currSubString;
							ruleBody.add(targetIndex, targtElement);
						}
					}
				}
			}
		}

		ArrayList<GrammarRule> newRules = new ArrayList<GrammarRule>();
		ArrayList<GrammarRule> primeRules = new ArrayList<GrammarRule>();

		for (GrammarRule currentRule : rules) {
			String ruleHead = currentRule.getHead();
			ArrayList<String> currentRuleBody = currentRule.getBody();
			ArrayList<String> currentRuleBetas = currentRule.getBetas();
			ArrayList<String> currentRuleAlphas = currentRule.getAlphas();

			for (String bodyElement : currentRuleBody) {
				if (ruleHead.length() == 0){
					break;
				}
				if (ruleHead.length() == 1) {
					if (ruleHead.equals(bodyElement.charAt(0) + "")) {
						currentRuleAlphas.add(bodyElement.substring(1, bodyElement.length()));
					} else {
						currentRuleBetas.add(bodyElement);
					}
				} else {
					int endIndex = Math.min(ruleHead.length(), bodyElement.length());
					if (ruleHead.equals(bodyElement.substring(0, endIndex))) {
						int endIndex2 = Math.min(endIndex + bodyElement.length(), bodyElement.length());
						currentRuleAlphas.add(bodyElement.substring(endIndex, endIndex2));
					} else {
						currentRuleBetas.add(bodyElement);
					}
				}
			}
			GrammarRule ruleReCreated = new GrammarRule(ruleHead);
			String ruleReCreatedHead = ruleReCreated.getHead();
			ArrayList<String> ruleReCreatedBody = ruleReCreated.getBody();

			String rulePrimeHead = ruleReCreatedHead + "'";
			GrammarRule rulePrime = new GrammarRule(rulePrimeHead);
			ArrayList<String> rulePrimeBody = rulePrime.getBody();

			for (String beta : currentRuleBetas) {
				String newBodyElement;
				if (currentRuleAlphas.isEmpty()) {
					newBodyElement = beta;
				} else {
					newBodyElement = beta + rulePrimeHead;
				}
				ruleReCreatedBody.add(newBodyElement);
			}

			for (String alpha : currentRuleAlphas) {
				String newBodyElement = alpha + rulePrimeHead;
				rulePrimeBody.add(newBodyElement);
			}

			rulePrimeBody.add(GRConsts.EPSILON);
			newRules.add(ruleReCreated);
			if (rulePrime.getBody().size() > 1) {
				primeRules.add(rulePrime);
			}
		}

		rules.clear();
		rules.addAll(newRules);
		rules.addAll(primeRules);
		return grammar;
	}
}

package grammar;
import java.util.ArrayList;

import utils.GRConsts;

public class LeftRecursionElimination {
	public Grammar leftRecursionEliminator(Grammar inGrammar) {
		Grammar grammar = inGrammar;
		ArrayList<GrammarRule> rules = grammar.getRules();
		ArrayList<String> nonTerminals = grammar.getNonTerminals();

		for (int i = 1; i < rules.size(); i++) {
			GrammarRule rule = rules.get(i);
			ArrayList<String> ruleBody = rule.getBody();

			for (int j = 0; j < ruleBody.size(); j++) {
				String currentProduction = ruleBody.get(j);

				for (int k = 0; k < i; k++) {
					String currentNonTerminal = nonTerminals.get(k);
					String subString = "";

					if (currentNonTerminal.length() == 1) {
						if (currentNonTerminal.equals(currentProduction.charAt(0) + "")) {
							int subStringEnd = currentProduction.length();
							subString = currentProduction.substring(1, subStringEnd);
						}
					} else if (currentNonTerminal.length() > 1) {
						int nonTerminalEndIndex = currentNonTerminal.length();
						if (nonTerminalEndIndex > currentProduction.length()) {
							nonTerminalEndIndex = currentProduction.length();
						}
						if (currentNonTerminal.equals(currentProduction.substring(0, nonTerminalEndIndex))) {
							subString = currentProduction.substring(nonTerminalEndIndex, currentProduction.length());
						}
					}

					if (subString.length() > 0) {
						int targetIndex = ruleBody.indexOf(currentProduction);
						String targtElement = "";
						GrammarRule matchingRule = null;

						for (GrammarRule currentRule : rules) {
							if (currentRule.getHead().equals(currentNonTerminal)) {
								matchingRule = currentRule;
								break;
							}
						}
						int startingIndex = matchingRule.getBody().size() - 1;
						ruleBody.remove(targetIndex);

						for (int l = startingIndex; l >= 0; l--) {
							String bodyElement = matchingRule.getBody().get(l);
							targtElement = bodyElement + subString;
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
				if (ruleHead.length() == 1) {
					if (ruleHead.equals(bodyElement.charAt(0) + "")) {
						currentRuleAlphas.add(bodyElement.substring(1, bodyElement.length()));
					} else {
						currentRuleBetas.add(bodyElement);
					}
				} else if (ruleHead.length() > 1) {
					int endIndex = ruleHead.length();

					if (endIndex > bodyElement.length()) {
						endIndex = bodyElement.length();
					}
					if (ruleHead.equals(bodyElement.substring(0, endIndex))) {
						int endIndex2 = endIndex + bodyElement.length();

						if (endIndex2 > bodyElement.length()) {
							endIndex2 = bodyElement.length();
						}
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

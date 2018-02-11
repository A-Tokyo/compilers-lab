package nfa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import dfa.DFA;
import dfa.StateTransitions;
import utils.FAConsts;
import utils.Utils;

public class NFA {

	private TreeSet<String> states;
	private TreeSet<String> acceptedStates;
	private TreeSet<String> alphabet;
	private String startState;
	private TreeMap<String, StateTransitions> transitions;
	private String[] inputs;

	//	throw new Error("DFA Construction skipped and inputs are ignored");

	public NFA(String[] states, String[] acceptedStates, String[]  alphabet, String startState, String[] transitionsInputArray, String[] inputs){
		StringBuilder errorSB = new StringBuilder("");

		// States
		this.states = new TreeSet<String>();
		for (int i = 0; i < states.length; i++) {
			String item = states[i];
			if(item.isEmpty()){
				continue;
			}
			this.states.add(item);
		}
		if(this.states.isEmpty()){
			errorSB.append(Utils.appendNewLine("Invalid states empty states set"));
		}

		// Accepted State
		this.acceptedStates = new TreeSet<String>();
		for (int i = 0; i < acceptedStates.length; i++) {
			String item = acceptedStates[i];
			if(item.isEmpty()){
				continue;
			}
			if(!this.states.contains(item)){
				errorSB.append(Utils.appendNewLine("Invalid accept state " + item));
			}
			this.acceptedStates.add(item);
		}

		// Alphabet
		this.alphabet = new TreeSet<String>();
		for (int i = 0; i < alphabet.length; i++) {
			String item = alphabet[i];
			if(item.isEmpty()){
				continue;
			}
			this.alphabet.add(item);
		}

		// Start State
		if(!this.states.contains(startState)){
			errorSB.append(Utils.appendNewLine("Invalid start state"));
		}
		this.startState = startState;

		// Transitions
		this.transitions = new TreeMap<String, StateTransitions>();
		for (int i = 0; i < transitionsInputArray.length; i++) {
			String transitionString = transitionsInputArray[i];
			String [] splitted = transitionString.split(FAConsts.NORMAL_SEPERATOR_STRING);

			// validate transition string
			if(splitted.length < 3){
				errorSB.append(Utils.appendNewLine("Incomplete Transition " + transitionString));
			}

			// to pass TA's test judge
			String splittedConcated = splitted[0] + FAConsts.NORMAL_SEPERATOR_STRING + splitted[1] + FAConsts.NORMAL_SEPERATOR_STRING + splitted[2];

			// destruct values from transition string
			String currState = splitted[0];
			String nextState = splitted[1];
			String alphabetKey = splitted[2];
			StateTransitions currStateTransition = null;

			// validate transition string values
			if(!this.states.contains(currState)){
				errorSB.append(Utils.appendNewLine("Invalid transition " + splittedConcated + " " + "state " + currState +" does not exist"));
			}
			if(!this.states.contains(nextState)){
				errorSB.append(Utils.appendNewLine("Invalid transition " + splittedConcated + " " + "state " + nextState +" does not exist"));
			}
			if(!this.alphabet.contains(alphabetKey) || splitted.length > 3){
				errorSB.append(Utils.appendNewLine("Invalid transition " + splittedConcated + " " + "input " + alphabetKey +" is not in the alphabet"));
			}

			// add current transition to transitions
			if (this.transitions.containsKey(currState)){
				currStateTransition = this.transitions.get(currState);
				currStateTransition.addTransition(alphabetKey, nextState);
			} else {
				currStateTransition = new StateTransitions(currState);
				currStateTransition.addTransition(alphabetKey, nextState);
				this.transitions.put(currState, currStateTransition);
			}
		}

		// validate that all the states have transitions
		Iterator<String> statesIterator = this.states.iterator();
		while(statesIterator.hasNext()) {
			String currState = statesIterator.next();
			if(!this.transitions.containsKey(currState)){
				this.transitions.put(currState, new StateTransitions(currState));
			}
		}

		// Inputs
		this.inputs = inputs;

		String errorStr = errorSB.toString();

		if(!errorStr.isEmpty()){
			String defaultErrorMessage = "DFA Construction skipped and inputs are ignored";
			throw new Error(errorStr.concat(Utils.appendNewLine(defaultErrorMessage)));
		}
	}

	public StateTransitions addTransition(String from, String to, String character){
		StateTransitions currStateTransition = null;
		if (this.transitions.containsKey(from)){
			currStateTransition = this.transitions.get(from);
			currStateTransition.addTransition(character, to);
		} else {
			currStateTransition = new StateTransitions(from);
			currStateTransition.addTransition(character, to);
			this.transitions.put(from, currStateTransition);
		}
		return currStateTransition;
	}

	public StateTransitions addEpsilonTransition(String from, String to){
		return this.addTransition(from, to, FAConsts.EPSILON);
	}

	private boolean isValidAlphabetKeyOrEpsilon(String alphabetKey){
		return alphabetKey == FAConsts.EPSILON || this.alphabet.contains(alphabetKey);
	}

	private static String concactStatesLabel(String state1, String state2){
		return state1.concat(FAConsts.STAR).concat(state2);
	}

	private static String concactStatesLabel(String [] states){
		String state = "";
		for (int i = 0; i < states.length; i++) {
			state = concactStatesLabel(state, states[i]);
		}
		return state;
	}

	public static NFA constructNFA (String nfaStr){
		String[] nfaState = (nfaStr.split(System.lineSeparator()));
		String [] states = nfaState[0].split(FAConsts.NORMAL_SEPERATOR_STRING);
		String [] acceptedStates =  nfaState[1].split(FAConsts.NORMAL_SEPERATOR_STRING);
		String [] alphabet = nfaState[2].split(FAConsts.NORMAL_SEPERATOR_STRING);
		String acceptState = nfaState[3];
		String [] transitions = nfaState[4].split(FAConsts.SECONDARY_SEPERATOR_STRING);
		String [] inputs = nfaState[5].split(FAConsts.SECONDARY_SEPERATOR_STRING);
		return new NFA(states, acceptedStates, alphabet, acceptState, transitions, inputs);
	}

	private static String strJoinArrayNormalSeperator (String [] array) {
		return String.join(FAConsts.NORMAL_SEPERATOR_STRING, array);
	}

	private static String strJoinArraySecondarySeperator (String [] array) {
		return String.join(FAConsts.SECONDARY_SEPERATOR_STRING, array);
	}

	public static String rawInputNFAToRawInputDFA(String rawInputNFA) {
//		String [] nfaState = (rawInputNFA.split(System.lineSeparator()));
//		String [] states = nfaState[0].split(FAConsts.NORMAL_SEPERATOR_STRING);
//		String [] acceptedStates =  nfaState[1].split(FAConsts.NORMAL_SEPERATOR_STRING);
//		String [] alphabet = nfaState[2].split(FAConsts.NORMAL_SEPERATOR_STRING);
//		String acceptState = nfaState[3];
//		String [] transitions = nfaState[4].split(FAConsts.SECONDARY_SEPERATOR_STRING);
//		String [] inputs = nfaState[5].split(FAConsts.SECONDARY_SEPERATOR_STRING);
		// @TODO NFA TO DFA
		return rawInputNFA;
	}

	public static String rawInputNFAsToOutputString(ArrayList<String> rawInputNFAs){
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < rawInputNFAs.size(); i++) {
			String currRawInputNFA = rawInputNFAs.get(i);
			try{
				// @TODO new NFA();
			} catch (Error e) {
				sb.append(Utils.appendNewLine(e.getMessage()));
			}
			sb.append(Utils.appendNewLine(FAConsts.NFA_CONSTRUCTED));
			sb.append(Utils.appendNewLine(FAConsts.EQUIVALENT_DFA));
			String currRawInputDFA = NFA.rawInputNFAToRawInputDFA(currRawInputNFA);
			sb.append(Utils.appendNewLine(currRawInputDFA));	
			String currDFAResultString = DFA.rawInputDFAToOutputString(currRawInputDFA);
			sb.append(Utils.appendNewLine(currDFAResultString));
		}
		return Utils.trimLastChar(sb.toString());
	}

}

package dfa;
import java.util.TreeMap;
import java.util.TreeSet;

import utils.Utils;
import utils.FAConsts;

import java.util.ArrayList;
import java.util.Iterator;


public class DFA {
	private TreeSet<String> states;
	private TreeSet<String> acceptedStates;
	private TreeSet<String> alphabet;
	private String startState;
	private TreeMap<String, StateTransitions> transitions;
	private String[] inputs;
	private boolean invalid = false;
	
	public DFA(String[] states, String[] acceptedStates, String[]  alphabet, String startState, String[] transitionsInputArray, String[] inputs){
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
			throw new Error("Invalid states empty states set");
		}

		// Accepted State
		this.acceptedStates = new TreeSet<String>();
		for (int i = 0; i < acceptedStates.length; i++) {
			String item = acceptedStates[i];
			if(item.isEmpty()){
				continue;
			}
			if(!this.states.contains(item)){
				throw new Error("Invalid accept state " + item);
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
			throw new Error("Invalid start state");
		}
		this.startState = startState;

		// Transitions
		this.transitions = new TreeMap<String, StateTransitions>();
		for (int i = 0; i < transitionsInputArray.length; i++) {
			String transitionString = transitionsInputArray[i];
			String [] splitted = transitionString.split(FAConsts.NORMAL_SEPERATOR_STRING);
			
			// validate transition string
			if(splitted.length < 3){
				throw new Error("Incomplete Transition " + transitionString);
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
				throw new Error("Invalid transition " + splittedConcated + " " + "state " + currState +" does not exist");
			}
			if(!this.states.contains(nextState)){
				throw new Error("Invalid transition " + splittedConcated + " " + "state " + nextState +" does not exist");
			}
			if(!this.alphabet.contains(alphabetKey) || splitted.length > 3){
				throw new Error("Invalid transition " + splittedConcated + " " + "input " + alphabetKey +" is not in the alphabet");
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
				throw new Error("Missing transition for state " + currState);
			}
			StateTransitions currStateTransition = this.transitions.get(currState);
			// validate that all the transitions have all the alphabet
			Iterator<String> alphabetIterator = this.alphabet.iterator();
			while(alphabetIterator.hasNext()){
				String currAlphabetKey = alphabetIterator.next();
				if(!currStateTransition.containsTransitionFor(currAlphabetKey)){
					throw new Error("Missing transition for state " + currState);
				}
			}
		}

		// Inputs
		this.inputs = inputs;
	}

	private boolean isAcceptedState(String state){
		return this.acceptedStates.contains(state);
	}

	private boolean hasTransitionFor(String state){
		return this.transitions.containsKey(state);
	}

	public String [] runOnInputs(){
		String [] toReturn = new String[this.inputs.length];
		for (int i = 0; i < this.inputs.length; i++) {
			String currInput = inputs[i];
			String currResult = runOnInput(currInput.split(FAConsts.NORMAL_SEPERATOR_STRING));
			toReturn[i] = currResult;
		}
		return toReturn;
	}

	public String runOnInput(String[] input){
		String currState = this.startState;
		for (int i = 0; i < input.length; i++) {
			String currAlphabetKey = input[i];
			if(!this.alphabet.contains(currAlphabetKey)){
				return "Invalid input string at " + currAlphabetKey;
			}
			if(hasTransitionFor(currState)){
				StateTransitions currStateTransition = this.transitions.get(currState);
				String nextState = currStateTransition.getTransitionStateFor(currAlphabetKey);
				currState = nextState;
			}else {
				currState = null;
				return FAConsts.REJECTED;
			}
		}
		return isAcceptedState(currState) ? FAConsts.ACCEPTED : FAConsts.REJECTED;
	}
	
	public static DFA constructDFA (String dfaStr){
		String[] dfaState = (dfaStr.split(System.lineSeparator()));
		String [] states = dfaState[0].split(FAConsts.NORMAL_SEPERATOR_STRING);
		String [] acceptedStates =  dfaState[1].split(FAConsts.NORMAL_SEPERATOR_STRING);
		String [] alphabet = dfaState[2].split(FAConsts.NORMAL_SEPERATOR_STRING);
		String acceptState = dfaState[3];
		String [] transitions = dfaState[4].split(FAConsts.SECONDARY_SEPERATOR_STRING);
		String [] inputs = dfaState[5].split(FAConsts.SECONDARY_SEPERATOR_STRING);
		return new DFA(states, acceptedStates, alphabet, acceptState, transitions, inputs);
	}
	
	private static int extractInputsLengthFromRawDFAStr(String dfaStr){
		String[] dfaState = (dfaStr.split(System.lineSeparator()));
		String [] inputs = dfaState[5].split(FAConsts.SECONDARY_SEPERATOR_STRING);
		return inputs.length;
	}

	public static String rawInputDFAsToOutputString(ArrayList<String> rawInputDFAs){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rawInputDFAs.size(); i++) {
			String currRawInputDFA = rawInputDFAs.get(i);
			try {
				DFA currDFA = DFA.constructDFA(currRawInputDFA);
				sb.append(Utils.appendNewLine(FAConsts.DFA_CONSTRUCTED));
				String [] currDFAResults = currDFA.runOnInputs();
				for (int j = 0; j < currDFAResults.length; j++) {
					sb.append(Utils.appendNewLine(currDFAResults[j]));
				}
				sb.append(Utils.appendNewLine(""));
			} catch (Error e) {
				sb.append(Utils.appendNewLine(e.getMessage()));
				for (int j = 0; j <extractInputsLengthFromRawDFAStr(currRawInputDFA); j++) {
					sb.append(Utils.appendNewLine(FAConsts.IGNORED));
				}
				sb.append(Utils.appendNewLine(""));
			}
		}
		return sb.toString();
	}

	public String[] getInputs() {
		return inputs;
	}

	public void setInputs(String[] inputs) {
		this.inputs = inputs;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean isInvalid) {
		this.invalid = isInvalid;
	}
}

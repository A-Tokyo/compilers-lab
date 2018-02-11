package nfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

import dfa.DFA;
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
		this.alphabet.add(FAConsts.EPSILON);

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
		return alphabetKey.equals(FAConsts.EPSILON) || alphabetKey == FAConsts.EPSILON || this.alphabet.contains(alphabetKey);
	}

	public TreeSet<String> getReachableStates(String state){
		TreeSet<String> visited = new TreeSet<String>();		
		Queue <String> searchQueue = new LinkedList<String>();
		searchQueue.add(state);
		visited.add(state);
		while(!searchQueue.isEmpty()){
			String currState = searchQueue.poll();
			if(this.transitions.containsKey(currState)){
				StateTransitions currStateTransitions = this.transitions.get(currState);
				TreeSet<String> newReachableStatesSet = currStateTransitions.getTransitionStateSetFor(FAConsts.EPSILON);
				if(newReachableStatesSet != null){
					String [] newReachableStates = newReachableStatesSet.toArray(new String [0]);
					for (String currReachableState: newReachableStates) {
						if(!visited.contains(currReachableState)){
							searchQueue.add(currReachableState);
							visited.add(currReachableState);
						}
					}
				}
			}
		}

		return visited;
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

	public static String conactStateSetMap(TreeSet<String> states){
		String toReturn = "";
		String [] statesArray = states.toArray(new String [0]);
		for (int i = 0; i < statesArray.length; i++) {
			toReturn += statesArray[i];
			if(i != statesArray.length-1){
				toReturn += FAConsts.STAR;	
			}
		}
		return toReturn;
	}

	public String toDfaOutput() {
		TreeSet<String> visited = new TreeSet<String>();		
		Queue <TreeSet<String>> queue = new LinkedList<TreeSet<String>>();

		TreeSet<String> DFA_states = new TreeSet<String>();
		ArrayList<String> DFA_acceptedStates = new ArrayList<String>();
		// get dfa alphabet
		String [] DFA_alphabet = alphabetToDFAAlphabet().toArray(new String[0]);
		System.out.println(Arrays.toString(DFA_alphabet));
		String DFA_startState = null;
		ArrayList<String> DFA_transitionsInputArray = new ArrayList<String>();

		Boolean deadStateShowed = false;

		String currState = this.startState;		
		TreeSet<String> closureStates = getReachableStates(currState);
		currState = conactStateSetMap(closureStates);	
		// get dfa start state
		DFA_startState = currState;

//		visited.add(currState);
		queue.add(closureStates);

		while(!queue.isEmpty()){
			closureStates = queue.poll();
			System.out.println("closureStates " + closureStates);
			DFA_states.add(conactStateSetMap(closureStates));
			if(isAcceptedState(closureStates)){
				DFA_acceptedStates.add(conactStateSetMap(closureStates));
			}
			for (String currAlphabetKey: this.alphabet) {
				if(currAlphabetKey == FAConsts.EPSILON){
					continue;
				}
				TreeSet<String> nextStateClosure = new TreeSet<String>();
				// get all posible transitions
				for (String singleCurrState: closureStates) {
					if(this.transitions.containsKey(singleCurrState)){
						StateTransitions currStateTransitions = this.transitions.get(singleCurrState);
						TreeSet<String> newReachableStatesSet = currStateTransitions.getTransitionStateSetFor(FAConsts.EPSILON);						
						if(newReachableStatesSet != null){
							String [] newReachableStates = newReachableStatesSet.toArray(new String [0]);
							for (String currReachableState: newReachableStates) {
								nextStateClosure.addAll(getReachableStates(currReachableState));
							}
						}	
					}
				}
				if (nextStateClosure.isEmpty()){
					// go to dead
//					System.out.println("go to dead " + conactStateSetMap(closureStates) + "%%% " + currAlphabetKey + "   " + conactStateSetMap(nextStateClosure)  );
					deadStateShowed = true;
					DFA_transitionsInputArray.add(conactStateSetMap(closureStates) + FAConsts.NORMAL_SEPERATOR_STRING + FAConsts.DEAD + FAConsts.NORMAL_SEPERATOR_STRING + currAlphabetKey );
				} else {
//					System.out.println(" add new state " + conactStateSetMap(closureStates) + "%%% " + currAlphabetKey + "   " + conactStateSetMap(nextStateClosure) );
					// add new state
//					DFA_states.add(conactStateSetMap(nextStateClosure));
					DFA_transitionsInputArray.add(conactStateSetMap(closureStates) + FAConsts.NORMAL_SEPERATOR_STRING + conactStateSetMap(nextStateClosure) + FAConsts.NORMAL_SEPERATOR_STRING + currAlphabetKey );
					if(!visited.contains(conactStateSetMap(closureStates))){
						queue.add(nextStateClosure);
						visited.add(conactStateSetMap(closureStates));
					}
				}
			}
		}

		if(deadStateShowed){
			DFA_states.add(FAConsts.DEAD);
			// add transitions
			for (String alphabetKey: DFA_alphabet) {
				DFA_transitionsInputArray.add(FAConsts.DEAD + FAConsts.NORMAL_SEPERATOR_STRING + FAConsts.DEAD + FAConsts.NORMAL_SEPERATOR_STRING + alphabetKey );				
			}
		}
		
		StringBuilder resultSB = new StringBuilder("");
		resultSB.append(Utils.appendNewLine(String.join(FAConsts.NORMAL_SEPERATOR_STRING, DFA_states.toArray(new String[0]))));
		resultSB.append(Utils.appendNewLine(String.join(FAConsts.NORMAL_SEPERATOR_STRING, DFA_acceptedStates.toArray(new String[0]))));
		resultSB.append(Utils.appendNewLine(String.join(FAConsts.NORMAL_SEPERATOR_STRING, DFA_alphabet)));
		resultSB.append(Utils.appendNewLine(DFA_startState));
		resultSB.append(Utils.appendNewLine(String.join(FAConsts.SECONDARY_SEPERATOR_STRING, DFA_transitionsInputArray.toArray(new String[0]))));
		resultSB.append(Utils.appendNewLine(String.join(FAConsts.SECONDARY_SEPERATOR_STRING, this.inputs)));
		
		resultSB.append(DFA.constructorInputDFAToOutputString(DFA_states.toArray(new String[0]), DFA_acceptedStates.toArray(new String[0]),
				DFA_alphabet,
				DFA_startState,
				DFA_transitionsInputArray.toArray(new String[0]), this.inputs));
		return resultSB.toString();
	}
	
	public TreeSet<String> alphabetToDFAAlphabet(){
		TreeSet<String> newTreeSet = (TreeSet<String>) this.alphabet.clone();
		newTreeSet.remove(FAConsts.EPSILON);
		return newTreeSet;
	}

	public boolean isAcceptedState (TreeSet<String> states){
		for(String currState: states){
			if(this.acceptedStates.contains(currState)){
				return true;
			}
		}
		return false;
	}

	public static String rawInputNFAsToOutputString(ArrayList<String> rawInputNFAs){
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < rawInputNFAs.size(); i++) {
			String currRawInputNFA = rawInputNFAs.get(i);
			NFA myNfa = null;
			try{
				String [] nfaState = (currRawInputNFA.split(System.lineSeparator()));
				String [] states = nfaState[0].split(FAConsts.NORMAL_SEPERATOR_STRING);
				String [] acceptedStates =  nfaState[1].split(FAConsts.NORMAL_SEPERATOR_STRING);
				String [] alphabet = nfaState[2].split(FAConsts.NORMAL_SEPERATOR_STRING);
				String acceptState = nfaState[3];
				String [] transitions = nfaState[4].split(FAConsts.SECONDARY_SEPERATOR_STRING);
				String [] inputs = nfaState[5].split(FAConsts.SECONDARY_SEPERATOR_STRING);
				myNfa = new NFA(states, acceptedStates, alphabet, acceptState, transitions, inputs);
				
				sb.append(Utils.appendNewLine(FAConsts.NFA_CONSTRUCTED));
				sb.append(Utils.appendNewLine(FAConsts.EQUIVALENT_DFA));
				
				sb.append(myNfa.toDfaOutput());
				
			} catch (Error e) {
				sb.append(Utils.appendNewLine(e.getMessage()));
			}
		}
		return Utils.trimLastChar(sb.toString());
	}

}

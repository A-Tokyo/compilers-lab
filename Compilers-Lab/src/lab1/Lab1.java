package lab1;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class FAConsts {
	public final static String NORMAL_SEPERATOR_STRING = ",";
	public final static String SECONDARY_SEPERATOR_STRING = "#";
	
	public final static String ACCEPTED = "Accepted";
	public final static String REJECTED = "Rejected";
	public final static String IGNORED = "Ignored";
	
	public final static String DFA_CONSTRUCTED = "DFA constructed";
	public final static String NFA_CONSTRUCTED = "NFA constructed";
	public final static String EQUIVALENT_DFA = "Equivalent DFA:";
	
	public final static String STAR = "*";
	public final static String EPSILON = "$";
	
	public final static String DEAD = "Dead";

}

class Utils {
	
	public static String appendNewLine(String str){
		return str + System.lineSeparator();
	}
	
	public static void writeOutputFile(String fileText, String outFilePath)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFilePath));
		writer.write(fileText);
		writer.close();
	}
	
	public static String trimLastChar(String str){
		return str.substring(0, str.length()-1);
	}
	
	public static String attemptToString(Object o){
		if(o != null){
			return o.toString();			
		}
		return null;
	}
}



class StateTransitionsDFA {
	private String state;
	// <AlphabetCharacter, State>
	private TreeMap<String, String> transitions;
	
	public StateTransitionsDFA(String state){
		this.state = state;
		transitions = new TreeMap<String,String>();
	}
	
	public void addTransition(String alphabetKey, String state) {
		transitions.put(alphabetKey, state);
	}
	
	public boolean containsTransitionFor(String alphabetKey){
		return transitions.containsKey(alphabetKey);
	}
	
	public String getTransitionStateFor(String alphabetKey){
		return transitions.get(alphabetKey);
	}
	
	public String toString(){
		return "<Name:(" + this.state + ")##" + "Transitions: (" + this.transitions.toString()+")>";
	}
}


class DFA {
	private TreeSet<String> states;
	private TreeSet<String> acceptedStates;
	private TreeSet<String> alphabet;
	private String startState;
	private TreeMap<String, StateTransitionsDFA> transitions;
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
		this.transitions = new TreeMap<String, StateTransitionsDFA>();
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
			StateTransitionsDFA currStateTransition = null;

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
				currStateTransition = new StateTransitionsDFA(currState);
				currStateTransition.addTransition(alphabetKey, nextState);
				this.transitions.put(currState, currStateTransition);
			}
		}

		// validate that all the states have transitions
		Iterator<String> statesIterator = this.states.iterator();
		while(statesIterator.hasNext()) {
			String currState = statesIterator.next();
			if(!this.transitions.containsKey(currState)){
				System.out.println("Missing transition for state " + currState);
				System.out.println(this.states);
				throw new Error("Missing transition for state " + currState);
			}
			StateTransitionsDFA currStateTransition = this.transitions.get(currState);
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
				StateTransitionsDFA currStateTransition = this.transitions.get(currState);
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

	public static String rawInputDFAToOutputString(String rawInputDFA){
		StringBuilder sb = new StringBuilder("");
		String currRawInputDFA = rawInputDFA;
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
		return sb.toString();
	}
	
	public static String constructorInputDFAToOutputString(String[] states, String[] acceptedStates, String[]  alphabet, String startState, String[] transitionsInputArray, String[] inputs){
		StringBuilder sb = new StringBuilder("");
		try {
			DFA currDFA = new DFA(states, acceptedStates, alphabet, startState, transitionsInputArray, inputs);
			sb.append(Utils.appendNewLine(FAConsts.DFA_CONSTRUCTED));
			String [] currDFAResults = currDFA.runOnInputs();
			for (int j = 0; j < currDFAResults.length; j++) {
				sb.append(Utils.appendNewLine(currDFAResults[j]));
			}
			sb.append(Utils.appendNewLine(""));
		} catch (Error e) {
			sb.append(Utils.appendNewLine(e.getMessage()));
			for (int j = 0; j < inputs.length; j++) {
				sb.append(Utils.appendNewLine(FAConsts.IGNORED));
			}
			sb.append(Utils.appendNewLine(""));
		}
		return sb.toString();
	}

	public static String rawInputDFAsToOutputString(ArrayList<String> rawInputDFAs){
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < rawInputDFAs.size(); i++) {
			sb.append(rawInputDFAToOutputString(rawInputDFAs.get(i)));
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

class Lab1 {
	private static String DEFAULT_IN_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab1/in1.in";
	private static String DEFAULT_OUT_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab1/myout1.out";

	public static ArrayList<String> parseFileToDFAs(String inFilePath) throws FileNotFoundException, IOException{
		BufferedReader br;
		String currentDFA = "";
		ArrayList<String> dfasToReturn = new ArrayList<String>();		
		int currLineIndex = 0;
		try {
			br = new BufferedReader(new FileReader(inFilePath));
			try {
				String currLine;
				while ( (currLine = br.readLine()) != null ) {
					currLineIndex ++;
					if(currLineIndex%7 == 0 && currLine.isEmpty() ){
						dfasToReturn.add(currentDFA.trim());
						currentDFA = "";
					} else {
						currentDFA += (currLine + System.lineSeparator());
					}
				}
				return dfasToReturn;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				br.close();
			}
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return null;
	}
}

class StateTransitionsNFA {
	private String state;
	// <AlphabetCharacter, State>
	private TreeMap<String, TreeSet<String>> transitions;
	
	public StateTransitionsNFA(String state){
		this.state = state;
		transitions = new TreeMap<String, TreeSet<String>>();
	}
	
	public void addTransition(String alphabetKey, String state) {
		if(transitions.containsKey(alphabetKey)){
			TreeSet<String> newStatesSet = transitions.get(alphabetKey);
			newStatesSet.add(state);
			transitions.put(alphabetKey, newStatesSet);
		} else {
			TreeSet<String> newStatesSet = new TreeSet<String>();
			newStatesSet.add(state);
			transitions.put(alphabetKey, newStatesSet);
		}
//		System.out.println(this.state + " ->" + alphabetKey + " ->> " + this.transitions.get(alphabetKey));
	}
	
	public boolean containsTransitionFor(String alphabetKey){
		return transitions.containsKey(alphabetKey);
	}
	
	public TreeSet<String> getTransitionStateSetFor(String alphabetKey){
		return transitions.get(alphabetKey);
	}
	
	public String toString(){
		return "<Name:(" + this.state + ")##" + "Transitions: (" + this.transitions.toString()+")>";
	}
}

class NFA {

	private TreeSet<String> states;
	private TreeSet<String> acceptedStates;
	private TreeSet<String> alphabet;
	private String startState;
	private TreeMap<String, StateTransitionsNFA> transitions;
	private String[] inputs;

	//	throw new Error("DFA Construction skipped and inputs are ignored");

	public NFA(String[] states, String[] acceptedStates, String[]  alphabet, String startState, String[] transitionsInputArray, String[] inputs){

		System.out.println("NEEEWW");
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
		this.transitions = new TreeMap<String, StateTransitionsNFA>();
		for (int i = 0; i < transitionsInputArray.length; i++) {
			String transitionString = transitionsInputArray[i];
			String [] splitted = transitionString.split(FAConsts.NORMAL_SEPERATOR_STRING);

			// validate transition string
			if(splitted.length < 3){
				errorSB.append(Utils.appendNewLine("Incomplete Transition " + transitionString));
			}

			// to pass TA's test judge
			String splittedConcated = transitionString;
			if(splitted.length >= 3){
				splittedConcated = splitted[0] + FAConsts.NORMAL_SEPERATOR_STRING + splitted[1] + FAConsts.NORMAL_SEPERATOR_STRING + splitted[2];	
				// destruct values from transition string
				String currState = splitted[0];
				String nextState = splitted[1];
				String alphabetKey = splitted[2];
				StateTransitionsNFA currStateTransition = null;

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
					currStateTransition = new StateTransitionsNFA(currState);
					currStateTransition.addTransition(alphabetKey, nextState);
					this.transitions.put(currState, currStateTransition);
				}
			}
		}

		// validate that all the states have transitions
		Iterator<String> statesIterator = this.states.iterator();
		while(statesIterator.hasNext()) {
			String currState = statesIterator.next();
			if(!this.transitions.containsKey(currState)){
				this.transitions.put(currState, new StateTransitionsNFA(currState));
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

	public StateTransitionsNFA addTransition(String from, String to, String character){
		StateTransitionsNFA currStateTransition = null;
		if (this.transitions.containsKey(from)){
			currStateTransition = this.transitions.get(from);
			currStateTransition.addTransition(character, to);
		} else {
			currStateTransition = new StateTransitionsNFA(from);
			currStateTransition.addTransition(character, to);
			this.transitions.put(from, currStateTransition);
		}
		return currStateTransition;
	}

	public StateTransitionsNFA addEpsilonTransition(String from, String to){
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
				StateTransitionsNFA currStateTransitions = this.transitions.get(currState);
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
			System.out.println("closureStates Polled " + closureStates);
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
					StateTransitionsNFA currStateTransitions = this.transitions.get(singleCurrState);
					TreeSet<String> newReachableStatesSet = new TreeSet<String>();
					TreeSet<String> directReachableStatesByAlpha = currStateTransitions.getTransitionStateSetFor(currAlphabetKey);
					if(directReachableStatesByAlpha != null){
						// get colosures for these and add
						for (String directReachableStateByAlpha: directReachableStatesByAlpha) {
							newReachableStatesSet.addAll(getReachableStates(directReachableStateByAlpha));
						}
					}
					if(!newReachableStatesSet.isEmpty()){
						String [] newReachableStates = newReachableStatesSet.toArray(new String [0]);
						for (String currReachableState: newReachableStates) {
							nextStateClosure.addAll(getReachableStates(currReachableState));
						}
					}
				}
				if (nextStateClosure.isEmpty()){
					deadStateShowed = true;
					DFA_transitionsInputArray.add(conactStateSetMap(closureStates) + FAConsts.NORMAL_SEPERATOR_STRING + FAConsts.DEAD + FAConsts.NORMAL_SEPERATOR_STRING + currAlphabetKey );
				} else {
					DFA_transitionsInputArray.add(conactStateSetMap(closureStates) + FAConsts.NORMAL_SEPERATOR_STRING + conactStateSetMap(nextStateClosure) + FAConsts.NORMAL_SEPERATOR_STRING + currAlphabetKey );
					if(!visited.contains(conactStateSetMap(nextStateClosure))){
						queue.add(nextStateClosure);
					}
				}
				if(!visited.contains(conactStateSetMap(nextStateClosure))){
					visited.add(conactStateSetMap(nextStateClosure));
				}
			}
		}

		if(deadStateShowed){
			DFA_states.add(FAConsts.DEAD);
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
		System.out.println(rawInputNFAs.size());
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

public class Lab2 {
	private static String DEFAULT_IN_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab2/in1.in";
	private static String DEFAULT_OUT_FILEPATH = "/Users/Tokyo/Dev/Eclipse/compilers-lab/Compilers-Lab/src/assets/Lab2/myout1.out";
	
	public static ArrayList<String> parseFileToNFAs(String inFilePath) throws FileNotFoundException, IOException{
		BufferedReader br;
		String currentDFA = "";
		ArrayList<String> dfasToReturn = new ArrayList<String>();		
		int currLineIndex = 0;
		try {
			br = new BufferedReader(new FileReader(inFilePath));
			try {
				String currLine;
				while ( (currLine = br.readLine()) != null ) {
					currLineIndex ++;
					if(currLineIndex%7 == 0 && currLine.isEmpty() ){
						dfasToReturn.add(currentDFA.trim());
						currentDFA = "";
					} else {
						currentDFA += (currLine + System.lineSeparator());
					}
				}
				return dfasToReturn;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				br.close();
			}
		}
		catch (FileNotFoundException e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public static void runTask() throws FileNotFoundException, IOException{
		ArrayList<String> rawInputNFAs = parseFileToNFAs(DEFAULT_IN_FILEPATH);
		String resultFileText = NFA.rawInputNFAsToOutputString(rawInputNFAs);
		Utils.writeOutputFile(resultFileText, DEFAULT_OUT_FILEPATH);
	}
}

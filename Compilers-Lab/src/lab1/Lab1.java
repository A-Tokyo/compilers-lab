package lab1;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.util.TreeMap;
import java.util.TreeSet;

import java.util.ArrayList;
import java.util.Iterator;

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

public class Lab1 {
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
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		ArrayList<String> rawInputDFAs = parseFileToDFAs(DEFAULT_IN_FILEPATH);
		String resultFileText = DFA.rawInputDFAsToOutputString(rawInputDFAs);
		Utils.writeOutputFile(resultFileText, DEFAULT_OUT_FILEPATH);
	}
}
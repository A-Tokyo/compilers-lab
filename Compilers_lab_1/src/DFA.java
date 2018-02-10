import java.util.TreeMap;
import java.util.TreeSet;
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
				throw new Error("Invalid accepted state (" + item+")");
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
			String [] splitted = transitionString.split(",");

			// validate transition string
			if(splitted.length < 3){
				throw new Error("Invalid Incomplete " + transitionString);
			}

			// destruct values from transition string
			String currState = splitted[0];
			String nextState = splitted[1];
			String alphabetKey = splitted[2];
			StateTransitions currStateTransition = null;

			// validate transition string values
			if(!this.states.contains(currState)){
				throw new Error("Invalid transition " + transitionString + " " + "state " + currState +" is not in the states");
			}
			if(!this.states.contains(nextState)){
				throw new Error("Invalid transition " + transitionString + " " + "state " + nextState +" is not in the states");
			}
			if(!this.alphabet.contains(alphabetKey)){
				throw new Error("Invalid transition " + transitionString + " " + "input " + alphabetKey +" is not in the alphabet");
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
		for (int i = 0; i < this.inputs.length; i++) {
			String [] currInputArray = inputs[i].split(",");
			for (int j = 0; j < currInputArray.length; j++) {
				String alphabetKey = currInputArray[j];
				if(!this.alphabet.contains(alphabetKey)){
					throw new Error("Invalid input string at " + alphabetKey);
				}
			}
		}

		System.out.println("DFA constructed");
	}

	private boolean isAcceptedState(String state){
		return this.acceptedStates.contains(state);
	}

	private boolean hasTransitionFor(String state){
		return this.transitions.containsKey(state);
	}

	public boolean [] runOnInputs(){
		boolean[] toReturn = new boolean[this.inputs.length];
		for (int i = 0; i < this.inputs.length; i++) {
			boolean currResult = runOnInput(inputs[i].split(","));
			toReturn[i] = currResult;
		}
		for (int i = 0; i < toReturn.length; i++) {
			if(this.isInvalid()){
				System.out.println("Ignored");
				continue;
			}
			if(toReturn[i]){
				System.out.println("Accepted");
				continue;
			}
			System.out.println("Rejected");
		}
		System.out.println("");
		return toReturn;
	}

	public boolean runOnInput(String[] input){
		String currState = this.startState;
		if(isAcceptedState(currState)){
			return true;
		}
		for (int i = 0; i < input.length; i++) {
			String currAlphabetKey = input[i];
			if(hasTransitionFor(currState)){
				StateTransitions currStateTransition = this.transitions.get(currState);
				String nextState = currStateTransition.getTransitionStateFor(currAlphabetKey);
				currState = nextState;
			}else {
				currState = null;
			}
		}
		if(currState == null){
			return false;
		}
		return isAcceptedState(currState);
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

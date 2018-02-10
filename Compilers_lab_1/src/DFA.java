import java.util.TreeMap;
import java.util.TreeSet;

public class DFA {
	private TreeSet<String> states;
	private TreeSet<String> acceptedStates;
	private TreeSet<String> alphabet;
	private String startState;
	private TreeMap<String, StateTransitions> transitions;
	private String[] inputs;

	public DFA(String[] states, String[] acceptedStates, String[]  alphabet, String startState, String[] transitionsInputArray, String[] inputs){
		// States
		this.states = new TreeSet<String>();
		for (int i = 0; i < states.length; i++) {
			this.states.add(states[i]);
		}

		// Accepted State
		this.acceptedStates = new TreeSet<String>();
		for (int i = 0; i < acceptedStates.length; i++) {
			this.acceptedStates.add(acceptedStates[i]);
		}

		// Alphabet
		this.alphabet = new TreeSet<String>();
		for (int i = 0; i < alphabet.length; i++) {
			this.alphabet.add(alphabet[i]);
		}
		// Start State
		this.startState = startState;

		// Transitions
		this.transitions = new TreeMap<String, StateTransitions>();
		for (int i = 0; i < transitionsInputArray.length; i++) {
			String item = transitionsInputArray[i];			
			String [] splitted = item.split(",");

			String currState = splitted[0];
			String nextState = splitted[1];
			String alphabetKey = splitted[2];
			StateTransitions currStateTransition = null;

			if (this.transitions.containsKey(currState)){
				currStateTransition = this.transitions.get(currState);
				currStateTransition.addTransition(alphabetKey, nextState);
			} else {
				currStateTransition = new StateTransitions(currState);
				currStateTransition.addTransition(alphabetKey, nextState);
				this.transitions.put(currState, currStateTransition);
			}
		}
		this.inputs = inputs;
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
			if(toReturn[i]){
				System.out.println("Accepted");
				continue;
			}
			System.out.println("Rejected");
		}
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
}

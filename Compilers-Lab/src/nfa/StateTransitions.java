package nfa;
import java.util.TreeMap;
import java.util.TreeSet;

public class StateTransitions {
	private String state;
	// <AlphabetCharacter, State>
	private TreeMap<String, TreeSet<String>> transitions;
	
	public StateTransitions(String state){
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

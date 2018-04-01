package leftRecursionElimination;
import java.util.ArrayList;

public class GrammarRule {
	private String head;
	private ArrayList<String> body;
	private ArrayList<String> alphas;
	private ArrayList<String> betas;

	public GrammarRule(String head) {
		this.head = head;
		body = new ArrayList<String>();
		alphas = new ArrayList<String>();
		betas = new ArrayList<String>();
	}

	public String getHead() {
		return head;
	}

	public ArrayList<String> getBody() {
		return body;
	}

	public ArrayList<String> getAlphas() {
		return alphas;
	}

	public ArrayList<String> getBetas() {
		return betas;
	}
}

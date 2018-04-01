package grammar;
import java.util.ArrayList;

import utils.GRConsts;
import utils.Utils;

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
	
	public String toString() {
		String str = this.head + GRConsts.GRAM_GOES_TO  + "[";
		for (String bodyItem : this.body) {
			str += bodyItem;
			if (this.body.indexOf(bodyItem) != this.body.size() - 1) {
				str += Utils.appendSpace(GRConsts.NORMAL_SEPERATOR_STRING);
			}
		}
		return str + "]";
	}
}

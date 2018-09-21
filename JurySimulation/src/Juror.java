import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Juror class simulates the agents of the argumentation simulation
 * @author Panagiotis
 *
 */
public class Juror {
	// Agent's probability to pay attention to a specific argument
	private double attentionSpan;
	// Agent's knowledge base of presented arguments they paid attention to
	private ArrayList<Argument> knowledge;
	
	/**
	 * Constructor of Juror class with user-specified properties
	 * @param attentionSpan required
	 * @param knowledge required
	 */
	public Juror(double attentionSpan) {
		this.attentionSpan = attentionSpan;
		this.knowledge = new ArrayList<Argument>();
	}
	
	/**
	 * Constructor of Juror class with randomly initialized properties
	 */
	public Juror() {
		this.attentionSpan = ThreadLocalRandom.current().nextDouble();;
		this.knowledge = new ArrayList<Argument>();
	}
	
	/**
	 * Getter of attentionSpan property
	 * @return
	 */
	public double getAttentionSpan() {
		return attentionSpan;
	}
	
	/**
	 * Setter of attentionSpan property
	 * @param attentionSpan
	 */
	public void setAttentionSpan(double attentionSpan) {
		this.attentionSpan = attentionSpan;
	}
	
	/**
	 * Getter of knowledge property
	 * @return
	 */
	public ArrayList<Argument> getKnowledge() {
		return knowledge;
	}
	
	/**
	 * Setter of knowledge property
	 * @param knowledge
	 */
	public void setKnowledge(ArrayList<Argument> knowledge) {
		this.knowledge = knowledge;
	}
	
	
	
}

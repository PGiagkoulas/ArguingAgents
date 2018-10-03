import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Juror class simulates the agents of the argumentation simulation
 * @author Panagiotis
 *
 */
public class Juror {
	// id of argument
	private int id; // TODO: more elaborate?
	// Agent's probability to pay attention to a specific argument
	private int attentionSpan;
	// Agent's knowledge base of presented arguments they paid attention to
	private ArrayList<Argument> knowledge;
	
	// instances counter
	private static int counter = 0;
		
	/**
	 * Constructor of Juror class with user-specified properties
	 * @param attentionSpan required
	 * @param knowledge required
	 */
	public Juror(int attentionSpan) {
		counter++;
		this.id = counter;
		this.attentionSpan = attentionSpan;
		this.knowledge = new ArrayList<Argument>();
	}
	
	/**
	 * Constructor of Juror class with randomly initialized properties
	 */
	public Juror() {
		counter++;
		this.id = counter;
		this.attentionSpan = ThreadLocalRandom.current().nextInt(1,101);;
		this.knowledge = new ArrayList<Argument>();
	}
	
	/**
	 * Getter of juror's id
	 * @return int id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter of attentionSpan property
	 * @return double attentionSpan
	 */
	public double getAttentionSpan() {
		return attentionSpan;
	}
	
	/**
	 * Setter of attentionSpan property
	 * @param attentionSpan
	 */
	public void setAttentionSpan(int attentionSpan) {
		this.attentionSpan = attentionSpan;
	}
	
	/**
	 * Getter of knowledge property
	 * @return ArrayList<Argument> knowledge
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
	
	/**
	 * Function that decides which arguments the juror remembers. Runs through the whole list of arguments.
	 * @param providedArguments: all of the arguments in the current case/court
	 */
	public void takeInArguments(ArrayList<Argument> providedArguments) {
		for(Argument a:providedArguments) { // for every argument
			if(ThreadLocalRandom.current().nextInt(1,101)<=attentionSpan) { // generate a random value
				// if it is smaller or equal to the juror's attention span, the argument is added to the juror's knowledge
				knowledge.add(a);
			}
		}
	}
	
	// TODO: not necessary?
	public boolean calculateVote() {
		int innocent = 0;
		for(Argument a:knowledge) {
			innocent = (a.isInnocent()) ? innocent+1 : innocent-1;
		}
		return (innocent>=0);
	}
	
	
}

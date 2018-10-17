import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Juror class simulates the agents of the argumentation simulation
 * @author Panagiotis
 *
 */
public class Juror {
	// id of argument
	private int id; // TODO: more elaborate?
	// Agent's knowledge base of presented arguments they paid attention to
	private ArrayList<Argument> knowledge;
	// Agent's acceptance of every argument type
	private Map<Utils.ArgumentType, Double> argumentTypeAcceptance;
	
	// instances counter
	private static int counter = 0;
	// members in a small jury
	private static final double MIN_ACCEPTANCE = 0.6;
	// members in a big jury
	private static final double MAX_ACCEPTANCE = 1.0;
		
	/**
	 * Constructor of Juror class with user-specified properties
	 * @param argumentTypeAcceptance required
	 */
	public Juror(Map<Utils.ArgumentType, Double> argumentTypeAcceptance) {
		counter++;
		this.id = counter;
		this.knowledge = new ArrayList<Argument>();
		this.argumentTypeAcceptance = argumentTypeAcceptance;
	}
	
	/**
	 * Constructor of Juror class with bias
	 * @param: biased whether the generated agent will be biased
	 * @param: biasLevel how many claims the biased juror will have, relative to trial's num of arguments 
	 * @param: correctVerdict the expected verdict
	 */
	public Juror(Map<Utils.ArgumentType, Double> argumentTypeAcceptance, Utils.BiasLevel biasLevel, int trialArguments, boolean correctVerdict) {
		counter++;
		this.id = counter;
		this.knowledge = new ArrayList<Argument>();
		this.argumentTypeAcceptance = new HashMap<Utils.ArgumentType, Double>();
		this.argumentTypeAcceptance.put(Utils.ArgumentType.CLAIM, ThreadLocalRandom.current().nextDouble(MIN_ACCEPTANCE, MAX_ACCEPTANCE));
		this.argumentTypeAcceptance.put(Utils.ArgumentType.EVIDENCE, ThreadLocalRandom.current().nextDouble(MIN_ACCEPTANCE, MAX_ACCEPTANCE));
		this.argumentTypeAcceptance.put(Utils.ArgumentType.TESTIMONY, ThreadLocalRandom.current().nextDouble(MIN_ACCEPTANCE, MAX_ACCEPTANCE));
		
		this.argumentTypeAcceptance = argumentTypeAcceptance;
		
		int numOfClaims = (int)(trialArguments*biasLevel.getPercentage());
		for(int i=0; i<numOfClaims; i++) {
			this.knowledge.add(new Argument(Utils.ArgumentType.CLAIM, !correctVerdict));
		}
	}
	
	/**
	 * Getter of juror's id
	 * @return int id
	 */
	public int getId() {
		return id;
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
	 * Function that decides which arguments the juror accepts. Runs through the whole list of arguments.
	 * @param providedArguments: all of the arguments in the current case/court
	 */
	public void takeInArguments(ArrayList<Argument> providedArguments) {
		// for every argument in the argument list
		for(Argument a:providedArguments) {
			// check if random chance equal or smaller than acceptance of argument type to add argument
			this.takeInArgument(a);
		}
	}
	
	/**
	 * Function that decides if the juror accepts an argument.
	 * @param providedArgument: argument to decided if it is going to be accepted
	 */
	public void takeInArgument(Argument providedArgument) {
		if( ThreadLocalRandom.current().nextDouble(MIN_ACCEPTANCE, MAX_ACCEPTANCE) <= this.argumentTypeAcceptance.get(providedArgument.getType()) ) {
			this.knowledge.add(providedArgument);
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

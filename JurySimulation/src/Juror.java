import java.util.ArrayList;
import java.util.Collections;
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
	// Agent's initiative to participate in the deliberations
	private double participation;
	// Agent's willingness to continue deliberating
	private double willingness;

	// instances counter
	private static int counter = 0;
	// members in a small jury
	private static final double MIN_ACCEPTANCE = 0.0;
	// members in a big jury
	private static final double MAX_ACCEPTANCE = 1.0;

	/**
	 * Constructor of Juror class with user-specified properties
	 * @param argumentTypeAcceptance required
	 */
	public Juror(Map<Utils.ArgumentType, Double> argumentTypeAcceptance, double participation, double willingness) {
		counter++;
		this.id = counter;
		this.knowledge = new ArrayList<Argument>();
		this.argumentTypeAcceptance = argumentTypeAcceptance;
		this.participation = participation;
		this.willingness = willingness;
	}

	/**
	 * Constructor of Juror class with bias
	 * @param: biased whether the generated agent will be biased
	 * @param: biasLevel how many claims the biased juror will have, relative to trial's num of arguments 
	 * @param: correctVerdict the expected verdict
	 */
	public Juror(Map<Utils.ArgumentType, Double> argumentTypeAcceptance, double participation, double willingness,
			Utils.BiasLevel biasLevel, int trialArguments, ArrayList<Argument> claims) {
		counter++;
		this.id = counter;
		this.participation = participation;
		this.willingness = willingness;		
		this.argumentTypeAcceptance = new HashMap<Utils.ArgumentType, Double>();
		this.argumentTypeAcceptance = argumentTypeAcceptance;
		this.knowledge = new ArrayList<Argument>();
		// adding claims to biased agent's knowledge base
		int numOfClaims = (int)(trialArguments*biasLevel.getPercentage());
		Collections.shuffle(claims);
		for(int i=0; i<numOfClaims; i++) {
			this.knowledge.add(claims.get(i));
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
	 * Gets acceptance of each argument type
	 * @return getArgumentTypeAcceptance
	 */
	public Map<Utils.ArgumentType, Double> getArgumentTypeAcceptance() {
		return argumentTypeAcceptance;
	}
	/**
	 * Sets acceptance of each argument type
	 */
	public void setArgumentTypeAcceptance(Map<Utils.ArgumentType, Double> argumentTypeAcceptance) {
		this.argumentTypeAcceptance = argumentTypeAcceptance;
	}
	/**
	 * Gets agent's participation
	 * @return participation
	 */
	public double getParticipation() {
		return participation;
	}
	/**
	 * Sets agent's participation
	 */
	public void setParticipation(double participation) {
		this.participation = participation;
	}
	/**
	 * Gets agent's willingness
	 * @return willingness
	 */
	public double getWillingness() {
		return willingness;
	}
	/**
	 * Sets agent's willingness
	 */
	public void setWillingness(double willingness) {
		this.willingness = willingness;
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
		if( ThreadLocalRandom.current().nextDouble(MIN_ACCEPTANCE, MAX_ACCEPTANCE) <= this.argumentTypeAcceptance.get(providedArgument.getType()) 
				&& !this.knowledge.contains(providedArgument)) {
			this.knowledge.add(providedArgument);
		}	
	}

	// TODO: not necessary?

	/**
	 * Get the argument type with the highest acceptance from agent's acceptance map
	 * @return argument type with highest acceptance
	 */
	public Utils.ArgumentType getNextHighestAccetingArgumentType(int trial){
		Map<Utils.ArgumentType, Double> tempAccMapp = this.argumentTypeAcceptance;
		Map.Entry<Utils.ArgumentType, Double> maxEntry = null;
		for (Map.Entry<Utils.ArgumentType, Double> entry : tempAccMapp.entrySet())	{
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		if(trial > 1) {
			tempAccMapp.remove(maxEntry.getKey());
		}
		if(trial == 2) {
			for (Map.Entry<Utils.ArgumentType, Double> entry : tempAccMapp.entrySet())	{
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}
		}
		else if(trial == 3) {
			for (Map.Entry<Utils.ArgumentType, Double> entry : tempAccMapp.entrySet())	{
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) < 0) {
					maxEntry = entry;
				}
			}
		}
		return maxEntry.getKey();
	}

	/**
	 * Calculates the jurors current vote/opinion
	 * @return innocent=false if most arguments in knowledge are negative. True otherwise.
	 */
	public boolean calculateVote() {
		int innocent = 0;
		for(Argument a:knowledge) {
			innocent = (a.isInnocent()) ? innocent+1 : innocent-1;
		}
		return (innocent>=0);
	}
	
	public int testVoteScore() {
		int innocent = 0;
		for(Argument a:knowledge) {
			innocent = (a.isInnocent()) ? innocent+1 : innocent-1;
		}
		return innocent;
	}

	@Override
	public String toString() {
		return String.format(">ID: %d.\n"
				+ " Num of arguments in knowledge: %d.\n"
				+ " Acceptance : %s.\n"
				+ " Innocent suspect: %b \n",
				this.id, this.knowledge.size(), this.argumentTypeAcceptance, calculateVote());
	}
	
	/**
	 * Method to calculate the argument type statistics for the Juror
	 * @return Map<Utils.ArgumentType, Double> stats
	 */
	public Map<Utils.ArgumentType, Double> calculateArgumentTypeStatistics(){
		Map<Utils.ArgumentType, Double> stats = new HashMap<Utils.ArgumentType, Double>();
		for(Argument a:this.knowledge) {
			if(stats.containsKey(a.getType())){
				stats.put(a.getType(), stats.get(a.getType())+1);
			}
			else
			{
				stats.put(a.getType(), new Double(1));
			}			
		}
		for(Map.Entry<Utils.ArgumentType, Double> entry : stats.entrySet()) {
			entry.setValue(entry.getValue()/this.knowledge.size());
		}
		return stats;
	}
	
	/**
	 * Method to calculate the argument side statistics for the Juror
	 * @return Map<Sting, Double> stats
	 */
	public Map<String, Double> calculateArgumentSideStatistics(){
		Map<String, Double> stats = new HashMap<String, Double>();
		double positive = 0, negative = 0;
		for(Argument a:this.knowledge) {
			if(a.isInnocent()) {
				positive++;
			}
			else {
				negative++;
			}
		}
		stats.put("Exonerating", positive/this.knowledge.size());
		stats.put("Incriminating", negative/this.knowledge.size());
		return stats;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Juror) {
			return this.id == (((Juror)other).getId());
		}
		else {
			return false;
		}
	}
}

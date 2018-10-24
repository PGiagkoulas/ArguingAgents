import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Court class is the regulator of the simulation
 * @author Panagiotis
 *
 */
public class Court {
	// List of jury members
	private ArrayList<Juror> jurorList;
	// Jury size
	private Utils.JurySize jurySize;
	// List of all arguments to be presented in the simulation
	private ArrayList<Argument> argumentList;
	// Set of all claims that the jury has (uniques)
	private Set<Argument> assignedClaims;
	// List of all claims that the jury has
	private ArrayList<Argument> claims;
	// verdict based on the list of all arguments
	private boolean correctVerdict;
	// verdict based on the votes of the jury
	private String trialVerdict;
	// type of vote
	private Utils.VoteType voteType;
	
	// Statistics counters
	// number of deliberations that took place
	private int totalDeliberations;
	private int totalClaimsSpread;
	private double avgClaimSpread;
	private int totalNonClaimsSpread;
	private double avgNonClaimSpread;
	
	// minimum number of arguments in a case
	private static final int MIN_NUM_ARG = 20;
	// maximum number of arguments in a case
	private static final int MAX_NUM_ARG = 50;
	// minimum number of deliberations in a case
	private static final int MIN_NUM_DEL = 1;
	// maximum number of deliberations in a case
	private static final int MAX_NUM_DEL = 3;

	/**
	 * Constructor of Court class with user-specified properties
	 * @param jurorList
	 * @param argumentList
	 * @param numOfDeliberations
	 */
	public Court(ArrayList<Juror> jurorList, ArrayList<Argument> argumentList, ArrayList<Argument> claims, Utils.VoteType voteType) {
		this.jurorList = jurorList;
		this.argumentList = argumentList;
		this.claims = claims;
		this.voteType = voteType;
		this.correctVerdict = this.calculateVerdict(this.argumentList);
	}
	
	/**
	 * Constructor of Court class with user-specified properties
	 * @param voteType
	 * @param numOfArguments
	 * @param jurySize
	 * @param numOfBiasedJurors
	 * @param biasedDetails
	 */
	public Court(Utils.VoteType voteType, int numOfArguments, Utils.JurySize jurySize, int numOfBiasedJurors, int[] biasedDetails) {
		this.voteType = voteType;
		this.argumentList = new ArrayList<Argument>();
		
		// generating random types of argument. 25% innocent - 75% guilty
		int innArgs = 0, guilArgs=0;
		if(ThreadLocalRandom.current().nextBoolean()) {
			innArgs = (int)Math.floor(numOfArguments*0.45);
			guilArgs = (int)Math.ceil(numOfArguments*0.55);
		}
		else {
			innArgs = (int)Math.ceil(numOfArguments*0.55);
			guilArgs = (int)Math.floor(numOfArguments*0.45);
		}
		// exonerating
		for(int i=0; i<innArgs; i++) {
			// technical evidence
			if(ThreadLocalRandom.current().nextBoolean()) {
				this.argumentList.add(new Argument(Utils.ArgumentType.EVIDENCE, true));
			}
			//testimonies
			else {
				this.argumentList.add(new Argument(Utils.ArgumentType.TESTIMONY, true));
			}
		}
		// incriminating
		for(int i=0; i<guilArgs; i++) {
			// technical evidence
			if(ThreadLocalRandom.current().nextBoolean()) {
				this.argumentList.add(new Argument(Utils.ArgumentType.EVIDENCE, false));
			}
			//testimonies
			else {
				this.argumentList.add(new Argument(Utils.ArgumentType.TESTIMONY, false));
			}
		}
		
		this.correctVerdict = this.calculateVerdict(this.argumentList);
		
		// generating jury
		this.jurySize = jurySize;
		this.jurorList = new ArrayList<Juror>();
		this.claims = new ArrayList<Argument>();
		this.assignedClaims = new HashSet<Argument>();
		// biased jurors
		if(numOfBiasedJurors>0) {
			claims = Utils.generateClaims( (int)Math.floor(0.5*this.argumentList.size()), this.correctVerdict);
			for(int i=0; i<biasedDetails[0]; i++) {
				this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.CLAIM), 
						ThreadLocalRandom.current().nextDouble(0.8, 1.0), 1.0,
						Utils.BiasLevel.LOW, numOfArguments, claims));
			}
			for(int j=0; j<biasedDetails[1]; j++) {
				this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.CLAIM), 
						ThreadLocalRandom.current().nextDouble(0.8, 1.0), 1.0,
						Utils.BiasLevel.HIGH, numOfArguments, claims));
			}
			;
			for(Juror j:this.jurorList) {
				for(Argument a:j.getKnowledge()) {
					this.assignedClaims.add(a);
				}
			}
		}
		// neutral jurors
		for(int i=0; i<jurySize.getsize()-numOfBiasedJurors; i++) {
			// high evidence acceptance
			if(ThreadLocalRandom.current().nextBoolean()) {
				this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.EVIDENCE),
												ThreadLocalRandom.current().nextDouble(0.8, 1.0), 1.0));
			}
			// high testimony acceptance
			else {
				this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.TESTIMONY),
												ThreadLocalRandom.current().nextDouble(0.8, 1.0), 1.0));
			}
		}
		long seed = System.nanoTime();
		Collections.shuffle(this.jurorList, new Random(seed));
	}

	/**
	 * Getter of all jurors
	 * @return ArrayList<Juror> jurorList
	 */
	public ArrayList<Juror> getJurorList() {
		return jurorList;
	}

	/**
	 * Getter of all arguments
	 * @return ArrayList<Argument> argumentList
	 */
	public ArrayList<Argument> getArgumentList() {
		return argumentList;
	}

	/**
	 * Returns verdict based on full argument list
	 * @return false: guilty, true: innocent
	 */
	public boolean isCorrectVerdict() {
		return correctVerdict;
	}

	/**
	 * Returns verdict based on jury vote
	 * @return false: guilty, true: innocent
	 */
	public String isTrialVerdict() {
		return trialVerdict;
	}

	/**
	 * Getter of vote type
	 * @return String voteType
	 */
	public Utils.VoteType getVoteType() {
		return voteType;
	}

	/**
	 * Gets the average spread of claims through all deliberation rounds
	 * @return avgClaimSpread
	 */
	public double getAvgClaimSpread() {
		return avgClaimSpread;
	}
	

	/**
	 * Gets the average spread of non-claim arguments through all deliberation rounds
	 * @return avgNonClaimsSpread
	 */


	public double getAvgNonClaimsSpread() {
		return avgNonClaimSpread;
	}
	
	/**
	 * Get total number of deliberations	
	 * @return totalDeliberations
	 */

	public int getTotalDeliberations() {
		return totalDeliberations;
	}
	

	/**
	 * Emulates the presentation of arguments to all jury members.
	 */
	public void provideArguments() {
		for(Juror j:jurorList) {
			j.takeInArguments(argumentList);
		}
	}

	/**
	 * Prints the details of the trial/simulation
	 */
	@Override
	public String toString() {
		return String.format("Number of jurors: %d.\n"
				+ "Number of arguments: %d.\n"
				+ "Innocent suspect: %s.\n"
				+ "Voting type: %s vote.\n"
				+ "Final verdict: %s.\n",
				this.jurorList.size(), this.argumentList.size(), this.correctVerdict, this.voteType, this.trialVerdict);
	}

	/**
	 * Function that simulates a deliberation round. Each jury member chooses an argument they know 
	 * and share it with the rest of the jury. Any jury members missing that argument, add it to their knowledge base.
	 */
	public void juryDeliberation() {
		// initialize statistics
		this.totalDeliberations = 0;
		this.totalClaimsSpread = 0;
		this.avgClaimSpread = 0;
		this.totalNonClaimsSpread = 0;
		this.avgNonClaimSpread = 0;
		// get initial jury willingness
		double juryWillingness = calculateJuryWillingness(this.jurorList);
		Map<Argument, Integer> presentedArguments = new HashMap<Argument, Integer>();
		// keep deliberating as long as jury is willing 
		// and not all arguments and claims have been presented
		// and unanimity has not been reached 
		while(juryWillingness >= ThreadLocalRandom.current().nextDouble()
				&& presentedArguments.size() < (this.argumentList.size() + this.assignedClaims.size())
				) {
			this.totalDeliberations++;
			// every juror gets a chance to speak
			for(Juror j:this.jurorList) {
				// if the juror wants to speak
				if(j.getParticipation() >= ThreadLocalRandom.current().nextDouble()) {
					boolean presented = false;
					Map<Utils.ArgumentType, Double> tempAccMap = new HashMap<Utils.ArgumentType, Double>(j.getArgumentTypeAcceptance());
					// while juror has not presented an argument and has arguments in his knowledge base that have not been presented
					while(!presented && tempAccMap.size()>0) {
						// get the next type with the highest acceptance
						Utils.ArgumentType argType = Utils.getMaxValueKey(tempAccMap);
						int argIndex = 0;
						ArrayList<Argument> currJurorKnowledge = new ArrayList<Argument>(j.getKnowledge());
						// while juror has not presented, search through juror's arguments to choose one to present
						while(!presented && argIndex < currJurorKnowledge.size()) {
							Argument argumentToPresent = currJurorKnowledge.get(argIndex);
							// check if next argument to be presented has been presented too many times
							boolean repetition = false;
							if(presentedArguments.containsKey(argumentToPresent)) {
								repetition = (presentedArguments.get(argumentToPresent) > 3);
							}
							boolean jurorOpinion = j.calculateVote();
							// if juror current argument is of the specified type and it has not been presented too many times
							if(argumentToPresent.getType().equals(argType) && argumentToPresent.isInnocent()==jurorOpinion && !repetition) {
								// present argument to all other jurors
								for(Juror listeningJuror:this.jurorList) {
									// if it is not the presenting juror
									if(!listeningJuror.equals(j)) {
										int prevSize = listeningJuror.getKnowledge().size();										
										listeningJuror.takeInArgument(argumentToPresent);
										// keep track of accepted arguments
										if(listeningJuror.getKnowledge().size() > prevSize) {
											if(argumentToPresent.getType().equals(Utils.ArgumentType.CLAIM)) {
												this.totalClaimsSpread++;
											}
											else {
												this.totalNonClaimsSpread++;
											}
										}
									}
								}
								// juror presented an argument
								presented = true;
								// argument occurrence is incremented to avoid extended repetition
								if(presentedArguments.containsKey(argumentToPresent)){
									presentedArguments.put(argumentToPresent, presentedArguments.get(argumentToPresent)+1);
								}
								else
								{
									presentedArguments.put(argumentToPresent, 1);
								}
							}
							// else go to next argument
							else {
								argIndex++;
							}
						}
						tempAccMap.remove(argType);
					}
				}
				// after 10 deliberations
				if(this.totalDeliberations > 10) {
					// after jurors got a chance to present, reduce the individual willingness
					j.setWillingness(j.getWillingness()*(1-this.voteType.getPenalty()));
				}
			}
			// recalculate jury's willingness
			juryWillingness = calculateJuryWillingness(this.jurorList);
		}
		this.avgClaimSpread = (double)this.totalClaimsSpread/this.totalDeliberations;
		this.avgNonClaimSpread = (double)this.totalNonClaimsSpread/this.totalDeliberations;
		System.out.println("Number of deliberations: " + this.totalDeliberations);
		System.out.println("Number of non-claims spread in deliberation: " + this.totalNonClaimsSpread);
		System.out.println("Number of claims spread in deliberation: " + this.totalClaimsSpread);
		System.out.println("Average non-claim spread: " + this.avgClaimSpread);
		System.out.println("Average claim spread: " + this.avgNonClaimSpread);
	}

	/**
	 * Function that simulates the voting process of the jury
	 */
	public String juryVote() {
		int innocent = 0;
		for(Juror j:jurorList) {
			innocent = (j.calculateVote()) ? innocent+1 : innocent-1;
		}
		// unanimous voting system
		if(this.voteType.equals(Utils.VoteType.UNANIMOUS)) {
			// all of the jury members support "guilty" verdict
			if( (innocent < 0) && (Math.abs(innocent)==this.jurorList.size()) ) {
				this.trialVerdict = "guilty";
			}
			// all of the jury members support "innocent" verdict
			else if( (innocent >= 0) && (Math.abs(innocent)==this.jurorList.size()) ) {
				this.trialVerdict = "innocent";
			}
			// in any other case, without unanimous vote, we have a hung jury
			else {
				this.trialVerdict = "hung jury";
			}
		}
		// majority vote
		else{
			// simple majority supports "guilty" verdict
			if( (innocent < 0) && (Math.abs(innocent)>0) ) {
				this.trialVerdict = "guilty";
			}
			// simple majority supports "innocent" verdict
			else if( (innocent > 0) && (Math.abs(innocent)>0) ) {
				this.trialVerdict = "innocent";
			}
			// in any other case, without simple majority vote, we have a hung jury
			else {
				this.trialVerdict = "hung jury";
			}
		}
		return this.trialVerdict;
	}

	/**
	 * Argument distribution of current trial 
	 * @return Map<Utils.ArgumentType, Double> typeDistribution
	 */
	public Map<Utils.ArgumentType, Double> getTrialArgumentTypeDistribution(){
		Map<Utils.ArgumentType, Double> typeDistribution = new HashMap<Utils.ArgumentType, Double>();
		
		typeDistribution.put(Utils.ArgumentType.CLAIM, 100*(double)this.assignedClaims.size()/(this.assignedClaims.size()+this.argumentList.size()));
		for(Argument a:this.argumentList) {
			if(typeDistribution.containsKey(a.getType())){
				typeDistribution.put(a.getType(), typeDistribution.get(a.getType())+1);
			}
			else
			{
				typeDistribution.put(a.getType(), new Double(1));
			}			
		}
		for(Map.Entry<Utils.ArgumentType, Double> entry : typeDistribution.entrySet()) {
			entry.setValue(100*entry.getValue()/(this.assignedClaims.size()+this.argumentList.size()));
		}
		return typeDistribution;
	}
	
	/**
	 * Argument type distribution of current jury
	 * @return Map<Utils.ArgumentType, Double> typeDistribution
	 */
	public Map<Utils.ArgumentType, Double> getJuryArgumentTypeDistribution(){		
		Map<Utils.ArgumentType, Double> trialJuryStats = new HashMap<Utils.ArgumentType, Double>();
		for(Juror j:this.jurorList) {
			Map<Utils.ArgumentType, Double> jurorStats = j.calculateArgumentTypeStatistics();
			for(Map.Entry<Utils.ArgumentType, Double> entry : jurorStats.entrySet()) {
				if( trialJuryStats.containsKey(entry.getKey()) ) {
					trialJuryStats.put(entry.getKey(), trialJuryStats.get(entry.getKey()) + entry.getValue());
				}
				else {
					trialJuryStats.put(entry.getKey(), entry.getValue());
				}
			}
		}
		for(Map.Entry<Utils.ArgumentType, Double> entry : trialJuryStats.entrySet()) {
			entry.setValue(100*entry.getValue()/this.jurorList.size());
		}
		return trialJuryStats;
	}
	
	/**
	 * String form of current trial distributions of argument types
	 * @return String completeStats
	 */
	public String getSimulationStatistics() {
		String completeStats = "";
		
		Map<Utils.ArgumentType, Double> trialTypeDistribution = new HashMap<Utils.ArgumentType, Double>(this.getTrialArgumentTypeDistribution());
		
		completeStats = String.format("Evidence: %.2f%% \n"
									   + "Testimonies: %.2f%% \n"
									   + "Claims: %.2f%% \n",
									   trialTypeDistribution.get(Utils.ArgumentType.EVIDENCE), 
									   trialTypeDistribution.get(Utils.ArgumentType.TESTIMONY),
									   trialTypeDistribution.get(Utils.ArgumentType.CLAIM));
		
		completeStats = completeStats + "\n=====================================================\n";
		
		Map<Utils.ArgumentType, Double> trialJuryStats = new HashMap<Utils.ArgumentType, Double>(this.getJuryArgumentTypeDistribution());
		
		completeStats = completeStats + String.format("Average juror percentage of evidence arguments: %.2f%%\n"
									   + "Average juror percentage of testimony arguments: %.2f%%\n"
									   + "Average juror percentage of claim arguments: %.2f%%\n", 
									   trialJuryStats.get(Utils.ArgumentType.EVIDENCE), 
									   trialJuryStats.get(Utils.ArgumentType.TESTIMONY),
									   trialJuryStats.get(Utils.ArgumentType.CLAIM));
		
		return completeStats;
	}
	
	/**
	 * Function to calculate verdict based on a list of arguments
	 * @param ArrayList<Argument> arguments
	 * @return false if most arguments are incriminating and true otherwise
	 */
 	private boolean calculateVerdict(ArrayList<Argument> arguments) {
		int innocent = 0;
		for(Argument a:arguments) {
			innocent = (a.isInnocent()) ? innocent+1 : innocent-1;
		}
		return (innocent>=0);
	}

	/**
	 * Calculates the jury's average willingness to continue deliberating
	 * @param jurorList
	 * @return groupWillingness
	 */
	private double calculateJuryWillingness(ArrayList<Juror> jurorList) {
		double totalWillingness = 0.0;
		for(Juror j:jurorList) {
			totalWillingness += j.getWillingness();
		}
		return totalWillingness/jurorList.size();
	}

	
	private boolean checkVotingPrereq(Utils.VoteType voteType) {
		int innocent = 0;
		for(Juror j:this.jurorList) {
			innocent = (j.calculateVote()) ? innocent+1 : innocent-1;
		}
		if(voteType.equals(Utils.VoteType.UNANIMOUS)) {
			return (Math.abs(innocent)==this.jurorList.size());
		}
		else {
			return (Math.abs(innocent) > 0);
		}
	}

}

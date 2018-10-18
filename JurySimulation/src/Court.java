import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
	// List of all claims that the jury has
	private ArrayList<Argument> claims;
	// verdict based on the list of all arguments
	private boolean correctVerdict;
	// verdict based on the votes of the jury
	private String trialVerdict;
	// type of vote
	private Utils.VoteType voteType;

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
		
		// generating random types of argument. 30% innocent - 70% guilty
		int innArgs = (int)Math.floor(numOfArguments*0.3);
		int guilArgs = (int)Math.ceil(numOfArguments*0.7);
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
		// biased jurors
		if(numOfBiasedJurors>0) {
			ArrayList<Argument> claims = Utils.generateClaims( (int)Math.floor(Utils.BiasLevel.HIGH.getPercentage()*this.argumentList.size()), this.correctVerdict);
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
		int numOfDeliberations = 0;
		double juryWillingness = calculateJuryWillingness(this.jurorList);
		ArrayList<Argument> presentedArguments = new ArrayList<Argument>();
		// keep deliberating as long as jury is willing 
		// and not all arguments have been presented
		// and unanimity has not been reached 
		while(juryWillingness >= ThreadLocalRandom.current().nextDouble()
				&& presentedArguments.size() < this.argumentList.size()
				&& !checkVotingPrereq(this.getVoteType())) {
			numOfDeliberations++;
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
						// while juror has not presented, search through juror's arguments to choose one to present
						while(!presented && argIndex < j.getKnowledge().size()) {
							ArrayList<Argument> currJurorKnowledge = j.getKnowledge();
							// if juror current argument is of the specified type and it has not been presented
							if(currJurorKnowledge.get(argIndex).getType().equals(argType) && !presentedArguments.contains(currJurorKnowledge.get(argIndex))) {
								Argument presentedArgument = currJurorKnowledge.get(argIndex);
								// present argument to all other jurors
								for(Juror listeningJuror:this.jurorList) {
									// if it is not the presenting juror
									if(!listeningJuror.equals(j)) {
										listeningJuror.takeInArgument(presentedArgument);
									}
								}
								// juror presented an argument
								presented = true;
								// argument is added to presented arguments to avoid repetition
								presentedArguments.add(presentedArgument);
							}
							// else go to next argument
							else {
								argIndex++;
							}
						}
						tempAccMap.remove(argType);
					}
				}
				// after jurors got a chance to present, reduce the individual willingness
				j.setWillingness(j.getWillingness()*(1-this.voteType.getPenalty()));
			}
			// recalculate jury's willingness
			juryWillingness = calculateJuryWillingness(this.jurorList);
		}
		System.out.println("Number of deliberations: " + numOfDeliberations);
	}

	/**
	 * Function that simulates the voting process of the jury
	 */
	public String juryVote() {
		int innocent = 0;
		for(Juror j:jurorList) {
			innocent = (calculateVerdict(j.getKnowledge())) ? innocent+1 : innocent-1;
		}
		// unanimous voting system
		if(this.voteType.equals(Utils.VoteType.UNANIMOUS)) {
			// all of the jury members support "guilty" verdict
			if( (innocent < 0) && (Math.abs(innocent)==this.jurorList.size()) ) {
				this.trialVerdict = "guilty";
			}
			// all of the jury members support "innocent" verdict
			else if( (innocent > 0) && (Math.abs(innocent)==this.jurorList.size()) ) {
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
			if( (innocent < 0) && (Math.abs(innocent)>=(Math.floorDiv(this.jurorList.size(), 2) + 1)) ) {
				this.trialVerdict = "guilty";
			}
			// simple majority supports "innocent" verdict
			else if( (innocent > 0) && (Math.abs(innocent)>=(Math.floorDiv(this.jurorList.size(), 2) + 1)) ) {
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
			return (Math.abs(innocent) >= (Math.floorDiv(this.jurorList.size(),2) + 1) );
		}
	}
}

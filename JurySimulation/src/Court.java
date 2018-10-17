import java.util.ArrayList;
import java.util.Formatter;
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
	public Court(ArrayList<Juror> jurorList, ArrayList<Argument> argumentList, Utils.VoteType voteType) {
		this.jurorList = jurorList;
		this.argumentList = argumentList;
		this.voteType = voteType;
		this.correctVerdict = this.calculateVerdict(this.argumentList);
	}
	
	public Court(Utils.VoteType voteType, int numOfArguments, Utils.JurySize jurySize, int numOfBiasedJurors, int[] biasedDetails) {
		this.voteType = voteType;
		this.argumentList = new ArrayList<Argument>();
		
		// generating random arguments. 40% innocent - 60% guilty
		int innArgs = (int)Math.floor(numOfArguments*0.4);
		int guilArgs = (int)Math.ceil(numOfArguments*0.6);
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
		for(int i=0; i<biasedDetails[0]; i++) {
			this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.CLAIM), Utils.BiasLevel.LOW, numOfArguments, this.correctVerdict));
		}
		for(int j=0; j<biasedDetails[1]; j++) {
			this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.CLAIM), Utils.BiasLevel.HIGH, numOfArguments, this.correctVerdict));
		}
		// neutral jurors
		for(int i=0; i<jurySize.getsize()-numOfBiasedJurors; i++) {
			// high evidence acceptance
			if(ThreadLocalRandom.current().nextBoolean()) {
				this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.EVIDENCE)));
			}
			// high testimony acceptance
			else {
				this.jurorList.add(new Juror(Utils.generateAcceptance(Utils.ArgumentType.TESTIMONY)));
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
		StringBuilder sbuf = new StringBuilder();
		Formatter fmt = new Formatter(sbuf);
		fmt.format("Number of jurors: %d.\n"
				+ "Number of arguments: %d.\n"
				+ "Innocent suspect: %s.\n"
				+ "Voting type: %s vote.\n"
				+ "Final verdict: %s.\n",
				this.jurorList.size(), this.argumentList.size(), this.correctVerdict, this.voteType, this.trialVerdict);
		return fmt.toString();
	}

	/**
	 * Function that simulates a deliberation round. Each jury member chooses an argument they know 
	 * and share it with the rest of the jury. Any jury members missing that argument, add it to their knowledge base.
	 */
	public void juryDeliberation() {
		int argumentToShareIndex;
		ArrayList<Argument> argumentsToShare = new ArrayList<Argument>();
		// go through all the jury members
		for(Juror j:jurorList) {
			// a juror will deliberate only if they have at least one argument in their knowledge base
			if(j.getKnowledge().size() > 0) {
				// pick a random argument from the juror's knowledge base
				argumentToShareIndex = ThreadLocalRandom.current().nextInt(0,j.getKnowledge().size());
				Argument argumentToShare = j.getKnowledge().get(argumentToShareIndex);
				// if it is not already included in the to-be-shared arguments, add it
				if(!argumentsToShare.contains(argumentToShare)) {
					argumentsToShare.add(j.getKnowledge().get(argumentToShareIndex));
				}
			}			
		}
		// traverse all jury members again
		for(Juror j:jurorList) {
			for(Argument a:argumentsToShare) {
				// add any shared argument that is not already in the juror's knowledge base
				if(!j.getKnowledge().contains(a)) {
					j.getKnowledge().add(a);
				}
			}
		}
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
		if(this.voteType.equals("unanimous")) {
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


}

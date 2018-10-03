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
	// List of all arguments to be presented in the simulation
	private ArrayList<Argument> argumentList;
	// number of jury deliberations 
	private int numOfDeliberations;
	// verdict based on the list of all arguments
	private boolean correctVerdict;
	// verdict based on the votes of the jury
	private boolean trialVerdict;
	// type of vote
	private String voteType;
	// members in a small jury
	private static final int SMALL_JURY = 6;
	// members in a big jury
	private static final int BIG_JURY = 12;
	// minimum number of arguments in a case
	private static final int MIN_NUM_ARG = 5;
	// maximum number of arguments in a case
	private static final int MAX_NUM_ARG = 10;
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
	public Court(ArrayList<Juror> jurorList, ArrayList<Argument> argumentList, int numOfDeliberations, String voteType) {
		this.jurorList = jurorList;
		this.argumentList = argumentList;
		this.numOfDeliberations = numOfDeliberations;
		this.voteType = voteType.toLowerCase();
		this.correctVerdict = this.calculateVerdict(this.argumentList);
	}

	/**
	 * Constructor of Court class with randomly initialized properties
	 */
	public Court() {
		int numOfJurors = (ThreadLocalRandom.current().nextBoolean()) ? SMALL_JURY : BIG_JURY;
		int numOfArguments = ThreadLocalRandom.current().nextInt(MIN_NUM_ARG, MAX_NUM_ARG + 1);
		this.numOfDeliberations = ThreadLocalRandom.current().nextInt(MIN_NUM_DEL, MAX_NUM_DEL + 1);

		this.jurorList = new ArrayList<Juror>();
		for(int i=0; i<numOfJurors; i++) {
			this.jurorList.add(new Juror());
		}

		this.argumentList = new ArrayList<Argument>();
		for(int i=0; i<numOfArguments; i++) {
			this.argumentList.add(new Argument());
		}

		this.correctVerdict = this.calculateVerdict(this.argumentList);

		this.voteType = (ThreadLocalRandom.current().nextBoolean()) ? "unanimous" : "majority";
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
	 * Getter of deliberation number
	 * @return int numOfDeliberations
	 */
	public int getNumOfDeliberations() {
		return numOfDeliberations;
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
	public boolean isTrialVerdict() {
		return trialVerdict;
	}

	/**
	 * Getter of vote type
	 * @return String voteType
	 */
	public String getVoteType() {
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
				+ "Number of deliberations: %d.\n",
				this.jurorList.size(), this.argumentList.size(), this.correctVerdict, this.voteType, this.numOfDeliberations);
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
	public boolean juryVote() {
		int innocent = 0;
		for(Juror j:jurorList) {
			innocent = (calculateVerdict(j.getKnowledge())) ? innocent+1 : innocent-1;
		}
		this.trialVerdict = (innocent>=0);
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

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
		this.correctVerdict = this.calculateCorrectVerdict(this.argumentList);
	}

	/**
	 * Constructor of Court class with randomly initialized properties
	 */
	public Court() {
		int numOfJurors = ThreadLocalRandom.current().nextInt(1, 15 + 1);
		int numOfArguments = ThreadLocalRandom.current().nextInt(1, 9 + 1);
		this.numOfDeliberations = ThreadLocalRandom.current().nextInt(1, 3 + 1);
		
		this.jurorList = new ArrayList<Juror>();
		for(int i=0; i<numOfJurors; i++) {
			this.jurorList.add(new Juror());
		}
		
		this.argumentList = new ArrayList<Argument>();
		for(int i=0; i<numOfArguments; i++) {
			this.argumentList.add(new Argument());
		}
		
		this.correctVerdict = this.calculateCorrectVerdict(this.argumentList);
		
		this.voteType = (ThreadLocalRandom.current().nextInt(2) == 0) ? "unanimous" : "majority";
	}
	
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
	 * Function to calculate the "correct" verdict based on the list of all arguments
	 * @param arguments
	 * @return false if most arguments are incriminating and true otherwise
	 */
	private boolean calculateCorrectVerdict(ArrayList<Argument> arguments) {
		int innocent = 0;
		for(Argument a:arguments) {
			innocent = (a.isInnocent()) ? innocent+1 : innocent-1;
		}
		return (innocent>=0);
	}
	
	
}

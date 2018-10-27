
/**
 * Utility class to facilitate user provided parameters for the simulations
 * @author Panagiotis
 *
 */
public class MenuSelectionObject {
	private Utils.JurySize juryChoice;
	private Utils.VoteType voteChoice;
	private int argumentChoice;
	private int biasedChoice;
	private int lowBiasedChoice;
	private int highBiasedChoice;
	private int numOfSimulations;
	
	/**
	 * Creates a menu selection object
	 * @param juryChoice
	 * @param voteChoice
	 * @param argumentChoice
	 * @param biasedChoice
	 * @param lowBiasedChoice
	 * @param highBiasedChoice
	 * @param numOfSimulations
	 */
	public MenuSelectionObject(int juryChoice, int voteChoice, int argumentChoice, int biasedChoice,
			int lowBiasedChoice, int highBiasedChoice, int numOfSimulations) {
		if(juryChoice==1) {
			this.juryChoice = Utils.JurySize.SMALL;
		}
		else {
			this.juryChoice = Utils.JurySize.BIG;
		}
		if(voteChoice == 1) {
			this.voteChoice = Utils.VoteType.UNANIMOUS;
		}
		else {
			this.voteChoice = Utils.VoteType.MAJORITY;
		}
		this.argumentChoice = argumentChoice;
		this.biasedChoice = biasedChoice;
		this.lowBiasedChoice = lowBiasedChoice;
		this.highBiasedChoice = highBiasedChoice;
		this.numOfSimulations = numOfSimulations;
	}

	public Utils.JurySize getJuryChoice() {
		return juryChoice;
	}


	public Utils.VoteType getVoteChoice() {
		return voteChoice;
	}


	public int getArgumentChoice() {
		return argumentChoice;
	}


	public int getBiasedChoice() {
		return biasedChoice;
	}


	public int getLowBiasedChoice() {
		return lowBiasedChoice;
	}


	public int getHighBiasedChoice() {
		return highBiasedChoice;
	}


	public int getNumOfSimulations() {
		return numOfSimulations;
	}
	
}

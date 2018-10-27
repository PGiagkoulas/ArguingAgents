import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public	 class Utils {
	
	/**
	 * Enumerator from the different argument types
	 */
	public enum ArgumentType{
		CLAIM,
		TESTIMONY,
		EVIDENCE;
		
		public static ArgumentType getRandomArgumentType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
	}
	
	/**
	 * Enumerator from the different argument types
	 */
	public enum VoteType{
		UNANIMOUS (0.02),
		MAJORITY (0.075);
		private final double penalty;
		private VoteType(double penalty) {
			this.penalty = penalty;
		}
		public double getPenalty() {
			return this.penalty;
		}
	}
	
	/**
	 * Enumerator from the different Bias levels
	 */
	public enum BiasLevel{
		LOW (0.10),
		HIGH (0.20);
		private final double percentage;
		private BiasLevel(double percentage) {
			this.percentage = percentage;
		}
		public double getPercentage() {
			return this.percentage;
		}
	}
	
	/**
	 * Enumerator from the different Jury sizes
	 */
	public enum JurySize{
		SMALL (6),
		BIG (12);
		private final int size;
		private JurySize(int size) {
			this.size = size;
		}
		public int getsize() {
			return this.size;
		}
	}
	
	/**
	 * Generate acceptance array with fixed percentages and highest on the given argument type
	 * @param argtype: which type to have the highest acceptance
	 * @return acceptanceMap
	 */
	public static Map<ArgumentType, Double> generateAcceptance(ArgumentType argtype){
		Map<ArgumentType, Double> acceptanceMap = new HashMap<ArgumentType, Double>();
		switch(argtype) {
		case CLAIM:
			acceptanceMap.put(argtype, ThreadLocalRandom.current().nextDouble(0.7, 0.85));
			acceptanceMap.put(ArgumentType.EVIDENCE, ThreadLocalRandom.current().nextDouble(0.35, 0.55));
			acceptanceMap.put(ArgumentType.TESTIMONY, ThreadLocalRandom.current().nextDouble(0.4, 0.6));
			break;
		case TESTIMONY:
			acceptanceMap.put(argtype, ThreadLocalRandom.current().nextDouble(0.7, 0.85));
			acceptanceMap.put(ArgumentType.CLAIM, ThreadLocalRandom.current().nextDouble(0.2, 0.4));
			acceptanceMap.put(ArgumentType.EVIDENCE, ThreadLocalRandom.current().nextDouble(0.4, 0.6));
			break;
		case EVIDENCE:
			acceptanceMap.put(argtype, ThreadLocalRandom.current().nextDouble(0.7, 0.85));
			acceptanceMap.put(ArgumentType.CLAIM, ThreadLocalRandom.current().nextDouble(0.2, 0.4));
			acceptanceMap.put(ArgumentType.TESTIMONY, ThreadLocalRandom.current().nextDouble(0.4, 0.6));
			break;
		default:
		}
		return acceptanceMap;
	}
	
	/**
	 * Get the key of the <key,value> pair with max value
	 * @param tempMap
	 * @return key of maxEntry
	 */
	public static ArgumentType getMaxValueKey(Map<ArgumentType, Double> tempMap){
		Map.Entry<ArgumentType, Double> maxEntry = null;
		for (Map.Entry<ArgumentType, Double> entry : tempMap.entrySet())	{
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		return maxEntry.getKey();
	}
	
	/**
	 * Prompting message to user
	 */
	public static MenuSelectionObject getSimulationParametersFromUser() {
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Choose jury size (enter: 1 or 2):\n"
						 + "1. 6-member jury \n"
						 + "2. 12-member jury\n");
		int juryChoice = reader.nextInt();
		System.out.println("Choose voting system (enter: 1 or 2):\n"
						 + "1. Unanimous\n"
						 + "2. Majority\n");
		int voteChoice = reader.nextInt();
		System.out.println("Enter number of arguments (enter: 25 to 500):\n");
		int argumentChoice = reader.nextInt();
		System.out.println("Enter number of biased agents (enter: 0 to selected jury size):\n");
		int biasedChoice = reader.nextInt();
		int lowBiasedChoice = 0, highBiasedChoice = 0;
		if(biasedChoice>0) {
			System.out.println("Enter number of agents with low bias (enter: 0 to selected number of biased agents):\n");
			lowBiasedChoice = reader.nextInt();
			System.out.println("Enter number of agents with high bias (enter: 0 to selected number of biased agents):\n");
			highBiasedChoice = reader.nextInt();
		}
		System.out.println("Enter number of simulations: ");
		int numOfSimulations = reader.nextInt(); // Scans the next token of the input as an int.
		//once finished
		reader.close();
		
		MenuSelectionObject mso = new MenuSelectionObject(juryChoice, voteChoice, argumentChoice, biasedChoice, lowBiasedChoice, highBiasedChoice, numOfSimulations);
		return mso;
	}

	public static ArrayList<Argument> generateClaims(int numOfClaims, boolean correctVerdict){
		ArrayList<Argument> claims = new ArrayList<Argument>();
		for(int i=0; i<numOfClaims; i++) {
			claims.add(new Argument(Utils.ArgumentType.CLAIM, !correctVerdict));
		}
		return claims;
	}
}

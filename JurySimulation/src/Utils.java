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
		UNANIMOUS,
		MAJORITY;
	}
	
	/**
	 * Enumerator from the different Bias levels
	 */
	public enum BiasLevel{
		LOW (0.1),
		HIGH (0.5);
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
			acceptanceMap.put(argtype, ThreadLocalRandom.current().nextDouble(0.8, 1.0));
			acceptanceMap.put(ArgumentType.EVIDENCE, ThreadLocalRandom.current().nextDouble(0.4, 0.6));
			acceptanceMap.put(ArgumentType.TESTIMONY, ThreadLocalRandom.current().nextDouble(0.2, 0.4));
			break;
		case TESTIMONY:
			acceptanceMap.put(argtype, ThreadLocalRandom.current().nextDouble(0.8, 1.0));
			acceptanceMap.put(ArgumentType.CLAIM, ThreadLocalRandom.current().nextDouble(0.4, 0.6));
			acceptanceMap.put(ArgumentType.TESTIMONY, ThreadLocalRandom.current().nextDouble(0.2, 0.4));
			break;
		case EVIDENCE:
			acceptanceMap.put(argtype, ThreadLocalRandom.current().nextDouble(0.8, 1.0));
			acceptanceMap.put(ArgumentType.EVIDENCE, ThreadLocalRandom.current().nextDouble(0.4, 0.6));
			acceptanceMap.put(ArgumentType.CLAIM, ThreadLocalRandom.current().nextDouble(0.2, 0.4));
			break;
		default:
		}
		return acceptanceMap;
	}
	
	
	/**
	 * Prompting message to user
	 */
	public static int getSimulationNumberFromuser() {
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter number of simulations: ");
		int numOfSimulations = reader.nextInt(); // Scans the next token of the input as an int.
		//once finished
		reader.close();
		return numOfSimulations;
	}
}

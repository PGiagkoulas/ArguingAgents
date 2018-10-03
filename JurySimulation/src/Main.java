import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Enter number of simulations: ");
		int numOfSimulations = reader.nextInt(); // Scans the next token of the input as an int.
		//once finished
		reader.close();
		int falsePositive = 0, falseNegative = 0;
		
		for(int i=1; i<=numOfSimulations; i++) {
			// initialize a simulation
			Court simulation = new Court();
			//System.out.println(simulation.toString());
			// arguments are presented to the jury
			simulation.provideArguments();
			/*for(Juror j:simulation.getJurorList()) {
				System.out.println("Juror " + j.getId() + " knowledge: ");
				for(Argument a:j.getKnowledge()) {
					System.out.println("Argument " + a.getId() + " supports innocence: " + a.isInnocent());
				}
			}
			System.out.println("\n=========================================================\n");*/
			// deliberation takes place
			simulation.juryDeliberation();
			/*for(Juror j:simulation.getJurorList()) {
				System.out.println("Juror " + j.getId() + " knowledge: ");
				for(Argument a:j.getKnowledge()) {
					System.out.println("Argument " + a.getId() + " supports innocence: " + a.isInnocent());
				}
			}
			System.out.println("\n=========================================================\n");*/
			// voting takes place
			boolean verdict = simulation.juryVote();
			/*System.out.println("Suspect is exonerated: " + verdict);*/
			if(simulation.isCorrectVerdict() && !verdict) {
				falsePositive++;
			}
			else if(!simulation.isCorrectVerdict() && verdict) {
				falseNegative++;
			}
		}
		System.out.println("Out of " + numOfSimulations + " trials, we had " + falseNegative + " false negatives and " + falsePositive + " false positives.");
		
	}

}

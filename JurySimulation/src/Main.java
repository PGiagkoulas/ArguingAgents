
public class Main {
	
	public static void main(String[] args) {
		int numOfSimulations = Utils.getSimulationNumberFromUser();
		int falsePositive = 0, falseNegative = 0, hungJury = 0;
		
		for(int i=1; i<=numOfSimulations; i++) {
//			System.out.println("\n=========================================================\n");
			// initialize a simulation
			Court simulation = new Court(Utils.VoteType.UNANIMOUS, 50, Utils.JurySize.BIG, 1, new int[] {1,0});
			// arguments are presented to the jury
			simulation.provideArguments();			
			// deliberation takes place
			simulation.juryDeliberation();
			System.out.println("\n=========================================================\n");
			//System.out.println("AFTER DELIBERATION");
			// voting takes place
			String verdict = simulation.juryVote();
			if(simulation.isCorrectVerdict() && verdict.equals("guilty") ) {
				falsePositive++;
			}
			else if(!simulation.isCorrectVerdict() && verdict.equals("innocent")) {
				falseNegative++;
			}
			else if(verdict.equals("hung jury")){
				hungJury++;
			}
//			for(Juror j:simulation.getJurorList()) {
//				System.out.println(j.toString());
//			}
			System.out.println("Simulation: " + i + "\n" + simulation.toString());
		}
		System.out.println(String.format("* False Negatives: %d\n"
						 + "* False Positives: %d\n"
						 + "* Hung Juries: %d",
						 falseNegative, falsePositive, hungJury));
	}

}

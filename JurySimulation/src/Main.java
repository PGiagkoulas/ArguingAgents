
public class Main {
	
	public static void main(String[] args) {
		int numOfSimulations = Utils.getSimulationNumberFromuser();
		int falsePositive = 0, falseNegative = 0, hungJury = 0;
		
		for(int i=1; i<=numOfSimulations; i++) {
			// initialize a simulation
			Court sim = new Court(Utils.VoteType.UNANIMOUS, 50, Utils.JurySize.BIG, 3, new int[] {1,2});
			System.out.println(sim.toString());
			// arguments are presented to the jury
			
			System.out.println("\n=========================================================\n");
			// deliberation takes place
			
			System.out.println("\n=========================================================\n");
			// voting takes place
			
			/*System.out.println("Suspect is exonerated: " + verdict);*/
//			if(simulation.isCorrectVerdict() && verdict.equals("guilty") ) {
//				falsePositive++;
//			}
//			else if(!simulation.isCorrectVerdict() && verdict.equals("innocent")) {
//				falseNegative++;
//			}
//			else if(verdict.equals("jung jury")){
//				hungJury++;
//			}
//			System.out.println(simulation.toString());
		}
		
	}

}

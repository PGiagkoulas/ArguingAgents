import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		int numOfSimulations = Utils.getSimulationNumberFromUser();
		int falsePositive = 0, falseNegative = 0, hungJury = 0, correctVerdicts = 0;
		Map<Utils.ArgumentType, Double> totalTrialArgTypeDistr = new HashMap<Utils.ArgumentType, Double>();
		Map<Utils.ArgumentType, Double> totalJuryArgTypeDistr = new HashMap<Utils.ArgumentType, Double>();
		PrintWriter out = new PrintWriter("U12.txt");
		for(int i=1; i<=numOfSimulations; i++) {
			System.out.println("\n=========================================================\n");
			// initialize a simulation
			Court simulation = new Court(Utils.VoteType.UNANIMOUS, 100, Utils.JurySize.BIG, 0, new int[]{0,0});
			System.out.println("Suspect is innocent: " + simulation.isCorrectVerdict());
			// arguments are presented to the jury
			simulation.provideArguments();
			System.out.println("BEFORE DELIBERATION");
			for(Juror j:simulation.getJurorList()) {
				System.out.println("Juror " + j.getId() + " score: " + j.testVoteScore());
			}
			// deliberation takes place
			simulation.juryDeliberation();
			System.out.println("AFTER DELIBERATION");
			for(Juror j:simulation.getJurorList()) {
				System.out.println("Juror " + j.getId() + " score: " + j.testVoteScore());
			}
			// voting takes place
			String verdict = simulation.juryVote();
			
			// stat stuff counting
			Map<Utils.ArgumentType, Double> trialArgTypeDistr = new HashMap<Utils.ArgumentType, Double>(simulation.getTrialArgumentTypeDistribution());
			for(Map.Entry<Utils.ArgumentType, Double> entry : trialArgTypeDistr.entrySet()) {
				if( totalTrialArgTypeDistr.containsKey(entry.getKey()) ) {
					totalTrialArgTypeDistr.put(entry.getKey(), totalTrialArgTypeDistr.get(entry.getKey()) + entry.getValue());
				}
				else {
					totalTrialArgTypeDistr.put(entry.getKey(), entry.getValue());
				}
			}			
			Map<Utils.ArgumentType, Double> juryArgTypeDistr = new HashMap<Utils.ArgumentType, Double>(simulation.getJuryArgumentTypeDistribution());
			for(Map.Entry<Utils.ArgumentType, Double> entry : juryArgTypeDistr.entrySet()) {
				if( totalJuryArgTypeDistr.containsKey(entry.getKey()) ) {
					totalJuryArgTypeDistr.put(entry.getKey(), totalJuryArgTypeDistr.get(entry.getKey()) + entry.getValue());
				}
				else {
					totalJuryArgTypeDistr.put(entry.getKey(), entry.getValue());
				}
			}
			if(simulation.isCorrectVerdict() && verdict.equals("guilty") ) {
				falsePositive++;
			}
			else if(!simulation.isCorrectVerdict() && verdict.equals("innocent")) {
				falseNegative++;
			}
			else if(verdict.equals("hung jury")){
				hungJury++;
			}
			else {
				correctVerdicts++;
			}
			System.out.println(simulation.isTrialVerdict());
		}
		
		for(Map.Entry<Utils.ArgumentType, Double> entry : totalTrialArgTypeDistr.entrySet()) {
			entry.setValue(entry.getValue()/numOfSimulations);
		}
		for(Map.Entry<Utils.ArgumentType, Double> entry : totalJuryArgTypeDistr.entrySet()) {
			entry.setValue(entry.getValue()/numOfSimulations);
		}
		System.out.println(String.format("trial averages: Evidence : %.2f %%  Testimonies: %.2f %%  Claims: %.2f %% \n"
									   + "jury averages: Evidence: %.2f %%  Testimonies: %.2f %%  Claims: %.2f %% \n", 
									   totalTrialArgTypeDistr.get(Utils.ArgumentType.EVIDENCE), 
									   totalTrialArgTypeDistr.get(Utils.ArgumentType.TESTIMONY),
									   totalTrialArgTypeDistr.get(Utils.ArgumentType.CLAIM),
									   totalJuryArgTypeDistr.get(Utils.ArgumentType.EVIDENCE), 
									   totalJuryArgTypeDistr.get(Utils.ArgumentType.TESTIMONY),
									   totalJuryArgTypeDistr.get(Utils.ArgumentType.CLAIM)));
		out.println("\n==================== Accuracy of " + numOfSimulations + " simulations ========================\n");
		System.out.println(String.format("* Wrong Verdicts: %d\n"
						 + "* Hung Juries: %d\n"
						 + "* Correct Verdicts: %d",
						 falseNegative+falsePositive, hungJury, correctVerdicts));
		
		out.close();
	}

}

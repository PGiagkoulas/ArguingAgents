import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		// init statistics stuff
		int falsePositive = 0, falseNegative = 0, hungJury = 0, correctVerdicts = 0;
		double totalAvgClaimSpread = 0, totalAvgNonClaimSpread = 0, avgDeliberations = 0;
		Map<Utils.ArgumentType, Double> totalTrialArgTypeDistr = new HashMap<Utils.ArgumentType, Double>();
		Map<Utils.ArgumentType, Double> totalJuryArgTypeDistr = new HashMap<Utils.ArgumentType, Double>();
		// results file
		PrintWriter out = new PrintWriter("results.txt");
		// number of simulations
		int numOfSimulations = Utils.getSimulationNumberFromUser();
		// go
		for(int i=1; i<=numOfSimulations; i++) {
			System.out.println("\n=========================================================\n");
			// initialize a simulation
			Court simulation = new Court(Utils.VoteType.MAJORITY, 100, Utils.JurySize.BIG, 4, new int[]{2,2});
			System.out.println("Suspect is innocent: " + simulation.isCorrectVerdict());
			// arguments are presented to the jury
			simulation.provideArguments();
			System.out.println("BEFORE DELIBERATION");
			for(Juror j:simulation.getJurorList()) {
				System.out.println("Juror " + j.getId() + " score: " + j.testVoteScore());
			}
			// deliberation takes place
			simulation.juryDeliberation();
			// statistics stuff counting
			totalAvgClaimSpread += simulation.getAvgClaimSpread();
			totalAvgNonClaimSpread += simulation.getAvgNonClaimsSpread();
			avgDeliberations += simulation.getTotalDeliberations();
			
			System.out.println("AFTER DELIBERATION");
			for(Juror j:simulation.getJurorList()) {
				System.out.println("Juror " + j.getId() + " score: " + j.testVoteScore());
			}
			// voting takes place
			String verdict = simulation.juryVote();
			
			// statistics stuff counting
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
		// last calcs on statistics
		totalAvgClaimSpread = totalAvgClaimSpread/numOfSimulations;
		totalAvgNonClaimSpread = totalAvgNonClaimSpread/numOfSimulations;
		avgDeliberations = avgDeliberations/numOfSimulations;
		for(Map.Entry<Utils.ArgumentType, Double> entry : totalTrialArgTypeDistr.entrySet()) {
			entry.setValue(entry.getValue()/numOfSimulations);
		}
		for(Map.Entry<Utils.ArgumentType, Double> entry : totalJuryArgTypeDistr.entrySet()) {
			entry.setValue(entry.getValue()/numOfSimulations);
		}
		// console printing
		System.out.println(String.format("> For an average of %.2f deliberations per simulation: \n"
									   + "* Trial argument averages: Evidence : %.2f %%  Testimonies: %.2f %%  Claims: %.2f %% \n"
									   + "* Jury argument distribution: Evidence: %.2f %%  Testimonies: %.2f %%  Claims: %.2f %% \n"
									   + "* Average claim spread per deliberation round: %.2f\n"
									   + "* Average non-claim spread per deliberation round: %.2f",
									   avgDeliberations,
									   totalTrialArgTypeDistr.get(Utils.ArgumentType.EVIDENCE), 
									   totalTrialArgTypeDistr.get(Utils.ArgumentType.TESTIMONY),
									   totalTrialArgTypeDistr.get(Utils.ArgumentType.CLAIM),
									   totalJuryArgTypeDistr.get(Utils.ArgumentType.EVIDENCE), 
									   totalJuryArgTypeDistr.get(Utils.ArgumentType.TESTIMONY),
									   totalJuryArgTypeDistr.get(Utils.ArgumentType.CLAIM),
									   totalAvgClaimSpread,
									   totalAvgNonClaimSpread));
		System.out.println(String.format("* Wrong Verdicts: %d\n"
						 + "* Hung Juries: %d\n"
						 + "* Correct Verdicts: %d",
						 falseNegative+falsePositive, hungJury, correctVerdicts));
		
		// Output statistics to file
		out.println("\n==================== Accuracy of " + numOfSimulations + " simulations ========================\n");
		out.println(String.format("trial averages: Evidence : %.2f %%  Testimonies: %.2f %%  Claims: %.2f %% \n"
				   + "jury averages: Evidence: %.2f %%  Testimonies: %.2f %%  Claims: %.2f %% \n", 
				   totalTrialArgTypeDistr.get(Utils.ArgumentType.EVIDENCE), 
				   totalTrialArgTypeDistr.get(Utils.ArgumentType.TESTIMONY),
				   totalTrialArgTypeDistr.get(Utils.ArgumentType.CLAIM),
				   totalJuryArgTypeDistr.get(Utils.ArgumentType.EVIDENCE), 
				   totalJuryArgTypeDistr.get(Utils.ArgumentType.TESTIMONY),
				   totalJuryArgTypeDistr.get(Utils.ArgumentType.CLAIM)));		
		out.println(String.format("* Wrong Verdicts: %d\n"
				 + "* Hung Juries: %d\n"
				 + "* Correct Verdicts: %d",
				 falseNegative+falsePositive, hungJury, correctVerdicts));
		
		out.close();
	}

}

import java.util.concurrent.ThreadLocalRandom;

/**
 * Argument class simulates the arguments of the argumentation simulation
 * @author Panagiotis
 *
 */
public class Argument {
	// Is argument exonerating or incriminating
	private boolean innocent;

	/**
	 * Constructor of Argument class with user-specified properties
	 * @param innocent
	 */
	public Argument(boolean innocent) {
		this.innocent = innocent;
	}
	
	/**
	 * Constructor of Argument class with randomly initialized properties
	 */
	public Argument() {
		this.innocent = ThreadLocalRandom.current().nextBoolean();
	}

	/**
	 * Getter of innocent property
	 * @return
	 */
	public boolean isInnocent() {
		return innocent;
	}

	/**
	 * Setter of innocent property
	 * @param innocent
	 */
	public void setInnocent(boolean innocent) {
		this.innocent = innocent;
	}
	
	
}

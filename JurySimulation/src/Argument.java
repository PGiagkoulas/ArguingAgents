import java.util.concurrent.ThreadLocalRandom;

/**
 * Argument class simulates the arguments of the argumentation simulation
 * @author Panagiotis
 *
 */
public class Argument {
	// id of argument
	private int id; // TODO: more elaborate?
	// Is argument exonerating or incriminating
	private boolean innocent;

	// instances counter
	private static int counter = 0;

	/**
	 * Constructor of Argument class with user-specified properties
	 * @param innocent
	 */
	public Argument(boolean innocent) {
		counter++;
		this.id = counter;
		this.innocent = innocent;
	}

	/**
	 * Constructor of Argument class with randomly initialized properties
	 */
	public Argument() {
		counter++;
		this.id = counter;
		this.innocent = ThreadLocalRandom.current().nextBoolean();
	}

	/**
	 * Getter of argument's id
	 * @return int id
	 */
	public int getId() {
		return id;
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

	@Override
	public boolean equals(Object other) {
		if(other instanceof Argument) {
			return this.id == ((Argument)other).getId();
		}
		else {
			return false;
		}
	}
}

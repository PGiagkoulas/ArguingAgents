import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;




/**
 * Argument class simulates the arguments of the argumentation simulation
 * @author Panagiotis
 *
 */
public class Argument {
	// id of argument
	private UUID id; // TODO: more elaborate?
	// Is argument exonerating or incriminating
	private boolean innocent;
	// Argument type
	private Utils.ArgumentType type;
	// instances counter
	private static int counter = 0;

	/**
	 * Constructor of Argument class with user-specified properties
	 * @param innocent
	 */
	public Argument(Utils.ArgumentType type, boolean innocent) {
		counter++;
		this.id = UUID.randomUUID();
		this.type = type;
		this.innocent = innocent;
	}

	/**
	 * Constructor of Argument class with randomly initialized properties
	 */
	public Argument() {
		counter++;
		this.id = UUID.randomUUID();
		this.type = Utils.ArgumentType.getRandomArgumentType();
		this.innocent = ThreadLocalRandom.current().nextBoolean();
	}

	/**
	 * Getter of argument's id
	 * @return int id
	 */
	public UUID getId() {
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
	
	public Utils.ArgumentType getType() {
		return type;
	}

	public void setType(Utils.ArgumentType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof Argument) {
			return this.id.equals(((Argument)other).getId());
		}
		else {
			return false;
		}
	}
}

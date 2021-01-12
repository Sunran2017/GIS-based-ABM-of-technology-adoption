package repast.simphony.distribution;

/**
 * Exception thrown during distribution creation.
 * 
 * @author Nick Collier
 */
public class DistributionCreationException extends RuntimeException {
	
	private static final long serialVersionUID = 2723831058121564838L;

	public DistributionCreationException(String msg) {
		super(msg);
	}
}
package repast.simphony.distribution;

/**
 * Interface for classes that implement random number draws
 * from a particular distribution.
 *
 * @author Nick Collier
 *
 */
public interface RSDistribution {

  /**
   * Gets the next random value.
   *
   * @return the next random value.
   */
  double nextDouble();

  /**
   * Gets the name of the distribution. This is not the
   * distribution type but rather some model specific name like
   * "Truck break down probability".
   *
   * @return the name of the distribution.
   */
  String getName();
}

package repast.simphony.distribution;

import cern.jet.random.AbstractDistribution;

/**
 * Adapts a cern.jet.random.AbstractDistribution to a
 * RSDistribution. Calls AbstractDistribution.nextDouble() to
 * return the next random value.
 *
 * @author Nick Collier
 */
public class SimpleDistribution implements RSDistribution {

  private AbstractDistribution distribution;
  private String name;

  /**
   * Creates a SimpleDistribution.
   *
   * @param name the name of this distribution (e.g. "starting agent count").
   * @param distribution the distribution this simple distribution wraps.
   */
  public SimpleDistribution(String name, AbstractDistribution distribution) {
    this.distribution = distribution;
    this.name = name;
  }

  /**
   * Gets the name of the distribution.
   *
   * @return the name of the distribution.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the next random value.
   *
   * @return the next random value.
   */
  public double nextDouble() {
    return distribution.nextDouble();
  }
}

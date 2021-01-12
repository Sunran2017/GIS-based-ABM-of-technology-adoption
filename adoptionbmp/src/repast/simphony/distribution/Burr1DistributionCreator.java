package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a distribution which can return random numbers from the
 * Burr II, VII, VIII and X distributions.
 *
 * @author Nick Collier
 */
public class Burr1DistributionCreator extends AbstractDistributionCreator {

  private static class Distribution implements RSDistribution {

    RandomEngine generator;
    String name;
    double r;
    int nr;

    private Distribution(String name, RandomEngine generator) {
      this.generator = generator;
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      return Distributions.nextBurr1(r, nr, generator);
    }
  }

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {

    Distribution d = new Distribution(strValue("name", params), generator);
    d.r = doubleValue("r", params);
    d.nr = intValue("nr", params);
    return d;
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Burr1";
  }
}
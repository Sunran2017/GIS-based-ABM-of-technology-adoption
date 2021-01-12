package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a power law  RSDistribution
 *
 * @author Nick Collier
 */
public class PowLawDistributionCreator extends AbstractDistributionCreator {

  private static class Distribution implements RSDistribution {

    RandomEngine generator;
    String name;
    double alpha, cut;

    private Distribution(String name, RandomEngine generator) {
      this.generator = generator;
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      return Distributions.nextPowLaw(alpha, cut, generator);
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
    d.alpha = doubleValue("alpha", params);
    d.cut = doubleValue("cut", params);
    return d;
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "PowerLaw";
  }
}
package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Cauchy RSDistribution.
 *
 * @author Nick Collier
 */
public class CauchyDistributionCreator extends AbstractDistributionCreator {

  private static class Distribution implements RSDistribution {

    RandomEngine generator;
    String name;

    private Distribution(String name, RandomEngine generator) {
      this.generator = generator;
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      return Distributions.nextCauchy(generator);
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
    return new Distribution(strValue("name", params), generator);
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Cauchy";
  }
}
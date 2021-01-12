package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Lambda RSDistribution
 *
 * @author Nick Collier
 */
public class LambdaDistributionCreator extends AbstractDistributionCreator {

  private static class Distribution implements RSDistribution {

    RandomEngine generator;
    String name;
    double l3, l4;

    private Distribution(String name, RandomEngine generator) {
      this.generator = generator;
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      return Distributions.nextLambda(l3, l4, generator);
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
    d.l3 = doubleValue("l3", params);
    d.l4 = doubleValue("l4", params);
    return d;
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Lambda";
  }
}
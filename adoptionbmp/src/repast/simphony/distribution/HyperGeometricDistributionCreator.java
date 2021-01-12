package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.HyperGeometric;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a HyperGeometric RSDistribution.
 *
 * @author Nick Collier
 */
public class HyperGeometricDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    int N = intValue("N", params);
    int s = intValue("s", params);
    int n = intValue("n", params);
    return new SimpleDistribution(strValue("name", params), new HyperGeometric(N, s, n, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "HyperGeometric";
  }

}
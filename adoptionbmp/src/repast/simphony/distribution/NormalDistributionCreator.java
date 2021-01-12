package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Normal RSDistribution.
 *
 * @author Nick Collier
 */
public class NormalDistributionCreator extends AbstractDistributionCreator {

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Normal";
  }

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    double mean = doubleValue("mean", params);
    double std = doubleValue("std", params);
    return new SimpleDistribution(strValue("name", params), new Normal(mean, std, generator));
  }
}
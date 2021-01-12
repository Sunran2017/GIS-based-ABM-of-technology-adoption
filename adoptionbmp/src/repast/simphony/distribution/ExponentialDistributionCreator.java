package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Exponential;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates an Exponential RSDistribution.
 *
 * @author Nick Collier
 */
public class ExponentialDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    double lambda = doubleValue("lambda", params);
    return new SimpleDistribution(strValue("name", params), new Exponential(lambda, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Exponential";
  }

}
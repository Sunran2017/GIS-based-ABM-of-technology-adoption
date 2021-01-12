package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Beta;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Beta RSDistribution.
 *
 * @author Nick Collier
 */
public class BetaDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
  	double alpha = doubleValue("alpha", params);
    double beta = doubleValue("beta", params);
    String name = strValue("name", params);
    return new SimpleDistribution(name, new Beta(alpha, beta, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Beta";
  }

}
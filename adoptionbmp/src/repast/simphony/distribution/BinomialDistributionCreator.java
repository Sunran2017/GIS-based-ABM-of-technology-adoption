package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Binomial;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Binomial RSDistribution.
 *
 * @author Nick Collier
 */
public class BinomialDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    int n = intValue("n", params);
    double p = doubleValue("p", params);
    return new SimpleDistribution(strValue("name", params), new Binomial(n, p, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Binomial";
  }

}
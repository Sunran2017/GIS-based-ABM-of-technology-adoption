package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a uniform RSDistribution.
 *
 * @author Nick Collier
 */
public class UniformDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    double min = doubleValue("min", params);
    double max = doubleValue("max", params);
    return new SimpleDistribution(strValue("name", params), new Uniform(min, max, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Uniform";
  }

}

package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.VonMises;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a VonMises RSDistribution.
 *
 * @author Nick Collier
 */
public class VonMisesDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    double freedom = doubleValue("freedom", params);
    return new SimpleDistribution(strValue("name", params), new VonMises(freedom, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "VonMises";
  }
}
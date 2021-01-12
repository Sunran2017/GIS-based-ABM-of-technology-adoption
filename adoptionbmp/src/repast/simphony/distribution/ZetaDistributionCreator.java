package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Zeta;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Zeta RSDistribution.
 *
 * @author Nick Collier
 */
public class ZetaDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    double ro = doubleValue("ro", params);
    double pk = doubleValue("pk", params);
    return new SimpleDistribution(strValue("name", params), new Zeta(ro, pk, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Zeta";
  }
}
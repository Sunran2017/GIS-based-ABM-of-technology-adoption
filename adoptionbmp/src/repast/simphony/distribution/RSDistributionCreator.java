package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.engine.RandomEngine;

/**
 * Interface for classes that create specific RSDistributions.
 *
 * @author Nick Collier
 */
public interface RSDistributionCreator {

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName();

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params the parameters used to create the RSDistribution
   *
   * @return the created RSDistribution.
   */
  RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params);
}

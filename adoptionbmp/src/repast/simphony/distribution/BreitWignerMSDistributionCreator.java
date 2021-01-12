package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.BreitWignerMeanSquare;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a BreitWignerMeanSquare RSDistribution.
 *
 * @author Nick Collier
 */
public class BreitWignerMSDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
  	double mean = doubleValue("mean", params);
    double gamma = doubleValue("gamma", params);
    double cut = doubleValue("cut", params);
    return new SimpleDistribution(strValue("name", params), new BreitWignerMeanSquare(mean, gamma, cut, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "BreitWignerMeanSquare";
  }

}
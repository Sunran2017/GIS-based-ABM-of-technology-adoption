package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.StudentT;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a StudentT RSDistribution.
 *
 * @author Nick Collier
 */
public class StudentTDistributionCreator extends AbstractDistributionCreator {

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {
    double freedom = doubleValue("freedom", params);
    return new SimpleDistribution(strValue("name", params), new StudentT(freedom, generator));
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "StudentT";
  }
}
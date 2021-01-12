package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Weibull RSDistribution.
 *
 * @author Nick Collier
 */
public class WeibullDistributionCreator extends AbstractDistributionCreator {

  private static class WeibullDistribution implements RSDistribution {

    RandomEngine generator;
    String name;
    double alpha, beta, location;

    private WeibullDistribution(String name, RandomEngine generator) {
      this.generator = generator;
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      return ((beta * Math.pow( - Math.log(1.0 - generator.raw()), 1.0 / alpha)) + location);
    }
  }

  /**
   * Creates an RSDistribution using the specified generator and parameters.
   *
   * @param generator the stream generator
   * @param params    the parameters used to create the RSDistribution
   * @return the created RSDistribution.
   */
  public RSDistribution createRSDistribution(RandomEngine generator, Map<String, String> params) {

    WeibullDistribution wd = new WeibullDistribution(strValue("name", params), generator);
    wd.alpha = doubleValue("alpha", params);
    wd.beta = doubleValue("beta", params);
    wd.location = 0;
    if (params.get("location") != null) {
      wd.location = doubleValue("location", params);
    }
    return wd;
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Weibull";
  }
}
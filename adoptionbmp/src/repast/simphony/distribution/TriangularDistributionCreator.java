package repast.simphony.distribution;

import java.util.Map;

import cern.jet.random.Distributions;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a Triangular RSDistribution.
 *
 * @author Nick Collier
 */
public class TriangularDistributionCreator extends AbstractDistributionCreator {

  private static class TriangularDistribution implements RSDistribution {

    double min, max;
    RandomEngine generator;
    String name;

    private TriangularDistribution(double min, double max, String name,
                                   RandomEngine generator) {
      this.min = min;
      this.max = max;
      this.generator = generator;
      this.name = name;
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      double tri = Distributions.nextTriangular(generator);

			return tri * 0.5 * (min-max) + (min+max)/2;
    }
  }

  private static class TriangularDistribution2 implements RSDistribution {

    double min, max, mode, range, condition;
    Uniform uni;
    String name;

    private TriangularDistribution2(double min, double max, double mode, String name,
                                   RandomEngine generator) {
      this.min = min;
      this.max = max;
      this.mode = mode;
      uni = new Uniform(generator);
      this.name = name;
      this.range = max - min;
      this.condition = (mode - min) / (max - min);
    }

    public String getName() {
      return name;
    }


    public double nextDouble() {
      double val = uni.nextDouble();
      if (val <= condition) {
        return (min + Math.sqrt(val * range * (mode - min)));
      } else {
        return (max - Math.sqrt((1.0 - val) * range * (max - mode)));
      }
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
    double min = doubleValue("min", params);
    double max = doubleValue("max", params);
    String sMode = params.get("mode");
    if (sMode != null && sMode.length() > 0) {
      double mode = doubleValue("mode", params);
      return new TriangularDistribution2(min, max, mode, strValue("name", params), generator);
    } else {
      return new TriangularDistribution(min, max, strValue("name", params), generator);
    }
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Triangular";
  }
}
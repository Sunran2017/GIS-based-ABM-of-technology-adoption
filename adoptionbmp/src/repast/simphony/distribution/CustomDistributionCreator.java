package repast.simphony.distribution;

import java.util.Map;
import java.util.StringTokenizer;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * Creates a custom RSDistribution where
 * the user supplies a map of probabilities and values.
 *
 * @author Nick Collier
 */
public class CustomDistributionCreator extends AbstractDistributionCreator {


  static class CustomDistribution implements RSDistribution {

    private String name;
    private double[] values, probs;
    private Uniform uniform;

    CustomDistribution(String name, double[] probs, double[] values, RandomEngine generator) {
      this.name = name;
      this.probs = probs;
      this.values = values;
      uniform = new Uniform(0, 1, generator);

    }

    public String getName() {
      return name;
    }

    public double nextDouble() {
      double val = uniform.nextDouble();
      int i = 0;
      while (val > probs[i] && i < probs.length) i++;
      return values[i];
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
    String sProbs = params.get("probabilities");
    String sVals = params.get("values");

    StringTokenizer tok = new StringTokenizer(sProbs.trim(), " ");
    int tokenCount = tok.countTokens();
    double[] probs = new double[tokenCount];
    for (int i = 0; i < tokenCount; i++) {
      probs[i] = Float.valueOf(tok.nextToken());
    }

    tok = new StringTokenizer(sVals.trim(), " ");
    tokenCount = tok.countTokens();
    double[] vals = new double[tokenCount];
    for (int i = 0; i < tokenCount; i++) {
      vals[i] = Float.valueOf(tok.nextToken());
    }

    // calculate the cumulative probabilites.
    for (int i = 1; i < probs.length; i++) {
      probs[i] += probs[i - 1];
    }

    return new CustomDistribution(strValue("name", params), probs, vals, generator);
  }

  /**
   * Gets the name of the distribution this creates.
   *
   * @return the name of the distribution this creates.
   */
  public String getDistributionName() {
    return "Custom";
  }

}
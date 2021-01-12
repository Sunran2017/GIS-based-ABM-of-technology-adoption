package repast.simphony.distribution;

import java.util.HashMap;
import java.util.Map;

import cern.jet.random.engine.RandomEngine;

/**
 * Factory for creating RSDistributions.
 *
 * @author Nick Collier
 */
public class RSDistributionFactory {

  private static RSDistributionFactory factory = new RSDistributionFactory();

  /**
   * Creates an RSDistribution using the specified dom element and
   * random engine.
   *
   * @param element the dom element
   * @param generator the engine that supplies the random stream.
   *
   * @return the created RSDistribution.
   */
  public static RSDistribution createDistribution(String type, Map<String, String> attributes, RandomEngine generator) {
  	return factory.doCreateDistribution(type, attributes, generator);
  }

  private Map<String, RSDistributionCreator> creatorMap = new HashMap<String, RSDistributionCreator>();

  private RSDistributionFactory() {
    RSDistributionCreator creator = new UniformDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new NormalDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new TriangularDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new WeibullDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new BetaDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new BinomialDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new BreitWignerDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new ChiSquareDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new ExponentialDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new ExponentialPowDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new GammaDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new HyperGeometricDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);

    creator = new LogarithmicDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new NegativeBinomialDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new PoissonDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);

    creator = new StudentTDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new VonMisesDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new ZetaDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);

    creator = new Burr1DistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new Burr2DistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new CauchyDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);

    creator = new ErlangDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new GeometricDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new LambdaDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);

    creator = new LaplaceDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new LogisticDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new PowLawDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
    creator = new ZipfIntDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);

    creator = new CustomDistributionCreator();
    creatorMap.put(creator.getDistributionName(), creator);
  }

  private RSDistribution doCreateDistribution(String type, Map<String, String> attributes, RandomEngine generator) {
    RSDistributionCreator creator = creatorMap.get(type);
    if (creator == null) throw new DistributionCreationException("Unknown distribution type '" + type + "'");
    return creator.createRSDistribution(generator, attributes);
  }
}

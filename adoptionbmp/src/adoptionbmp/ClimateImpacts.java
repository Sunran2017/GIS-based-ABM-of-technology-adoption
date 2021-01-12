package adoptionbmp;

public class ClimateImpacts {

	private double fractionChangePerDegreeC              = 0;
	private double fractionDecreasePer10mmPrecipitation  = 0;
	
	public ClimateImpacts(double frcChangePerDegreeC, double frcDecreasePer10mmPrecip){
		fractionChangePerDegreeC             = frcChangePerDegreeC;
		fractionDecreasePer10mmPrecipitation = frcDecreasePer10mmPrecip;
				
	}
	
	public double getFractionChangePerDegreeC(){
	  return fractionChangePerDegreeC;	
	}
	
	public double getFractionDecreasePer10mmPrecipitation(){
	  return fractionDecreasePer10mmPrecipitation;
	}
		
}

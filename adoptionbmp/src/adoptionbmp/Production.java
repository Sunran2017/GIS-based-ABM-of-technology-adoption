package adoptionbmp;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class Production {
	
	//static Logger logger = Logger.getLogger(class.getCanonicalName());
	
	public  int 						currentYear;
	
	//Climate
	public static Map<Integer, Double> 					temps = new HashMap<>();				// average temp
	public static Map<Integer, Double> 					precips = new HashMap<>();				// total precipitation
	
	
	//Crop prices
	public static Map<Integer, Double> 					cornPrice = new HashMap<>();			// corn price ($/bushel)
	public static Map<Integer, Double>					soyPrice= new HashMap<>();				// soybean price ($/bushel)
	public static Map<Integer, Double>					wheatPrice = new HashMap<>();			// average wheat price ($/bushel)

	
	
	//Crop costs
	public static Map<Integer, Double> 					cornCost = new HashMap<>();			// corn costs ($/acre)
	public static Map<Integer, Double>					soyCost= new HashMap<>();			// soybean costs ($/acre)
	public static Map<Integer, Double>					wheatCost = new HashMap<>();		// wheat costs ($/acre)

		
	//
	
	public static List<Integer> 						cornAcre = new ArrayList<>();		//corn acre
	public static List<Integer> 						soyAcre = new ArrayList<>();		//soy acre
	public static List<Integer> 						wheatAcre = new ArrayList<>();		//wheat acre
	
	
	enum crops {corn, soy, wheat}
	
	
	public static double curCornYields;
	public static double curSoyYields;
	public static double curWheatYields;
	
	public static double curCornPrice;
	public static double curSoyPrice;
	public static double curWheatPrice;
	
	public static double deltaCornYields;
	public static double deltaSoyYields;
	public static double deltaWheatYields;
	
	public static double curRevCorn;
	public static double curRevSoy;
	public static double curRevWheat;
	
	public static double curCornCost;
	public static double curSoyCost;
	public static double curWheatCost;
	
	
	public static double minCornRatio;
	public static double maxCornRatio;
	
	public static double minSoyRatio;
	public static double maxSoyRatio;
	
	public static double minWheatRatio;
	public static double maxWheatRatio;
	

	static int shortTerm = 3 ;				// price adjusted for shortTerm stock impacts
	static int medTerm = 5;				//
	static int	longTerm = 10;				//
	
	public static int yr = 5 ;  // from when to consider adjusted price;
	public static int term = shortTerm;
			

	
	public static void getProductionData(int currentYear, int term) {
		
		//Current Climate;
		double curTemp = temps.get(currentYear);
		double curPrecip = precips.get(currentYear);	
				
		//Current Crop prices;		
		curCornPrice = cornPrice.get(currentYear);
		curSoyPrice = soyPrice.get(currentYear);
		curWheatPrice = wheatPrice.get(currentYear);
		
		// after specific year, prices will be adjusted based on effects of stock first and random generated using short term crop prices sd
		
		
		if(currentYear > ContextCreator.startYear + yr) {
						
			int index = currentYear - ContextCreator.startYear - 1;
			double deltaCorn = (cornAcre.get(index) - cornAcre.get(index - term)) * Constants.deltaPriceCorn;
			curCornPrice = curCornPrice * (1 + deltaCorn/100);
					
			double deltaSoy = (soyAcre.get(index) - soyAcre.get(index - term)) * Constants.deltaPriceSoy;
			curSoyPrice = curSoyPrice * (1 + deltaSoy/100);
					
			double deltaWheat = (wheatAcre.get(index) - wheatAcre.get(index - term)) * Constants.deltaPriceWheat;
			curWheatPrice = curWheatPrice * (1 + deltaWheat/100);
			
			//calculate moving price standard deviation;
			double sdCornPrice = calculateSD(cornPrice, currentYear);
			double sdSoyPrice = calculateSD(soyPrice, currentYear);
			double sdWheatPrice = calculateSD(wheatPrice, currentYear);
			
			// if exceeding ratio
			
			curSoyPrice = Math.max(curSoyPrice, Constants.minSoyToCornPrice);
			
			//random generating crop prices after 10 years from starting year
			
			curCornPrice = Sup.getNormalDouble(curCornPrice, sdCornPrice);
			curSoyPrice = Sup.getNormalDouble(curSoyPrice, sdSoyPrice);
			curWheatPrice = Sup.getNormalDouble(curWheatPrice, sdWheatPrice);
			
					
			//System.out.println("Adjusted corn price : " + curCornPrice + " at " + currentYear);
			
		}
				
		//Current Crop yields and crop yields in CDSI;
		curCornYields = Sup.getNormalDouble(Constants.avgCornYields, Constants.devCornYields);
		curSoyYields =  Sup.getNormalDouble(Constants.avgSoyYields, Constants.devSoyYields);
		curWheatYields = Sup.getNormalDouble(Constants.avgWheatYields, Constants.devWheatYields);
				
		//cur yields adjusted by precipitation and temp
		deltaCornYields = Constants.betaCornPrecip * (curPrecip - Constants.avgPrecip)
							+ Constants.betaCornTemp * (curTemp - Constants.avgTemp);
				
		deltaSoyYields = Constants.betaSoyPrecip * (curPrecip - Constants.avgPrecip)
							+ Constants.betaSoyTemp * (curTemp - Constants.avgTemp);
				
		deltaWheatYields = Constants.betaWheatPrecip * (curPrecip - Constants.avgPrecip)
							+ Constants.betaWheatTemp * (curTemp - Constants.avgTemp);
			
			
		//Current Crop costs calculation: consider difference between various farm 95% confidence intervene [0.5, 1.5]
		curCornCost = Production.cornCost.get(currentYear) * (1 + Constants.optionalCornCostRatio) * Sup.getNormalDouble(1, 0.2);
		curSoyCost = Production.soyCost.get(currentYear) * (1 + Constants.optionalSoyCostRatio) * Sup.getNormalDouble(1, 0.2);
		curWheatCost = Production.wheatCost.get(currentYear) * (1 + Constants.optionalWheatCostRatio) * Sup.getNormalDouble(1, 0.2);
			
		//consider climate change impact on crop revenue:
					
		if(ContextCreator.climateChange) {
			curRevCorn = Production.curCornPrice * Production.curCornYields * (1 + Production.deltaCornYields);
			curRevSoy = Production.curSoyPrice * Production.curSoyYields * (1 + Production.deltaSoyYields);
			curRevWheat = Production.curWheatPrice * Production.curWheatYields * (1 + Production.deltaWheatYields);
		}else {
			curRevCorn = Production.curCornPrice * Production.curCornYields;
			curRevSoy = Production.curSoyPrice * Production.curSoyYields;
			curRevWheat = Production.curWheatPrice * Production.curWheatYields;
		}
			
		//CropRatio adjusted by crop price 
			
			
		minCornRatio = Constants.consCornEff + Production.curCornPrice * Constants.minCornEff ;
		maxCornRatio = Constants.consCornEff + Production.curCornPrice * Constants.maxCornEff ;
			
		if(minCornRatio <= Constants.minCornRatio || minCornRatio >= Constants.maxCornRatio ) {
			minCornRatio = Constants.minCornRatio;
		}
		if(maxCornRatio >= Constants.maxCornRatio || maxCornRatio <= Constants.minCornRatio) {
			maxCornRatio = Constants.maxCornRatio;
		}
			
			 
		/*
		minCornRatio = Constants.minCornRatio;
		maxCornRatio = Constants.maxCornRatio;
		*/
			
			
		/*
		minSoyRatio = Constants.consSoyEff + curSoyPrice * Constants.minSoyEff ;
		maxSoyRatio = Constants.consSoyEff + curSoyPrice * Constants.maxSoyEff ;
			
		if(minSoyRatio <= Constants.minSoyRatio || minSoyRatio >= Constants.maxSoyRatio) {
			minSoyRatio = Constants.minSoyRatio;
		}
			
		if(maxSoyRatio >= Constants.maxSoyRatio || maxSoyRatio <= Constants.minSoyRatio) {
			maxSoyRatio = Constants.maxSoyRatio;
		}
		*/
		
		minSoyRatio = Constants.minSoyRatio;
		maxSoyRatio = Constants.maxSoyRatio;
		
			
		minWheatRatio = Constants.consWheatEff + Production.curWheatPrice * Constants.minWheatEff ;
		maxWheatRatio = Constants.consWheatEff + Production.curWheatPrice * Constants.maxWheatEff ;
			
		if(minWheatRatio <= Constants.minWheatRatio || minWheatRatio >= Constants.maxWheatRatio ) {
			minWheatRatio = Constants.minWheatRatio;
		}
			
		if(maxWheatRatio >= Constants.maxWheatRatio || maxWheatRatio <= Constants.minWheatRatio) {
			maxWheatRatio = Constants.maxWheatRatio;
		}
				
	}
	
	//calculate Sd for last several years
	public static double calculateSD(Map<Integer, Double> map, int currentYear){
		List<Double> list = new ArrayList<Double>();
		for(int i = 0; i < yr; i++) {
			list.add(map.get(currentYear - i));	
		}
		
        double sum = 0.0, standardDeviation = 0.0;
        int length = list.size();

        for(double num : list) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: list) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
	
	
	
}
	
	
	



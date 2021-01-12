/**
 * 
 */
package adoptionbmp;


/**
 * Constants used by the adoption model.
 * 
 *
 */
public interface Constants {
	
	
	
	double acreToM2 = 4046.86;
	double phi = 3.1415;
	
	
	// alpha and beta for yields estimation:
	
	
	double betaCornPrecip =  -0.0000473;
	double betaCornTemp = -0.0308024;
	double alphaCorn = 5.425592;
	
	
	double betaSoyPrecip = -0.0000501 ;
	double betaSoyTemp = 0.0065519;
	double alphaSoy =  3.789805 ;
	
	
	double betaWheatPrecip =  -0.0002313;
	double betaWheatTemp  = -0.0015467 ;
	double alphaWheat =  4.531703;	
	
	
	// 1969-2019 average precipitation and temp 
	
	double avgPrecip = 879.6763;
	double avgTemp = 9.51053;
	
	
	//price adjusted to last year seeded acre
	
	double deltaPriceCorn = 0.0000164;
	double deltaPriceSoy = -4.98e-07;
	double deltaPriceWheat = -4.21e-06; 
	
	
	
	//soy to corn ratio
	
	double minSoyToCornPrice = 1.814;
	double maxSoyToCornPrice = 4.395;
	
	//wheat to corn ratio
	
	double minWheatToCornPrice = 0.602;
	double maxWheatToCornPrice = 3;
	
	// yields
	
	double avgCornYields = 160.4267 ;
	double devCornYields = 17.4697;
	
	double avgSoyYields = 45.8467;
	double devSoyYields = 5.24634;
	
	double avgWheatYields = 76.9;
	double devWheatYields = 8.7174;
					
	
	
	
	//Yields deviation/mean ratio
	
	double sdParamCorn = 0.1089;
	double sdParamSoy = 0.1144;
	double sdParamWheat = 0.1134;

	
	
	
	//crops seeded acre ratio
	
	double avgCropAcreRatio = 0.92;
	double devCropAcreRatio = 0.09;
			
	
	
	//Yields difference between FD and CDSI;
	double diffCornYields = 0.04;
	double diffSoyYields = 0.04;
	double diffWheatYields = 0.04;
	double diffYields = 0.04;
	
	//Input difference between FD and CDSI;
	double diffWaterUse = 0.20;
	double diffFertilizer = 0.05;
	double diffLabor = 0.1;
	
	//Water management cost
	
	double	fixedCostCDSI = 41.05;	// total fixed costs of CDSI($/acre)
	double	fixedCostFD = 30.40;	// total fixed costs of FD($/acre)
		
	//Input prices
	
	double 	meanFertilizerPrice = 10;		//fertilizerPrice;
	double 	devFertilizerPrice = 2;
	double 	meanLaborPrice = 20;				//laborPrice;
	double  devLaborPrice = 2;
	double 	meanWaterPrice = 30;				//waterPrice;
	double  devWaterPrice = 2;
		
	//acre seeded ratio
	
	
	double avgCornRatio = 0.18;
	double devCornRatio=0.04;
	double avgSoyRatio = 0.64;
	double devSoyRatio = 0.05;
	double avgWheatRatio = 0.18;
	double devWheatRatio = 0.05;
	
	
	double minCornRatio = 0.08;      //    99% confidence level
	double maxCornRatio = 0.28;
	
	double minSoyRatio = 0.10;    // 0.51 for county
	double maxSoyRatio = 0.76;
	
	double minWheatRatio = 0.10;
	double maxWheatRatio = 0.75;  // 0.3 for county
	
	double minCornEff =  0.006542; // Coefficients of crop seeded land percentage and crop prices C
	double maxCornEff = 0.0299577;
	double consCornEff = 0.1077175 ;
	
	
	double minSoyEff =  -0.0190758; // Coefficients of crop seeded land percentage and crop prices C
	double maxSoyEff =  0.0020753;
	double consSoyEff = 0.7307317;
	
	double minWheatEff = -0.038886; // Coefficients of crop seeded land percentage and crop prices C
	double maxWheatEff = 0.0020472;
	double consWheatEff =  0.2852566;
	
	
	
	
	//CDSI investment return rate
	
	double r = 0.0495;
	
	
	//investment cost
	
	double invCostLowManual = 62.82;
	double invCostMedManual = 84.16;
	double invCostHighManual= 153.6;
	
	double invCostLowAuto = 188.95;
	double invCostMedAuto = 266.14;
	double invCostHighAuto= 531.30;
	
	double invCostLowRc = 330.42;
	double invCostMedRc = 463.07;
	double invCostHighRc= 931.35;
	
	
	double annualCostLowManual = 1.52;
	double annualCostMedManual = 2.37;
	double annualCostHighManual = 5.33 ;
	
	
	double annualCostLowAuto = 0.58 ;
	double annualCostMedAuto = 0.91;
	double annualCostHighAuto = 2.04;
	
	double annualCostLowRc = 25.62 ;
	double annualCostMedRc = 39.85;
	double annualCostHighRc = 89.62;
	
	
	//CDSI land suitability level
	
	int landLevel = 6;
	
	
	double avgProfitMargin = 0.167;  //  cents per $
	double highProfitMargin = 0.3;
	double floorProfitMargin = 0.05;
	
			
	int lifeExpect = 25;
	
	
	
	//interest rate variation;
	
	double optionalCornCostRatio = 0.20;
	double optionalSoyCostRatio = 0.15;
	double optionalWheatCostRatio = 0.15;
	
	
	//loan ratio
	
	double invLoanRatio1 = 0.9;
	double invLoanRatio2 = 0.8;
	double invLoanRatio3 = 0.7;
	double invLoanRatio4 = 0.6;
	double invLoanRatio5 = 0.5;


	
	//retire
	
	double avgRetireAge = 70;
	double devRetireAge = 2;
	int ageGap = 20;
	int avgAge = 58;
	

	// save file path
	
	String pathProduction = "/Users/ran/ABM/agentsProductionResults.csv";   		// production results path
	
	String pathAdoption1 = "/Users/ran/ABM/adoptionResults1.csv";					// adoption results path
	String pathAdoption2 = "/Users/ran/ABM/adoptionResults2.csv";					// adoption results path
	String pathAdoption3 = "/Users/ran/ABM/adoptionResults3.csv";					// adoption results path
	
}

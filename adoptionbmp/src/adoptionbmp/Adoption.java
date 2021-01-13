package adoptionbmp;

public class Adoption {
	
	//CDSI type
	enum typeCDSI {manual, auto, remoteControl};
	static int type = 1;
	

	
	//CDSI investment costs
	
	static double[] manual = {Constants.invCostHighManual, Constants.annualCostLowManual, 
            Constants.invCostMedManual, Constants.annualCostMedManual,
            Constants.invCostLowManual, Constants.annualCostLowManual};

	static double[] auto = {Constants.invCostHighAuto, Constants.annualCostHighAuto, 
            Constants.invCostMedAuto, Constants.annualCostMedAuto,
            Constants.invCostLowAuto, Constants.annualCostLowAuto};
	
	static double[] remoteControl = {Constants.invCostHighRc, Constants.annualCostHighRc, 
            Constants.invCostMedRc, Constants.annualCostMedRc,
            Constants.invCostLowRc, Constants.annualCostLowRc};
	
	
	
	

}

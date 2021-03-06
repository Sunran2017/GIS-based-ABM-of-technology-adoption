package adoptionbmp;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import repast.simphony.engine.schedule.ScheduledMethod;




/**
 * Here Agent represents a farmer.
 * 
 *Autonomous agents with various characteristics;
 *
 * 
 * Agents consider adoption decision following two steps:
 * 
 * 1, land distribution to grow corn, wheat and soy;
 * 2, adoption decision to innovative water management system CDSI;
 *
 * 
 * 
 * 
 * @author Ran Sun
 *
 */
public class Agent {
	
	//Farmer characteristics
	public String					id;					// unique id number for each farmer	
	public Coordinate 				coord;				// farmer's x,y location
	
	public boolean                  sex;				// 0 if male; 1 if female
	public int 						age;				// Age of the farmer
	public int 						edu;				// Education of the farmer
	public int						hhtype;				// Household type
	public boolean					tenure;				// true if the farmer owns the land
	public String					type;				// farmer's type
	public double 					farmSize  ; 		// Farm size
	public boolean					active = true; 			// true if active, false if close
	
	
	double 							totalInvCost;		//total investment cost for each farm
	
	
	//calculation by lp_solver
	
	public Map<Integer, Double>		revenue = new HashMap<>(); 			//Revenue in current year
	public Map<Integer, Double>		profit = new HashMap<>();   		//profit in current year
	public Map<Integer, Double>		profitUpdate = new HashMap<>(); 	//profit after CDSI adoption
	

	
	
	//age varation
	
	public Map<Integer, Integer> curAge = new HashMap<>();           //Age in current year;
	
	//Adoption decision
	
	public boolean 					CDSIPotential = false ;		//True if the land is feasible for CDSI
	public boolean					adoptedFD = false ;	         // True if adopted FD
	public double 					fdAcres;
		
	public boolean					adoptedCDSI = false;	// True if adopted CDSI
	
	public int						adoptedYear = 0;		//The year when adoptedCDSI
	
	
	public boolean 					netAdopt;			//false if non-adoption within networking, true if adopted by network 
	
	public Color					myColor;    		// color of this farmer
	public Color					myBorderColor; 		//border color of the farmer depending on the personality type
	
	//year
	public Integer					currentYear;		//currentYear in production
	public Integer					curYear; 			// currentYear in adoption
	public Integer					endYear = 2049;
	
	public  static final double    	nA             = -9999999;
	
	
	
	public Agent(String id,Coordinate coord) {
		this.id = id;  
		this.coord = coord;
	}
	
	public void setColor () { 		// set agents' initial color based on adoption:
		if( adoptedFD )
			myColor = Color.black;	
		if( CDSIPotential )
			myColor = Color.red;
	}
	
	//Production Linear Programming by lp_solver
	//Step 1 :Calculate optimal Acre seeded each year for corn, soy and wheat using LP_solve;
	
	public double cornAcre = 0;
	public double soyAcre = 0;
	public double wheatAcre = 0;
	
	
	public void production(int currentYear, int term) throws Exception {
		
		//prepare production data
		Production.getProductionData(currentYear, term);
	

		//lp solver
	
		/**
		 * lp_solver
		 * variables:   cornAcre   soyAcre  wheatAcre   
		 * 
		 * max P = cornPrice * cornYields * cornAcre 
		 * 			+ soyPrice * soyYields*soyAcre 
		 * 			+ wheatPrice * wheatYields*wheatAcre 
		 * 
		 * 
		 * CAD / Bushel 
		 * Bushel/Acre
		 * 
		 * subject to: 1 + 3 * 3 crops = 10 constraints
		 * 
		 * cornAcre + soyAcre + wheatAcre <= FarmSize
		 * cornAcre >= minCornRatio * land
		 * cornAcre <= maxCornRatio * land
		 * cornAcre >=0
		 * 
		 */
		
		
		// set up obj array;
		
		double[] curObj =  new double[3] ;
		curObj[0] = Production.curRevCorn - Production.curCornCost;	
		curObj[1] = Production.curRevSoy - Production.curSoyCost;
		curObj[2] = Production.curRevWheat - Production.curWheatCost;
		
		if(Integer.valueOf(id) % 100 ==0) {
			System.out.println("Start simulation for this agent :" + id + " at "+ currentYear);	
		}
		
		
		// TODO Auto-generated method stub
			try {
		      // Create a problem with 3 variables and 10 constraints
		      LpSolve solver = LpSolve.makeLp(0, 3);
		      // set objective function
		      //solver.setObjFn(curObj);
		      //print objective function
		      //System.out.println("objective function: " + curObj[0]+ "x + " + curObj[1]+ "y + " + curObj[2] + "z" +"\n");
		      
		      
		      String a = String.valueOf(curObj[0]);
		      String b = String.valueOf(curObj[1]);
		      String c = String.valueOf(curObj[2]);
			
		      String str1 = new String(a + " " + b + " " + c);
		      //System.out.println(str1);
		      
		      //set objective fn
		      solver.strSetObjFn(str1);
		     
		      // Set max objective
		      solver.setMaxim();
		      
		      // add constraints 1-4
		      solver.strAddConstraint("1 1 1",  LpSolve.LE, farmSize);
		      solver.strAddConstraint("1 0 0",  LpSolve.GE, 0);
		      solver.strAddConstraint("0 1 0",  LpSolve.GE, 0);
		      solver.strAddConstraint("0 0 1",  LpSolve.GE, 0);
		      solver.strAddConstraint("1 0 0 ", LpSolve.GE, farmSize * Production.minCornRatio);
		      solver.strAddConstraint("1 0 0 ", LpSolve.LE, farmSize * Production.maxCornRatio);
		      solver.strAddConstraint("0 1 0 ", LpSolve.GE, farmSize * Production.minSoyRatio);
		      solver.strAddConstraint("0 1 0 ", LpSolve.LE, farmSize * Production.maxSoyRatio);
		      solver.strAddConstraint("0 0 1 ", LpSolve.GE, farmSize * Production.minWheatRatio);
		      solver.strAddConstraint("0 0 1 ", LpSolve.LE, farmSize * Production.maxWheatRatio);
		    
		   
		      // solve the problem
		      solver.solve();

		      // print solution
		      //System.out.println("Value of objective function: " + solver.getObjective());
		      
		      double[] var = solver.getPtrVariables();
		      double pft = solver.getObjective();
		      
		      double cornProfitPerAcre =  curObj[0];
		      double soyProfitPerAcre =  curObj[1];
		      double wheatProfitPerAcre =  curObj[2];
		      
		      double rev =  Production.curRevCorn * var[0] + Production.curRevSoy * var[1] + Production.curRevWheat * var[2];
		      
		      revenue.put(currentYear, rev);
		      profit. put(currentYear, pft);
		      cornAcre = var[0];
		      soyAcre = var[1];
		      wheatAcre = var[2];
		      
		      
		      /**print 
		      solver.printSolution(1);
		      
		      for (int i = 1; i < var.length; i++) {  
		        System.out.println("Value of var[" + i + "] = " + var[i]);
		      }
		      
		      System.out.println("curCornCost = " + curCornCost +"\n"
		    		  +"curSoyCost = " + curSoyCost +"\n"
		    		  +"curWheatCost = " + curWheatCost +"\n"
		    		  );
		      
		      /** print 

		    ["id","year",
		    "cornAcre","soyAcre","wheatAcre",
            "profit","revenue",
            "cornProfit","soyProfit","wheatProfit",
            "cornRev","soyRev","wheatRev","active"]
		     */
		      // writeText
		      String writeContext = id + "," + currentYear 
		    		+ "," + var[0] + "," + var[1] + "," + var[2] 
		    		+ "," + pft
		    		+ "," + revenue.get(currentYear)
		    		+ "," + cornProfitPerAcre
		    		+ "," + soyProfitPerAcre
		    		+ "," + wheatProfitPerAcre
		    		+ "," + Production.curRevCorn * var[0]
		    		+ "," + Production.curRevSoy * var[1]	
		    		+ "," + Production.curRevWheat * var[2]
		    		+ "," + active;
		      
		      writeText(Constants.pathProduction, writeContext);	       
		      
		      // delete the problem and free memory
		      
		      solver.deleteLp();
		    }
		    catch (LpSolveException e) {
		       e.printStackTrace();
		    }	
		}		

	
	//Step 2: Adoption;	

	@ScheduledMethod(start = 1, interval = 1)
	public void adoptionDecision() throws Exception { 	
		//FD diffusion 
		diffusionFD();
		
		//Base Scenario
		//basescenario(0);
	
		//adoption considering environmental awareness
		//adoptionWithEnv();
		
		
		//costShare and low-interest program
		
		costShare(ContextCreator.costShare, ContextCreator.intrSup);
		
		
		//Extension
		if(curYear < ContextCreator.startYear + 4 && ContextCreator.extension != 0) {
			Extension(ContextCreator.extension);	
		}
		
		//initial installation cost share
		
		//Interest support
		/*
		for(int i = 1; i <=10; i++) {
			costSharescenario(0,i);
		}
		*/

		
		//Base Adoption Networking Scenario:
		adoptionWithNet();	
		
		curYear += 1;	
	}
		
	
	
	
	
	

	

	//Base scenario with calculating npv and deciding adoption without network
	
	public void basescenario(int cnt) throws Exception {
		Map<Integer, Double> npv  = new HashMap<Integer, Double>();	
		
		if(Adoption.type == 1) {
			npv = this.calNpv(Adoption.manual,0,0);
			adoption(npv, "manual",String.valueOf(cnt));
		}else if(Adoption.type == 2) {
			npv = this.calNpv(Adoption.auto,0,0);
			adoption(npv, "auto",String.valueOf(cnt));
		}else {
			npv = this.calNpv(Adoption.remoteControl,0,0);
			adoption(npv, "remoteControl",String.valueOf(cnt));	
		}	
	}
	
	
	//Network
	private void adoptionWithNet() {
		if(!adoptedCDSI && (this.profit.get(curYear)/this.revenue.get(curYear) > Constants.highProfitMargin) 
				&& this.rateCDSIwithNet() > Constants.CDSIRateThreshold) {
			adoptedCDSI = true;	
		}
	}

	
	
	//cost-share and low interest rate scenario  
	
	public void costShare(int cnt1, int cnt2) throws Exception{
		Map<Integer, Double> npv  = new HashMap<Integer, Double>();	
		if(Adoption.type == 1) {
			npv = this.calNpv(Adoption.manual,Policy.shareRate[cnt1], Policy.intrSup[cnt2]);
			adoption(npv,"manual", String.valueOf(cnt1) +"+" + String.valueOf(cnt2));
		}else if(Adoption.type == 2) {
			npv = this.calNpv(Adoption.auto,Policy.shareRate[cnt1],Policy.intrSup[cnt2]);
			adoption(npv, "auto",String.valueOf(cnt1) +"+" + String.valueOf(cnt2));
		}else {
			npv = this.calNpv(Adoption.remoteControl,Policy.shareRate[cnt1],Policy.intrSup[cnt2]);
			adoption(npv, "remoteControl",String.valueOf(cnt1) +"+" + String.valueOf(cnt2));	
		}	
	}
		
	
	//Extension scenario 
	private void Extension(int extension) {
		double mean = 0;
		
		if (extension == 1) {
			mean = Constants.meanExt1;
		}else if(extension == 2) {
			mean = Constants.meanExt2;
		}else if (extension ==3) {
			mean = Constants.meanExt3;
		}else if(extension == 5) {
			mean = Constants.meanExt5;
		}else if(extension == 7) {
			mean = Constants.meanExt7;
		}
		
		
		else {
			System.out.print("Extension scenario can not be initialized");
		}
		
		double extPar = Sup.getNormalDouble(mean,1);
		
		if(!adoptedCDSI && extPar > 0 
			&& (this.profit.get(curYear)/this.revenue.get(curYear) > Constants.highProfitMargin)) {
			adoptedCDSI = true;	
		}		
	}

	
	
	
	
	
	
	
	
	
	
	

	// Calculate npv of CDSI investment
	public  Map<Integer, Double> calNpv(double[] arr, double shareRate, double investSubRate) throws Exception {

		Map<Integer, Double> npv = new HashMap<Integer, Double>();
		
		double invReturn = 0;
		double invCostPerAcre = 0;
		double annualCostPerAcre = 0;
		int remainYear = endYear - curYear;
		
		if(farmSize < 10) {
			invCostPerAcre = arr[0];
			annualCostPerAcre  = arr[1];	
			
		}else if(farmSize >= 10 && farmSize < 23) {
			invCostPerAcre = arr[2];
			annualCostPerAcre  = arr[3];
		}else if(farmSize >= 23) {
			invCostPerAcre = arr[4];
			annualCostPerAcre  = arr[5];
		}		
		
		totalInvCost = invCostPerAcre * farmSize * (1 - shareRate);
		
		if(remainYear >= Constants.lifeExpect) {
			for(int i = curYear; i <= curYear + Constants.lifeExpect; i++) {
				//System.out.println( id + "\n"+ "profit from "+ i + "\n"+ profit.get(i));
				if(profit.get(i) != null && profit.get(i) >=0) {
					invReturn += Constants.diffYields * profit.get(i)/Math.pow(1 + Constants.investReturnRate * (1-investSubRate), i-curYear);			
				}else {
					invReturn += Constants.diffYields * revenue.get(i)/Math.pow(1 + Constants.investReturnRate * (1-investSubRate), i-curYear);	
				}
			}

			invReturn = invReturn - annualCostPerAcre * Constants.lifeExpect * farmSize;
			npv.put(curYear, invReturn-totalInvCost);
			
		}else {
			int cnt = 0;
			double avgReturn = 0;
			for(int j = curYear; j <= endYear; j++) {
				if(profit.get(j) != null && profit.get(j) >=0) {
					invReturn += Constants.diffYields * profit.get(j)/Math.pow(1 + Constants.investReturnRate - (1-investSubRate)*Constants.loanRate, j-curYear);
				}else {
					invReturn += Constants.diffYields * revenue.get(j)/Math.pow(1 + Constants.investReturnRate - (1-investSubRate)*Constants.loanRate, j-curYear);	
				}
				cnt +=1;
			}
			
			avgReturn = invReturn/cnt;
			invReturn = invReturn + avgReturn * (Constants.lifeExpect - remainYear) - (annualCostPerAcre * Constants.lifeExpect) * farmSize ;
			npv.put(curYear, invReturn - totalInvCost);	
		}
		
		//System.out.println(id + "\n" + "npv from " + curYear + "\n" + npv.get(curYear));
		return npv;

	}
	
	//Adoption base:
	public void adoption(Map<Integer, Double> npv,String str, String name) {
		
		double curNpv = npv.get(curYear);
		
	
		// adoption condition based on operation
		double profitMargin = profit.get(curYear)/revenue.get(curYear);
		
		if(CDSIPotential && adoptedFD && curNpv > 0 && profitMargin > Constants.highProfitMargin && profit.get(curYear) > totalInvCost ) {
			adoptedCDSI = true;
			if(adoptedYear == 0) {
				adoptedYear = curYear;	
			}		
		}
		
		try {
			writeAdoption(Constants.pathAdoption1 + name +  ".csv", curNpv,adoptedYear, adoptedCDSI);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	
	//Adoption with learning costs:
	private void adoptionWithEnv() {
		// TODO Auto-generated method stub
		
	}
	
	

	public void writeAdoption(String path, double curNpv, double adoptedYear, boolean adoptedCDSI) throws IOException{
		
		String writeContext = id + "," + curYear + "," + adoptedCDSI 
				+ "," + curNpv
				+ "," + profit.get(curYear)
				+ "," + farmSize
				+ "," + adoptedYear
				+ "," + edu
				+ "," + age
				+ "," + tenure;
		
		writeText(path, writeContext);		
		
		/*
		System.out.println(id + ":" + curYear 
				+ "\n"+ "profit:" + profit.get(curYear) 
				+ "\n"+ "npv:" + npv.get(curYear)
				+ "\n"+ "CDSI adoption:" + adoptedCDSI
				+ "\n"+ "Adopted Year:" + adoptedYear);
		*/
		
	}
	
	
	
	
	private void diffusionFD() {
		if(!adoptedFD && (this.profit.get(curYear)/this.revenue.get(curYear) > Constants.highProfitMargin) 
				&& this.rateFDwithNet() > Constants.FDRateThreshold) {
			adoptedFD = true;	
		}	
	}
	
	
	public double rateFDwithNet() {
		 List<Agent> netList= ContextCreator.neighbourMap.get(this);
		 double rate = 0;
		 
		 if(netList == null || netList.size() == 0) {
			 return rate; 
		 }
		 
		 int cnt = 0;
		 for(int i = 0; i < netList.size(); i++) {
			 if(netList.get(i).adoptedFD) { 
				 cnt ++;
			 }	  
		 }
		 rate= cnt / netList.size();
		 
		 return rate;
	}
	
	
	public double rateCDSIwithNet() {
		 List<Agent> netList= ContextCreator.neighbourMap.get(this);
		 double rate = 0;
		 
		 if(netList == null || netList.size() == 0) {
			 return rate; 
		 }
		 
		 int cnt = 0;
		 for(int i = 0; i < netList.size(); i++) {
			 if(netList.get(i).adoptedCDSI) { 
				 cnt ++;
			 }	  
		 }
		
		 rate = cnt / netList.size();
		 return rate;
	}

	
	
	
	public String getId(){
		return id;
	}

	
	public void getOlder() {
		this.age += 1;
	}
	
	
	public boolean getCDSIPotential() {
		return this.CDSIPotential;
	}
	
	public boolean getFD() {
		return this.adoptedFD;
	}
	
	public boolean isAdoptedCDSI() {
		return this.adoptedCDSI;
	}
	
	public void setAdoptedCDSI(boolean AdoptedCDSI) { 
		this.adoptedCDSI = AdoptedCDSI; 
	}	

	public double getfarmSize() { 	
		return this.farmSize;
	}
	
	public void setfarmSize(double size) { 
		this.farmSize = size;
	}
	
	
	public void writeText(String filename, String writeContext) {
		
		try {
	    	  //true = append file
	    	  FileWriter fileWritter = new FileWriter(filename, true);
	    	  BufferedWriter writeText = new BufferedWriter(fileWritter);
	    	  writeText.newLine();
	    	  writeText.write(writeContext); 
	    	  writeText.close();
	    	  //System.out.println("Write Done");
			}catch(FileNotFoundException e){
				System.out.println("cant find file");
			}catch(IOException e){
				System.out.println("file error");		  
	      }	
	}
	
	

	

}









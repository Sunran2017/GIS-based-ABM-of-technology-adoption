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
	public Map<Integer, Double>	 	npv ;								//Net present value of CDSI investment
	
	
	//age varation
	
	public Map<Integer, Integer> curAge = new HashMap<>();           //Age in current year;
	
	//Adoption decision
	
	public boolean 					CDSIPotential = false ;		//True if the land is feasible for CDSI
	public boolean					adoptedFD = false ;	         // True if adopted FD
	public double 					fdAcres;
		
	public boolean					adoptedCDSI = false;	// True if adopted CDSI
	
	public int						adoptedYear;		//The year when adoptedCDSI
	
	
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
		
		System.out.println("Start simulation for this agent :" + id + " at "+ currentYear);
		
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
		
		if(!adoptedFD && (this.profit.get(curYear)/this.revenue.get(curYear) > Constants.highProfitMargin) 
						&& this.rateFDwithNet() > Constants.FDRateThreshold) {
			adoptedFD = true;	
		}
		//Base adoption
		baseSenario(0);
		
		//Base Adoption Networking Scenario: including Learning costs
		adoptionWithLearningCosts();
		
		
		//Policy 
		if(Policy.policySenario == 1) {
			for(int i = 1; i <= 10;i++) {
				costShareSenario(i);
			}	
		}else if (Policy.policySenario == 2) {
			for(int i = 1; i <= 10;i++) {
				costShareSenario(i);
			}
		}
		
		curYear += 1;	
	}
	
	
	
	
	
	
	//Base scenario 1calculate npv and decide adoption without network
	
	public void baseSenario(int cnt) throws Exception {
		if(Adoption.type == 1) {
			this.calNpv(Adoption.manual,0,0);
			adoption("manual",cnt);
		}else if(Adoption.type == 2) {
			this.calNpv(Adoption.auto,0,0);
			adoption("auto",cnt);
		}else {
			this.calNpv(Adoption.remoteControl,0,0);
			adoption("remoteControl",cnt);	
		}	
	}
	
	
	
	//cost-share scenario  
	
	public void costShareSenario(int cnt) throws Exception{
		
		if(Adoption.type == 1) {
			this.calNpv(Adoption.manual,Policy.shareRate[cnt],0);
			adoption("manual",cnt);
		}else if(Adoption.type == 2) {
			this.calNpv(Adoption.auto,Policy.shareRate[cnt],0);
			adoption("auto",cnt);
		}else {
			this.calNpv(Adoption.remoteControl,Policy.shareRate[cnt],0);
			adoption("remoteControl",cnt);	
		}	
	}
	
	//interest rate support scenario
	public void intrSupSenario(int cnt) throws Exception{

		if(Adoption.type == 1) {
			this.calNpv(Adoption.manual,0,Policy.intrSup[cnt]);
			adoption("manual",cnt);
		}else if(Adoption.type == 2) {
			this.calNpv(Adoption.auto,0,Policy.intrSup[cnt]);
			adoption("auto",cnt);
		}else {
			this.calNpv(Adoption.remoteControl,0,Policy.intrSup[cnt]);
			adoption("remoteControl",cnt);	
		}	
		
		
		
	}
	
	
	
	
	

	// Calculate npv of CDSI investment
	public void calNpv(double[] arr, double shareRate,double investSubRate) throws Exception {

		double invReturn = 0;
		double invCostPerAcre = 0;
		double annalCostPerAcre = 0;
		npv = new HashMap<>();
		int remainYear = endYear - curYear;
		
		if(farmSize < 10) {
			invCostPerAcre = arr[0];
			annalCostPerAcre  = arr[1];	
			
		}else if(farmSize >= 10 && farmSize < 23) {
			invCostPerAcre = arr[2];
			annalCostPerAcre  = arr[3];
		}else if(farmSize >= 23) {
			invCostPerAcre = arr[4];
			annalCostPerAcre  = arr[5];
		}		
		
		totalInvCost = invCostPerAcre * farmSize * (1 - shareRate);
		
		if(remainYear >= Constants.lifeExpect) {
			for(int i = curYear; i <= curYear + Constants.lifeExpect; i++) {
				//System.out.println( id + "\n"+ "profit from "+ i + "\n"+ profit.get(i));
				if(profit.get(i) != null) {
					invReturn += Constants.diffYields * profit.get(i)/Math.pow(1 + Constants.investReturnRate * (1-investSubRate), i-curYear);			
				}
			}

			invReturn = invReturn - annalCostPerAcre * Constants.lifeExpect * farmSize;
			npv.put(curYear, invReturn-totalInvCost);
			
		}else {
			int cnt = 0;
			double avgReturn = 0;
			for(int j = curYear; j <= endYear; j++) {
				if(profit.get(j) != null) {
					invReturn += Constants.diffYields * profit.get(j)/Math.pow(1 + Constants.investReturnRate * (1-investSubRate), j-curYear);
								
				}	
				cnt +=1;
			}
			
			avgReturn = invReturn/cnt;
			invReturn = invReturn + avgReturn * (Constants.lifeExpect - remainYear) - annalCostPerAcre  * Constants.lifeExpect * farmSize ;
	
			npv.put(curYear, invReturn - totalInvCost);	
		}

		//System.out.println(id + "\n" + "npv from " + curYear + "\n" + npv.get(curYear));
	}
	
	//Adoption base:
	public void adoption(String str, int cnt) {
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
			writeAdoption(Constants.pathAdoption1 + String.valueOf(cnt) +  ".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	
	//Adoption with learning costs:
	private void adoptionWithLearningCosts() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	

	public void writeAdoption(String path) throws IOException{
		
		String writeContext = id + "," + curYear + "," + adoptedCDSI 
				+ "," + npv.get(curYear)
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
		 int cnt = 0;
		 for(int i = 0; i < netList.size(); i++) {
			 if(netList.get(i).adoptedCDSI) { 
				 cnt ++;
			 }	  
		 }
		 double rate = cnt / netList.size();
		 
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









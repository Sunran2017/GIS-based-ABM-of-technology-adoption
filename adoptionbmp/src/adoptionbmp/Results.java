package adoptionbmp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import adoptionbmp.Agent;

public class Results {
	
    
    public double			initialAdoptedBMP = 0;		// proportion of farmers that already adopted BMP initially

    public int				adoptionState = 0;			// adoption status of every farmer
    
    public int 				numberAdoptedBMP;			// total # of farms that adopted BMP
    
 // instance variables for aggregate measures
 	public int				numberAdopted;  	    // number of farmers who adopted the practice
 	public double			percentAdopted;  	    // percentage of farmers who adopted the practice
 	public double			avgFarmerAge ;			// average farmer age 
 	public double			avgFarmSize ;			// average farm size for traditional farmers
    
    
 	public double getInitialAdoptedBMP () { return initialAdoptedBMP; }
    
 	
 	
 // instance variables for model "structures"
 	public static ArrayList<Agent>   farmerList = new ArrayList<Agent> ();	// arraylist for farmers
    
    
    
 // getters for aggregate measures
	
 	public int getNumberAdoptedBMP() 
 	{		int n_adopted = 0;	
 		for ( Agent agent : farmerList ) {  // look for every farmer and check if they adopted none of the practices
 			if (agent.isAdoptedCDSI())  
 				n_adopted++;  // if they did increase number of adopted
 		}		
 		return n_adopted;
 	}  
 	
 	
 	
    
}

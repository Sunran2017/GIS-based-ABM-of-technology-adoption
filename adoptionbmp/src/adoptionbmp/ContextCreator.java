package adoptionbmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.gis.util.GeometryUtil;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.graph.Network;

import repast.simphony.query.space.gis.GeographyWithin;

/**
 * @author Ran Sun : 
 * Department of Agricultural and Resource Economics 
 * University of Saskatchewan
 * 
 */

public class ContextCreator implements ContextBuilder<Object> {

	int numAgents = 933;					//total number of farm at starting year 933
	int period 	= 34;						//2016-2049 : 34
	static int startYear = 2016;			//start year at 2016
	static int dist = 1000;     			// the distance for deciding neighbor 
	static boolean  climateChange = false;  // whether consider the impacts of climate change on yields
	static int costShare = 0;				// costShare program scenarios
	static int intrSup = 0;					//interest credits program scenarios
	static int policySenario = 0;
	

	//networking
	ArrayList<Agent> agentList = new ArrayList<>();
	static Map<Agent, List<Agent>> neighbourMap = new HashMap<Agent, List<Agent>>();
	int neighbourCount;
	
	
	@Override
	public Context<Object> build(Context<Object> context) {
		
		//generate output file for multiple run
		
		try {
			Sup.fileCreate(Constants.pathProduction);	
			Sup.fileCreate(Constants.runRecord);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		/*
		try {
			Sup.fileCheck(Constants.pathProduction);
			//Sup.fileCheck(Constants.pathAdoption1);
			//Sup.fileCheck(Constants.pathAdoption2);
			//Sup.fileCheck(Constants.pathAdoption3);
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		*/
		
		
		//create the geography using a factory to make sure that it is initialized
		GeographyParameters geoParams = new GeographyParameters();		
		GeographyFactory factory = GeographyFactoryFinder.createGeographyFactory(null);	
		Geography geography = factory.createGeography("Geography", context, geoParams);			
		GeometryFactory fac = new GeometryFactory();
		
		// envelope of the geography
		ReferencedEnvelope env = new ReferencedEnvelope(-83.126279741, -82.430442990, 42.9013587, 41.909384337, DefaultGeographicCRS.WGS84);
		
		
		// Load Features from shapefiles
		Sup.loadFeatures( "./data/EssexSimplified/EssexSimplified.shp", context, geography);
		Sup.loadFeatures2( "./data/TILE_DRAIN/TILE_DRAINAGE_AREA_ESSEX.shp", context, geography);
		System.out.printf( "==> shapefile load succuss...\n" );
		
		
		//create network
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("Network", context, true);
		Network<Object> net = netBuilder.buildNetwork();
		
		
		//Load crop price and yields data;		
		List<String[]> cropData = Sup.loadFile("./data/production.csv");
		
		for (int i = 0;i < cropData.size()-1; i++) {
			int year = Integer.parseInt(cropData.get(i+1)[0]);
			double temp = Double.parseDouble(cropData.get(i+1)[1]);
			double precip = Double.parseDouble(cropData.get(i+1)[2]);
			
			double cornPrice = Double.parseDouble(cropData.get(i+1)[3]);
			double soyPrice = Double.parseDouble(cropData.get(i+1)[4]);
			double wheatPrice = Double.parseDouble(cropData.get(i+1)[5]);
			
			double cornCost = Double.parseDouble(cropData.get(i+1)[6]);
			double soyCost = Double.parseDouble(cropData.get(i+1)[7]);
			double wheatCost = Double.parseDouble(cropData.get(i+1)[8]);
					
			Production.temps.put(year,temp);
			Production.precips.put(year, precip);
			Production.cornPrice.put(year, cornPrice);
			Production.soyPrice.put(year, soyPrice);
			Production.wheatPrice.put(year, wheatPrice);
					
			Production.cornCost.put(year, cornCost);
			Production.soyCost.put(year, soyCost);
			Production.wheatCost.put(year, wheatCost);
		}		
		
		System.out.println("==> Production data loading successly" );
		
		//Load agent data
		List<String[]> agentData = Sup.loadFile("./data/farmer.csv");

		// Create the agents from the collection of random coords and constructing spatial network	
		String boundaryFilename = "./data/Essex/essexShapefile.shp";
		List<SimpleFeature> features = Sup.loadFeaturesFromShapefile(boundaryFilename);
		
		int cnt = 0;
		
		Collections.shuffle(features);	
		
		for (int i = 0;i < numAgents; i++) {
			
			// Create an area in which to create agents.  This border is loaded from a shapefile.
			Geometry boundary = (MultiPolygon)features.get(i).getDefaultGeometry();
	
			// Generate random points in the area to create agents.
			List<Coordinate> agentCoords = GeometryUtil.generateRandomPointsInPolygon(boundary, numAgents);		
			Coordinate coord = agentCoords.get(i) ;
			Agent agent = new Agent("Site " + cnt,coord);
			
			agent.id = agentData.get(i+1)[0];
			agent.sex = Boolean.parseBoolean(agentData.get(i+1)[1]);
		    agent.age = Integer.parseInt(agentData.get(i+1)[2]);
		    agent.edu = Integer.parseInt(agentData.get(i+1)[3]);
		    agent.hhtype = Integer.parseInt(agentData.get(i+1)[4]);
		    agent.farmSize = Double.parseDouble(agentData.get(i+1)[5]);
		    agent.tenure = Boolean.parseBoolean(agentData.get(i+1)[6]);
		    
		    agent.currentYear = startYear;
		    agent.curYear = startYear;
		
			context.add(agent);
			agentList.add(agent);
			
			Point geom = fac.createPoint(coord);
			geography.move(agent, geom);
		
			GeographyWithin distance = new GeographyWithin(geography, Math.sqrt(agent.farmSize * Constants.acreToM2/Constants.phi) + dist, agent);
			
			List<Agent> list = new LinkedList<>();
			
			for (Object o : distance.query()) {
                if (o != null &&  o instanceof Agent){
                	list.add((Agent)o); 
                	net.addEdge(agent, o);
                }
			}
			
			neighbourMap.put(agent, list);
			//int neighbourCount = neighbourMap.get(agent).size();
			
			/*
			System.out.printf("agent.id :" + agent.id + "\n" 
							+ net.getAdjacent(agent) + "\n" 
							+ "Number of Neighbour : " + neighbourCount + "\n");
							
			System.out.printf(neighbourMap.get(agent) + "Number of Neighbour" + "\n" + neighbourCount + "\n");
			*/
			if(cnt % 100 == 0) {
				System.out.printf( "==> agent create succussfully..."+ agent.getId()+ "\n");
			}
			
			cnt++;			
		}	
		
		//production decision with drop-out
		for(int tk = startYear; tk <= startYear + period - 1; tk++) {
			int sumCornAcre = 0;
			int sumSoyAcre = 0;
			int sumWheatAcre = 0;
			for(Agent agent : agentList) {
				//if agent is active at current year;	
				try { 
					agent.production(tk, Production.term);
					
					if(Integer.parseInt(agent.id) % 100 == 0 && tk == 34) {
						System.out.println("Complete production calculation for agent:" + agent.id + " at " + tk + "\n");	
					}
					
					sumCornAcre += agent.cornAcre;
					sumSoyAcre += agent.soyAcre;
					sumWheatAcre += agent.wheatAcre;
				} catch (Exception e) {
						e.printStackTrace();
				}
				
				/*
				String writeYields =  tk
	    				+ "," + Production.curCornYields
	    				+ "," + Production.deltaCornYields
	    				+ "," + Production.curSoyYields
	    				+ "," + Production.deltaSoyYields
	    				+ "," + Production.curWheatYields
	    				+ "," + Production.deltaWheatYields
						+ "," + Production.curCornPrice
						+ "," + Production.curSoyPrice
						+ "," + Production.curWheatPrice;
						
				agent.writeText("./data/yields.csv", writeYields);		
				*/	
			}
			
			Production.cornAcre.add(sumCornAcre);
			Production.soyAcre.add(sumSoyAcre);
			Production.wheatAcre.add(sumWheatAcre);
				
			//create list for saving closedAgent
			List<Agent> closedAgent = new ArrayList<Agent>();
			
			int cnt1 = 0;  	//cnt for closeAgent because of low profit;
			int cnt2 = 0; 	//cnt for closeAgent because of retirement;
			int cnt3 = 0;   //cnt for retired agent handled to next generation;
			
			//drop-out decision next year;
			
			for(Agent agent: agentList) {
				//consider whether to drop-out based on two years' operation, so starting from second year;
				if(tk >= startYear + 1) {
					double profit = agent.profit.get(tk);
					double profitLast = agent.profit.get(tk-1);
					
					if(profit + profitLast < 0) {
						//sell to buyer: consider potential buyer
						Agent buyer = getBuyer(agent,tk);
						
						if(buyer != null) {
							System.out.printf("Agent: " + agent.id + " decide to close and sell to buyer id:" + buyer.id + " at " + tk + "\n");
							agent.active = false;
							closedAgent.add(agent);
							buyer.farmSize += agent.farmSize;
							cnt1 ++;
							
						}else {
							System.out.println("decide to close but no buyer for:" + agent.id + " at " + tk + "\n");
						}	
					}else {
						//System.out.println("keep operation:" + agent.id + " at the next year of " +  tk + "\n");
					}					
				}
				
				//consider whether to drop-out based on operators' age	
				
				double retireAge = Sup.getNormalDouble(Constants.avgRetireAge, Constants.devRetireAge);
				
				/*hhtype:
				 * 
				 * 8: Non-family households: One person only
				 * 
				 * 
				 */
				
				if(agent.age >= retireAge && agent.hhtype == 8){
					//sell farm
					Agent buyer = getBuyer(agent,tk);
					
					if(buyer != null) {
						System.out.printf("Agent: " + agent.id + " retired and sell to buyer id:" + buyer.id + " at " + tk + "\n");
						agent.active = false;
						closedAgent.add(agent);
						buyer.farmSize += agent.farmSize;
						cnt2 ++;
					}else {
						System.out.printf("Agent: " + agent.id + " retired and change operator:"  + " at " + tk + "\n");
						agent.age = Constants.avgAge;
					}
				}else if(agent.age >= retireAge && agent.hhtype == 2) {
					System.out.println("retired and handle farm to next generation:" + agent.id + " at " + tk + "\n");
					cnt3 ++;
					agent.age -= 20;
				}else {
					//System.out.println("keep working:" + agent.id + " at the next year of " +  tk + "\n");	
				}
				agent.curAge.put(tk, agent.age);
				agent.getOlder();	
			}
			
			
			//delete close agent from context, agentList and neighbourMap, both in key and values.
			for(int i = 0; i < closedAgent.size();i++) {
				Agent a = closedAgent.get(i);
				context.remove(a);
				agentList.remove(a);
				neighbourMap.remove(a);
				
				for(Map.Entry<Agent, List<Agent>> entry : neighbourMap.entrySet()) {
					Agent key = entry.getKey();
					if(entry.getValue().contains(a)) {
						int index =  neighbourMap.get(key).indexOf(a);
						neighbourMap.get(key).remove(index);
					}		
				}
			}
							
			if(tk == 2049) {
				System.out.println("Total number of agent : " + agentList.size() + " at " + tk + "\n");
				String writeList =  String.valueOf(agentList.size())
									+ "," + climateChange
									+ "," + dist
									+ "," + costShare
									+ "," + intrSup ;
				Sup.writeText(Constants.runRecord,writeList );			
			}
			
			
			//System.out.println("closedAgent number : " + closedAgent.size() + " at " + tk + "\n");
			//System.out.println("closedAgent number because of low profit : " + cnt1 + " at " + tk + "\n");
			//System.out.println("closedAgent number because of retirement : " + cnt2 + " at " + tk + "\n");	
			//System.out.println("number of retired and handle farm to next generation : " + cnt3 + " at " + tk + "\n");
		}

		
		
		/**
		 * CDSI feasibility includes CDSI potential check based on soil type and FD check (the base technology needed)
		 * Since reading GIS layer information may cost a long time and memory, so instead of running the following 
		 * procedure each time for simulation, the list of agents with CDSI will be printed out after the procedure 
		 * and be read into agents' attributes.
		 * 
		 * 
		 * 
		 **/
		
		/*
		 
		//check CDSI potential for Agents;	
		for(Object instance: context.getObjects(ZoneCDSIPotential.class)) {
			if(instance instanceof ZoneCDSIPotential) {
				ZoneCDSIPotential cur = (ZoneCDSIPotential) instance;
				cur.checkCDSIPotential(context);
			}
		}
		
		//check whether agents have adopted FD;
		for(Object instance: context.getObjects(ZoneFD.class)) {
			if(instance instanceof ZoneFD) {
				ZoneFD cur = (ZoneFD) instance;
				cur.checkFD(context);
			}
		}
		
		
		*/
		
		//Load adoptedFD data
		List<String[]> adoptedFD = Sup.loadFile("./data/adoptedFD.csv");
		ArrayList<String> FDlist = new ArrayList<>();
		
		int lenFD = adoptedFD.size();
		for(int i = 1; i < lenFD; i++) {
			FDlist.add(adoptedFD.get(i)[0]);
		}
		
		//Load CDSIPotential data
		
		List<String[]> fitCDSI = Sup.loadFile("./data/CDSIPotential.csv");
		ArrayList<String> CDSIPotentialList = new ArrayList<>();
		
		int lenCD = fitCDSI.size();
		for(int i = 1; i < lenCD; i++) {
			CDSIPotentialList.add(fitCDSI.get(i)[0]);
		}
		
		for(Agent agent: agentList) {
			
			if(FDlist.contains(agent.id)) {
				agent.adoptedFD = true;
			}
			
			if(CDSIPotentialList.contains(agent.id)) {
				agent.CDSIPotential = true;
			}
		}
		
		
		Parameters params = RunEnvironment.getInstance().getParameters();

		
		//2016 -- 2049: 34 years
		RunEnvironment.getInstance().endAt(period);	
		
		
		return context;
			
	}

	

	//Agent get buyer
	private Agent getBuyer(Agent agent, int tk) {
		Map<Agent,Double> neighbourProfit = new HashMap<Agent, Double>();
		Agent buyer = null ;
		
		if(!neighbourMap.get(agent).isEmpty()) {
			for(Agent neighbour : neighbourMap.get(agent)) {
				
				double rev = neighbour.revenue.get(tk);
				double profit = neighbour.profit.get(tk);
				double operatingMargin = profit / rev;
				
				if(profit > 0 && operatingMargin > Constants.highProfitMargin && rev > agent.revenue.get(tk)) {
					neighbourProfit.put(neighbour, operatingMargin);
				}
			}	
			//System.out.println("Agent id: " + agent.id + " has neighbourProfitMap" + neighbourProfit +"\n");
		}
	
		if(!neighbourProfit.isEmpty()) {
			List<Entry<Agent,Double>> list = new ArrayList<Entry<Agent, Double>>(neighbourProfit.entrySet());	
			Collections.sort(list, (o1, o2) -> (o1.getValue().intValue() - o2.getValue().intValue()));
			buyer = list.get(list.size() - 1).getKey();	
			System.out.println("buyer from current farm operator :" + buyer + " for " + agent.id);	
		}else {
			System.out.println("no potential buyer from current farm for" + agent.id);
		}
		return buyer;	
	}


		
		
}

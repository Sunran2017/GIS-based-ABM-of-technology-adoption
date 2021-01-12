package adoptionbmp;

import com.vividsolutions.jts.geom.Geometry;

import adoptionbmp.Agent;
import adoptionbmp.ContextCreator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.gis.GeographyWithin;
import repast.simphony.query.space.gis.IntersectsQuery;
import repast.simphony.space.gis.Geography;
import repast.simphony.util.ContextUtils;

/**
 * The Zone agent defines a specific region in the geography that is 
 * represented by a polygon feature.  
 * 
 *
 */
public class ZoneCDSIPotential {
	
	public String GRIDCODE;			// GRIDCODE	
	public int CDSI;		// Score for CDSI Potential;0-3 poor, 6-9 fair, >10 GOOD
	
	public ZoneCDSIPotential(String GRIDCODE, int CDSI){
		this. GRIDCODE =  GRIDCODE;
		this. CDSI = CDSI;
	}
	

	public void checkCDSIPotential(Context context) {
		/**Checks first if the zone intersects a agent to determine if the zone contain agent.
		 */
		Geography geography = (Geography)context.getProjection("Geography"); 
		IntersectsQuery query = new IntersectsQuery(geography, this);
		
		for (Object obj : query.query()) {
			if (obj instanceof Agent){
				Agent agent = (Agent)obj;
				// If the zone finds a Agent, set CDSI is true;	
				if (this.CDSI >= Constants.landLevel){
				agent.CDSIPotential = true;
				System.out.println(agent.id + "CDSI Potential:"+ agent.getCDSIPotential());
				
				String writeContext = agent.id + "," + agent.CDSIPotential;
				agent.writeText("./data/CDSIPotential.csv", writeContext);
				
				}		
		
			}
			
		}				
	}
	
	public String getGRIDCODE() {
		return GRIDCODE;
	}
	
}
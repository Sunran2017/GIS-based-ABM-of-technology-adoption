package adoptionbmp;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
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
public class ZoneFD {
	
	private Long OGFID;
	private Long getYrInstall;
	private double AREAACRES;
	
	public ZoneFD(Long OGFID,double AREAACRES ){
		this.OGFID = OGFID;
		this.AREAACRES = AREAACRES;
	}

	public void checkFD(Context context) {  	
		/**Checks first if the zone intersects a agent to determine if the zone contain agent.
		 */
		Geography geography = (Geography)context.getProjection("Geography"); 
		IntersectsQuery query = new IntersectsQuery(geography, this);
		
		for (Object obj : query.query()) {		
			if (obj instanceof Agent){
				// If the zone finds a Agent, set fd is ture
				Agent agent = (Agent)obj;	
				agent.adoptedFD = true;
				agent.fdAcres = AREAACRES;
				System.out.println(agent.id + "Adopted FD: " + agent.getFD());
				
				String writeContext = agent.id + "," + agent.adoptedFD + ","+ agent.fdAcres;
				agent.writeText("./data/adoptedFD.csv", writeContext);
			}				
		}		
		
	}

	
	public Long getOGFID() {
		return OGFID;
	}
		
	public Long getYrInstall() {
		return getYrInstall;
	}
		
	public double getAREAACRES() {
		return AREAACRES;
		
	}

}

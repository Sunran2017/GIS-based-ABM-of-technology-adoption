package adoptionbmp.style;

import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfaceShape;

import java.awt.Color;

import adoptionbmp.ZoneFD;
import repast.simphony.visualization.gis3D.style.SurfaceShapeStyle;

/**
 * Style for ZoneFD.
 * 
 *
 */
public class ZoneStyle implements SurfaceShapeStyle<ZoneFD>{

	@Override
	public SurfaceShape getSurfaceShape(ZoneFD object, SurfaceShape shape) {
		return new SurfacePolygon();
	}
	

	@Override
	public Color getFillColor(ZoneFD zone) {
		
	//	if (zone.checkCDSIPotential(true)  )
		
		return Color.CYAN;
	}

	@Override
	public double getFillOpacity(ZoneFD obj) {
		return 0.25;
	}
	

	@Override
	public Color getLineColor(ZoneFD obj) {
		return Color.black;
	}

	@Override
	public double getLineOpacity(ZoneFD obj) {
		return 1;
	}
	
	@Override
	public double getLineWidth(ZoneFD obj) {
		return 3;
	}

}
package adoptionbmp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import au.com.bytecode.opencsv.CSVReader;
import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;


public class Sup {
	
	
	
	//create file
	
	
	public static void fileCreate(String filename) throws IOException {
		File file = new File(filename);
		if(!file.exists()){
		  file.createNewFile();
		}
	}
	
	//if file does exists, then create it
	public static void fileCheck(String filename) throws IOException {
		File file = new File(filename);
		if(!file.exists()){
		  file.createNewFile();
		}else {
		file.delete();
		file.createNewFile();
		}	
	}
	
	
	
	
	
	//load file
	public static List<String[]> loadFile(String filename) {
		CSVReader reader;
		List<String[]> vals = null;
		try {
			reader = new CSVReader(new BufferedReader(new FileReader(filename)));
			vals   = reader.readAll();		
			
		} catch (IOException e) {
				e.printStackTrace();
		}
		return vals;
	}
	
	
	
	//load Features From Shapefile
	public static List<SimpleFeature> loadFeaturesFromShapefile(String filename){
		URL url = null;
		try {
			url = new File(filename).toURL();
		} catch (MalformedURLException e1) {
		e1.printStackTrace();
		}

		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
	
		// Try to load the shapefile
		SimpleFeatureIterator fiter = null;
		ShapefileDataStore store = null;
		store = new ShapefileDataStore(url);

		try {
			fiter = store.getFeatureSource().getFeatures().features();
			while(fiter.hasNext()){
				features.add(fiter.next());
			}		
		} catch (IOException e) {
		e.printStackTrace();
			}
	
		finally{
			fiter.close();
			store.dispose();
		}
		return features;
	}
	
	
	//load Features CDSI LAYER
	public static void loadFeatures (String filename, Context context, Geography geography){
		
		List<SimpleFeature> features = loadFeaturesFromShapefile(filename);
	
		// For each feature in the file
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			Object agent = null;

			if (!geom.isValid()){
				System.out.println("Invalid geometry: " + feature.getID());
			}
	
			// For Polygons, create ZoneAgents
			if (geom instanceof MultiPolygon){
				MultiPolygon mp = (MultiPolygon)feature.getDefaultGeometry();
				geom = (Polygon)mp.getGeometryN(0);

			// Read the feature attributes and assign to the ZoneAgent
			String GRIDCODE = String.valueOf((long)feature.getAttribute("GRIDCODE"));
			int CDSI = (int)feature.getAttribute("CDSI");
			agent = new ZoneCDSIPotential(GRIDCODE,CDSI);	
			}
			
			// For Lines, create WaterLines
			/**
			else if (geom instanceof MultiLineString){
				MultiLineString line = (MultiLineString)feature.getDefaultGeometry();
				geom = (LineString)line.getGeometryN(0);
				// Read the feature attributes and assign to the ZoneAgent
				String name = (String)feature.getAttribute("GRIDCODE");
				agent = new WaterLine(name);
			}
			*/

			if (agent != null){
				context.add(agent);
				geography.move(agent, geom);		
				
			}else{
				System.out.println("Error creating zoneagent for  " + geom);
			}			
		}
	}
	


	//load Features FD LAYER
	public static void loadFeatures2 (String filename, Context context, Geography geography){
		List<SimpleFeature> features = loadFeaturesFromShapefile(filename);
		// For each feature in the file
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			ZoneFD zone2 = null;
			if (!geom.isValid()){
				System.out.println("Invalid geometry: " + feature.getID());
			}		
			// For Polygons, create ZoneAgents
			if (geom instanceof MultiPolygon){
				MultiPolygon mp = (MultiPolygon)feature.getDefaultGeometry();
				geom = (Polygon)mp.getGeometryN(0);
			// Read the feature attributes and assign to the Agent
			Long OGFID = (long)feature.getAttribute("OGF_ID");
			double AREAACRES;
			try {
				AREAACRES = (double)feature.getAttribute("AREA_ACRES");
			} catch (NullPointerException e) {
				AREAACRES = 0;
			}		
			zone2 = new ZoneFD(OGFID,AREAACRES);	
			}
			if (zone2 != null){
				context.add(zone2);
				geography.move(zone2, geom);
			}	
			else{
				System.out.println("Error creating zoneagent for  " + geom);
			}
		}	
	}	
	
	public static void writeText(String filename, String writeContext) {
		
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
	
	// getNormalDouble
	public static double getNormalDouble(double mean, double sd ) {
		NormalDistribution N = new NormalDistribution(mean,sd);
		double randNum = N.sample();
		// System.out.println( "getNormalDouble:  " + randNum );
		return randNum;
	}
	
	
	
	
	

}

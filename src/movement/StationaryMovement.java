/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import core.Coord;
import core.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A dummy stationary "movement" model where nodes do not move.
 * Might be useful for simulations with only external connection events. 
 */
public class StationaryMovement extends MovementModel {
	/** Per node group setting for setting the location ({@value}) */
	public static final String LOCATION_S = "nodeLocation";
	public static final String ROUTE_FILE = "routeFile";

	private Coord loc; /** The location of the nodes */
	private  static int num;
	private static List<Coord> locations = new ArrayList<>();
	
	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param s The Settings object where the settings are read from
	 */
	public StationaryMovement(Settings s) {
		super(s);
		this.num = 0;
		int coords[];
		String fileName = s.getSetting(ROUTE_FILE);
		readLine(fileName);
//		coords = s.getCsvInts(LOCATION_S, 2);
		Coord c = locations.get(num++);
		this.loc = new Coord(c.getX(), c.getY());
	}
	
	/**
	 * Copy constructor. 
	 * @param sm The StationaryMovement prototype
	 */
	public StationaryMovement(StationaryMovement sm) {
		super(sm);
		Coord c = locations.get(num++);
		this.loc = new Coord(c.getX(), c.getY());
	}
	
	/**
	 * Returns the only location of this movement model
	 * @return the only location of this movement model
	 */
	@Override
	public Coord getInitialLocation() {
		return loc;
	}
	
	/**
	 * Returns a single coordinate path (using the only possible coordinate)
	 * @return a single coordinate path
	 */
	@Override
	public Path getPath() {
		Path p = new Path(0);
		p.addWaypoint(loc);
		return p;
	}
	
	@Override
	public double nextPathAvailable() {
		return Double.MAX_VALUE;	// no new paths available
	}
	
	@Override
	public StationaryMovement replicate() {
		return new StationaryMovement(this);
	}
	private void readLine(String fileName){
		try{

			Scanner sc = new Scanner(new File(fileName));
			while (sc.hasNext()){
				String line = sc.nextLine();
				String[] locationStr = line.split(" ");
				double x = Double.valueOf(locationStr[0]);
				double y = Double.valueOf(locationStr[1]);
				locations.add(new Coord(x, y));
			}
		}catch(	Exception e){

		}
	}

}

package skew.models.misorientation;

import fava.datatypes.Maybe;

/**
 * MisAnglePoint defines the structure for storing the mis-angle for one scan
 * point in an area scan. The mis-angle for each scan point is set as the
 * average of all mis-angles to its 8 neighbors; It also records the angles to
 * the east and to the south neighbors as the reference for drawing grain
 * boundaries
 * 
 * @author Jinhui Qin, 2011
 * 
 */
public class MisAngle
{
	
	// average is mean average of all eight neighbors (if any<5)
	public Maybe<Double> average, north, east, south, west;

	public MisAngle()
	{		
		average = new Maybe<>();
		north = new Maybe<>();
		east = new Maybe<>();
		south = new Maybe<>();
		west = new Maybe<>();
		

		
	}


}

package misorientation.model;

import misorientation.calculation.misorientation.OrientationMatrix;

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
public class MisAnglePoint
{

	public double	average;	// average of eight neighbors (if any<5)
	public double	north, east, south, west;
	public OrientationMatrix orientation;
	public int grain = -1;
	
	public int index, x, y;
	
	public MisAnglePoint(int index, int x, int y)
	{
		average = -1.;
		south = -1.;
		east = -1.;
		north = -1;
		west = -1;
		grain = -1;
		
		this.index = index;
		this.x = x;
		this.y = y;
	}

}

package skew.models.Misorientation;

import java.util.List;

import scitypes.DirectionVector;
import skew.models.OrientationMatrix.IOrientationMatrix;
import skew.models.OrientationMatrix.OrientationMatrix;

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
	
	public double	average;	// average of eight neighbors (if any<5)
	public double	north, east, south, west;
	
	public IOrientationMatrix orientation;
	
	public int grain = -1;
	
	public List<DirectionVector> orientationVectors;
	
	public double grainMagnitude;
	public double intraGrainMisorientation = -1;
	
	public MisAngle()
	{		
		average = -1.;
		south = -1.;
		east = -1.;
		north = -1;
		west = -1;
		grain = -1;
		
		orientation = new OrientationMatrix();
	}



}

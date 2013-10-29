package skew.models.misorientation;

import com.google.common.base.Optional;


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
	
	// average is mean average of all eight neighbors (of those < boundary)
	public Optional<Double> average, north, east, south, west;

	public MisAngle()
	{		
		average = Optional.absent();
		north = Optional.absent();
		east = Optional.absent();
		south = Optional.absent();
		west = Optional.absent();

	}


}

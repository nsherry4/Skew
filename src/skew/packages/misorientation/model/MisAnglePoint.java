package skew.packages.misorientation.model;

import java.util.List;

import scitypes.DirectionVector;
import skew.core.model.SkewPoint;
import skew.packages.misorientation.datasource.calculation.misorientation.OrientationMatrix;

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
public class MisAnglePoint implements SkewPoint
{
	
	public double	average;	// average of eight neighbors (if any<5)
	public double	north, east, south, west;
	public OrientationMatrix orientation;
	public int grain = -1;
	
	protected int index, x, y;
	
	public List<DirectionVector> orientationVectors;
	
	public double grainMagnitude;
	public double intraGrainMisorientation = -1;
	
	public MisAnglePoint(int index, int x, int y)
	{
		average = -1.;
		south = -1.;
		east = -1.;
		north = -1;
		west = -1;
		grain = -1;
		
		orientation = new OrientationMatrix();
		
		this.index = index;
		this.x = x;
		this.y = y;
	}

	@Override
	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	@Override
	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

}

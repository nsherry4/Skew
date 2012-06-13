package misorientation.model;

/**
 * MisAnglePointList defines the structure for storing the mis-angles for all scan points in an area scan. 
 * @author Jinhui Qin, 2011
 *
 */
import java.util.List;


import misorientation.calculation.magnitude.GrainIdentify;

import fava.functionable.FList;

public class MisAngleGrid
{
	
	private FList<MisAnglePoint>     values;
	
	public int                       width;
	public int                       height;
	public FList<Grain>              grains;
	
	
	public int grainCount = 0;

	
	

	public MisAngleGrid(int width, int height)
	{
		this.width = width;
		this.height = height;
				
		this.values = new FList<MisAnglePoint>(width * height);
		for (int i = 0; i < width * height; i++)
		{
			values.add(new MisAnglePoint(i, i % width, i / width));
		}
		
		this.grains = new FList<Grain>();

	}
	
	public MisAngleGrid(int width, int height, List<MisAnglePoint> points)
	{
		this.width = width;
		this.height = height;
		this.values = new FList<MisAnglePoint>(width * height);
		for (int i = 0; i < width * height; i++)
		{
			values.add(points.get(i));
		}
	}

	public int size()
	{
		return values.size();
	}
	
	
	public FList<MisAnglePoint> getBackingList()
	{
		return values;
	}
	
	public MisAnglePoint get(int position)
	{
		if (position < 0) return null;
		if (position >= values.size()) return null;
		return values.get(position);
	}

	public MisAnglePoint get(int x, int y)
	{
		return get(width * y + x);
	}


	
	public void calculateGrains()
	{
		GrainIdentify.calculate(this);
	}
	
	public MisAnglePoint goNorth(MisAnglePoint p)
	{
		return get(p.x-1, p.y);
	}
	
	public MisAnglePoint goNorthEast(MisAnglePoint p)
	{
		return get(p.x-1, p.y+1);
	}
	
	public MisAnglePoint goNorthWest(MisAnglePoint p)
	{
		return get(p.x-1, p.y-1);
	}
	
	public MisAnglePoint goEast(MisAnglePoint p)
	{
		return get(p.x, p.y+1);
	}
	
	public MisAnglePoint goSouth(MisAnglePoint p)
	{
		return get(p.x+1, p.y);
	}
	
	public MisAnglePoint goSouthEast(MisAnglePoint p)
	{
		return get(p.x+1, p.y+1);
	}
	
	public MisAnglePoint goSouthWest(MisAnglePoint p)
	{
		return get(p.x+1, p.y-1);
	}
	
	public MisAnglePoint goWest(MisAnglePoint p)
	{
		return get(p.x, p.y-1);
	}

}

package skew.packages.misorientation.datasource.calculation.magnitude;

import java.util.List;

import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;

import fava.functionable.FList;


public class GrainIdentify
{
	
	private MisAngleGrid<? extends MisAnglePoint> data;
	
	public static void calculate(MisAngleGrid<? extends MisAnglePoint> data)
	{
		new GrainIdentify(data).calculateGrains();
		
	}
	
	private GrainIdentify(MisAngleGrid<? extends MisAnglePoint> data)
	{
		this.data = data;
	}
	
	private void calculateGrains()
	{
		
		for (int i = 0; i < data.size(); i++)
		{
			data.get(i).grain = i;
		}
		
		for (int y = 0; y < data.getHeight(); y++) {
			for (int x = 0; x < data.getWidth(); x++){
			
				MisAnglePoint point = data.get(x, y);
				
				//skip unindexed points, and points out of bounds
				if (point == null) continue;
				if (point.average < 0) continue;
				
				boolean north = sameGrainNorth(x, y);
				boolean west = sameGrainWest(x, y);
				
				int northGrain = find(x, y-1);
				int westGrain = find(x-1, y);
				
				if (north && west)
				{
					union(westGrain, northGrain);
					point.grain = northGrain;
				}
				else if (north)
				{
					point.grain = northGrain;
				}
				else if (west)
				{
					point.grain = westGrain;
				}
				else
				{
					if (point.average < 0) point.grain = -1;
				}

			}
		}
		
		for (int i = 0; i < data.size(); i++)
		{
			find(i);
		}
		
		List<Integer> labels = new FList<Integer>();
		int fresh = 0;
		for (int i = 0; i < data.size(); i++)
		{
			
			MisAnglePoint p = data.get(i);
			if (p == null) continue;
			if (p.average < 0 || isSinglePixelGrain(i)) {
				p.grain = -1;
				continue;
			}
			
			if (labels.size() <= p.grain || labels.get(p.grain) == null)
			{
				labels.set(p.grain, fresh);
				fresh++;
			}
			
			p.grain = labels.get(p.grain);
			
			
		}

		data.grainCount = fresh;
		
	}
	
	private boolean sameGrainNorth(int x, int y)
	{
		if (y == 0) return false;
		MisAnglePoint point = data.get(x, y);
		MisAnglePoint pointNorth = data.get(x, y-1);
		
		if (pointNorth.average < 0) return false;
		return point.north < 5 && point.north >= 0;
		
	}
	
	private boolean sameGrainWest(int x, int y)
	{
		if (x == 0) return false;
		MisAnglePoint point = data.get(x, y);
		MisAnglePoint pointWest = data.get(x-1, y);
		
		if (pointWest.average < 0) return false;
		return point.west < 5 && point.west >= 0;
		
	}
	
	private boolean isSinglePixelGrain(int i)
	{
		MisAnglePoint p = data.get(i);
		if (p == null) return true;
		
		boolean west = p.west > 5 || p.west < 0;
		boolean east = p.east > 5 || p.east < 0;
		boolean north = p.north > 5 || p.north < 0;
		boolean south = p.south > 5 || p.south < 0;
		
		if (west && east && south && north) return true;
		return false;
	}
	
	
	private int find(int x, int y)
	{
		return find(y * data.getWidth() + x);
	}
	
	private int find(int x)
	{
		if (x >= data.size()) return -1;
		if (x < 0) return -1;
		
		int y = x;
		while (data.get(y).grain != y)
		{
			y = data.get(y).grain;
		}
		
		while (data.get(x).grain != x)
		{
			int z = data.get(x).grain;
			data.get(x).grain = y;
			x = z;
		}
		
		return y;
	}
	
	
	private void union(int x, int y)
	{
		data.get(find(x)).grain = find(y);
		
	}

	
}

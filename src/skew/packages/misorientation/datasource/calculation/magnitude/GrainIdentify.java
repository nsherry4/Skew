package skew.packages.misorientation.datasource.calculation.magnitude;

import java.util.List;

import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import fava.functionable.FList;


public class GrainIdentify
{
	
	private MisAngleGrid data;
	
	public static void calculate(MisAngleGrid data)
	{
		new GrainIdentify(data).calculateGrains();
		
	}
	
	private GrainIdentify(MisAngleGrid data)
	{
		this.data = data;
	}
	
	private void calculateGrains()
	{
		
		for (int i = 0; i < data.size(); i++)
		{
			data.get(i).getData().grain = i;
		}
		
		for (int y = 0; y < data.getHeight(); y++) {
			for (int x = 0; x < data.getWidth(); x++){
			
				MisAngle point = data.get(x, y).getData();
				
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
			
			MisAngle p = data.get(i).getData();
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
		MisAngle point = data.get(x, y).getData();
		MisAngle pointNorth = data.get(x, y-1).getData();
		
		if (pointNorth.average < 0) return false;
		return point.north < 5 && point.north >= 0;
		
	}
	
	private boolean sameGrainWest(int x, int y)
	{
		if (x == 0) return false;
		MisAngle point = data.get(x, y).getData();
		MisAngle pointWest = data.get(x-1, y).getData();
		
		if (pointWest.average < 0) return false;
		return point.west < 5 && point.west >= 0;
		
	}
	
	private boolean isSinglePixelGrain(int i)
	{
		MisAngle p = data.get(i).getData();
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
		while (data.get(y).getData().grain != y)
		{
			y = data.get(y).getData().grain;
		}
		
		while (data.get(x).getData().grain != x)
		{
			int z = data.get(x).getData().grain;
			data.get(x).getData().grain = y;
			x = z;
		}
		
		return y;
	}
	
	
	private void union(int x, int y)
	{
		data.get(find(x)).getData().grain = find(y);
		
	}

	
}

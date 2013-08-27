package skew.datasources.misorientation.datasource.calculation.magnitude;

import java.util.List;

import skew.core.model.ISkewGrid;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class GrainIdentify
{
	
	private GrainModel grainModel;
	private ISkewGrid<MisAngle> misModel;
	
	public static void calculate(ISkewGrid<MisAngle> misModel, GrainModel grainModel)
	{
		new GrainIdentify(misModel, grainModel).calculateGrains();
		
	}
	
	private GrainIdentify(ISkewGrid<MisAngle> misModel, GrainModel grainModel)
	{
		this.grainModel = grainModel;
		this.misModel = misModel;
	}
	
	private void calculateGrains()
	{
		
		for (int i = 0; i < misModel.size(); i++)
		{
			misModel.getData(i).grainIndex.set(i);
		}
		
		for (int y = 0; y < misModel.getHeight(); y++) {
			for (int x = 0; x < misModel.getWidth(); x++){
			
				MisAngle point = misModel.getData(x, y);
				
				//skip unindexed points, and points out of bounds
				if (point == null) continue;
				if (!point.average.is()) continue;
				
				boolean north = sameGrainNorth(x, y);
				boolean west = sameGrainWest(x, y);
				
				int northGrain = find(x, y-1);
				int westGrain = find(x-1, y);
				
				if (north && west)
				{
					union(westGrain, northGrain);
					point.grainIndex.set(northGrain);
				}
				else if (north)
				{
					point.grainIndex.set(northGrain);
				}
				else if (west)
				{
					point.grainIndex.set(westGrain);
				}
				else
				{
					if (!point.average.is()) point.grainIndex.set();
				}

			}
		}
		
		for (int i = 0; i < misModel.size(); i++)
		{
			find(i);
		}
		
		List<Integer> labels = new FList<Integer>();
		int fresh = 0;
		for (int i = 0; i < misModel.size(); i++)
		{
			
			MisAngle p = misModel.getData(i);
			if (p == null) continue;
			if ((!p.average.is()) || isSinglePixelGrain(i)) {
				p.grainIndex.set();
				continue;
			}
			
			if (labels.size() <= p.grainIndex.get() || labels.get(p.grainIndex.get()) == null)
			{
				labels.set(p.grainIndex.get(), fresh);
				fresh++;
			}
			
			p.grainIndex.set(labels.get(p.grainIndex.get()));
			
			
		}

		grainModel.grainCount = fresh;
		
	}
	
	private boolean sameGrainNorth(int x, int y)
	{
		if (y == 0) return false;
		MisAngle point = misModel.getData(x, y);
		MisAngle pointNorth = misModel.getData(x, y-1);
		
		if (!pointNorth.average.is()) return false;
		return point.north.get() < 5 && point.north.get() >= 0;
		
	}
	
	private boolean sameGrainWest(int x, int y)
	{
		if (x == 0) return false;
		MisAngle point = misModel.getData(x, y);
		MisAngle pointWest = misModel.getData(x-1, y);
		
		if (!pointWest.average.is()) return false;
		return point.west.get() < 5 && point.west.get() >= 0;
		
	}
	
	private boolean isSinglePixelGrain(int i)
	{
		MisAngle p = misModel.getData(i);
		if (p == null) return true;
		
		boolean west = (!p.west.is()) || p.west.get() > 5;
		boolean east = (!p.east.is()) || p.east.get() > 5;
		boolean north = (!p.north.is()) || p.north.get() > 5;
		boolean south = (!p.south.is()) || p.south.get() > 5;
		
		if (west && east && south && north) return true;
		return false;
	}
	
	
	private int find(int x, int y)
	{
		return find(y * misModel.getWidth() + x);
	}
	
	private int find(int i)
	{
		if (i >= misModel.size()) return -1;
		if (i < 0) return -1;
		
		int j = i;
		while (misModel.getData(j).grainIndex.get() != j)
		{
			j = misModel.getData(j).grainIndex.get();
		}
		
		while (misModel.getData(i).grainIndex.get() != i)
		{
			int k = misModel.getData(i).grainIndex.get();
			misModel.getData(i).grainIndex.set(j);
			i = k;
		}
		
		return j;
	}
	
	
	private void union(int x, int y)
	{
		misModel.getData(find(x)).grainIndex.set(find(y));
		
	}

	
}

package skew.datasources.misorientation.datasource.calculation.magnitude;

import java.util.List;

import skew.core.model.ISkewGrid;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class GrainIdentify
{
	
	private ISkewGrid<GrainPixel> grainModel;
	private ISkewGrid<MisAngle> misModel;
	
	public static void calculate(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel)
	{
		new GrainIdentify(misModel, grainModel).calculateGrains();
		
	}
	
	private GrainIdentify(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel)
	{
		this.grainModel = grainModel;
		this.misModel = misModel;
	}
	
	//determines the grain index number for each pixel
	private void calculateGrains()
	{
		
		for (int i = 0; i < grainModel.size(); i++)
		{
			grainModel.getData(i).grainIndex.set(i);
		}
		
		for (int y = 0; y < grainModel.getHeight(); y++) {
			for (int x = 0; x < grainModel.getWidth(); x++){
			
				GrainPixel grainData = grainModel.getData(x, y);
				MisAngle misData = misModel.getData(x, y);
				
				//skip unindexed points, and points out of bounds
				if (grainData == null) continue;
				if (!misData.average.is()) continue;
				
				boolean north = sameGrainNorth(x, y);
				boolean west = sameGrainWest(x, y);
				
				int northGrain = find(x, y-1);
				int westGrain = find(x-1, y);
				
				if (north && west)
				{
					union(westGrain, northGrain);
					grainData.grainIndex.set(northGrain);
				}
				else if (north)
				{
					grainData.grainIndex.set(northGrain);
				}
				else if (west)
				{
					grainData.grainIndex.set(westGrain);
				}
				else
				{
					if (!misData.average.is()) grainData.grainIndex.set();
				}

			}
		}
		
		for (int i = 0; i < grainModel.size(); i++)
		{
			find(i);
		}
		
		List<Integer> labels = new FList<Integer>();
		int fresh = 0;
		for (int i = 0; i < grainModel.size(); i++)
		{
			
			MisAngle misData = misModel.getData(i);
			GrainPixel grainData = grainModel.getData(i);
			
			if (misData == null) continue;
			if ((!misData.average.is()) || isSinglePixelGrain(i)) {
				grainData.grainIndex.set();
				continue;
			}
			
			if (labels.size() <= grainData.grainIndex.get() || labels.get(grainData.grainIndex.get()) == null)
			{
				labels.set(grainData.grainIndex.get(), fresh);
				fresh++;
			}
			
			grainData.grainIndex.set(labels.get(grainData.grainIndex.get()));
			
			
		}

		//TODO: Is this required anywhere? 
		//grainModel.grainCount = fresh;
		
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
		if (i >= grainModel.size()) return -1;
		if (i < 0) return -1;
		
		int j = i;
		while (grainModel.getData(j).grainIndex.get() != j)
		{
			j = grainModel.getData(j).grainIndex.get();
		}
		
		while (grainModel.getData(i).grainIndex.get() != i)
		{
			int k = grainModel.getData(i).grainIndex.get();
			grainModel.getData(i).grainIndex.set(j);
			i = k;
		}
		
		return j;
	}
	
	
	private void union(int x, int y)
	{
		grainModel.getData(find(x)).grainIndex.set(find(y));
		
	}

	
}

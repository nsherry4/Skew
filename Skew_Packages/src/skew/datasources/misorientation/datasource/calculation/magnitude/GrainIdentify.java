package skew.datasources.misorientation.datasource.calculation.magnitude;

import java.util.List;

import com.google.common.base.Optional;

import skew.core.model.ISkewGrid;
import skew.models.grain.GrainPixel;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;


public class GrainIdentify
{
	
	private ISkewGrid<GrainPixel> grainModel;
	private ISkewGrid<MisAngle> misModel;
	private double boundary;
	
	public static void calculate(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel, double boundary)
	{
		new GrainIdentify(misModel, grainModel, boundary).calculateGrains();
		
	}
	
	private GrainIdentify(ISkewGrid<MisAngle> misModel, ISkewGrid<GrainPixel> grainModel, double boundary)
	{
		this.grainModel = grainModel;
		this.misModel = misModel;
		this.boundary = boundary;		
	}
	
	//determines the grain index number for each pixel
	private void calculateGrains()
	{
		
		for (int i = 0; i < grainModel.size(); i++)
		{
			grainModel.getData(i).grainIndex = Optional.of(i);
		}
		
		for (int y = 0; y < grainModel.getHeight(); y++) {
			for (int x = 0; x < grainModel.getWidth(); x++){
			
				GrainPixel grainData = grainModel.getData(x, y);
				MisAngle misData = misModel.getData(x, y);
				
				//skip unindexed points, and points out of bounds
				if (grainData == null) continue;
				if (!misData.average.isPresent()) continue;
				
				boolean north = sameGrainNorth(x, y);
				boolean west = sameGrainWest(x, y);
				
				int northGrain = find(x, y-1);
				int westGrain = find(x-1, y);
				
				if (north && west)
				{
					union(westGrain, northGrain);
					grainData.grainIndex = Optional.of(northGrain);
				}
				else if (north)
				{
					grainData.grainIndex = Optional.of(northGrain);
				}
				else if (west)
				{
					grainData.grainIndex = Optional.of(westGrain);
				}
				else
				{
					if (!misData.average.isPresent()) grainData.grainIndex = Optional.absent();
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
			//if ((!misData.average.isPresent()) || isSinglePixelGrain(i)) {
			if ((!misData.average.isPresent())) {
				grainData.grainIndex = Optional.absent();
				continue;
			}
			
			if (labels.size() <= grainData.grainIndex.get() || labels.get(grainData.grainIndex.get()) == null)
			{
				labels.set(grainData.grainIndex.get(), fresh);
				fresh++;
			}
			
			grainData.grainIndex = Optional.of(labels.get(grainData.grainIndex.get()));
			
			
		}

		//TODO: Is this required anywhere? 
		//grainModel.grainCount = fresh;
		
	}
	
	private boolean sameGrainNorth(int x, int y)
	{
		if (y == 0) return false;
		MisAngle point = misModel.getData(x, y);
		MisAngle pointNorth = misModel.getData(x, y-1);
		
		if (!pointNorth.average.isPresent()) return false;
		return point.north.get() < boundary && point.north.get() >= 0;
		
	}
	
	private boolean sameGrainWest(int x, int y)
	{
		if (x == 0) return false;
		MisAngle point = misModel.getData(x, y);
		MisAngle pointWest = misModel.getData(x-1, y);
		
		if (!pointWest.average.isPresent()) return false;
		return point.west.get() < boundary && point.west.get() >= 0;
		
	}
	
	private boolean isSinglePixelGrain(int i)
	{
		MisAngle p = misModel.getData(i);
		if (p == null) return true;
		
		boolean west = (!p.west.isPresent()) || p.west.get() > boundary;
		boolean east = (!p.east.isPresent()) || p.east.get() > boundary;
		boolean north = (!p.north.isPresent()) || p.north.get() > boundary;
		boolean south = (!p.south.isPresent()) || p.south.get() > boundary;
		
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
			grainModel.getData(i).grainIndex = Optional.of(j);
			i = k;
		}
		
		return j;
	}
	
	
	private void union(int x, int y)
	{
		grainModel.getData(find(x)).grainIndex = Optional.of(find(y));
		
	}

	
}

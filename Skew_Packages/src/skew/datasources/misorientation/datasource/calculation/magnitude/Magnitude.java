package skew.datasources.misorientation.datasource.calculation.magnitude;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.models.grain.Grain;
import skew.models.grain.GrainUtil;
import skew.models.grain.GrainPixel;
import skew.models.orientation.IOrientationMatrix;
import fava.functionable.FList;

public class Magnitude
{
	public static int setupGrains(ISkewGrid<GrainPixel> grainModel)
	{
		
		//Construct a list of Grains from the data in the grain index value for each point
		List<Grain> grains = new FList<>();
		for (int y = 0; y < grainModel.getHeight(); y++){
			for (int x = 0; x < grainModel.getWidth(); x++){

				ISkewPoint<GrainPixel> gridPoint = grainModel.getPoint(x, y);
				
				if (!gridPoint.getData().grainIndex.is()) continue;
				int index = gridPoint.getData().grainIndex.get();
				
				//if the grain array isn't as large as the index, or the element at this index is null, set this element to a new grain 
				if (grains.size() <= index || grains.get(index) == null)
				{
					grains.set(index, new Grain(index));
				}
				
			}
		}

		//Give each GrainPixel a reference to its Grain
		for (ISkewPoint<GrainPixel> grainPoint : grainModel){
			GrainPixel grainData = grainPoint.getData();
			if (!grainData.grainIndex.is()) continue;
			grainData.grain = grains.get(grainData.grainIndex.get());
		}
		
		//find the neighbours of each grain
		int maxNeighbours = 0;
		for (Grain grain : grains)
		{
			for (ISkewPoint<GrainPixel> grainPoint : GrainUtil.getGrainPoints(grainModel, grain))
			{
				addNeighbour(grain, SkewGrid.goNorth(grainModel, grainPoint));
				addNeighbour(grain, SkewGrid.goEast(grainModel, grainPoint));
				addNeighbour(grain, SkewGrid.goSouth(grainModel, grainPoint));
				addNeighbour(grain, SkewGrid.goWest(grainModel, grainPoint));
				
				addNeighbour(grain, SkewGrid.goNorthEast(grainModel, grainPoint));
				addNeighbour(grain, SkewGrid.goNorthWest(grainModel, grainPoint));
				addNeighbour(grain, SkewGrid.goSouthEast(grainModel, grainPoint));
				addNeighbour(grain, SkewGrid.goSouthWest(grainModel, grainPoint));
				
			}
			
			maxNeighbours = Math.max(maxNeighbours, grain.neighbours.size());
		}
		
		
		
		FList<Grain> sortedGrains = new FList<Grain>(grains);
		sortedGrains.sort(new Comparator<Grain>() {
			
			@Override
			public int compare(Grain g1, Grain g2)
			{
				Integer s1 = g1.neighbours.size();
				Integer s2 = g2.neighbours.size();
				return s2.compareTo(s1);
			}
		});
		
		for (Grain g : sortedGrains)
		{
			colourGrain(g);
		}
		
		return maxNeighbours;
		
	}
	
	
	private static void addNeighbour(Grain g, ISkewPoint<GrainPixel> otherPoint)
	{
		if (otherPoint == null) return;
		Grain other = otherPoint.getData().grain;
		if (other != null) g.neighbours.add(other);
	}
	
	private static void colourGrain(Grain g)
	{
		Set<Integer> colours = new LinkedHashSet<Integer>();
		for (int i = 0; i < g.neighbours.size() + 1; i++){ colours.add(i); }
		for (Grain n : g.neighbours) { colours.remove(n.colourIndex); }
		g.colourIndex = colours.iterator().next();
		
	}
	
	public static void calcMagnitude(ISkewGrid<IOrientationMatrix> omGrid, Collection<ISkewPoint<GrainPixel>> grainPoints, Grain g)
	{
		double magAvg = 0;
		double magMax = 0;
		double magMin = Double.MAX_VALUE;
		
		ISkewPoint<GrainPixel> minPoint = null;
		double innerSum;
		
		IOrientationMatrix omPoint, otherOMPoint;
		
		for (ISkewPoint<GrainPixel> point : grainPoints)
		{
			innerSum = 0;
			omPoint = omGrid.getData(point);
			
			for (ISkewPoint<GrainPixel> otherGrainPoint : grainPoints)
			{
				otherOMPoint = omGrid.getData(otherGrainPoint);
				if (otherOMPoint == omPoint) continue;
				innerSum += Calculation.calculateAngle(omPoint, otherOMPoint);
			}
			
			magAvg += innerSum;
			
			if (innerSum > magMax) {
				magMax = innerSum;
			}
			
			if (innerSum < magMin) {
				magMin = innerSum;
				minPoint = point;
			}
			
		}
		magAvg /= (grainPoints.size() * grainPoints.size());
		magMin /= grainPoints.size();
		magMax /= grainPoints.size();
		g.magMin = magMin;
		g.magAvg = magAvg;
		g.magMax = magMax;
		
	
		//calculate the grain maximum misorientation angle.
		//actually find the angle in the 95th percentile to avoid
		//an errant pixel making the rest of a grain look flat
		g.intraGrainCenter = minPoint;
		//List<Double> intraGrainValues = new ArrayList<Double>(g.points.size()); 
		double[] igv = new double[grainPoints.size()];
		int count = 0;
		IOrientationMatrix minOMPoint = omGrid.getData(minPoint);
		for (ISkewPoint<GrainPixel> misPoint : grainPoints)
		{
			omPoint = omGrid.getData(misPoint);
			
			misPoint.getData().intraGrainMisorientation.set(Calculation.calculateAngle(omPoint, minOMPoint));
			igv[count++] = misPoint.getData().intraGrainMisorientation.get();
		}
		Arrays.sort(igv);
		g.intraGrainMax = igv[(int)(grainPoints.size() * 0.95)];
		
		
	}
	
	
	
	
}



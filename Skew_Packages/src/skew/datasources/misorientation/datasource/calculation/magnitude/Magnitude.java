package skew.datasources.misorientation.datasource.calculation.magnitude;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.models.grain.Grain;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import fava.functionable.FList;

public class Magnitude
{
	public static int setupGrains(GrainModel data, ISkewGrid<MisAngle> misModel)
	{
		
		//Construct a list of Grains from the data
		data.grains = new FList<Grain>();
		for (int y = 0; y < misModel.getHeight(); y++){
			for (int x = 0; x < misModel.getWidth(); x++){

				ISkewPoint<MisAngle> misPoint = misModel.getPoint(x, y);	
				
				if (misPoint == null) continue;
				if (!misPoint.getData().grainIndex.is()) continue;
				int index = misPoint.getData().grainIndex.get();
				
				//if the grain array isn't as large as the index, or the element at this index is null, set this element to a new grain 
				if (data.grains.size() <= index || data.grains.get(index) == null)
				{
					data.grains.set(index, new Grain(index));
				}
				
				Grain g = data.grains.get(index);
				g.points.add(misPoint);
			}
		}

		//find the neighbours of each grain
		int maxNeighbours = 0;
		for (Grain grain : data.grains)
		{
			for (ISkewPoint<MisAngle> misPoint : grain.points)
			{
				addNeighbour(grain, data, SkewGrid.goNorth(misModel, misPoint));
				addNeighbour(grain, data, SkewGrid.goEast(misModel, misPoint));
				addNeighbour(grain, data, SkewGrid.goSouth(misModel, misPoint));
				addNeighbour(grain, data, SkewGrid.goWest(misModel, misPoint));
				
				addNeighbour(grain, data, SkewGrid.goNorthEast(misModel, misPoint));
				addNeighbour(grain, data, SkewGrid.goNorthWest(misModel, misPoint));
				addNeighbour(grain, data, SkewGrid.goSouthEast(misModel, misPoint));
				addNeighbour(grain, data, SkewGrid.goSouthWest(misModel, misPoint));
				
			}
			
			maxNeighbours = Math.max(maxNeighbours, grain.neighbours.size());
		}
		
		
		
		FList<Grain> sortedGrains = new FList<Grain>(data.grains);
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
	
	private static void addNeighbour(Grain g, GrainModel data, ISkewPoint<MisAngle> p)
	{
		if (p == null) return;
		Grain other = data.getGrain(p);
		if (other != null) g.neighbours.add(other);
	}
	
	private static void colourGrain(Grain g)
	{
		Set<Integer> colours = new LinkedHashSet<Integer>();
		for (int i = 0; i < g.neighbours.size() + 1; i++){ colours.add(i); }
		for (Grain n : g.neighbours) { colours.remove(n.colourIndex); }
		g.colourIndex = colours.iterator().next();
		
	}
	
	public static void calcMagnitude(ISkewGrid<MisAngle> misGrid, ISkewGrid<IOrientationMatrix> omGrid, Grain g)
	{
		double magAvg = 0;
		double magMax = 0;
		double magMin = Double.MAX_VALUE;
		ISkewPoint<MisAngle> minPoint = null;
		
		
		for (ISkewPoint<MisAngle> point : g.points)
		{
			MisAngle data = point.getData();
			data.grainMagnitude = calcPointMagnitude( g, omGrid, omGrid.getData(point.getIndex()) );
			
			magAvg += data.grainMagnitude;
			
			if (data.grainMagnitude > magMax)
			{
				magMax = data.grainMagnitude;
			}
			
			if (data.grainMagnitude < magMin)
			{
				magMin = data.grainMagnitude;
				minPoint = point;
			}
			
		}
		magAvg /= g.points.size();
		g.magMin = magMin;
		g.magAvg = magAvg;
		g.magMax = magMax;
		
		
		//calculate the grain maximum misorientation angle.
		//actually find the angle in the 95th percentile to avoid
		//an errant pixel making the rest of a grain look flat
		g.intraGrainCenter = minPoint;
		//List<Double> intraGrainValues = new ArrayList<Double>(g.points.size()); 
		double[] igv = new double[g.points.size()];
		int count = 0;
		for (ISkewPoint<MisAngle> misPoint : g.points)
		{
			IOrientationMatrix omPoint = omGrid.getData(misPoint.getIndex());
			IOrientationMatrix minOMPoint = omGrid.getData(minPoint.getIndex());
			
			misPoint.getData().intraGrainMisorientation.set(Calculation.calculateAngle(omPoint, minOMPoint));
			igv[count++] = misPoint.getData().intraGrainMisorientation.get();
			//intraGrainValues.add(point.intraGrainMisorientation);
			//g.intraGrainMax = Math.max(g.intraGrainMax, point.intraGrainMisorientation);
		}
		Arrays.sort(igv);
		//Collections.sort(intraGrainValues);
		g.intraGrainMax = igv[(int)(g.points.size() * 0.95)];
		
		
	}
	
	private static double calcPointMagnitude(Grain g, ISkewGrid<IOrientationMatrix> omGrid, IOrientationMatrix omPoint)
	{
		double sum = 0;
		for (ISkewPoint<MisAngle> misPoint : g.points)
		{
			IOrientationMatrix grainOMPoint = omGrid.getData(misPoint.getIndex());
			if (grainOMPoint == omPoint) continue;
			sum += Calculation.calculateAngle(omPoint, grainOMPoint);
		}
		return sum / g.points.size();
	}
	
	
}



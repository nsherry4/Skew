package skew.packages.misorientation.datasource.calculation.magnitude;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.models.Grain.Grain;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;
import fava.functionable.FList;

public class Magnitude
{
	public static int setupGrains(MisAngleGrid data)
	{
		
		//Construct a list of Grains from the data
		data.grains = new FList<Grain>();
		for (int y = 0; y < data.getHeight(); y++){
			for (int x = 0; x < data.getWidth(); x++){

				ISkewPoint<MisAngle> p = data.get(x, y);
				
				if (p == null) continue;
				if (p.getData().grain == -1) continue;
				
				if (data.grains.size() <= p.getData().grain || data.grains.get(p.getData().grain) == null)
				{
					data.grains.set(p.getData().grain, new Grain(p.getData().grain));
				}
				
				Grain g = data.grains.get(p.getData().grain);
				g.points.add(p);
			}
		}

		//find the neighbours of each grain
		int maxNeighbours = 0;
		for (Grain grain : data.grains)
		{
			for (ISkewPoint<MisAngle> p : grain.points)
			{
				addNeighbour(grain, data, data.goNorth(p));
				addNeighbour(grain, data, data.goEast(p));
				addNeighbour(grain, data, data.goSouth(p));
				addNeighbour(grain, data, data.goWest(p));
				
				addNeighbour(grain, data, data.goNorthEast(p));
				addNeighbour(grain, data, data.goNorthWest(p));
				addNeighbour(grain, data, data.goSouthEast(p));
				addNeighbour(grain, data, data.goSouthWest(p));
				
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
	
	private static void addNeighbour(Grain g, MisAngleGrid data, ISkewPoint<MisAngle> p)
	{
		if (p == null) return;
		Grain other = data.getGrainAtPoint(p);
		if (other != null) g.neighbours.add(other);
	}
	
	private static void colourGrain(Grain g)
	{
		Set<Integer> colours = new LinkedHashSet<Integer>();
		for (int i = 0; i < g.neighbours.size() + 1; i++){ colours.add(i); }
		for (Grain n : g.neighbours) { colours.remove(n.colourIndex); }
		g.colourIndex = colours.iterator().next();
		
	}
	
	public static void calcMagnitude(ISkewGrid<MisAngle> grid, Grain g)
	{
		double magAvg = 0;
		double magMax = 0;
		double magMin = Double.MAX_VALUE;
		ISkewPoint<MisAngle> minPoint = null;
		
		
		for (ISkewPoint<MisAngle> point : g.points)
		{
			MisAngle data = point.getData();
			data.grainMagnitude = calcPointMagnitude(grid, g, point);
			
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
		for (ISkewPoint<MisAngle> point : g.points)
		{
			point.getData().intraGrainMisorientation = Calculation.calculateAngle(point.getData().orientation, minPoint.getData().orientation);
			igv[count++] = point.getData().intraGrainMisorientation;
			//intraGrainValues.add(point.intraGrainMisorientation);
			//g.intraGrainMax = Math.max(g.intraGrainMax, point.intraGrainMisorientation);
		}
		Arrays.sort(igv);
		//Collections.sort(intraGrainValues);
		g.intraGrainMax = igv[(int)(g.points.size() * 0.95)];
		
		
	}
	
	private static double calcPointMagnitude(ISkewGrid<MisAngle> data, Grain g, ISkewPoint<MisAngle> p)
	{
		double sum = 0;
		for (ISkewPoint<MisAngle> point : g.points)
		{
			if (point == p) continue;
			sum += Calculation.calculateAngle(p.getData().orientation, point.getData().orientation);
		}
		return sum / g.points.size();
	}
	
	
}



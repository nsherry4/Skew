package skew.packages.misorientation.datasource.calculation.magnitude;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import skew.core.model.ISkewGrid;
import skew.models.Grain.Grain;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;
import fava.functionable.FList;

public class Magnitude
{
	public static int setupGrains(MisAngleGrid<? extends MisAnglePoint> data)
	{
		
		//Construct a list of Grains from the data
		data.grains = new FList<Grain>();
		for (int y = 0; y < data.getHeight(); y++){
			for (int x = 0; x < data.getWidth(); x++){

				MisAnglePoint p = data.get(x, y);
				if (p == null) continue;
				if (p.grain == -1) continue;
				
				if (data.grains.size() <= p.grain || data.grains.get(p.grain) == null)
				{
					data.grains.set(p.grain, new Grain(p.grain));
				}
				
				Grain g = data.grains.get(p.grain);
				g.points.add(p);
			}
		}

		//find the neighbours of each grain
		int maxNeighbours = 0;
		for (Grain grain : data.grains)
		{
			for (MisAnglePoint p : grain.points)
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
	
	private static void addNeighbour(Grain g, MisAngleGrid<? extends MisAnglePoint> data, MisAnglePoint p)
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
	
	public static void calcMagnitude(ISkewGrid data, Grain g)
	{
		double magAvg = 0;
		double magMax = 0;
		double magMin = Double.MAX_VALUE;
		MisAnglePoint minPoint = null;
		
		
		for (MisAnglePoint point : g.points)
		{
			point.grainMagnitude = calcPointMagnitude(data, g, point);
			
			magAvg += point.grainMagnitude;
			
			if (point.grainMagnitude > magMax)
			{
				magMax = point.grainMagnitude;
			}
			
			if (point.grainMagnitude < magMin)
			{
				magMin = point.grainMagnitude;
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
		for (MisAnglePoint point : g.points)
		{
			point.intraGrainMisorientation = Calculation.calculateAngle(point.orientation, minPoint.orientation);
			igv[count++] = point.intraGrainMisorientation;
			//intraGrainValues.add(point.intraGrainMisorientation);
			//g.intraGrainMax = Math.max(g.intraGrainMax, point.intraGrainMisorientation);
		}
		Arrays.sort(igv);
		//Collections.sort(intraGrainValues);
		g.intraGrainMax = igv[(int)(g.points.size() * 0.95)];
		
		
	}
	
	private static double calcPointMagnitude(ISkewGrid data, Grain g, MisAnglePoint p)
	{
		double sum = 0;
		for (MisAnglePoint point : g.points)
		{
			if (point == p) continue;
			sum += Calculation.calculateAngle(p.orientation, point.orientation);
		}
		return sum / g.points.size();
	}
	
	
}



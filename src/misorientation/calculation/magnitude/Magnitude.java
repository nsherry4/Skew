package misorientation.calculation.magnitude;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fava.functionable.FList;
import misorientation.calculation.misorientation.Calculation;
import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;

public class Magnitude
{
	public static int setupGrains(MisAngleGrid data)
	{
		
		//Construct a list of Grains from the data
		FList<Grain> grains = new FList<Grain>();
		for (int y = 0; y < data.height; y++){
			for (int x = 0; x < data.width; x++){

				MisAnglePoint p = data.get(x, y);
				if (p == null) continue;
				if (p.grain == -1) continue;
				
				if (grains.size() <= p.grain || grains.get(p.grain) == null)
				{
					grains.set(p.grain, new Grain(p.grain));
				}
				
				Grain g = grains.get(p.grain);
				g.points.add(p);
			}
		}

		//find the neighbours of each grain
		int maxNeighbours = 0;
		for (Grain grain : grains)
		{
			for (MisAnglePoint p : grain.points)
			{
				addNeighbour(grains, grain, data.goNorth(p));
				addNeighbour(grains, grain, data.goEast(p));
				addNeighbour(grains, grain, data.goSouth(p));
				addNeighbour(grains, grain, data.goWest(p));
				
				addNeighbour(grains, grain, data.goNorthEast(p));
				addNeighbour(grains, grain, data.goNorthWest(p));
				addNeighbour(grains, grain, data.goSouthEast(p));
				addNeighbour(grains, grain, data.goSouthWest(p));
				
			}
			
			maxNeighbours = Math.max(maxNeighbours, grain.neighbours.size());
		}
		
		data.grains = grains;
		
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
	
	private static void addNeighbour(List<Grain> grains, Grain g, MisAnglePoint p)
	{
		if (p == null) return;
		int otherIndex = p.grain;
		if (otherIndex < 0 || otherIndex == g.index) return;
		Grain other = grains.get(otherIndex);
		if (other != null) g.neighbours.add(other);
	}
	
	private static void colourGrain(Grain g)
	{
		Set<Integer> colours = new LinkedHashSet<Integer>();
		for (int i = 0; i < g.neighbours.size() + 1; i++){ colours.add(i); }
		for (Grain n : g.neighbours) { colours.remove(n.colourIndex); }
		g.colourIndex = colours.iterator().next();
		
	}
	
	public static void calcMagnitude(MisAngleGrid data, Grain g)
	{
		double maxMag = 0;
		for (MisAnglePoint point : g.points)
		{
			maxMag = Math.max(maxMag, calcPointMagnitude(data, g, point));
		}
		
		g.magnitude = maxMag;
	}
	
	private static double calcPointMagnitude(MisAngleGrid data, Grain g, MisAnglePoint p)
	{
		double sum = 0;
		for (MisAnglePoint point : g.points)
		{
			if (point == p) continue;
			sum += Calculation.calculateAngle(data.get(p.index).orientation, data.get(point.index).orientation);
		}
		return sum / g.points.size();
	}
	
	
}



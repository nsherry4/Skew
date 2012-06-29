package skew.packages.misorientation.model;

import java.util.HashSet;
import java.util.Set;

import skew.core.model.SkewPoint;

import fava.functionable.FList;

public class Grain
{
	
	public FList<MisAnglePoint> points = new FList<MisAnglePoint>();
	
	public double magMin = 0;
	public double magMax = 0;
	public double magAvg = 0;
	
	public Set<Grain> neighbours = new HashSet<Grain>();
	public int colourIndex = -1;
	
	public boolean selected = false;
	public int index;
	
	public SkewPoint intraGrainCenter;
	public double intraGrainMax = 0.0001;
	
	public Grain(int index)
	{
		this.index = index;
	}
	

}
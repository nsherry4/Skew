package misorientation.model;

import java.util.HashSet;
import java.util.Set;

import fava.functionable.FList;

public class Grain
{
	
	public FList<MisAnglePoint> points = new FList<MisAnglePoint>();
	public double magnitude = 0;
	public Set<Grain> neighbours = new HashSet<Grain>();
	public int colourIndex = -1;
	public boolean selected = false;
	public int index;
	
	public Grain(int index)
	{
		this.index = index;
	}
	

}
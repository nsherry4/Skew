package skew.models.grain;

import java.util.HashSet;
import java.util.Set;

import skew.core.model.ISkewPoint;
import skew.models.misorientation.MisAngle;
import fava.functionable.FList;

public class Grain
{
	
	public FList<ISkewPoint<MisAngle>> points = new FList<ISkewPoint<MisAngle>>();
	
	
	public double magMin = 0;
	public double magMax = 0;
	public double magAvg = 0;
	
	public Set<Grain> neighbours = new HashSet<Grain>();
	public int colourIndex = -1;
	
	public boolean selected = false;
	public int index;
	
	public ISkewPoint<MisAngle> intraGrainCenter;
	public double intraGrainMax = 0.0001;
	
	public Grain(int index)
	{
		this.index = index;
	}
	

}
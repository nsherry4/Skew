package skew.models.grain;

import java.util.HashSet;
import java.util.Set;

import skew.core.model.ISkewPoint;

public class Grain
{
		
	public double magMin = 0;
	public double magMax = 0;
	public double magAvg = 0;
	
	public Set<Grain> neighbours = new HashSet<Grain>();
	public int colourIndex = -1;
	
	public int index;
	
	public ISkewPoint<GrainPixel> intraGrainCenter;
	public double intraGrainMax = 0.0001;
	
	public Grain(int index)
	{
		this.index = index;
	}
	

}
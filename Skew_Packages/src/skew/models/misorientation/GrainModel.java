package skew.models.misorientation;

/**
 * MisAnglePointList defines the structure for storing the mis-angles for all scan points in an area scan. 
 * @author Jinhui Qin, 2011
 *
 */
import java.util.List;

import skew.core.model.IModel;
import skew.core.model.ISkewPoint;
import skew.models.grain.Grain;
import fava.functionable.FList;


public class GrainModel implements IModel
{
	
	public List<Grain> grains;
	public int grainCount = 0;

	private int width, height;
	
	public GrainModel(int width, int height)
	{
		this.grains = new FList<>();
		this.width = width;
		this.height = height;
	}
		
	
	public Grain getGrain(ISkewPoint<MisAngle> point) {
		return getGrain(point.getData());
	}
	
	public Grain getGrain(MisAngle data)
	{
		if (data == null) return null;
		if (!data.grainIndex.is()) return null;
		if (data.grainIndex.get() >= grains.size()) return null;
		
		return grains.get(data.grainIndex.get());
	}
	 


	@Override
	public int getWidth() {
		return width;
	}


	@Override
	public int getHeight() {
		return height;
	}



	
}

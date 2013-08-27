package skew.models.misorientation;

/**
 * MisAnglePointList defines the structure for storing the mis-angles for all scan points in an area scan. 
 * @author Jinhui Qin, 2011
 *
 */
import java.util.ArrayList;
import java.util.List;

import skew.core.model.ISkewPoint;
import skew.models.grain.Grain;
import fava.functionable.FList;


public class GrainModel
{
	
	public List<Grain> grains;
	public int grainCount = 0;

	public GrainModel()
	{
		this.grains = new FList<>();
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
	 
	public boolean selectGrain(MisAngle point, boolean multiselect)
	{
		Grain g = getGrain(point);
		if (g == null) return false;
		
		boolean alreadySelected = g.selected;
		if (!multiselect) for (Grain grain : grains) { grain.selected = false; }
		g.selected = !alreadySelected;
		
		return g.selected;
		
	}
	
	public List<Grain> getSelectedGrains()
	{
		List<Grain> selected = new ArrayList<Grain>();
		for (Grain g : grains) { if (g.selected) selected.add(g); }
		return selected;
	}

	
}

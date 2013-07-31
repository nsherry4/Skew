package skew.models.Misorientation;

/**
 * MisAnglePointList defines the structure for storing the mis-angles for all scan points in an area scan. 
 * @author Jinhui Qin, 2011
 *
 */
import java.util.ArrayList;
import java.util.List;

import skew.core.model.ISkewPoint;
import skew.core.model.impl.SkewGrid;
import skew.models.Grain.Grain;
import skew.packages.misorientation.datasource.calculation.magnitude.GrainIdentify;


public class MisAngleGrid extends SkewGrid<MisAngle>
{
	
	public List<Grain>               grains;
	public int grainCount = 0;

	public MisAngleGrid(int width, int height, List<ISkewPoint<MisAngle>> points)
	{
		super(width, height, points);
		this.grains = new ArrayList<Grain>();
	}
	
	
	@Override
	public void setPointSelected(int x, int y, boolean deselectAll)
	{
		if (get(x, y) == null) return;
		selectGrainAtPoint(x, y, deselectAll);
	}
	
	
	
	
	public List<ISkewPoint<MisAngle>> getBackingList()
	{
		return values;
	}


	public void calculateGrains()
	{
		GrainIdentify.calculate(this);
	}
	
	public ISkewPoint<MisAngle> goNorth(ISkewPoint<MisAngle> p)
	{
		return get(p.getX()-1, p.getY());
	}
	
	public ISkewPoint<MisAngle> goNorthEast(ISkewPoint<MisAngle> p)
	{
		return get(p.getX()-1, p.getY()+1);
	}
	
	public ISkewPoint<MisAngle> goNorthWest(ISkewPoint<MisAngle> p)
	{
		return get(p.getX()-1, p.getY()-1);
	}
	
	public ISkewPoint<MisAngle> goEast(ISkewPoint<MisAngle> p)
	{
		return get(p.getX(), p.getY()+1);
	}
	
	public ISkewPoint<MisAngle> goSouth(ISkewPoint<MisAngle> p)
	{
		return get(p.getX()+1, p.getY());
	}
	
	public ISkewPoint<MisAngle> goSouthEast(ISkewPoint<MisAngle> p)
	{
		return get(p.getX()+1, p.getY()+1);
	}
	
	public ISkewPoint<MisAngle> goSouthWest(ISkewPoint<MisAngle> p)
	{
		return get(p.getX()+1, p.getY()-1);
	}
	
	public ISkewPoint<MisAngle> goWest(ISkewPoint<MisAngle> p)
	{
		return get(p.getX(), p.getY()-1);
	}
	
	public Grain getGrainAtPoint(ISkewPoint<MisAngle> p)
	{
		MisAngle data = p.getData();
		if (data == null) return null;
		if (data.grain < 0) return null;
		if (data.grain >= grains.size()) return null;
		
		return grains.get(data.grain);
	}
	
	public Grain getGrainAtPoint(int x, int y)
	{
		return getGrainAtPoint(get(x, y));
	}
	
	public boolean selectGrainAtPoint(int x, int y, boolean multiselect)
	{
		Grain g = getGrainAtPoint(x, y);
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

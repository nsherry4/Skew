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


public class MisAngleGrid<T extends MisAnglePoint> extends SkewGrid<T>
{
	
	public List<Grain>               grains;
	public int grainCount = 0;

	public MisAngleGrid(int width, int height, List<T> points)
	{
		super(width, height, points);
		this.grains = new ArrayList<Grain>();
	}
	
	
	@Override
	public boolean setPointSelected(int x, int y, boolean deselectAll)
	{
		if (get(x, y) == null) return false;
		return selectGrainAtPoint(x, y, deselectAll);
	}
	
	
	
	
	public List<T> getBackingList()
	{
		return values;
	}


	public void calculateGrains()
	{
		GrainIdentify.calculate(this);
	}
	
	public T goNorth(MisAnglePoint p)
	{
		return get(p.getX()-1, p.getY());
	}
	
	public T goNorthEast(MisAnglePoint p)
	{
		return get(p.getX()-1, p.getY()+1);
	}
	
	public T goNorthWest(MisAnglePoint p)
	{
		return get(p.getX()-1, p.getY()-1);
	}
	
	public T goEast(MisAnglePoint p)
	{
		return get(p.getX(), p.getY()+1);
	}
	
	public T goSouth(MisAnglePoint p)
	{
		return get(p.getX()+1, p.getY());
	}
	
	public T goSouthEast(MisAnglePoint p)
	{
		return get(p.getX()+1, p.getY()+1);
	}
	
	public T goSouthWest(MisAnglePoint p)
	{
		return get(p.getX()+1, p.getY()-1);
	}
	
	public T goWest(MisAnglePoint p)
	{
		return get(p.getX(), p.getY()-1);
	}
	
	public Grain getGrainAtPoint(MisAnglePoint p)
	{
		if (p == null) return null;
		if (p.grain < 0) return null;
		if (p.grain >= grains.size()) return null;
		
		return grains.get(p.grain);
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

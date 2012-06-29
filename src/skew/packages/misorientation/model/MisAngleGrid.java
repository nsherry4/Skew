package skew.packages.misorientation.model;

/**
 * MisAnglePointList defines the structure for storing the mis-angles for all scan points in an area scan. 
 * @author Jinhui Qin, 2011
 *
 */
import java.util.ArrayList;
import java.util.List;

import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.packages.misorientation.datasource.calculation.magnitude.GrainIdentify;


public class MisAngleGrid<T extends MisAnglePoint> implements SkewGrid
{
	
	private List<T>                  values;
	
	protected int                   width;
	protected int                   height;
	public List<Grain>               grains;

	public int grainCount = 0;



	/*
	public MisAngleGrid(int width, int height)
	{
		this.width = width;
		this.height = height;
				
		this.values = new FList<MisAnglePoint>(width * height);
		for (int i = 0; i < width * height; i++)
		{
			values.add(new MisAnglePoint(i, i % width, i / width));
		}
		
		this.grains = new FList<Grain>();

	}
	*/
	
	public MisAngleGrid(int width, int height, List<T> points)
	{
		this.width = width;
		this.height = height;
		
		this.values = points;
		
		this.grains = new ArrayList<Grain>();
	}
	
	@Override
	public int size()
	{
		return values.size();
	}
	
	
	public List<T> getBackingList()
	{
		return values;
	}
	
	@Override
	public T get(int position)
	{
		if (position < 0) return null;
		if (position >= values.size()) return null;
		return values.get(position);
	}

	@Override
	public T get(int x, int y)
	{
		return get(width * y + x);
	}


	public void calculateGrains()
	{
		GrainIdentify.calculate(this);
	}
	
	public T goNorth(MisAnglePoint p)
	{
		return get(p.x-1, p.y);
	}
	
	public T goNorthEast(MisAnglePoint p)
	{
		return get(p.x-1, p.y+1);
	}
	
	public T goNorthWest(MisAnglePoint p)
	{
		return get(p.x-1, p.y-1);
	}
	
	public T goEast(MisAnglePoint p)
	{
		return get(p.x, p.y+1);
	}
	
	public T goSouth(MisAnglePoint p)
	{
		return get(p.x+1, p.y);
	}
	
	public T goSouthEast(MisAnglePoint p)
	{
		return get(p.x+1, p.y+1);
	}
	
	public T goSouthWest(MisAnglePoint p)
	{
		return get(p.x+1, p.y-1);
	}
	
	public T goWest(MisAnglePoint p)
	{
		return get(p.x, p.y-1);
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

	@Override
	public boolean setPointSelected(SkewPoint p, boolean deselectAll)
	{
		if (p == null) return false;
		return selectGrainAtPoint(p.getX(), p.getY(), deselectAll);
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

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}
	
}

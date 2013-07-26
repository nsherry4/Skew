package skew.core.model.impl;

import java.util.ArrayList;
import java.util.List;

import skew.core.datasource.IDataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import fava.functionable.FList;

public class SkewDataset implements ISkewDataset {

	private String name;
	private String path;
	private List<ISkewGrid> grids;
	private IDataSource ds;
	
	
	
	public SkewDataset(String name, String path, ISkewGrid grid, IDataSource ds) {
		this(name, path, new FList<ISkewGrid>(grid), ds);
	}
	
	public SkewDataset(String name, String path, List<ISkewGrid> grids, IDataSource ds) {
		this.name = name;
		this.path = path;
		this.grids = grids;
		this.ds = ds;
	}
	
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public String path() {
		return path;
	}

	
	@Override
	public IDataSource datasource() {
		return ds;
	}


	@Override
	public int height() {
		int max = 0;
		for (ISkewGrid g : grids) {
			max = Math.max(max, g.getHeight());
		}
		return max;
	}

	@Override
	public int width() {
		int max = 0;
		for (ISkewGrid g : grids) {
			max = Math.max(max, g.getWidth());
		}
		return max;
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {
		for (ISkewGrid g : grids){
			g.setPointSelected(x, y, deselectAll);
		}
	}

	@Override
	public List<ISkewPoint> get(int x, int y) {
		List<ISkewPoint> points = new ArrayList<ISkewPoint>();
		ISkewPoint p;
		for (ISkewGrid g : grids){
			p = g.get(x, y);
			if (p != null) points.add(p);
		}
		return points;
	}

}

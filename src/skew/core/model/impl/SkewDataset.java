package skew.core.model.impl;

import skew.core.datasource.IDataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;

public class SkewDataset implements ISkewDataset {

	private String name;
	private String path;
	private ISkewGrid grid;
	private IDataSource ds;
	
	public SkewDataset(String name, String path, ISkewGrid grid, IDataSource ds) {
		this.name = name;
		this.path = path;
		this.grid = grid;
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
	public ISkewGrid grid() {
		return grid;
	}

	@Override
	public IDataSource datasource() {
		return ds;
	}

}

package skew.core.model;

import skew.core.datasource.IDataSource;

public interface ISkewDataset {

	public abstract String name();
	public abstract String path();
	public abstract IDataSource datasource();
		
	public abstract int height();
	public abstract int width();
	
	public abstract void setPointSelected(int x, int y, boolean deselectAll);	
}

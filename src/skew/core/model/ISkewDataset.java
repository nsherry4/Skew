package skew.core.model;

import skew.core.datasource.IDataSource;

public interface ISkewDataset {

	public abstract String name();
	public abstract String path();
	public abstract ISkewGrid grid();
	public abstract IDataSource datasource();
	
}

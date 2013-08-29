package skew.core.model;

import skew.core.datasource.IDataSource;

public interface ISkewDataset {

	String name();
	String path();
	IDataSource datasource();
	int getWidth();
	int getHeight();

}

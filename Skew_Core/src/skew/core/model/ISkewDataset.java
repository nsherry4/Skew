package skew.core.model;

import skew.core.datasource.DataSource;

public interface ISkewDataset {

	String name();
	String path();
	DataSource datasource();
	int getWidth();
	int getHeight();

}

package skew.ui;

import java.util.List;

import skew.core.datasource.DataSource;

public class InputSelection
{
	DataSource datasource;
	List<String> files;
	
	public InputSelection(DataSource datasource, List<String> files) {
		this.datasource = datasource;
		this.files = files;
	}
}

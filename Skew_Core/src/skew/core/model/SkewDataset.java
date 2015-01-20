package skew.core.model;

import java.util.List;

import skew.core.datasource.DataSource;
import fava.functionable.FList;

public class SkewDataset implements ISkewDataset {

	private String name;
	private String path;
	private List<IModel> models;
	private DataSource ds;
	
	
	
	public SkewDataset(String name, String path, IModel model, DataSource ds) {
		this(name, path, new FList<IModel>(model), ds);
	}
	
	public SkewDataset(String name, String path, List<IModel> models, DataSource ds) {
		this.name = name;
		this.path = path;
		this.models = models;
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
	public DataSource datasource() {
		return ds;
	}


	@Override
	public int getHeight() {
		int max = 0;
		for (IModel g : models) {
			max = Math.max(max, g.getHeight());
		}
		return max;
	}

	@Override
	public int getWidth() {
		int max = 0;
		for (IModel g : models) {
			max = Math.max(max, g.getWidth());
		}
		return max;
	}


}

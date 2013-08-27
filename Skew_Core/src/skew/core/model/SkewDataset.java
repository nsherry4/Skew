package skew.core.model;

import java.util.List;

import skew.core.datasource.IDataSource;
import fava.functionable.FList;

public class SkewDataset implements ISkewDataset {

	private String name;
	private String path;
	private List<ISkewGrid<?>> models;
	private IDataSource ds;
	
	
	
	public SkewDataset(String name, String path, ISkewGrid<?> model, IDataSource ds) {
		this(name, path, new FList<ISkewGrid<?>>(model), ds);
	}
	
	public SkewDataset(String name, String path, List<ISkewGrid<?>> models, IDataSource ds) {
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
	public IDataSource datasource() {
		return ds;
	}


	@Override
	public int height() {
		int max = 0;
		for (ISkewGrid<?> g : models) {
			max = Math.max(max, g.getHeight());
		}
		return max;
	}

	@Override
	public int width() {
		int max = 0;
		for (ISkewGrid<?> g : models) {
			max = Math.max(max, g.getWidth());
		}
		return max;
	}

	@Override
	public void setPointSelected(int x, int y, boolean deselectAll) {
		for (ISkewGrid<?> model : models){
			model.setPointSelected(x, y, deselectAll);
		}
	}

}

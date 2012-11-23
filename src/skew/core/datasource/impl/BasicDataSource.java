package skew.core.datasource.impl;

import java.util.List;

import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewGrid;

public abstract class BasicDataSource implements IDataSource
{

	private String ext, desc, title;
	
	public BasicDataSource(String extension, String description, String title)
	{
		this.ext = extension;
		this.desc = description;
		this.title = title;
	}
	
	@Override
	public String extension()
	{
		return ext;
	}

	@Override
	public String description()
	{
		return desc;
	}

	@Override
	public String title()
	{
		return title;
	}


	@Override
	public final ExecutorSet<ISkewGrid> calculate(final List<String> filenames, final Coord<Integer> mapsize)
	{
		final DummyExecutor exec = new DummyExecutor(true);
		
		ExecutorSet<ISkewGrid> execset = new ExecutorSet<ISkewGrid>("Opening Data Set") {

			@Override
			protected ISkewGrid execute()
			{
				exec.advanceState();
				ISkewGrid grid = load(filenames, mapsize);
				exec.advanceState();
				return grid;
			}};
		
		execset.addExecutor(exec, "Loading Data...");
		
		return execset;
	}
	
	public abstract ISkewGrid load(List<String> filenames, Coord<Integer> mapsize);


}

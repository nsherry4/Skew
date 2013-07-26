package skew.core.datasource.impl;

import java.io.File;
import java.util.List;

import commonenvironment.IOOperations;
import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.impl.SkewDataset;

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
	public final ExecutorSet<ISkewDataset> calculate(final List<String> filenames, final Coord<Integer> mapsize)
	{
		final DummyExecutor exec = new DummyExecutor(true);
		
		ExecutorSet<ISkewDataset> execset = new ExecutorSet<ISkewDataset>("Opening Data Set") {

			@Override
			protected ISkewDataset execute()
			{
				exec.advanceState();
				ISkewGrid grid = load(filenames, mapsize);
				exec.advanceState();
				
				return new SkewDataset(
						new File(IOOperations.getCommonFileName(filenames)).getName(), 
						new File(filenames.get(0)).getParent(), 
						grid,
						BasicDataSource.this
					);
			}};
		
		execset.addExecutor(exec, "Loading Data...");
		
		return execset;
	}
	
	public abstract ISkewGrid load(List<String> filenames, Coord<Integer> mapsize);


}

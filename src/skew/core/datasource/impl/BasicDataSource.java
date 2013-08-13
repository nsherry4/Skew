package skew.core.datasource.impl;

import java.io.File;
import java.util.List;

import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.impl.SkewDataset;
import commonenvironment.IOOperations;

public abstract class BasicDataSource extends DataSource
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
				List<ISkewGrid<?>> grids = load(filenames, mapsize, exec);
				exec.advanceState();
				
				return new SkewDataset(
						new File(getDatasetTitle(filenames)).getName(), 
						new File(filenames.get(0)).getParent(), 
						grids,
						BasicDataSource.this
					);
			}};
		
		execset.addExecutor(exec, "Loading Data...");
		
		return execset;
	}
	
	protected String getDatasetTitle(List<String> filenames)
	{
		return IOOperations.getCommonFileName(filenames);
	}
	
	public abstract List<ISkewGrid<?>> load(List<String> filenames, Coord<Integer> mapsize, PluralExecutor executor);


}

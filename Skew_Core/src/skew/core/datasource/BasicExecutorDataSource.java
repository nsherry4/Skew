package skew.core.datasource;

import java.io.File;
import java.util.List;

import plural.executor.DummyExecutor;
import plural.executor.ExecutorSet;
import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.model.IModel;
import skew.core.model.ISkewDataset;
import skew.core.model.SkewDataset;
import commonenvironment.IOOperations;

public abstract class BasicExecutorDataSource implements ExecutorDataSource
{

	private DataSourceDescription desc;
	
	public BasicExecutorDataSource(String extension, String description, String title)
	{
		desc = new DataSourceDescription(title, description, extension);
	}
	
	@Override
	public DataSourceDescription getDescription() {
		return desc;
	}


	@Override
	public final ExecutorSet<ISkewDataset> loadDataset(final List<String> filenames, final Coord<Integer> mapsize)
	{
		final DummyExecutor exec = new DummyExecutor(true);
		
		ExecutorSet<ISkewDataset> execset = new ExecutorSet<ISkewDataset>("Opening Data Set") {

			@Override
			protected ISkewDataset execute()
			{
				exec.advanceState();
				List<IModel> grids = load(filenames, mapsize, exec);
				exec.advanceState();
				
				return new SkewDataset(
						new File(getDatasetTitle(filenames)).getName(), 
						new File(filenames.get(0)).getParent(), 
						grids,
						BasicExecutorDataSource.this
					);
			}};
		
		exec.setName("Loading Data...");
		execset.addExecutor(exec);
		
		return execset;
	}
	
	protected String getDatasetTitle(List<String> filenames)
	{
		return IOOperations.getCommonFileName(filenames);
	}
	
	public abstract List<IModel> load(List<String> filenames, Coord<Integer> mapsize, PluralExecutor executor);


	public static boolean allWithExtension(List<String> filenames, String ext) {
		return filenames.stream().map((f) -> f.toLowerCase().endsWith(ext)).reduce(true, (a, b) -> a && b);		
	}
	
}

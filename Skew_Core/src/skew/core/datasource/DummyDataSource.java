package skew.core.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.viewer.modes.views.MapView;
import autodialog.model.Parameter;
import fava.functionable.FList;

public class DummyDataSource implements ExecutorDataSource
{

	@Override
	public DataSourceDescription getDescription()
	{
		return new DataSourceDescription("", "", "");
	}

	@Override
	public FileFormatAcceptance accepts(List<String> filenames)
	{
		return FileFormatAcceptance.REJECT;
	}


	@Override
	public List<MapView> getViews()
	{
		//return new FList<MapView>(new DummyView());
		return new FList<>();
	}

	@Override
	public ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize)
	{
		return null;
	}

	@Override
	public List<Parameter<?>> getLoadParameters() {
		return new FList<>();
	}

	@Override
	public String getLoadParametersInformation() {
		return null;
	}

	@Override
	public List<Parameter<?>> getRuntimeParameters() {
		return new FList<>();
	}

	@Override
	public void recalculate() {}

	@Override
	public FileOrFolder fileOrFolder() {
		return FileOrFolder.FILE;
	}

}

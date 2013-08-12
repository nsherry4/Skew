package skew.core.datasource.impl;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewDataset;
import skew.core.viewer.modes.views.MapView;
import autodialog.model.Parameter;
import fava.functionable.FList;

public class DummyDataSource implements IDataSource
{

	@Override
	public String extension()
	{
		return "";
	}

	@Override
	public String description()
	{
		return "";
	}

	@Override
	public String title()
	{
		return "";
	}

	@Override
	public Acceptance accepts(List<String> filenames)
	{
		return Acceptance.REJECT;
	}


	@Override
	public List<MapView> getViews()
	{
		//return new FList<MapView>(new DummyView());
		return new FList<>();
	}

	@Override
	public ExecutorSet<ISkewDataset> calculate(List<String> filenames, Coord<Integer> mapsize)
	{
		return null;
	}

	@Override
	public List<Parameter<?>> userQueries() {
		return new FList<>();
	}

	@Override
	public String userQueryInformation() {
		return null;
	}

}

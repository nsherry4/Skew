package skew.core.datasource.impl;

import java.util.List;

import fava.functionable.FList;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.impl.DummyView;

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
		return new FList<MapView>(new DummyView());
	}

	@Override
	public ExecutorSet<ISkewGrid> calculate(List<String> filenames, Coord<Integer> mapsize)
	{
		return null;
	}

}

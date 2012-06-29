package skew.core.datasource;

import java.util.List;

import fava.functionable.FList;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.SkewGrid;
import skew.core.viewer.modes.views.DummyView;
import skew.core.viewer.modes.views.MapView;

public class DummyDataSource extends DataSource
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
	public ExecutorSet<SkewGrid> calculate(List<String> filenames, Coord<Integer> mapsize)
	{
		return null;
	}

}

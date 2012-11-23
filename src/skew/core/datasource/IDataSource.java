package skew.core.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.views.MapView;



public interface IDataSource
{
	
	public String extension();
	public String description();
	public String title();
	
	public Acceptance accepts(List<String> filenames);
	
	public List<MapView> getViews();	
	
	public ExecutorSet<ISkewGrid> calculate(List<String> filenames, Coord<Integer> mapsize);

}

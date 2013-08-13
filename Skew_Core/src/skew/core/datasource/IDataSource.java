package skew.core.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.viewer.modes.views.MapView;
import autodialog.model.Parameter;



public interface IDataSource
{
	
	public String extension();
	public String description();
	public String title();
	
	public Acceptance accepts(List<String> filenames);
	
	public List<Parameter<?>> userQueries();
	public String userQueryInformation();
	
	public List<MapView> getViews();	
	
	public ExecutorSet<ISkewDataset> calculate(List<String> filenames, Coord<Integer> mapsize);

}

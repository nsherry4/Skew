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
	
	public List<Parameter<?>> getLoadParameters();
	public String getLoadParametersInformation();
	public List<Parameter<?>> getRuntimeParameters();
	
	
	public List<MapView> getViews();	
	
	/**
	 * Loads a dataset from the given filename(s) on disk 
	 */
	public ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize);
	
	/**
	 * If a dataset has any runtime parameters, this method is called to recalculate the dataset values
	 * with altered parameter values
	 */
	public void recalculate();

}

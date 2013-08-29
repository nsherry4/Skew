package skew.core.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.viewer.modes.views.MapView;
import autodialog.model.Parameter;



public interface IDataSource
{
	
	String extension();
	String description();
	String title();
	
	Acceptance accepts(List<String> filenames);
	
	List<Parameter<?>> getLoadParameters();
	String getLoadParametersInformation();
	List<Parameter<?>> getRuntimeParameters();
	
	
	List<MapView> getViews();	
	
	/**
	 * Loads a dataset from the given filename(s) on disk 
	 */
	ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize);
	
	/**
	 * If a dataset has any runtime parameters, this method is called to recalculate the dataset values
	 * with altered parameter values
	 */
	void recalculate();

}

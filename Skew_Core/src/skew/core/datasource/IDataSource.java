package skew.core.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.viewer.modes.views.MapView;
import autodialog.model.Parameter;



public interface IDataSource
{
	
	public enum FileFormatAcceptance
	{
		REJECT, MAYBE, ACCEPT
	}

	public enum FileOrFolder
	{
		FILE, FOLDER, EITHER
	}
	
	/**
	 * The file extension for this type of data. This should not include any leading dot.
	 */
	String extension();
	String description();
	String title();
	
	/**
	 * Specifies if this datasource opens an entire folder, or one or more files.
	 */
	FileOrFolder fileOrFolder();
	FileFormatAcceptance accepts(List<String> filenames);
	
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

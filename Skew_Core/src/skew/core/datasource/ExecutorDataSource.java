package skew.core.datasource;

import java.util.List;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewDataset;

public interface ExecutorDataSource extends DataSource
{

	/**
	 * Loads a dataset from the given filename(s) on disk 
	 */
	ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize);
	
	
}

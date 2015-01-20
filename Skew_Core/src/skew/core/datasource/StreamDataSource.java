package skew.core.datasource;

import java.util.stream.Stream;

import scitypes.Coord;

public interface StreamDataSource extends DataSource
{

	/**
	 * Loads a dataset from the given filename(s) on disk 
	 */
	void loadDataset(Stream<String> filenames, Coord<Integer> mapsize);
	
	
}

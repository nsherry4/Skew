package skew.core.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.core.viewer.modes.views.MapView;
import autodialog.model.Parameter;



public interface DataSource
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
	/*String extension();
	String description();
	String title();
	*/
	DataSourceDescription getDescription();
	
	/**
	 * Specifies if this datasource opens an entire folder, or one or more files.
	 */
	FileOrFolder fileOrFolder();
	FileFormatAcceptance accepts(List<String> filenames);
	default FileFormatAcceptance acceptsFiles(List<File> files) {
		return accepts(files.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()));
	}
	
	List<Parameter<?>> getLoadParameters();
	String getLoadParametersInformation();
	List<Parameter<?>> getRuntimeParameters();
	
	
	List<MapView> getViews();	
	

	/**
	 * If a dataset has any runtime parameters, this method is called to recalculate the dataset values
	 * with altered parameter values
	 */
	void recalculate();
	
	
	public static <S> List<ISkewPoint<S>> createPoints(Coord<Integer> mapsize, Supplier<S> create)
	{
		List<ISkewPoint<S>> points = new ArrayList<ISkewPoint<S>>();
		for (int i = 0; i < mapsize.x * mapsize.y; i++)
		{
			points.add(new SkewPoint<>(i % mapsize.x, i / mapsize.x, i, create.get()));
		}
		return points;
		
	}
	
		
	public static <S> ISkewGrid<S> createGrid(Coord<Integer> mapsize, Supplier<S> create) {
		return new SkewGrid<>(mapsize.x, mapsize.y, createPoints(mapsize, create));
	}

}

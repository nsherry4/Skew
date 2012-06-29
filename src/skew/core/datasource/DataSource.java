package skew.core.datasource;

import java.util.List;

import fava.functionable.FList;

import plural.executor.ExecutorSet;
import scitypes.Coord;
import skew.core.model.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.packages.misorientation.datasource.EBSDDataSource;
import skew.packages.misorientation.datasource.INDDataSource;
import skew.packages.xrd.datasource.SEQDataSource;


public abstract class DataSource
{
	
	public abstract String extension();
	public abstract String description();
	public abstract String title();
	
	public abstract Acceptance accepts(List<String> filenames);
	
	public abstract List<MapView> getViews();	
	
	public abstract ExecutorSet<SkewGrid> calculate(List<String> filenames, Coord<Integer> mapsize);
	
	
	public static List<DataSource> getSources()
	{
		return new FList<DataSource>(new EBSDDataSource(), new INDDataSource(), new SEQDataSource());		
	}
	
	

}

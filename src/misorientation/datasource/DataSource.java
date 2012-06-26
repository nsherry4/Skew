package misorientation.datasource;

import java.util.List;

import fava.functionable.FList;

import misorientation.calculation.misorientation.OrientationMatrix;
import plural.executor.map.MapExecutor;

public abstract class DataSource
{
	
	public abstract String extension();
	public abstract String description();
	public abstract String title();
	
	public abstract Acceptance accepts(List<String> filenames);
	
	public abstract MapExecutor<String, String> loadOMList(final List<OrientationMatrix> values, List<String> filenames);
	
	
	public static List<DataSource> getSources()
	{
		return new FList<DataSource>(new EBSDDataSource(), new INDDataSource(), new SEQDataSource());		
	}

}

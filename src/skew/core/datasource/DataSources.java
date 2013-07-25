package skew.core.datasource;

import java.util.List;

import skew.packages.misorientation.datasource.EBSDDataSource;
import skew.packages.misorientation.datasource.INDDataSource;
import skew.packages.pixeldeviation.datasource.PixDevDataSource;
import skew.packages.xrd.datasource.SEQDataSource;
import skew.packages.xrdstrain.datasource.SubtractionDataSource;
import fava.functionable.FList;

public class DataSources
{
	public static List<IDataSource> getSources()
	{
		return new FList<IDataSource>(
				new EBSDDataSource(), 
				new INDDataSource(), 
				new SEQDataSource(), 
				new PixDevDataSource(),
				new SubtractionDataSource()
			);		
	}
}

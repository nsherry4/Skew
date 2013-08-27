package skew;

import java.util.List;

import skew.core.datasource.IDataSource;
import skew.datasources.misorientation.datasource.EBSDDataSource;
import skew.datasources.misorientation.datasource.INDDataSource;
import skew.datasources.pixeldeviation.PixDevDataSource;
import skew.datasources.xrd.sequence.SEQDataSource;
import skew.datasources.xrd.subtraction.PairSubtractionDataSource;
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
				//new SubtractionDataSource(),
				new PairSubtractionDataSource()
			);
		
		
	}
}

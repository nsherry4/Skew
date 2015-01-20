package skew;

import java.util.List;

import skew.core.datasource.DataSource;
import skew.datasources.misorientation.datasource.EBSDDataSource;
import skew.datasources.misorientation.datasource.INDDataSource;
import skew.datasources.pixeldeviation.PixDevDataSource;
import skew.datasources.xrd.sequence.SEQDataSource;
import skew.datasources.xrd.strfiles.ExecutorStrDataSource;
import skew.datasources.xrd.strfiles.StreamStrDataSource;
import skew.datasources.xrd.subtraction.PairSubtractionDataSource;
import fava.functionable.FList;

public class DataSources
{
	public static List<DataSource> getSources()
	{
		
		return new FList<DataSource>(
				new EBSDDataSource(), 
				new INDDataSource(),
				new SEQDataSource(), 
				new PixDevDataSource(),
				//new SubtractionDataSource(),
				new PairSubtractionDataSource(),
				//new ExecutorStrDataSource()
				new StreamStrDataSource()
			);
		
		
	}
}

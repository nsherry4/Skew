package skew;

import java.util.List;

import skew.core.datasource.IDataSource;
import skew.packages.misorientation.datasource.EBSDDataSource;
import skew.packages.misorientation.datasource.INDDataSource;
import skew.packages.pixeldeviation.datasource.PixDevDataSource;
import skew.packages.xrd.sequence.datasource.SEQDataSource;
import skew.packages.xrd.strain.datasource.PairSubtractionDataSource;
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
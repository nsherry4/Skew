package misorientation.datasource;

import java.io.File;
import java.util.List;

import ca.sciencestudio.process.xrd.datastructures.mapdata.SequenceEntry;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;

import misorientation.calculation.misorientation.Calculation;
import misorientation.calculation.misorientation.OrientationMatrix;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;

public class SEQDataSource extends DataSource
{

	@Override
	public String extension()
	{
		return "seq";
	}
	
	@Override
	public String title()
	{
		return "Sequence"; 
	}
	
	@Override
	public String description()
	{
		return "FOXMAS/XMAS Text Sequence File";
	}

	@Override
	public Acceptance accepts(List<String> filenames)
	{
		if (filenames.size() > 1) return Acceptance.REJECT;
		boolean accept = filenames.get(0).toLowerCase().endsWith("seq");
		return accept ? Acceptance.ACCEPT : Acceptance.REJECT;
	}

	@Override
	public MapExecutor<String, String> loadOMList(final List<OrientationMatrix> values, List<String> filenames)
	{

		final String filename = filenames.get(0); 
		
		try {
			
			List<String> lines = FStringInput.lines(new File(filename)).toSink();
			
			//strip header
			//lines.remove(0);
			
			FnMap<String, String> eachFilename = new FnMap<String, String>(){
	
				@Override
				public String f(String line) {
	
					SequenceEntry seq = new SequenceEntry(line);
					
					int index = seq.imageNumber();
					
					OrientationMatrix om = values.get(index);
					om.index = index;
					loadOrientationMatrixSequence(seq, om);
					
					return "";
				}};
				
			MapExecutor<String, String> exec = new PluralMapExecutor<String, String>(lines, eachFilename);
			exec.setName("Reading Files");
			return exec;
		} catch (Exception e){ return null; }
	}
	
	public static boolean loadOrientationMatrixSequence(SequenceEntry seq, OrientationMatrix om)
	{
		
		int quality = seq.indexQuality();
		if (quality <= 0) return false;
		
		om.inverse = seq.orientationMatrix();
		Calculation.invert3(om.inverse, om.direct);
		om.index = seq.imageNumber();
		
		return true;
		
	}
	

}

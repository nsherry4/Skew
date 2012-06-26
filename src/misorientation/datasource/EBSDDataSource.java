package misorientation.datasource;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.File;
import java.util.List;

import fava.functionable.FStringInput;
import fava.signatures.FnMap;

import misorientation.calculation.misorientation.Calculation;
import misorientation.calculation.misorientation.OrientationMatrix;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;

public class EBSDDataSource extends DataSource
{

	@Override
	public String extension()
	{
		return "txt";
	}

	@Override
	public String title()
	{
		return "EBSD"; 
	}
	
	@Override
	public String description()
	{
		return "EBSD File";
	}

	@Override
	public Acceptance accepts(List<String> filenames)
	{
		if (filenames.size() > 1) return Acceptance.REJECT;
		return Acceptance.MAYBE;
	}

	@Override
	public MapExecutor<String, String> loadOMList(final List<OrientationMatrix> values, List<String> filenames)
	{
		
		final String filename = filenames.get(0); 
		
		try {
			
			List<String> lines = FStringInput.lines(new File(filename)).toSink();
			
			lines.remove(0);

			
			FnMap<String, String> eachFilename = new FnMap<String, String>(){
	
				@Override
				public String f(String line) {
	
					List<String> words = FStringInput.words(line).toSink();
					int index = Integer.parseInt(words.get(0)) - 1;
					OrientationMatrix om = values.get(index);
					om.index = index;
					loadOrientationMatrix(words, om);
					
					return "";
				}};
				
			MapExecutor<String, String> exec = new PluralMapExecutor<String, String>(lines, eachFilename);
			exec.setName("Reading Files");
			return exec;
		} catch (Exception e){ return null; }
	}
	
	
	public boolean loadOrientationMatrix(List<String> words, OrientationMatrix om)
	{
		
		if (words.get(9).trim().equals("1")) return false;
		
		float phi = (float) (Float.parseFloat(words.get(4)) / 180.0 * Math.PI);
		float theta = (float) (Float.parseFloat(words.get(5)) / 180.0 * Math.PI);
		float psi = (float) (Float.parseFloat(words.get(6)) / 180.0 * Math.PI);
		
		
		//x-convention
		om.direct[0][0] = (float) (cos(psi)*cos(theta)*cos(phi)-sin(psi)*sin(phi));
		om.direct[0][1] = (float) (cos(psi)*cos(theta)*sin(phi)+sin(psi)*cos(phi));
		om.direct[0][2] = (float) (-cos(psi)*sin(theta));
		om.direct[1][0] = (float) (-sin(psi)*cos(theta)*cos(phi)-cos(psi)*sin(phi));
		om.direct[1][1] = (float) (-sin(psi)*cos(theta)*sin(phi)+cos(psi)*cos(phi));
		om.direct[1][2] = (float) (sin(psi)*sin(theta));
		om.direct[2][0] = (float) (sin(theta)*cos(phi));
		om.direct[2][1] = (float) (sin(theta)*sin(phi));
		om.direct[2][2] = (float) cos(theta);
		
		Calculation.invert3(om.direct, om.inverse);
		
		return true;
		
	}
	

}

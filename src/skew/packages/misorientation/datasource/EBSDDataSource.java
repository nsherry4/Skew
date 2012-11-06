package skew.packages.misorientation.datasource;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.File;
import java.util.List;

import com.ezware.dialog.task.TaskDialogs;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;

import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import skew.core.datasource.Acceptance;
import skew.core.viewer.modes.views.MapView;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;
import skew.packages.misorientation.datasource.calculation.misorientation.OrientationMatrix;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;
import skew.packages.misorientation.view.grain.GrainLabelView;
import skew.packages.misorientation.view.grain.OrientationView;
import skew.packages.misorientation.view.misangle.InterGrainView;
import skew.packages.misorientation.view.misangle.LocalView;
import skew.packages.misorientation.view.misangle.MagnitudeView;

public class EBSDDataSource extends MisorientationDataSource
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
	public MapExecutor<String, String> loadPoints(final MisAngleGrid<? extends MisAnglePoint> values, List<String> filenames)
	{
		
		final String filename = filenames.get(0); 
		
		try 
		{
			
			List<String> lines = FStringInput.lines(new File(filename)).toSink();
			
			lines.remove(0);

			
			FnMap<String, String> eachFilename = new FnMap<String, String>(){
	
				@Override
				public String f(String line) {
	
					List<String> words = FStringInput.words(line).toSink();
					int index = Integer.parseInt(words.get(0)) - 1;
					MisAnglePoint p = values.get(index);
					OrientationMatrix om = p.orientation;
					om.index = index;
					p.hasOMData = loadOrientationMatrix(words, om);
					return "";
				}};
				
			MapExecutor<String, String> exec = new PluralMapExecutor<String, String>(lines, eachFilename);
			exec.setName("Reading Files");
			return exec;
		}
		catch (Exception e)
		{ 
			TaskDialogs.showException(e);
			return null;
		}
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

	@Override
	public List<MapView> getViews()
	{
		return new FList<MapView>(
				new LocalView(),
				new InterGrainView(),
				new MagnitudeView(),
				new OrientationView(),
				new GrainLabelView()
			);

	}
	

}

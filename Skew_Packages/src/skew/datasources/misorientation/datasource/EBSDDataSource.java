package skew.datasources.misorientation.datasource;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.File;
import java.util.List;

import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import skew.core.datasource.Acceptance;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.views.CompositeView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.models.orientation.IOrientationMatrix;
import skew.views.OrientationView;
import skew.views.misorientation.GrainLabelView;
import skew.views.misorientation.ThresholdSecondaryView;
import skew.views.misorientation.InterGrainView;
import skew.views.misorientation.LocalView;
import skew.views.misorientation.MagnitudeView;
import autodialog.model.Parameter;

import com.ezware.dialog.task.TaskDialogs;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;

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
	public MapExecutor<String, String> loadPoints(List<String> filenames)
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
					ISkewPoint<IOrientationMatrix> omPoint = omModel.getPoint(index);
					omPoint.setValid(loadOrientationMatrix(words, omPoint.getData()));
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
	
	
	public boolean loadOrientationMatrix(List<String> words, IOrientationMatrix om)
	{
		
		if (words.get(9).trim().equals("1")) return false;
		
		float phi = (float) (Float.parseFloat(words.get(4)) / 180.0 * Math.PI);
		float theta = (float) (Float.parseFloat(words.get(5)) / 180.0 * Math.PI);
		float psi = (float) (Float.parseFloat(words.get(6)) / 180.0 * Math.PI);
		
		
		//x-convention
		om.getDirect()[0][0] = (float) (cos(psi)*cos(theta)*cos(phi)-sin(psi)*sin(phi));
		om.getDirect()[0][1] = (float) (cos(psi)*cos(theta)*sin(phi)+sin(psi)*cos(phi));
		om.getDirect()[0][2] = (float) (-cos(psi)*sin(theta));
		om.getDirect()[1][0] = (float) (-sin(psi)*cos(theta)*cos(phi)-cos(psi)*sin(phi));
		om.getDirect()[1][1] = (float) (-sin(psi)*cos(theta)*sin(phi)+cos(psi)*cos(phi));
		om.getDirect()[1][2] = (float) (sin(psi)*sin(theta));
		om.getDirect()[2][0] = (float) (sin(theta)*cos(phi));
		om.getDirect()[2][1] = (float) (sin(theta)*sin(phi));
		om.getDirect()[2][2] = (float) cos(theta);
		
		Calculation.invert3(om.getDirect(), om.getInverse());
		
		return true;
		
	}

	@Override
	public List<MapView> getViews()
	{
		double angle = boundaryParameter.getValue();
	
		return new FList<MapView>(
				new CompositeView(new LocalView(misModel), new ThresholdSecondaryView(misModel, grainModel,angle)),
				new CompositeView(new InterGrainView(misModel, grainModel), new ThresholdSecondaryView(misModel, grainModel, angle)),
				new CompositeView(new MagnitudeView(misModel, grainModel), new ThresholdSecondaryView(misModel, grainModel, angle)),
				new CompositeView(new OrientationView(omModel), new ThresholdSecondaryView(misModel, grainModel, angle)),
				new CompositeView(new GrainLabelView(misModel, grainModel), new ThresholdSecondaryView(misModel, grainModel, angle))
			);

	}



	

}

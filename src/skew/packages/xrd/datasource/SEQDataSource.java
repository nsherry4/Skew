package skew.packages.xrd.datasource;

import java.io.File;
import java.util.List;

import ca.sciencestudio.process.xrd.util.SequenceEntry;

import com.ezware.dialog.task.TaskDialogs;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import skew.core.datasource.Acceptance;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.impl.CompositeView;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.models.OrientationMatrix.IOrientationMatrix;
import skew.packages.misorientation.datasource.MisorientationDataSource;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;
import skew.packages.misorientation.view.GrainSecondaryView;
import skew.packages.misorientation.view.grain.GrainLabelView;
import skew.packages.misorientation.view.grain.OrientationView;
import skew.packages.misorientation.view.misangle.InterGrainView;
import skew.packages.misorientation.view.misangle.LocalView;
import skew.packages.misorientation.view.misangle.MagnitudeView;
import skew.packages.xrd.model.XRDPoint;
import skew.packages.xrdstrain.view.StrainView;
import skew.packages.xrdstrain.view.StressView;

public class SEQDataSource extends MisorientationDataSource
{

	
	public MisAnglePoint createPoint(int index, int x, int y)
	{
		return new XRDPoint(index, x, y);
	}
	
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
	public MapExecutor<String, String> loadPoints(final MisAngleGrid<? extends MisAnglePoint> values, List<String> filenames)
	{

		
		
		final String filename = filenames.get(0); 
		
		try {
			
			List<String> lines = FStringInput.lines(new File(filename)).toSink();
			
			//strip header
			if (lines.get(0).trim().startsWith("IMAGE")) lines.remove(0);
			
			FnMap<String, String> eachFilename = new FnMap<String, String>(){
	
				@Override
				public String f(String line) {
	
					SequenceEntry seq = new SequenceEntry(line);
					int index = seq.imageNumber();
					
					XRDPoint point = (XRDPoint)values.get(index);
					
					point.orientation.setHasOMData(loadOrientationMatrix(seq, point.orientation));
					
					point.str.setHasStrainData(loadStrain(seq, point));

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
	
	private static boolean loadOrientationMatrix(SequenceEntry seq, IOrientationMatrix om)
	{
		
		int quality = seq.indexQuality();
		if (quality <= 0) return false;
		
		om.setInverse(seq.orientationMatrix());
		Calculation.invert3(om.getInverse(), om.getDirect());
		om.setMatrixIndex(seq.imageNumber());
				
		return true;
		
	}

	private static boolean loadStrain(SequenceEntry seq, XRDPoint point)
	{
		int quality = seq.indexQuality();
		if (quality <= 0) return false;
		
		float[][] strain = seq.strainMatrix();
		
		point.strain()[0] = strain[0][0];
		point.strain()[1] = strain[1][1];
		point.strain()[2] = strain[2][2];
		point.strain()[3] = strain[0][1];
		point.strain()[4] = strain[0][2];
		point.strain()[5] = strain[1][2];
		point.strain()[6] = SequenceEntry.vonMises(strain);
		
		
		float[][] stress = seq.stressMatrix();
		
		point.stress()[0] = stress[0][0];
		point.stress()[1] = stress[1][1];
		point.stress()[2] = stress[2][2];
		point.stress()[3] = stress[0][1];
		point.stress()[4] = stress[0][2];
		point.stress()[5] = stress[1][2];
		point.stress()[6] = SequenceEntry.vonMises(stress);
		
		return true;
		
	}
	

	
	
	@Override
	public List<MapView> getViews()
	{
		return new FList<MapView>(
				new CompositeView(new LocalView(), new GrainSecondaryView()),
				new CompositeView(new InterGrainView(), new GrainSecondaryView()),
				new CompositeView(new MagnitudeView(), new GrainSecondaryView()),
				new CompositeView(new OrientationView(), new GrainSecondaryView()),
				new CompositeView(new GrainLabelView(), new GrainSecondaryView()),
				new CompositeView(new StrainView(), new GrainSecondaryView()),
				new CompositeView(new StressView(), new GrainSecondaryView())
			);

	}
	


}

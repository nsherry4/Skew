package skew.packages.xrd.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.impl.BasicSkewPoint;
import skew.core.model.impl.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.impl.CompositeView;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;
import skew.models.OrientationMatrix.IOrientationMatrix;
import skew.models.XRDStrain.IXRDStrain;
import skew.models.XRDStrain.XRDStrain;
import skew.packages.misorientation.datasource.MisorientationDataSource;
import skew.packages.misorientation.datasource.calculation.misorientation.Calculation;
import skew.packages.misorientation.view.GrainSecondaryView;
import skew.packages.misorientation.view.grain.GrainLabelView;
import skew.packages.misorientation.view.grain.OrientationView;
import skew.packages.misorientation.view.misangle.InterGrainView;
import skew.packages.misorientation.view.misangle.LocalView;
import skew.packages.misorientation.view.misangle.MagnitudeView;
import skew.packages.xrdstrain.view.StrainView;
import skew.packages.xrdstrain.view.StressView;
import ca.sciencestudio.process.xrd.util.SequenceEntry;

import com.ezware.dialog.task.TaskDialogs;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;

public class SEQDataSource extends MisorientationDataSource implements IDataSource
{

	
	SkewGrid<BasicSkewPoint<IXRDStrain>> strainModel;
	
	public MisAnglePoint createPoint(int index, int x, int y)
	{
		return new MisAnglePoint(x, y, index);
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

	public MapExecutor<String, String> loadPoints(final MisAngleGrid<? extends MisAnglePoint> misangleGrid, List<String> filenames)
	{

		misModel = misangleGrid;
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
					
					//Grab misorientation model information
					MisAnglePoint misAnglePoint = misangleGrid.get(index);
					misAnglePoint.orientation.setHasOMData(loadOrientationMatrix(seq, misAnglePoint.orientation));
					
					//Grab strain model information
					BasicSkewPoint<IXRDStrain> strPoint = strainModel.get(index);
					strPoint.setValid(loadStrain(seq, strPoint.getData()));

					
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

	private static boolean loadStrain(SequenceEntry seq, IXRDStrain point)
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
				new CompositeView(new LocalView(misModel), new GrainSecondaryView(misModel)),
				new CompositeView(new InterGrainView(misModel), new GrainSecondaryView(misModel)),
				new CompositeView(new MagnitudeView(misModel), new GrainSecondaryView(misModel)),
				new CompositeView(new OrientationView(misModel), new GrainSecondaryView(misModel)),
				new CompositeView(new GrainLabelView(misModel), new GrainSecondaryView(misModel)),
				new CompositeView(new StrainView(strainModel), new GrainSecondaryView(misModel)),
				new CompositeView(new StressView(strainModel), new GrainSecondaryView(misModel))
			);

	}

	private void createEmptyModels(Coord<Integer> mapsize)
	{
		List<MisAnglePoint> misanglePoints = new ArrayList<MisAnglePoint>();
		List<BasicSkewPoint<IXRDStrain>> strainPoints = new ArrayList<BasicSkewPoint<IXRDStrain>>();
		
		for (int i = 0; i < mapsize.x * mapsize.y; i++)
		{
			misanglePoints.add(new MisAnglePoint(i % mapsize.x, i / mapsize.x, i));
			strainPoints.add(new BasicSkewPoint<IXRDStrain>(i % mapsize.x, i / mapsize.x, i, new XRDStrain()));
		}
		
		misModel = new MisAngleGrid<MisAnglePoint>(mapsize.x, mapsize.y, misanglePoints);
		strainModel = new SkewGrid<BasicSkewPoint<IXRDStrain>>(mapsize.x, mapsize.y, strainPoints);
		
	}
	
	@Override
	public ExecutorSet<ISkewDataset> calculate(List<String> filenames, Coord<Integer> mapsize) {
		
		createEmptyModels(mapsize);
		return Calculation.calculate(filenames, this, mapsize);
				
	}
	


}

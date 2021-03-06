package skew.datasources.xrd.sequence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import scitypes.Coord;
import skew.core.datasource.DataSource;
import skew.core.datasource.DataSourceDescription;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.core.viewer.modes.views.CompositeView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.MisorientationDataSource;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import skew.models.strain.IXRDStrain;
import skew.models.strain.XRDStrain;
import skew.views.OrientationView;
import skew.views.misorientation.GrainLabelView;
import skew.views.misorientation.ThresholdSecondaryView;
import skew.views.misorientation.InterGrainView;
import skew.views.misorientation.LocalView;
import skew.views.misorientation.MagnitudeView;
import skew.views.strain.StrainView;
import skew.views.strain.StressView;
import ca.sciencestudio.process.xrd.util.SequenceEntry;

import com.ezware.dialog.task.TaskDialogs;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;

public class SEQDataSource extends MisorientationDataSource implements DataSource
{
	SkewGrid<IXRDStrain> strainModel;
	
	public ISkewPoint<MisAngle> createPoint(int index, int x, int y)
	{
		return new SkewPoint<MisAngle>(x, y, index, new MisAngle());
	}
	
	@Override
	public DataSourceDescription getDescription() {
		return new DataSourceDescription("Sequence", "FOXMAS/XMAS Text Sequence File", "seq");
	}
	

	@Override
	public FileFormatAcceptance accepts(List<String> filenames)
	{
		if (filenames.size() > 1) return FileFormatAcceptance.REJECT;
		boolean accept = filenames.get(0).toLowerCase().endsWith("seq");
		return accept ? FileFormatAcceptance.ACCEPT : FileFormatAcceptance.REJECT;
	}

	public MapExecutor<String, String> loadPoints(List<String> filenames)
	{

		final String filename = filenames.get(0); 
		
		try {
			
			List<String> lines = FStringInput.lines(new File(filename)).toSink();
			
			//strip header
			if (lines.get(0).trim().startsWith("IMAGE")) lines.remove(0);
			
			FnMap<String, String> eachFilename = (line) -> {
	
				SequenceEntry seq = new SequenceEntry(line);
				int index = seq.imageNumber();
				
				//Grab misorientation model information
				ISkewPoint<IOrientationMatrix> omPoint = misdata.omModel.getPoint(index);
				omPoint.setValid(loadOrientationMatrix(seq, omPoint.getData()));
				
				//Grab strain model information
				ISkewPoint<IXRDStrain> strPoint = strainModel.getPoint(index);
				strPoint.setValid(loadStrain(seq, strPoint.getData()));

				
				return "";
			};
				
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
				new CompositeView(new LocalView(misdata.misModel), misdata.grainView()),
				new CompositeView(new InterGrainView(misdata.misModel, misdata.grainModel), misdata.grainView()),
				new CompositeView(new MagnitudeView(misdata.misModel, misdata.grainModel), misdata.grainView()),
				new CompositeView(new OrientationView(misdata.omModel), misdata.grainView()),
				new CompositeView(new GrainLabelView(misdata.misModel, misdata.grainModel), misdata.grainView()),
				new CompositeView(new StrainView(strainModel), misdata.grainView()),
				new CompositeView(new StressView(strainModel), misdata.grainView())
			);

	}

	private void createStrainModel(Coord<Integer> mapsize)
	{
		//List<ISkewPoint<MisAngle>> misanglePoints = new ArrayList<ISkewPoint<MisAngle>>();
		List<ISkewPoint<IXRDStrain>> strainPoints = new ArrayList<ISkewPoint<IXRDStrain>>();
		
		for (int i = 0; i < mapsize.x * mapsize.y; i++)
		{
			//misanglePoints.add(new SkewPoint<MisAngle>(i % mapsize.x, i / mapsize.x, i, new MisAngle()));
			strainPoints.add(new SkewPoint<IXRDStrain>(i % mapsize.x, i / mapsize.x, i, new XRDStrain()));
		}
		
		//grainModel = new GrainModel(mapsize.x, mapsize.y);
		//misModel = new SkewGrid<MisAngle>(mapsize.x, mapsize.y, misanglePoints);
		strainModel = new SkewGrid<IXRDStrain>(mapsize.x, mapsize.y, strainPoints);
		
	}
	
	@Override
	public ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize) {
		
		super.createModels(mapsize);
		createStrainModel(mapsize);
		
		return Calculation.calculate(filenames, loadPoints(filenames), this, misdata, mapsize, misdata.boundaryParameter.getValue());
				
	}

	@Override
	public FileOrFolder fileOrFolder() {
		return FileOrFolder.FILE;
	}



}

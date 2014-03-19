package skew.datasources.xrd.strfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.sciencestudio.process.xrd.util.SequenceEntry;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnGet;
import plural.executor.ExecutorSet;
import plural.executor.PluralExecutor;
import autodialog.model.Parameter;
import scitypes.Coord;
import skew.core.datasource.BasicDataSource;
import skew.core.datasource.DataSource;
import skew.core.model.IModel;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.views.CompositeView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.calculation.misorientation.FoxmasFileName;
import skew.models.misorientation.MisAngle;
import skew.models.strain.IXRDStrain;
import skew.models.strain.XRDStrain;
import skew.views.strain.StrainView;
import skew.views.strain.StressView;

public class StrDataSource extends BasicDataSource
{

	public StrDataSource() {
		super("str", "FOXMAS str Files", "Strain");		
	}



	private static final int startNum = 1;
	
	private ISkewGrid<IXRDStrain> strainModel;
	
	@Override
	public FileOrFolder fileOrFolder() {
		return FileOrFolder.FILE;
	}

	@Override
	public FileFormatAcceptance accepts(List<String> filenames) {
		for (String file : filenames) {
			if (!file.toLowerCase().endsWith("str")) return FileFormatAcceptance.REJECT;
		}
		return FileFormatAcceptance.ACCEPT;
	}

	@Override
	public List<Parameter<?>> getLoadParameters() {
		return new ArrayList<>();
	}

	@Override
	public String getLoadParametersInformation() {
		return "";
	}

	@Override
	public List<Parameter<?>> getRuntimeParameters() {
		return new ArrayList<>();
	}

	@Override
	public List<MapView> getViews() {
		
		List<MapView> views = new FList<MapView>(new StrainView(strainModel), new StressView(strainModel));
		
		return views;
		
	}

	
	@Override
	public void recalculate() {}
	
	
	
	@Override
	public List<IModel> load(List<String> filenames, Coord<Integer> mapsize, PluralExecutor executor) {
		
		strainModel = DataSource.getEmptyGrid(mapsize, new FnGet<IXRDStrain>(){
			@Override public IXRDStrain f() { return new XRDStrain(); }});
		
		executor.setStalling(false);
		executor.setWorkUnits(filenames.size());
		
		for (String filename : filenames) {
			
			executor.workUnitCompleted();
			
			int index = FoxmasFileName.getFileNumber(filename)-startNum;
			if (index >= strainModel.size()) continue;
			
			processStrainMapData(filename, strainModel.getPoint(index));
			
		}
		
		return new FList<IModel>(strainModel);
		
	}

	
	
	
	
	
	
	
	
	
	
/*
 * BORROWED FROM PROCESSXRD - BAD CODE
 */
	
	// parse the provided file for strain map data
	private void processStrainMapData(String fileName, ISkewPoint<IXRDStrain> point)
	{

		// variables
		float strainTensor[][] = new float[3][3];
		float stress[][] = new float[3][3];

		if (loadStrainMatrix(strainTensor, stress, fileName))
		{

			IXRDStrain strain = point.getData();
			
			strain.strain()[0] = strainTensor[0][0];
			strain.strain()[1] = strainTensor[1][1];
			strain.strain()[2] = strainTensor[2][2];
			strain.strain()[3] = strainTensor[0][1];
			strain.strain()[4] = strainTensor[0][2];
			strain.strain()[5] = strainTensor[1][2];
			strain.strain()[6] = SequenceEntry.vonMises(strainTensor);
			
			
			strain.stress()[0] = stress[0][0];
			strain.stress()[1] = stress[1][1];
			strain.stress()[2] = stress[2][2];
			strain.stress()[3] = stress[0][1];
			strain.stress()[4] = stress[0][2];
			strain.stress()[5] = stress[1][2];
			strain.stress()[6] = SequenceEntry.vonMises(stress);
			
			point.setValid(true);
			
		}

	}
	
	

	private boolean loadStrainMatrix(float strainTensor[][], float stress[][], String inputFile)
	{


		BufferedReader reader = null;
		try
		{
			String line;
			String delims = " ";
			String[] tokens;


			//BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			//reader = new BufferedReader(new FileReader(inputFile));
			reader = new BufferedReader(new StringReader(FStringInput.contents(new File(inputFile))));
			line = reader.readLine();

			while (line != null)
			{
				if (line.startsWith("deviatoric strain in ") && line.endsWith(" reference X Y Z (*10^3)"))
				{
					for (int i = 0; i < 3; i++)
					{
						line = reader.readLine();
						tokens = line.split(delims);

						List<String> list = new ArrayList<String>(Arrays.asList(tokens));
						list.removeAll(Arrays.asList(""));
						tokens = list.toArray(tokens);

						strainTensor[i][0] = Float.parseFloat(tokens[0]);
						strainTensor[i][1] = Float.parseFloat(tokens[1]);
						strainTensor[i][2] = Float.parseFloat(tokens[2]);
					}
				}

				if (line.startsWith("deviatoric stress in ") && line.endsWith(" reference X Y Z (MPa)"))
				{
					for (int i = 0; i < 3; i++)
					{
						line = reader.readLine();
						tokens = line.split(delims);

						List<String> list = new ArrayList<String>(Arrays.asList(tokens));
						list.removeAll(Arrays.asList(""));
						tokens = list.toArray(tokens);

						stress[i][0] = Float.parseFloat(tokens[0]);
						stress[i][1] = Float.parseFloat(tokens[1]);
						stress[i][2] = Float.parseFloat(tokens[2]);
					}
				}

				line = reader.readLine();
			}

			// close the file
			reader.close();

			return true;
		}

		catch(Exception e)
		{
			e.printStackTrace();
			return false;

		} finally {
			// Close resource.
			//
			try { 
				//System.out.println("close it");
				if (reader != null){ 
					reader.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	

}

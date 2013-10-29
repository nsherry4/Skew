package skew.datasources.misorientation.datasource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import skew.core.datasource.Acceptance;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.views.CompositeView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.datasources.misorientation.datasource.calculation.misorientation.IndexFileName;
import skew.models.orientation.IOrientationMatrix;
import skew.views.OrientationView;
import skew.views.misorientation.GrainLabelView;
import skew.views.misorientation.GrainSecondaryView;
import skew.views.misorientation.InterGrainView;
import skew.views.misorientation.LocalView;
import skew.views.misorientation.MagnitudeView;
import autodialog.model.Parameter;
import fava.functionable.FList;
import fava.signatures.FnMap;

public class INDDataSource extends MisorientationDataSource
{
	private static int startNum=1;
	

	@Override
	public String extension()
	{
		return "ind";
	}
	
	@Override
	public String title()
	{
		return "Index";
	}

	@Override
	public String description()
	{
		return "XMAS/FOXMAS Index Files";
	}

	@Override
	public Acceptance accepts(List<String> filenames)
	{
		boolean accepts = false;
		for (String filename : filenames)
		{
			accepts |= filename.toLowerCase().endsWith("ind");
		}
		return accepts ? Acceptance.ACCEPT : Acceptance.REJECT;
	}
	

	
	@Override
	public MapExecutor<String, String> loadPoints(List<String> filenames)
	{

		FnMap<String, String> eachFilename = new FnMap<String, String>(){

			@Override
			public String f(String filename) {
				int index = IndexFileName.getFileNumber(filename)-startNum;
				if (index >= omModel.size()) return "";
				ISkewPoint<IOrientationMatrix> om = omModel.getPoint(index);
				om.setValid(loadOM(filename, om.getData()));
				return "";
			}};
			
		MapExecutor<String, String> exec = new PluralMapExecutor<String, String>(filenames, eachFilename);
		exec.setName("Reading Files");
		return exec;
	}
	
	
	public boolean loadOM(String inputFile, IOrientationMatrix om)
	{
		BufferedReader reader = null;
		String line;
		String delims = " ";
		String[] tokens = null;

		int quality = 0;

		try
		{

			reader = new BufferedReader(new FileReader(inputFile));

			// skip first three lines
			reader.readLine();
			reader.readLine();
			reader.readLine();

			line = reader.readLine();
			while (line != null)
			{
				if (line.startsWith("Number of indexed reflections"))
				{
					tokens = line.split(delims);
					break;
				}
				else
				{
					line = reader.readLine();
				}
			}

			if (tokens != null)
			{
				quality = Integer.parseInt(tokens[4]);
			}
			// skip more lines until reaching the line for the matrix
			for (int i = 0; i < quality + 7; i++)
			{
				line = reader.readLine();

			}


			// load the matrix
			for (int i = 0; i < 3; i++)
			{
				line = reader.readLine();
				tokens = line.split(delims);

				ArrayList<String> list = new ArrayList<String>(Arrays.asList(tokens));
				list.removeAll(Arrays.asList(""));
				tokens = list.toArray(tokens);

				om.getInverse()[i][0] = Float.parseFloat(tokens[0]);// Float.parseFloat(tokens[0]);
				om.getInverse()[i][1] = Float.parseFloat(tokens[1]);// Float.parseFloat(tokens[1]);
				om.getInverse()[i][2] = Float.parseFloat(tokens[2]);// Float.parseFloat(tokens[2]);
			}

			// close the file
			reader.close();

			Calculation.invert3(om.getInverse(), om.getDirect());
			
			
			return true;
		}
		catch (Exception e)
		{
			System.out.println("Error reading " + inputFile);
			return false;
		}
		finally
		{
			// Close resource.
			//
			try
			{
				if (reader != null) { reader.close(); }
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public List<MapView> getViews()
	{
		return new FList<MapView>(
				new CompositeView(new LocalView(misModel), new GrainSecondaryView(misModel, grainModel)),
				new CompositeView(new InterGrainView(misModel, grainModel), new GrainSecondaryView(misModel, grainModel)),
				new CompositeView(new MagnitudeView(misModel, grainModel), new GrainSecondaryView(misModel, grainModel)),
				new CompositeView(new OrientationView(omModel), new GrainSecondaryView(misModel, grainModel)),
				new CompositeView(new GrainLabelView(misModel, grainModel), new GrainSecondaryView(misModel, grainModel))
			);

	}



}

package skew.datasources.misorientation.datasource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plural.executor.ExecutorSet;
import plural.executor.map.MapExecutor;
import plural.executor.map.implementations.PluralMapExecutor;
import scitypes.Coord;
import scitypes.DirectionVector;
import skew.core.datasource.DataSource;
import skew.core.model.ISkewDataset;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.views.CompositeView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.datasources.misorientation.datasource.calculation.misorientation.FoxmasFileName;
import skew.models.orientation.IOrientationMatrix;
import skew.views.OrientationView;
import skew.views.misorientation.GrainLabelView;
import skew.views.misorientation.InterGrainView;
import skew.views.misorientation.LocalView;
import skew.views.misorientation.MagnitudeView;
import skew.views.xrdmeta.EllipticityView;
import skew.views.xrdmeta.IndexQualityView;
import fava.functionable.FList;
import fava.signatures.FnGet;
import fava.signatures.FnMap;

public class INDDataSource extends MisorientationDataSource
{
	private static int startNum=1;
	
	public ISkewGrid<Integer> qualityModel;
	public ISkewGrid<DirectionVector> ellipModel;
	
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
	public FileFormatAcceptance accepts(List<String> filenames)
	{
		boolean accepts = false;
		for (String filename : filenames)
		{
			accepts |= filename.toLowerCase().endsWith("ind");
		}
		return accepts ? FileFormatAcceptance.ACCEPT : FileFormatAcceptance.REJECT;
	}
	

	
	public MapExecutor<String, String> loadPoints(List<String> filenames)
	{

		FnMap<String, String> eachFilename = (filename) -> {
			int index = FoxmasFileName.getFileNumber(filename)-startNum;
			if (index >= misdata.omModel.size()) return "";
			
			//load orientation
			loadOM(
					filename, 
					misdata.omModel.getPoint(index), 
					qualityModel.getPoint(index), 
					ellipModel.getPoint(index)
				);
			
			return "";
		};
			
		MapExecutor<String, String> exec = new PluralMapExecutor<String, String>(filenames, eachFilename);
		exec.setName("Reading Files");
		return exec;
	}
	
	
	public void loadOM(
			String inputFile, 
			ISkewPoint<IOrientationMatrix> omPoint, 
			ISkewPoint<Integer> qualityPoint,
			ISkewPoint<DirectionVector> ellipPoint
		)
	{
		
		IOrientationMatrix om = omPoint.getData();
		
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
				qualityPoint.setData(quality);
			}
			
			
			// skip more lines until reaching the line for the matrix
			//also read ellipticity out of here
			float referenceAngle = 0f;
			DirectionVector vector = new DirectionVector();
			for (int i = 0; i < quality + 7; i++)
			{
				line = reader.readLine();
				if (!(i > 0 && i <= quality)) continue;
				
				// break the line into tokens and remove spaces
				tokens = line.split(delims);
				List<String> list = new FList<>(tokens);
				list.removeAll(Arrays.asList(""));
				tokens = list.toArray(tokens);
				
				if (!(tokens.length > 15)) continue;
				
				// use the first angle as a reference point
				if (i == 1)
				{
					referenceAngle = Float.parseFloat(tokens[15]);
					float ydist = Float.parseFloat(tokens[14]);
					float xdist = Float.parseFloat(tokens[13]);

					vector = new DirectionVector(Math.max(xdist, ydist), Float.parseFloat(tokens[15]));
				}

				else
				{
					// calculate the angle and also the opposite direction vector
					float angle1 = Float.parseFloat(tokens[15]);
					float angle2 = angle1 + 180;

					// find the closest to the reference point
					float dif1 = Math.abs(referenceAngle - angle1);
					if (dif1 > 180) { dif1 = 360 - dif1; }

					float dif2 = Math.abs(referenceAngle - angle2);
					if (dif2 > 180) { dif2 = 360 - dif2; }

					if (dif1 < dif2)	{ vector = vector.add(new DirectionVector(Float.parseFloat(tokens[14]), angle1)); }
					else				{ vector = vector.add(new DirectionVector(Float.parseFloat(tokens[14]), angle2)); }
				}

			}
			ellipPoint.setData(vector);
			ellipPoint.setValid(true);


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
			
			omPoint.setValid(true);
			qualityPoint.setValid(true);
			
		}
		catch (Exception e)
		{
			System.out.println("Error reading " + inputFile);
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
				new CompositeView(new LocalView(misdata.misModel), misdata.grainView()),
				new CompositeView(new InterGrainView(misdata.misModel, misdata.grainModel), misdata.grainView()),
				new CompositeView(new MagnitudeView(misdata.misModel, misdata.grainModel), misdata.grainView()),
				new CompositeView(new OrientationView(misdata.omModel), misdata.grainView()),
				new CompositeView(new GrainLabelView(misdata.misModel, misdata.grainModel), misdata.grainView()),
				new CompositeView(new IndexQualityView(qualityModel), misdata.grainView()),
				new CompositeView(new EllipticityView(ellipModel), misdata.grainView())
			);

	}


	@Override
	public FileOrFolder fileOrFolder() {
		return FileOrFolder.FILE;
	}
	

	public void createModels(Coord<Integer> mapSize) {
		super.createModels(mapSize);
		qualityModel = DataSource.createGrid(mapSize, () -> 0);
		ellipModel = DataSource.createGrid(mapSize, DirectionVector::new);
	}
	
	@Override
	public ExecutorSet<ISkewDataset> loadDataset(List<String> filenames, Coord<Integer> mapsize) {
		createModels(mapsize);
		return Calculation.calculate(filenames, loadPoints(filenames), this, misdata, mapsize, misdata.boundaryParameter.getValue());
	}

}

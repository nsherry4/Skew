package skew.packages.xrd.strain.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.BasicDataSource;
import skew.core.datasource.DataSource;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.models.XRDStrain.IXRDStrain;
import skew.models.XRDStrain.XRDStrain;
import skew.packages.misorientation.datasource.calculation.misorientation.IndexFileName;
import skew.packages.xrd.strain.XRDStrainUtil;
import skew.packages.xrd.strain.view.StrainView;
import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnEach;
import fava.signatures.FnGet;

public class PairSubtractionDataSource extends BasicDataSource
{
	String g = "Overlay";
	private Parameter<Integer> hShift = new Parameter<>("Horizontal Shift", new IntegerEditor(), 0, g);
	private Parameter<Integer> vShift = new Parameter<>("Vertical Shift", new IntegerEditor(), 0, g);
	
	private static String ext = "pair";
	private int startNum = 1;
	private List<MapView> views;
	
	private ISkewGrid<IXRDStrain> beforeModel;
	private ISkewGrid<IXRDStrain> afterModel;
	private ISkewGrid<IXRDStrain> subtractModel;
	
	private Coord<Integer> dimensions;
	
	public PairSubtractionDataSource() {
		super(ext, "Strain Difference", "Strain Difference");
		views = new ArrayList<MapView>();
		
		
	}

	@Override
	public Acceptance accepts(List<String> filenames) {
		for (String filename : filenames)
		{
			if (!filename.endsWith(ext)) { return Acceptance.REJECT; }
		}
		
		return Acceptance.ACCEPT;
			
	}

	@Override
	public List<MapView> getViews() {
		return new ArrayList<MapView>(views);
	}

	@Override
	public List<ISkewGrid<?>> load(final List<String> filenames, final Coord<Integer> mapsize, final PluralExecutor executor) {

		dimensions = mapsize;
		
		executor.setStalling(false);
		executor.setWorkUnits(filenames.size());
		
		
		final List<ISkewPoint<IXRDStrain>> beforeList = DataSource.getEmptyPoints(dimensions, new FnGet<IXRDStrain>() {

			@Override
			public IXRDStrain f() {
				return new XRDStrain();
			}
		});
		
		final List<ISkewPoint<IXRDStrain>> afterList = DataSource.getEmptyPoints(dimensions, new FnGet<IXRDStrain>() {

			@Override
			public IXRDStrain f() {
				return new XRDStrain();
			}
		});
		
		final List<ISkewPoint<IXRDStrain>> subtractList = DataSource.getEmptyPoints(dimensions, new FnGet<IXRDStrain>() {

			@Override
			public IXRDStrain f() {
				return new XRDStrain();
			}
		});
		
						
		FList.wrap(filenames).each(new FnEach<String>() {

			private void readFilePart(ISkewPoint<IXRDStrain> point, String filePart)
			{
				IXRDStrain data = point.getData();
				FStringInput input = FStringInput.lines(filePart);
				String line;
				
				line = input.next(); //discard first line
				line = input.next(); //check if next line is (not available) 
				if (line.startsWith("(not available)")) return;
				
				List<String> contents = FStringInput.tokens(line  + " " + input.next() + " " +  input.next(), "\\s+").toSink();
				
				data.strain()[0] = Double.parseDouble(contents.get(0));	//XX
				data.strain()[1] = Double.parseDouble(contents.get(1));	//XY
				data.strain()[2] = Double.parseDouble(contents.get(2));	//XZ
				data.strain()[3] = Double.parseDouble(contents.get(4));	//YY
				data.strain()[4] = Double.parseDouble(contents.get(5));	//YZ
				data.strain()[5] = Double.parseDouble(contents.get(8));	//ZZ
				//don't do von-mises here, we have to do it after the subtraction
				
				point.setValid(true);
			}
			
			@Override
			public void f(String filename) {
				
				int index = IndexFileName.getFileNumber(filename)-startNum;
				
				try {
					FList<String> fileParts = FStringInput.tokens(new File(filename), "\n\n").toSink();					
					readFilePart(beforeList.get(index), fileParts.get(0).trim());
					readFilePart(afterList.get(index), fileParts.get(1).trim());
					
					executor.workUnitCompleted();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		//Create a new Grid/Model from this list of points
		beforeModel = new SkewGrid<IXRDStrain>(dimensions.y, dimensions.x, beforeList);
		afterModel = new SkewGrid<IXRDStrain>(dimensions.y, dimensions.x, afterList);
		subtractModel = new SkewGrid<IXRDStrain>(dimensions.y, dimensions.x, subtractList);

		recalculate();
		
		//Create a new View from this model
		views.add(new StrainView(subtractModel));
		
		//Return a list of Grids/Models 
		return new FList<ISkewGrid<?>>(subtractModel);
	}
	
	private void subtractStrain(IXRDStrain difference, IXRDStrain before, IXRDStrain after)
	{
		for (int i = 0; i < 7; i++){
			difference.strain()[i] = after.strain()[i] - before.strain()[i];
		}
	}
	
	@Override //Manual
	protected String getDatasetTitle(List<String> filenames)
	{
		return super.getDatasetTitle(filenames) + " (" + hShift.getValue().toString() + ", " + vShift.getValue().toString() + ")";  
	}
	
	@Override
	public List<Parameter<?>> getLoadParameters() {
		List<Parameter<?>> params = new FList<>();
		params.add(hShift);
		params.add(vShift);
		return params;
	}
	
	@Override
	public String getLoadParametersInformation() {
		return "This type of data requires a subtraction of 'before' and 'after' scans. The additional 'Shift' parameters will shift the 'before' dataset prior to performing the subtraction.";
	}

	@Override
	public List<Parameter<?>> getRuntimeParameters() {
		List<Parameter<?>> params = new FList<>();
		params.add(hShift);
		params.add(vShift);
		return params;
	}

	@Override
	public void recalculate() {
		
		int tx = (Integer)hShift.getValue();
		int ty = (Integer)vShift.getValue();
		
		//After = After - Before
		for (int x = 0; x < dimensions.x; x++)	{
			for (int y = 0; y < dimensions.y; y++) {
				
				//offset x/y for before
				int dx = x - tx;
				int dy = y - ty;
				
				
				//look up the points in the three models
				ISkewPoint<IXRDStrain> differencePoint = subtractModel.get(x, y);
				//set point to invalid initially
				differencePoint.setValid(false);
				//range check
				if (dx < 0 || dx >= dimensions.x || dy < 0 || dy >= dimensions.y) continue;
				ISkewPoint<IXRDStrain> afterPoint = afterModel.get(x, y);
				ISkewPoint<IXRDStrain> beforePoint = beforeModel.get(dx, dy);
				
				//make sure both before and after points are valid
				if (!afterPoint.isValid() || !beforePoint.isValid()) continue;
				
				//perform the subtraction, storing the result in differencePoint, and set that point valid
				subtractStrain(differencePoint.getData(), beforePoint.getData(), afterPoint.getData());
				differencePoint.getData().strain()[6] = XRDStrainUtil.vonMises(differencePoint.getData().strain());
				differencePoint.setValid(true);
				
			}
		}
		
	}
	

}

package skew.datasources.xrd.subtraction;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.BasicDataSource;
import skew.core.datasource.DataSource;
import skew.core.model.IModel;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.core.model.TranslatingSkewGrid;
import skew.core.viewer.modes.views.CompositeView;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.calculation.magnitude.GrainIdentify;
import skew.datasources.misorientation.datasource.calculation.magnitude.Magnitude;
import skew.datasources.misorientation.datasource.calculation.misorientation.Calculation;
import skew.datasources.misorientation.datasource.calculation.misorientation.IndexFileName;
import skew.datasources.xrd.XRDUtil;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;
import skew.models.orientation.IOrientationMatrix;
import skew.models.orientation.OrientationMatrix;
import skew.models.strain.IXRDStrain;
import skew.models.strain.XRDStrain;
import skew.views.misorientation.GrainSecondaryView;
import skew.views.misorientation.LocalView;
import skew.views.strain.StrainView;
import skew.views.subtraction.LocalSubtractionView;
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
	
	
	private ISkewGrid<IXRDStrain> beforeStrModel, afterStrModel, subtractStrModel;
	private ISkewGrid<IOrientationMatrix> beforeOMModel, afterOMModel;
	private ISkewGrid<MisAngle> beforeMisModel, afterMisModel, subtractMisModel;
	
	private TranslatingSkewGrid<MisAngle> beforeTranslatedMisModel, afterTranslatedMisModel;
	private GrainModel beforeGrainModel, afterGrainModel;
	
	
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
	public List<IModel> load(final List<String> filenames, final Coord<Integer> mapsize, final PluralExecutor executor) {

		dimensions = mapsize;
		
		executor.setStalling(false);
		executor.setWorkUnits(filenames.size());
		
		
		//Strain models
		FnGet<IXRDStrain> getStrPoint = new FnGet<IXRDStrain>() {

			@Override
			public IXRDStrain f() {	return new XRDStrain(); }
		};
		final List<ISkewPoint<IXRDStrain>> beforeStrList = DataSource.getEmptyPoints(dimensions, getStrPoint);
		final List<ISkewPoint<IXRDStrain>> afterStrList = DataSource.getEmptyPoints(dimensions, getStrPoint);
		final List<ISkewPoint<IXRDStrain>> subtractStrList = DataSource.getEmptyPoints(dimensions, getStrPoint);
		
		
		
		//Orientation models
		FnGet<IOrientationMatrix> getOMPoint = new FnGet<IOrientationMatrix>() {

			@Override
			public IOrientationMatrix f() {	return new OrientationMatrix(); }
		};		
		final List<ISkewPoint<IOrientationMatrix>> beforeOMList = DataSource.getEmptyPoints(dimensions, getOMPoint);
		final List<ISkewPoint<IOrientationMatrix>> afterOMList = DataSource.getEmptyPoints(dimensions, getOMPoint);
		
		
		
		//MisAngle models
		FnGet<MisAngle> getMisPoint = new FnGet<MisAngle>() {

			@Override
			public MisAngle f() {	return new MisAngle(); }
		};		
		beforeMisModel = new SkewGrid<MisAngle>(dimensions.x , dimensions.y, DataSource.getEmptyPoints(dimensions, getMisPoint));
		afterMisModel = new SkewGrid<MisAngle>(dimensions.x , dimensions.y, DataSource.getEmptyPoints(dimensions, getMisPoint));
		subtractMisModel = new SkewGrid<MisAngle>(dimensions.x , dimensions.y, DataSource.getEmptyPoints(dimensions, getMisPoint));
		
		
		beforeGrainModel = new GrainModel(dimensions.x , dimensions.y);
		afterGrainModel = new GrainModel(dimensions.x , dimensions.y);
		
		
		FList.wrap(filenames).each(new FnEach<String>() {

			private void readFilePart(ISkewPoint<IXRDStrain> strPoint, ISkewPoint<IOrientationMatrix> omPoint, String filePart)
			{
			
				IXRDStrain strData = strPoint.getData();
				IOrientationMatrix omData = omPoint.getData();
				
				FStringInput input = FStringInput.lines(filePart);
				String line;
				
				line = input.next(); //discard line - filename
				line = input.next(); //next line is either (not available) or the title of the matrix 
				if (line.startsWith("(not available)")) return;
				

				//Strain Matrix
				List<String> contents = FStringInput.tokens(input.next()  + " " + input.next() + " " +  input.next(), "\\s+").toSink();
				strData.strain()[0] = Double.parseDouble(contents.get(0));	//XX
				strData.strain()[1] = Double.parseDouble(contents.get(1));	//XY
				strData.strain()[2] = Double.parseDouble(contents.get(2));	//XZ
				strData.strain()[3] = Double.parseDouble(contents.get(4));	//YY
				strData.strain()[4] = Double.parseDouble(contents.get(5));	//YZ
				strData.strain()[5] = Double.parseDouble(contents.get(8));	//ZZ
				//don't do von-mises here, we have to do it after the subtraction
				strPoint.setValid(true);
				
				
				line = input.next(); //discard line - title of second matrix
				
				//Orientation Matrix
				contents = FStringInput.tokens(input.next()  + " " + input.next() + " " +  input.next(), "\\s+").toSink();
				float[][] om = new float[3][3];
				om[0][0] = Float.parseFloat(contents.get(0));
				om[0][1] = Float.parseFloat(contents.get(1));
				om[0][2] = Float.parseFloat(contents.get(2));
				om[1][0] = Float.parseFloat(contents.get(3));
				om[1][1] = Float.parseFloat(contents.get(4));
				om[1][2] = Float.parseFloat(contents.get(5));
				om[2][0] = Float.parseFloat(contents.get(6));
				om[2][1] = Float.parseFloat(contents.get(7));
				om[2][2] = Float.parseFloat(contents.get(8));
				omData.setInverse(om);
				Calculation.invert3(omData.getInverse(), omData.getDirect());
				omPoint.setValid(true);
				
			}
			
			@Override
			public void f(String filename) {
				
				int index = IndexFileName.getFileNumber(filename)-startNum;
				
				try {
					FList<String> fileParts = FStringInput.tokens(new File(filename), "\n\n").toSink();					
					readFilePart(beforeStrList.get(index), beforeOMList.get(index), fileParts.get(0).trim());
					readFilePart(afterStrList.get(index), afterOMList.get(index), fileParts.get(1).trim());
					
					executor.workUnitCompleted();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		//Create a new Grid/Model from this list of points
		beforeStrModel = new SkewGrid<IXRDStrain>(dimensions.y, dimensions.x, beforeStrList);
		afterStrModel = new SkewGrid<IXRDStrain>(dimensions.y, dimensions.x, afterStrList);
		subtractStrModel = new SkewGrid<IXRDStrain>(dimensions.y, dimensions.x, subtractStrList);
		
		beforeOMModel = new SkewGrid<IOrientationMatrix>(dimensions.y, dimensions.x, beforeOMList);
		afterOMModel = new SkewGrid<IOrientationMatrix>(dimensions.y, dimensions.x, afterOMList);
	
		calculateMisModel(beforeMisModel, beforeGrainModel, beforeOMModel);
		calculateMisModel(afterMisModel, afterGrainModel, afterOMModel);

		beforeTranslatedMisModel = new TranslatingSkewGrid<MisAngle>(beforeMisModel, 0, 0) {

			@Override
			protected MisAngle getOutOfBoundsPoint() {
				return new MisAngle();
			}};
		
		afterTranslatedMisModel = new TranslatingSkewGrid<MisAngle>(afterMisModel, 0, 0) {

			@Override
			protected MisAngle getOutOfBoundsPoint() {
				return new MisAngle();
			}};
			
			
		recalculate();
		
		MapView beforeGrain = new GrainSecondaryView(beforeTranslatedMisModel, beforeGrainModel, Color.black, false);
		MapView afterGrain = new GrainSecondaryView(afterTranslatedMisModel, afterGrainModel, Color.white, false);
		beforeGrain.setTitle(beforeGrain.getTitle() + " (Before)");
		afterGrain.setTitle(afterGrain.getTitle() + " (After)");

		
		views.add(new CompositeView(new StrainView(subtractStrModel), beforeGrain, afterGrain));
		
		
		MapView primary;
		
		primary = new LocalView(beforeMisModel);
		primary.setTitle(primary.getTitle() + " (Before)");
		views.add(new CompositeView(primary, 
				new GrainSecondaryView(beforeMisModel, beforeGrainModel, Color.black, true)
			));
		
		
		primary = new LocalView(afterMisModel);
		primary.setTitle(primary.getTitle() + " (After)");
		views.add(new CompositeView(primary, 
				new GrainSecondaryView(afterMisModel, afterGrainModel, Color.white, true)
			));
		
		
		primary = new LocalSubtractionView(subtractMisModel);
		primary.setTitle(primary.getTitle() + " (Subtracted)");
		views.add(new CompositeView(primary, beforeGrain, afterGrain));
		
		
		//Return a list of Grids/Models 
		return new FList<IModel>(subtractStrModel, beforeMisModel, afterMisModel, beforeGrainModel, afterGrainModel);
	}
	
	private void calculateMisModel(ISkewGrid<MisAngle> misModel, GrainModel grainModel, ISkewGrid<IOrientationMatrix> omGrid)
	{
		Calculation.calcLocalMisorientation(misModel, omGrid, dimensions.x, dimensions.y).executeBlocking();
			
		//calculate which grain each pixel belongs to
		GrainIdentify.calculate(misModel, grainModel);

		//create grain objects for all grain labels
		Magnitude.setupGrains(grainModel, misModel);
		
		Calculation.calculateGrainMagnitude(grainModel, misModel, omGrid).executeBlocking();
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
		
		beforeTranslatedMisModel.setTranslation(-tx, -ty);
		
		//After = After - Before
		for (int x = 0; x < dimensions.x; x++)	{
			for (int y = 0; y < dimensions.y; y++) {
				
				//offset x/y for before
				int dx = x - tx;
				int dy = y - ty;
				
				recalculateStr(x, y, dx, dy);			
				recalculateLocalMisorientation(x, y, dx, dy);
				
			}
		}
		
	}
	
	
	private void recalculateStr(int x, int y, int dx, int dy)
	{
		//look up the point in the three models
		ISkewPoint<IXRDStrain> differencePoint = subtractStrModel.getPoint(x, y);
		//set point to invalid initially
		differencePoint.setValid(false);
		//range check
		if (dx < 0 || dx >= dimensions.x || dy < 0 || dy >= dimensions.y) return;
		ISkewPoint<IXRDStrain> afterPoint = afterStrModel.getPoint(x, y);
		ISkewPoint<IXRDStrain> beforePoint = beforeStrModel.getPoint(dx, dy);
		
		//make sure both before and after points are valid
		if (!afterPoint.isValid() || !beforePoint.isValid()) return;
		
		//perform the subtraction, storing the result in differencePoint, and set that point valid
		subtractStrain(differencePoint.getData(), beforePoint.getData(), afterPoint.getData());
		differencePoint.getData().strain()[6] = XRDUtil.vonMises(differencePoint.getData().strain());
		differencePoint.setValid(true);
	}
	
	
	private void recalculateLocalMisorientation(int x, int y, int dx, int dy)
	{
		ISkewPoint<MisAngle> diffPoint = subtractMisModel.getPoint(x, y);
		diffPoint.setValid(false);
		//range check
		if (dx < 0 || dx >= dimensions.x || dy < 0 || dy >= dimensions.y) return;
		ISkewPoint<MisAngle> afterPoint = afterMisModel.getPoint(x, y);
		ISkewPoint<MisAngle> beforePoint = beforeMisModel.getPoint(dx, dy);
		
		//make sure both before and after points are valid
		//if (!afterPoint.isValid() || !beforePoint.isValid()) return;
		
		MisAngle diff = diffPoint.getData();
		MisAngle before = beforePoint.getData();
		MisAngle after = afterPoint.getData();
		
		
		if (before.north.is() && after.north.is()) 		diff.north.set(after.north.get() - before.north.get());
		if (before.south.is() && after.south.is()) 		diff.south.set(after.south.get() - before.south.get());
		if (before.east.is() && after.east.is()) 		diff.east.set(after.east.get() - before.east.get());
		if (before.west.is() && after.west.is()) 		diff.west.set(after.west.get() - before.west.get());
		if (before.average.is() && after.average.is()) 	diff.average.set(after.average.get() - before.average.get());
		
		diffPoint.setValid(true);
		
	}
	

}

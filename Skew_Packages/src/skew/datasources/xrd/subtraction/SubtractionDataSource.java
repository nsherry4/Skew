package skew.datasources.xrd.subtraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.datasource.BasicExecutorDataSource;
import skew.core.datasource.DataSource;
import skew.core.model.IModel;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.datasources.misorientation.datasource.calculation.misorientation.FoxmasFileName;
import skew.datasources.xrd.XRDUtil;
import skew.models.strain.IXRDStrain;
import skew.models.strain.XRDStrain;
import skew.views.strain.StrainView;
import autodialog.model.Parameter;
import fava.functionable.FList;
import fava.functionable.FStringInput;


public class SubtractionDataSource extends BasicExecutorDataSource
{

	private int startNum = 1;
	private List<MapView> views;
	
	public SubtractionDataSource() {
		super("dif", "Strain Difference", "Strain Difference");
		views = new ArrayList<MapView>();
		
		
	}

	@Override
	public FileFormatAcceptance accepts(List<String> filenames) {
		if (allWithExtension(filenames, "dif")) return FileFormatAcceptance.ACCEPT;
		return FileFormatAcceptance.REJECT;				
	}

	@Override
	public List<MapView> getViews() {
		return new ArrayList<MapView>(views);
	}

	@Override
	public List<IModel> load(final List<String> filenames, final Coord<Integer> mapsize, final PluralExecutor executor) {

		executor.setStalling(false);
		executor.setWorkUnits(filenames.size());
		
		final List<ISkewPoint<IXRDStrain>> points = DataSource.createPoints(mapsize, XRDStrain::new);
		
		
		FList.wrap(filenames).each((filename) -> {
				
			int index = FoxmasFileName.getFileNumber(filename)-startNum;
			ISkewPoint<IXRDStrain> p = points.get(index);
			IXRDStrain data = p.getData();
			
			try {
				FList<String> contents = FStringInput.tokens(new File(filename), "\\s+").toSink();
				data.strain()[0] = Double.parseDouble(contents.get(0));	//XX
				data.strain()[1] = Double.parseDouble(contents.get(1));	//XY
				data.strain()[2] = Double.parseDouble(contents.get(2));	//XZ
				data.strain()[3] = Double.parseDouble(contents.get(4));	//YY
				data.strain()[4] = Double.parseDouble(contents.get(5));	//YZ
				data.strain()[5] = Double.parseDouble(contents.get(8));	//ZZ
				data.strain()[6] = XRDUtil.vonMises(data.strain());
				
				p.setValid(true);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			executor.workUnitCompleted();
	
		});
		
		//Create a new Grid/Model from this list of points
		ISkewGrid<IXRDStrain> model = new SkewGrid<IXRDStrain>(mapsize.y, mapsize.x, points);

		//Create a new View from this model
		views.add(new StrainView(model));
		
		//Return a list of Grids/Models 
		return new FList<IModel>(model);
	}
	
	@Override
	public List<Parameter<?>> getLoadParameters() {
		return new FList<>();
	}
	
	@Override
	public String getLoadParametersInformation() {
		return null;
	}

	@Override
	public List<Parameter<?>> getRuntimeParameters() {
		return new FList<>();
	}

	@Override
	public void recalculate() {}

	@Override
	public FileOrFolder fileOrFolder() {
		return FileOrFolder.FILE;
	}

}

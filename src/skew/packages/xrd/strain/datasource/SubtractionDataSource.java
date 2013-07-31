package skew.packages.xrd.strain.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.impl.BasicDataSource;
import skew.core.datasource.impl.DataSource;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.impl.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.models.XRDStrain.IXRDStrain;
import skew.models.XRDStrain.XRDStrain;
import skew.packages.misorientation.datasource.calculation.misorientation.IndexFileName;
import skew.packages.xrd.strain.XRDStrainUtil;
import skew.packages.xrd.strain.view.StrainView;
import autodialog.model.Parameter;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnEach;
import fava.signatures.FnGet;

public class SubtractionDataSource extends BasicDataSource
{

	private int startNum = 1;
	private List<MapView> views;
	
	public SubtractionDataSource() {
		super("dif", "Strain Difference", "Strain Difference");
		views = new ArrayList<MapView>();
		
		
	}

	@Override
	public Acceptance accepts(List<String> filenames) {
		for (String filename : filenames)
		{
			if (!filename.endsWith(".dif")) { return Acceptance.REJECT; }
		}
		
		return Acceptance.ACCEPT;
			
	}

	@Override
	public List<MapView> getViews() {
		return new ArrayList<MapView>(views);
	}

	@Override
	public List<ISkewGrid<?>> load(final List<String> filenames, final Coord<Integer> mapsize) {

		final List<ISkewPoint<IXRDStrain>> points = DataSource.getEmptyPoints(mapsize, new FnGet<IXRDStrain>() {

			@Override
			public IXRDStrain f() {
				return new XRDStrain();
			}
		});
						
		FList.wrap(filenames).each(new FnEach<String>() {

			@Override
			public void f(String filename) {
				
				int index = IndexFileName.getFileNumber(filename)-startNum;
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
					data.strain()[6] = XRDStrainUtil.vonMises(data.strain());
					
					p.setValid(true);
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		//Create a new Grid/Model from this list of points
		ISkewGrid<IXRDStrain> model = new SkewGrid<IXRDStrain>(mapsize.y, mapsize.x, points);

		//Create a new View from this model
		views.add(new StrainView(model));
		
		//Return a list of Grids/Models 
		return new FList<ISkewGrid<?>>(model);
	}
	
	@Override
	public List<Parameter> userQueries() {
		return new FList<>();
	}
	
	@Override
	public String userQueryInformation() {
		return null;
	}

}

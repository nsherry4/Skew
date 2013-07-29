package skew.packages.xrdstrain.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.impl.BasicDataSource;
import skew.core.model.ISkewGrid;
import skew.core.model.impl.BasicSkewPoint;
import skew.core.model.impl.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.models.XRDStrain.IXRDStrain;
import skew.models.XRDStrain.XRDStrain;
import skew.packages.misorientation.datasource.calculation.misorientation.IndexFileName;
import skew.packages.xrdstrain.XRDStrainUtil;
import skew.packages.xrdstrain.view.StrainView;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnMap;

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
		
		return Acceptance.MAYBE;
			
	}

	@Override
	public List<MapView> getViews() {
		return new ArrayList<MapView>(views);
	}

	@Override
	public ISkewGrid load(final List<String> filenames, final Coord<Integer> mapsize) {

		FList<BasicSkewPoint<IXRDStrain>> points = FList.wrap(filenames).map(new FnMap<String, BasicSkewPoint<IXRDStrain>>() {

			@Override
			public BasicSkewPoint<IXRDStrain> f(String filename) {
				
				int index = IndexFileName.getFileNumber(filename)-startNum;
				int y = index / mapsize.x;
				int x = index - (mapsize.x * y);
				IXRDStrain data = new XRDStrain();
				BasicSkewPoint<IXRDStrain> p = new BasicSkewPoint<IXRDStrain>(x, y, index, data);
				
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
				
				return p;
				
			}
		});
		
		SkewGrid<BasicSkewPoint<IXRDStrain>> model = new SkewGrid<BasicSkewPoint<IXRDStrain>>(mapsize.y, mapsize.x, points);
		views.add(new StrainView(model));
		return model;
	}

}

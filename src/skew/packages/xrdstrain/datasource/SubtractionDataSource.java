package skew.packages.xrdstrain.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.impl.BasicDataSource;
import skew.core.model.ISkewGrid;
import skew.core.model.impl.SkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.models.XRDStrain.IXRDStrainPoint;
import skew.models.XRDStrain.XRDStrainPoint;
import skew.packages.misorientation.datasource.calculation.misorientation.IndexFileName;
import skew.packages.xrdstrain.XRDStrain;
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

		FList<IXRDStrainPoint> points = FList.wrap(filenames).map(new FnMap<String, IXRDStrainPoint>() {

			@Override
			public IXRDStrainPoint f(String filename) {
				
				int index = IndexFileName.getFileNumber(filename)-startNum;
				int y = index / mapsize.x;
				int x = index - (mapsize.x * y);
				IXRDStrainPoint p = new XRDStrainPoint(x, y, index);
				
				try {
					FList<String> contents = FStringInput.tokens(new File(filename), "\\s+").toSink();
					p.strain()[0] = Double.parseDouble(contents.get(0));	//XX
					p.strain()[1] = Double.parseDouble(contents.get(1));	//XY
					p.strain()[2] = Double.parseDouble(contents.get(2));	//XZ
					p.strain()[3] = Double.parseDouble(contents.get(4));	//YY
					p.strain()[4] = Double.parseDouble(contents.get(5));	//YZ
					p.strain()[5] = Double.parseDouble(contents.get(8));	//ZZ
					p.strain()[6] = XRDStrain.vonMises(p.strain());
					
					p.setHasStrainData(true);
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return p;
				
			}
		});
		
		SkewGrid<IXRDStrainPoint> model = new SkewGrid<IXRDStrainPoint>(mapsize.y, mapsize.x, points);
		views.add(new StrainView(model));
		return model;
	}

}

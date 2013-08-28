package skew.datasources.pixeldeviation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import plural.executor.PluralExecutor;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.BasicDataSource;
import skew.core.model.IModel;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.core.viewer.modes.views.MapView;
import skew.views.PixelDeviationComparisonView;
import autodialog.model.Parameter;
import fava.functionable.FList;
import fava.functionable.FStringInput;

public class PixDevDataSource extends BasicDataSource
{

	private ISkewGrid<Float> model;
	
	public PixDevDataSource()
	{
		super("xpdm", "XRD Pixel Deviation Map", "XPDM");
	}

	@Override
	public Acceptance accepts(List<String> filenames)
	{
		for (String fn : filenames)
		{
			if (!fn.toLowerCase().endsWith("xpdm")) return Acceptance.REJECT;
		}
		return Acceptance.ACCEPT;
	}

	@Override
	public List<MapView> getViews()
	{
		return new FList<MapView>(new PixelDeviationComparisonView(model));
	}

	@Override
	public List<IModel> load(List<String> filenames, Coord<Integer> mapsize, final PluralExecutor executor)
	{
		//tokenize the file
		String filename = filenames.get(0);
		final List<String> tokens;
		try
		{
			tokens = FStringInput.tokens(new File(filename), ", ").toSink();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		
		executor.setStalling(false);
		executor.setWorkUnits(mapsize.x * mapsize.y);
		
		//convert to list of floats
		final List<ISkewPoint<Float>> values = new ArrayList<ISkewPoint<Float>>();
		for (int y = 0; y < mapsize.y; y++) {
			for (int x = 0; x < mapsize.x; x++) {
				
				executor.workUnitCompleted();
				
				int index = x + y*mapsize.x;
				if (index >= tokens.size()) 
				{
					values.add(new SkewPoint<Float>(x, y, index, 0f));
					continue;
				}
				float val = Float.parseFloat(tokens.get(index));
				values.add(new SkewPoint<Float>(x, y, index, val));
				
			}
		}
		
		//return data structure
		model = new SkewGrid<Float>(mapsize.x, mapsize.y, values);
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
	

}

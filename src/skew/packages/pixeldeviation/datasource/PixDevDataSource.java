package skew.packages.pixeldeviation.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import fava.functionable.FList;
import fava.functionable.FStringInput;

import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.impl.BasicDataSource;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.packages.pixeldeviation.model.PixDev;
import skew.packages.pixeldeviation.model.PixDevGrid;
import skew.packages.pixeldeviation.view.PixelDeviationComparisonView;

public class PixDevDataSource extends BasicDataSource
{

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
		return new FList<MapView>(new PixelDeviationComparisonView());
	}

	@Override
	public ISkewGrid load(List<String> filenames, Coord<Integer> mapsize)
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
		
		//convert to list of floats
		final List<PixDev> values = new ArrayList<PixDev>();
		for (int y = 0; y < mapsize.y; y++) {
			for (int x = 0; x < mapsize.x; x++) {
				int index = x + y*mapsize.x;
				if (index >= tokens.size()) 
				{
					values.add(new PixDev(x, y, index, 0));
					continue;
				}
				float val = Float.parseFloat(tokens.get(index));
				values.add(new PixDev(x, y, index, val));
			}
		}
		
		//return data structure
		return new PixDevGrid(mapsize.x, mapsize.y, values, new File(filename).getName());
		
	}

}

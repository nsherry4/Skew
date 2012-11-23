package skew.packages.pixeldeviation.datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnEach;

import plural.executor.ExecutorSet;
import plural.executor.eachindex.implementations.PluralEachIndexExecutor;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.IDataSource;
import skew.core.model.ISkewGrid;
import skew.core.viewer.modes.views.MapView;
import skew.packages.pixeldeviation.model.PixDev;
import skew.packages.pixeldeviation.model.PixDevGrid;
import skew.packages.pixeldeviation.view.PixelDeviationComparisonView;

public class PixDevDataSourceOld implements IDataSource
{

	@Override
	public String extension()
	{
		return "xpdm";
	}

	@Override
	public String description()
	{
		return "Pixel deviation for a pair of data sets";
	}

	@Override
	public String title()
	{
		return "X-ray Pixel Deviation Map";
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
	public ExecutorSet<ISkewGrid> calculate(final List<String> filenames, final Coord<Integer> mapsize)
	{

		final String filename = filenames.get(0);
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
		
		final List<PixDev> values = new ArrayList<PixDev>();
		for (int y = 0; y < mapsize.y; y++) {
			for (int x = 0; x < mapsize.x; x++) {
				values.add(new PixDev(x, y, x+y*mapsize.x, 0));
			}
		}
		
		final PluralEachIndexExecutor exec = new PluralEachIndexExecutor(mapsize.x * mapsize.y, new FnEach<Integer>() {
			
			@Override
			public void f(Integer i)
			{
				float val = Float.parseFloat(tokens.get(i));
				values.get(i).setValue(val);
			}
		});

		
		ExecutorSet<ISkewGrid> execset = new ExecutorSet<ISkewGrid>("Opening Data Set") {

			@Override
			protected PixDevGrid execute()
			{
				exec.executeBlocking();
				return new PixDevGrid(mapsize.x, mapsize.y, values, new File(filename).getName());
			}
			
		};
		
		execset.addExecutor(exec, "Reading XPDM File");
		
		return execset;
		
	}
	
}

package skew.core.viewer.modes.painter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.RasterColorMapPainter;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;

public abstract class RasterColorMapWrapper<T> extends RasterColorMapPainter
{

	
	public RasterColorMapWrapper()
	{
		super();
	}
	
	public void setData(ISkewGrid<T> data)
	{
		List<Color> colors = new ArrayList<Color>();
		for (int y = 0; y < data.getHeight(); y++) {
			for (int x = 0; x < data.getWidth(); x++) {
				colors.add(valueToColor(data.get(x, y)));
			}
		}
		setPixels(colors);
	}

	
	protected abstract Color valueToColor(ISkewPoint<T> point);
	
	
}

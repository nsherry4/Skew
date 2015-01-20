package skew.core.viewer.modes.views;

import java.awt.Color;
import java.util.List;


import fava.functionable.FList;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.viewer.modes.subviews.MapSubView;

public abstract class RasterColorMapView<T> extends MapView
{
	
	protected ISkewGrid<T> model;
	private RasterColorMapPainter painter;

	public RasterColorMapView(String title, ISkewGrid<T> model) {
		super(title);
		this.model = model;
		painter = new RasterColorMapPainter();
	}

	
	@Override
	public final List<MapPainter> getPainters(MapSubView subview, float maximum) {
		
		if (isUpdateRequired())
		{
			
			List<Color> pixelColours = new FList<Color>(model.getWidth() * model.getHeight());
			for (int i = 0; i < model.getWidth() * model.getHeight(); i++){ pixelColours.add(Color.black); }
			
			Color c;

			for (ISkewPoint<T> point : model.getPoints())
			{	
				if (point.isValid()) 
				{
					c = colorForPoint(point, subview, maximum);
				} else {
					c = backgroundGray;
				}
				
				pixelColours.set(point.getIndex(), c);
			}
			
			painter.setPixels(pixelColours);
			
			setUpdateComplete();
		}
		return new FList<MapPainter>(painter);
		
	}

	protected abstract Color colorForPoint(ISkewPoint<T> point, MapSubView subview, float maximum);
	
	

}

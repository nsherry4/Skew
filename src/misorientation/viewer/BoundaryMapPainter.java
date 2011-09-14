package misorientation.viewer;


import java.awt.Color;
import java.util.List;


import scidraw.drawing.backends.Surface.EndCap;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.PainterData;
import scitypes.Coord;

/**
 * 
 * This class implements the drawing of a map using block pixel filling
 * 
 * @author Nathaniel Sherry, 2009
 */

public class BoundaryMapPainter extends MapPainter
{
	
	private List<Coord<Double>> pixels;


	public BoundaryMapPainter()
	{
		super(new ThermalScalePalette(), null);
	}


	public void setPixels(List<Coord<Double>> pixels)
	{
		this.pixels = pixels;
	}
	
	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{

		p.context.save();

			drawPixels(p, pixels, cellSize);

		p.context.restore();

	}



	private void drawPixels(PainterData p, List<Coord<Double>> data, float cellSize)
	{

		p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();

		
		
		float baseline = Math.max(0.6f, cellSize * 0.4f);
		
		p.context.setLineWidth(baseline);
		p.context.setSource(Color.BLACK);
		p.context.setLineEnd(EndCap.ROUND);
		

		
		// draw the map
		Coord<Double> coord;
		for (int y = 0; y < p.dr.dataHeight; y++) {
			for (int x = 0; x < p.dr.dataWidth; x++) {

				
				int index = y * p.dr.dataWidth + x;
				if (index >= data.size()) break;
				if (data.get(index) == null) continue;
				coord = data.get(index);
				//p.context.rectangle(x * cellSize, y * cellSize, cellSize + 1, cellSize + 1);
				
				
				
				if (coord.x >= 5)
				{
					
					p.context.moveTo((x+1) * cellSize, y * cellSize);
					p.context.lineTo((x+1) * cellSize, (y+1) * cellSize);
					
				}

				if (coord.y >= 5)
				{
					p.context.moveTo(x * cellSize, (y+1) * cellSize);
					p.context.lineTo((x+1) * cellSize, (y+1) * cellSize);
					
				}
				

			}
		}
		

		p.context.stroke();
		
		
		
	}


	@Override
	public boolean isBufferingPainter()
	{
		return false;
	}


	@Override
	public void clearBuffer() {
		
	}
	
}

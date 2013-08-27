package skew.datasources.misorientation.drawing;


import java.awt.Color;

import scidraw.drawing.backends.Surface.EndCap;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.PainterData;
import skew.core.model.ISkewGrid;
import skew.models.misorientation.MisAngle;

/**
 * 
 * This class implements the drawing of boundaries on a map
 * 
 * @author Nathaniel Sherry, 2011
 */

public class BoundaryMapPainter extends MapPainter
{

	private Color color;
	private ISkewGrid<MisAngle> misModel;
	
	
	public BoundaryMapPainter(ISkewGrid<MisAngle> grid, Color color) {
		super(new ThermalScalePalette());
		this.color = color;
		this.misModel = grid;
	}
	
	public void setData(ISkewGrid<MisAngle> data)
	{
		this.misModel = data;
	}
	
	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{

		p.context.save();

			drawPixels(p, cellSize);

		p.context.restore();

	}

	private void drawPixels(PainterData p, float cellSize)
	{

		p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();

		
		
		float baseline = Math.max(0.6f, cellSize * 0.4f);
		
		p.context.setLineWidth(baseline);
		p.context.setSource(color);
		p.context.setLineEnd(EndCap.ROUND);
		

		
		// draw the map
		for (int y = 0; y < misModel.getHeight(); y++) {
			for (int x = 0; x < misModel.getWidth(); x++) {

				MisAngle point = misModel.getData(x, y);
				
				float fx = (float)x;
				float fy = (float)y;
				
				if (point.east.is() && point.east.get() >= 5)
				{
					
					p.context.moveTo( (fx+1f) * cellSize, fy * cellSize );
					p.context.lineTo( (fx+1f) * cellSize, (fy+1f) * cellSize );
					
				}

				if (point.south.is() && point.south.get() >= 5)
				{
					p.context.moveTo(fx * cellSize, (fy+1f) * cellSize);
					p.context.lineTo((fx+1f) * cellSize, (fy+1f) * cellSize);
					
				}
				

			}
		}
		

		p.context.stroke();
		
		
		
	}

	@Override
	public void clearBuffer() {}

	@Override
	public boolean isBufferingPainter() {
		return false;
	}

	
}

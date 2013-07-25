package skew.packages.misorientation.drawing;


import java.awt.Color;

import scidraw.drawing.backends.Surface.EndCap;
import scidraw.drawing.painters.PainterData;
import skew.models.Misorientation.MisAnglePoint;

/**
 * 
 * This class implements the drawing of boundaries on a map
 * 
 * @author Nathaniel Sherry, 2011
 */

public class BoundaryMapPainter extends AbstractMisAnglePainter
{

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
		p.context.setSource(Color.BLACK);
		p.context.setLineEnd(EndCap.ROUND);
		

		
		// draw the map
		for (int y = 0; y < data.getHeight(); y++) {
			for (int x = 0; x < data.getWidth(); x++) {

				MisAnglePoint point = data.get(x, y);
				
				float fx = (float)x;
				float fy = (float)y;
				
				if (point.east >= 5)
				{
					
					p.context.moveTo( (fx+1f) * cellSize, fy * cellSize );
					p.context.lineTo( (fx+1f) * cellSize, (fy+1f) * cellSize );
					
				}

				if (point.south >= 5)
				{
					p.context.moveTo(fx * cellSize, (fy+1f) * cellSize);
					p.context.lineTo((fx+1f) * cellSize, (fy+1f) * cellSize);
					
				}
				

			}
		}
		

		p.context.stroke();
		
		
		
	}

	
}

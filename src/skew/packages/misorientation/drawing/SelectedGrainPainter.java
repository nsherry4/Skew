package skew.packages.misorientation.drawing;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import scidraw.drawing.painters.PainterData;
import skew.core.model.ISkewPoint;
import skew.packages.misorientation.model.Grain;

public class SelectedGrainPainter extends AbstractMisAnglePainter
{


	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{
		p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();
		
		float pad = cellSize * 0.1f;
		

		for (Grain g : data.grains)
		{
		
			if (g == null) continue;
			if (!g.selected) continue;
				
			for (ISkewPoint point : g.points)
			{
				if (  (point.getX() % 2 == 1 && point.getY() % 2 == 1)  ||  (point.getX() % 2 == 0 && point.getY() % 2 == 0)  ){
					//p.context.rectangle(cellSize * point.x, cellSize * point.y, cellSize, cellSize);
					p.context.addShape(new Ellipse2D.Double(cellSize * point.getX() + pad, cellSize * point.getY() + pad, cellSize - (2*pad), cellSize - (2*pad)));
				}
			}
		}
		
		p.context.setSource(new Color(1f, 1f, 1f, 0.2f));
		p.context.fill();
		
		
		
		
		for (Grain g : data.grains)
		{
		
			if (g == null) continue;
			if (!g.selected) continue;
				
			for (ISkewPoint point : g.points)
			{
				if (!(  (point.getX() % 2 == 1 && point.getY() % 2 == 1)  ||  (point.getX() % 2 == 0 && point.getY() % 2 == 0)  )){
					p.context.addShape(new Ellipse2D.Double(cellSize * point.getX() + pad, cellSize * point.getY() + pad, cellSize - (2*pad), cellSize - (2*pad)));
					//p.context.rectangle(cellSize * point.x, cellSize * point.y, cellSize, cellSize);
				}
			}
		}

		p.context.setSource(new Color(0f, 0f, 0f, 0.2f));
		p.context.fill();
		
	}


}

package misorientation.viewer.drawing;

import java.awt.Color;

import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.PainterData;

public class SelectedGrainPainter extends MapPainter
{

	private MisAngleGrid data;
	
	public SelectedGrainPainter()
	{
		super(new ThermalScalePalette(), null);
	}
	
	public void setData(MisAngleGrid data)
	{
		this.data = data;
	}

	@Override
	public void clearBuffer(){}

	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{
		p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();

		Grain g = data.getSelectedGrain();
		if (g == null) return;
		

		
		for (MisAnglePoint point : g.points)
		{
			p.context.rectangle(cellSize * point.x, cellSize * point.y, cellSize, cellSize);
		}
		
		p.context.setSource(new Color(1f, 1f, 1f, 0.2f));
		p.context.fill();
		
	}

	@Override
	public boolean isBufferingPainter()
	{
		return false;
	}

}

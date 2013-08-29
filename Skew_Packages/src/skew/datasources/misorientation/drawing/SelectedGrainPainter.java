package skew.datasources.misorientation.drawing;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.PainterData;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.models.grain.Grain;
import skew.models.grain.GrainUtil;
import skew.models.grain.GrainPixel;

public class SelectedGrainPainter extends MapPainter
{

	private ISkewGrid<GrainPixel> grainModel;
	private List<Grain> selectedGrains = new ArrayList<>();
	private List<Grain> grains;
	
	public SelectedGrainPainter(ISkewGrid<GrainPixel> data) {
		super(new ThermalScalePalette());
		setData(data);
	}
	
	public void setData(ISkewGrid<GrainPixel> data) {
		this.grainModel = data;
		grains = GrainUtil.getGrains(grainModel);
	}
	
	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{
				
		p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();
		
		float pad = cellSize * 0.1f;
			
		
		for (Grain g : grains)
		{
		
			if (g == null) continue;
			if (!isGrainSelected(g)) continue;
				
			for (ISkewPoint<GrainPixel> point : GrainUtil.getGrainPoints(grainModel, g))
			{
				if (  (point.getX() % 2 == 1 && point.getY() % 2 == 1)  ||  (point.getX() % 2 == 0 && point.getY() % 2 == 0)  ){
					//p.context.rectangle(cellSize * point.x, cellSize * point.y, cellSize, cellSize);
					p.context.addShape(new Ellipse2D.Double(cellSize * point.getX() + pad, cellSize * point.getY() + pad, cellSize - (2*pad), cellSize - (2*pad)));
				}
			}
		}
		
		p.context.setSource(new Color(1f, 1f, 1f, 0.2f));
		p.context.fill();
		
		
		
		
		for (Grain g : grains)
		{
		
			if (g == null) continue;
			if (!isGrainSelected(g)) continue;
				
			for (ISkewPoint<GrainPixel> point : GrainUtil.getGrainPoints(grainModel, g))
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

	@Override
	public void clearBuffer() {}

	@Override
	public boolean isBufferingPainter() {
		return false;
	}

	public void setPointSelected(ISkewPoint<GrainPixel> grainPoint, boolean multiselect) 
	{
		Grain g = grainPoint.getData().grain;
		if (g == null) return;
		
		// if grain was already selected
		boolean alreadySelected = isGrainSelected(g);
		if (!multiselect) deselectAllGrains();
		if (!alreadySelected) selectGrain(g);
		
	}
	
	private boolean isGrainSelected(Grain g) { return selectedGrains.contains(g); }
	private void deselectAllGrains() { selectedGrains.clear(); }
	private void selectGrain(Grain g) { selectedGrains.add(g); }
	
	
}

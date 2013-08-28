package skew.datasources.misorientation.drawing;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.PainterData;
import skew.core.model.ISkewPoint;
import skew.models.grain.Grain;
import skew.models.misorientation.GrainModel;
import skew.models.misorientation.MisAngle;

public class SelectedGrainPainter extends MapPainter
{

	private GrainModel grid;
	private List<Grain> selectedGrains = new ArrayList<>();
	
	public SelectedGrainPainter(GrainModel data) {
		super(new ThermalScalePalette());
		this.grid = data;
	}
	
	public void setData(GrainModel data) {
		this.grid = data;
	}
	
	@Override
	public void drawMap(PainterData p, float cellSize, float rawCellSize)
	{
				
		p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
		p.context.clip();
		
		float pad = cellSize * 0.1f;
		
		for (Grain g : grid.grains)
		{
		
			if (g == null) continue;
			if (!isGrainSelected(g)) continue;
				
			for (ISkewPoint<MisAngle> point : g.points)
			{
				if (  (point.getX() % 2 == 1 && point.getY() % 2 == 1)  ||  (point.getX() % 2 == 0 && point.getY() % 2 == 0)  ){
					//p.context.rectangle(cellSize * point.x, cellSize * point.y, cellSize, cellSize);
					p.context.addShape(new Ellipse2D.Double(cellSize * point.getX() + pad, cellSize * point.getY() + pad, cellSize - (2*pad), cellSize - (2*pad)));
				}
			}
		}
		
		p.context.setSource(new Color(1f, 1f, 1f, 0.2f));
		p.context.fill();
		
		
		
		
		for (Grain g : grid.grains)
		{
		
			if (g == null) continue;
			if (!isGrainSelected(g)) continue;
				
			for (ISkewPoint<MisAngle> point : g.points)
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

	public void setPointSelected(ISkewPoint<MisAngle> misData, boolean multiselect) 
	{
		Grain g = grid.getGrain(misData);
		if (g == null) return;
		
		// if grain was already selected
		boolean alreadySelected = isGrainSelected(g);
		if (!multiselect) deselectAllGrains();
		if (!alreadySelected) selectGrain(g);
		
	}
	
	private boolean isGrainSelected(Grain g) { return selectedGrains.contains(g); }
	private void deselectGrain(Grain g) { selectedGrains.remove(g); }
	private void deselectAllGrains() { selectedGrains.clear(); }
	private void selectGrain(Grain g) { selectedGrains.add(g); }
	
/*
	public boolean selectGrain(MisAngle point, boolean multiselect)
	{
		Grain g = getGrain(point);
		if (g == null) return false;
		
		boolean alreadySelected = g.selected;
		if (!multiselect) for (Grain grain : grains) { grain.selected = false; }
		g.selected = !alreadySelected;
		
		return g.selected;
		
	}
	
	public List<Grain> getSelectedGrains()
	{
		List<Grain> selected = new ArrayList<Grain>();
		for (Grain g : grains) { if (g.selected) selected.add(g); }
		return selected;
	}
*/
	
}

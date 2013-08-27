package skew.datasources.misorientation.drawing;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import skew.core.model.ISkewGrid;
import skew.models.misorientation.MisAngle;


public abstract class AbstractMisAnglePainter extends MapPainter
{
	
	protected ISkewGrid<MisAngle> data;

	public AbstractMisAnglePainter()
	{
		super(new ThermalScalePalette());
	}

	public void setData(ISkewGrid<MisAngle> data)
	{
		this.data = data;
	}
	
	@Override
	public void clearBuffer()
	{
	}


	@Override
	public boolean isBufferingPainter()
	{
		return false;
	}

}

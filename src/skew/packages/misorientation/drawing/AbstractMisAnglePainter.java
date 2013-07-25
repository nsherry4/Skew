package skew.packages.misorientation.drawing;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import skew.core.model.ISkewGrid;
import skew.models.Misorientation.MisAngleGrid;
import skew.models.Misorientation.MisAnglePoint;


public abstract class AbstractMisAnglePainter extends MapPainter
{
	
	protected MisAngleGrid<MisAnglePoint> data;

	public AbstractMisAnglePainter()
	{
		super(new ThermalScalePalette());
	}

	@SuppressWarnings("unchecked")
	public void setData(ISkewGrid data)
	{
		this.data = (MisAngleGrid<MisAnglePoint>)data;
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

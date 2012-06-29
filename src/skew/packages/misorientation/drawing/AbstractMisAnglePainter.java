package skew.packages.misorientation.drawing;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import skew.core.model.SkewGrid;
import skew.packages.misorientation.model.MisAngleGrid;
import skew.packages.misorientation.model.MisAnglePoint;


public abstract class AbstractMisAnglePainter extends MapPainter
{
	
	protected MisAngleGrid<MisAnglePoint> data;

	public AbstractMisAnglePainter()
	{
		super(new ThermalScalePalette(), null);
	}

	public void setData(SkewGrid data)
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

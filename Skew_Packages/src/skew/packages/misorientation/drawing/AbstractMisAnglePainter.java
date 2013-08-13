package skew.packages.misorientation.drawing;

import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import skew.core.model.ISkewGrid;
import skew.models.Misorientation.MisAngle;
import skew.models.Misorientation.MisAngleGrid;


public abstract class AbstractMisAnglePainter extends MapPainter
{
	
	protected MisAngleGrid data;

	public AbstractMisAnglePainter()
	{
		super(new ThermalScalePalette());
	}

	public void setData(ISkewGrid<MisAngle> data)
	{
		this.data = (MisAngleGrid)data;
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

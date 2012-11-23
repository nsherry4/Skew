package skew.packages.pixeldeviation.model;

import java.util.List;

import skew.core.model.impl.SkewGrid;

public class PixDevGrid extends SkewGrid<PixDev>
{

	public PixDevGrid(int width, int height, List<PixDev> points, String name)
	{
		super(width, height, points, name);
	}
}

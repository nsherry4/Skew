package skew.core.viewer.modes.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SpinnerModel;

import skew.core.viewer.modes.subviews.MapSubView;

public abstract class SecondaryView extends MapView
{

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
	}

	@Override
	public String getSummaryText(int x, int y)
	{
		return "";
	}

	@Override
	public boolean hasSublist()
	{
		return false;
	}

	@Override
	public List<MapSubView> getSubList()
	{
		return null;
	}

	@Override
	public float getMaximumIntensity(MapSubView subview)
	{
		return 0;
	}


	@Override
	public void writeData(MapSubView subview, BufferedWriter writer) throws IOException
	{
		
	}
	
	@Override
	public String toString()
	{
		return "View";
	}


}

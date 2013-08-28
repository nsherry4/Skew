package skew.core.viewer.modes.views;

import java.util.List;

import javax.swing.SpinnerModel;

import skew.core.viewer.modes.subviews.MapSubView;

public abstract class SecondaryView extends MapView
{

	private boolean silent;
	
	public SecondaryView(String title, boolean silent) {
		super(title);
		this.silent = silent;
	}

	@Override
	public SpinnerModel scaleSpinnerModel(MapSubView subView)
	{
		return null;
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
	public String toString()
	{
		return title;
	}
	
	public boolean isSilent()
	{
		return silent;
	}


}

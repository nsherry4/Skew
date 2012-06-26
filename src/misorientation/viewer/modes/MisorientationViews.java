package misorientation.viewer.modes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;


import scitypes.SigDigits;

import misorientation.model.Grain;
import misorientation.model.MisAngleGrid;
import misorientation.model.MisAnglePoint;
import misorientation.viewer.modes.subviews.GrainMagnitudeSubView;
import misorientation.viewer.modes.subviews.IntraGrainSubView;
import misorientation.viewer.modes.subviews.OrientationSubView;
import misorientation.viewer.modes.subviews.MisorientationSubView;

public enum MisorientationViews
{
	
	
	LOCAL_MISORIENTATION {
		
		public String toString(){ return "Local Misorientation"; }

		@Override
		public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
		{
			return new SpinnerNumberModel(2, 0.0, 180.0, 0.1);
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{

			String avg = formatMisorientationValue(point.average);
			String east = formatMisorientationValue(point.east);
			String south = formatMisorientationValue(point.south);
			String west = formatMisorientationValue(point.west);
			String north = formatMisorientationValue(point.north);
			
			return ""+
				"Angles - " + 
				"Average: " + avg + 
				", \u2191" + north +
				", \u2192" + east + 
				", \u2193" + south +
				", \u2190" + west;
		}

		@Override
		public boolean hasNumericScale()
		{
			return true;
		}

		@Override
		public boolean hasSublist()
		{
			return false;
		}

		@Override
		public List<MisorientationSubView> getSubList()
		{
			return null;
		}
		
	}, 
	GRAIN_MAGNITUDE {
		
		public String toString(){ return "Grain Magnitude"; }

		private double defaultScale(MisAngleGrid data)
		{
			if (data == null) return 10;
			int max = 0;
			for (Grain g : data.grains)
			{
				max = (int) Math.max(max, g.magMin);
			}
			return max + 1;
		}

		@Override
		public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
		{
			return new SpinnerNumberModel(defaultScale(data), 0.0, 180.0, 0.1);
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{
		
			String grain = formatGrainValue(point.grain);
			String result = "Grain :" + grain;
			
			Grain g;
			try { g = data.grains.get(point.grain); }
			catch (ArrayIndexOutOfBoundsException e) { return result; }			
			
			if (g == null) return result; 
			
			String mag = formatMisorientationValue(g.magMin);
			return "Grain: " + grain + ", Magnitude: " + mag;
		}

		@Override
		public boolean hasNumericScale()
		{
			return false;
		}

		@Override
		public boolean hasSublist()
		{
			return true;
		}

		@Override
		public List<MisorientationSubView> getSubList()
		{
			return new ArrayList<MisorientationSubView>(Arrays.asList(new GrainMagnitudeSubView[]{
				new GrainMagnitudeSubView(0),
				new GrainMagnitudeSubView(1),
				new GrainMagnitudeSubView(2)
			}));
		}
		
	},
	INTRAGRAIN_MISORIENTATION {
		
		public String toString(){ return "Intragrain Misorientation"; }

		@Override
		public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
		{
			IntraGrainSubView igv = (IntraGrainSubView)subView;
			return igv.getSpinnerModel(data);
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{
		
			String grain = formatGrainValue(point.grain);
			String result = "Grain :" + grain;
			
			Grain g;
			try { g = data.grains.get(point.grain); }
			catch (ArrayIndexOutOfBoundsException e) { return result; }			
			
			if (g == null) return result; 
			
			String mag = formatMisorientationValue(point.intraGrainMisorientation);
			return "Grain: " + grain + ", Misorientation: " + mag + "\u00B0";
			
		}

		@Override
		public boolean hasNumericScale()
		{
			return false;
		}

		@Override
		public boolean hasSublist()
		{
			return true;
		}

		@Override
		public List<MisorientationSubView> getSubList()
		{
			return new ArrayList<MisorientationSubView>(Arrays.asList(new IntraGrainSubView[]{
					new IntraGrainSubView(0),
					new IntraGrainSubView(1)
				}));
		}
		
	}, 	
	LABELS {
		
		public String toString(){ return "Grain Labels"; }

		@Override
		public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
		{
			return null;
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{
			String grain = formatGrainValue(point.grain);
			String result = "Grain: " + grain;
			
			Grain g;
			try { g = data.grains.get(point.grain); }
			catch (ArrayIndexOutOfBoundsException e) { return result; }
			if (g == null) return result;
			
			result += ", Size: " + g.points.size() + " pixels";
			return result;
			
		}

		@Override
		public boolean hasNumericScale()
		{
			return false;
		}

		@Override
		public boolean hasSublist()
		{
			return false;
		}

		@Override
		public List<MisorientationSubView> getSubList()
		{
			return null;
		}
		
	},
	ORIENTATION {

		@Override
		public String toString()
		{
			return "Orientation";
		}

		@Override
		public SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView)
		{
			return null;
		}

		@Override
		public String getSummaryText(MisAnglePoint point, MisAngleGrid data)
		{
			return "";
		}

		@Override
		public boolean hasNumericScale()
		{
			return false;
		}

		@Override
		public boolean hasSublist()
		{
			return true;
		}

		@Override
		public List<MisorientationSubView> getSubList()
		{
			return new ArrayList<MisorientationSubView>(Arrays.asList(new OrientationSubView[]{
					new OrientationSubView(0),
					new OrientationSubView(1),
					new OrientationSubView(2)
				}));
		}};
	
		
		
		
	private static String formatGrainValue(double value)
	{
		if (value < 0) return "None";
		return "#" + (int)value;
	}
	
	private static String formatMisorientationValue(double value)
	{
		String valString;
		valString = SigDigits.roundFloatTo((float)value, 3);
		if (value < 0) valString = "Boundary";
		
		return valString;
	}
	
	
	public abstract SpinnerModel scaleSpinnerModel(MisAngleGrid data, MisorientationSubView subView);
	public abstract String getSummaryText(MisAnglePoint point, MisAngleGrid data);
	public abstract boolean hasNumericScale();
	public abstract boolean hasSublist();
	public abstract List<MisorientationSubView> getSubList();
	

	
}



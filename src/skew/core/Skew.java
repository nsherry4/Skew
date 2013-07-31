package skew.core;

import skew.core.viewer.SkewTabs;
import swidget.Swidget;
import swidget.icons.IconFactory;

public class Skew {

	public static void main(String[] args) {
		
		Swidget.initialize();
		IconFactory.customPath = "/skew/core/icons/";
		
		new SkewTabs();
		
	}

	
}

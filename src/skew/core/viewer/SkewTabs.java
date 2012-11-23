package skew.core.viewer;

import java.awt.Dimension;

import javax.swing.JFrame;

import swidget.widgets.tabbedinterface.TabbedInterface;


public class SkewTabs extends JFrame
{
	
	TabbedInterface<SkewUI> tabs;
	
	public SkewTabs()
	{
				
		tabs = new TabbedInterface<SkewUI>("No Data") {

			@Override
			protected SkewUI createComponent()
			{
				return new SkewUI(SkewTabs.this);
			}

			@Override
			protected void destroyComponent(SkewUI component){}

			@Override
			protected void titleChanged(String title)
			{
				setTitle(title + " - Skew");
			}};
			
		
		
		setPreferredSize(new Dimension(1000, 473));
		setTitle("Skew");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs.newTab();
		
		add(tabs);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
	}

	
	public void newTab()
	{
		tabs.newTab();
	}
	
	public void setTabTitle(SkewUI component, String title)
	{
		tabs.setTabTitle(component, title);
	}

	
}

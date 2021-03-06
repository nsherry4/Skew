package skew.ui;


import java.awt.Dimension;

import javax.swing.JFrame;

import swidget.widgets.tabbedinterface.TabbedInterface;


@SuppressWarnings("serial")
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
			protected void tabsChanged(String title) {
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

	
	public SkewUI newTab()
	{
		return tabs.newTab();
	}
	
	public void addTab(SkewUI ui)
	{
		tabs.addTab(ui);
	}
	
	public void closeTab(SkewUI ui) 
	{
		tabs.closeTab(ui);
	}
	
	public void setTabTitle(SkewUI component, String title)
	{
		tabs.setTabTitle(component, title);
	}

	public void setActiveTab(SkewUI ui) {
		tabs.setActiveTab(ui);
	}
	
}

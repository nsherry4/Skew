package skew.core.viewer;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

import skew.core.datasource.IDataSource;
import swidget.widgets.tabbedinterface.TabbedInterface;


@SuppressWarnings("serial")
public class SkewTabs extends JFrame
{
	
	TabbedInterface<SkewUI> tabs;
	
	public SkewTabs(final List<IDataSource> sources)
	{
				
		tabs = new TabbedInterface<SkewUI>("No Data") {

			@Override
			protected SkewUI createComponent()
			{
				return new SkewUI(SkewTabs.this, sources);
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
	
	public void setTabTitle(SkewUI component, String title)
	{
		tabs.setTabTitle(component, title);
	}

	
}

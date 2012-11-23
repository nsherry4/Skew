package skew.core.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;

import com.ezware.dialog.task.TaskDialogs;
import commonenvironment.AbstractFile;

import scidraw.swing.SavePicture;
import scitypes.Coord;
import skew.core.datasource.Acceptance;
import skew.core.datasource.IDataSource;
import skew.core.datasource.DataSourceSelection;
import skew.core.datasource.DataSources;
import skew.core.datasource.impl.DummyDataSource;
import skew.core.model.ISkewGrid;
import skew.core.model.ISkewPoint;
import skew.core.model.impl.DummyGrid;
import skew.core.viewer.SettingType;
import skew.core.viewer.SkewTabs;
import skew.core.viewer.SkewUI;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.impl.DummyView;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;

public class SkewController
{

	public IDataSource dataSource = null;
	public ISkewGrid data = null;
	
	public MapView viewMode = null;
	public MapSubView subView = null;
	
	private File dir;
	
	SkewUI ui;
	SkewTabs window;
	
	public SkewController(SkewUI ui, SkewTabs window)
	{
		this.ui = ui;
		this.window = window;
		data = new DummyGrid();
		dataSource = new DummyDataSource();
		viewMode = new DummyView();
		
		dir = new File("~");
		
	}
	

	
	public void setData(ISkewGrid newdata, IDataSource ds)
	{
		if (newdata == null || ds == null) return;
		data = newdata;
		dataSource = ds;
		ui.settingsChanged(SettingType.DATA);
		window.setTabTitle(ui, data.datasetName());
	}
	
	public void actionSaveText()
	{
		try
		{
		
			File tempfile = tempfile();
			
			OutputStream os = new FileOutputStream(tempfile);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
			viewMode.writeData(data, subView, writer);
			writer.close();
			
			InputStream is = new FileInputStream(tempfile);
			SwidgetIO.saveFile(window, "Save Data as Text...", "txt", "Text File", ".", is);
			is.close();
			
			tempfile.delete();
		} 
		catch (Exception e)
		{
			TaskDialogs.showException(e);
		}
	}
	
	public boolean actionOpenData()
	{
		//list of all data formats
		List<IDataSource> formats = DataSources.getSources();
		
		//get info for open dialogue
		String[][] exts = new String[formats.size()][1];
		String[] descs = new String[formats.size()];
		for (int i = 0; i < formats.size(); i++)
		{
			exts[i] = new String[]{formats.get(i).extension()};
			descs[i] = formats.get(i).description();
		}
		
		//get a list of filenames from the user
		List<AbstractFile> absfiles = SwidgetIO.openFiles(window, "Select Data Files to Open", exts, descs, ".");
		if (absfiles == null || absfiles.size() == 0) return false;
		
		List<String> files = new ArrayList<String>();
		for (AbstractFile af : absfiles)
		{
			files.add(af.getFileName());
		}
		
		
		//filter for just the working data sources
		List<IDataSource> acceptingFormats = new ArrayList<IDataSource>();
		List<IDataSource> maybeFormats = new ArrayList<IDataSource>();
		for (IDataSource ds : formats)
		{
			Acceptance acc = ds.accepts(files);
			if (acc == Acceptance.ACCEPT) acceptingFormats.add(ds);
			if (acc == Acceptance.MAYBE) maybeFormats.add(ds);
		}
		
		
		if (acceptingFormats.size() < 1) acceptingFormats = maybeFormats;
		
		IDataSource ds = null;
		
		if (acceptingFormats.size() > 1)
		{
			DataSourceSelection selection = new DataSourceSelection();
			ds = selection.pickDSP(window, acceptingFormats);
			if (ds != null) loadFiles(files, ds);
		}
		else if (acceptingFormats.size() == 0)
		{
			JOptionPane.showMessageDialog(
					window, 
					"Could not determine the data format of the selected file(s)", 
					"Open Failed", 
					JOptionPane.ERROR_MESSAGE, 
					StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
				);
			return false;
		}
		else
		{
			ds = acceptingFormats.get(0);
			loadFiles(files, ds);
		}
		
		return true;
	}
	
	private void loadFiles(List<String> filenames, IDataSource ds)
	{
		
		try {
			
			Integer width = Integer.parseInt(JOptionPane.showInputDialog(window, "Map Width", 1));
			Integer height = Integer.parseInt(JOptionPane.showInputDialog(window, "Map Height", 1));
		
			Coord<Integer> mapSize = new Coord<Integer>(width, height);
			
			ExecutorSet<ISkewGrid> execset = ds.calculate(filenames, mapSize);
			new ExecutorSetView(window, execset);
			setData(execset.getResult(), ds);					
			
		}
		catch (Exception ex)
		{
			TaskDialogs.showException(ex);
			ex.printStackTrace();
		}
		
	}
	
	private File tempfile() throws IOException
	{
		final File tempfile = File.createTempFile("Image File - ", " export");
		tempfile.deleteOnExit();
		return tempfile;
	}



	public void handleClick(int x, int y, boolean multiselect, boolean doubleclick)
	{
		Coord<Integer> coord = ui.drawing.getMapCoordinateAtPoint(x, y, false);
		if (coord == null) return;
		
		ISkewPoint point = data.get(coord.x, coord.y);
		if (point == null) return;
		
		
		if (doubleclick) data.setPointSelected(point, multiselect);
		
		
		ui.coords.setText("" +
				"(X: " + coord.x + 
				", Y:" + coord.y  + 
				")    " + 
				viewMode.getSummaryText(point, data)
			);
		
		ui.settingsChanged(SettingType.SELECTION);
	}



	public void actionSavePicture()
	{
		if (data != null) {
			new SavePicture(
					window, ui.graphics, dir.getAbsolutePath()
				);
		}
	}



	public void actionViewChange()
	{
		MapView newview = (MapView)ui.viewSelector.getSelectedItem();
		if (newview == null)
		{
			ui.viewSelector.setSelectedItem(viewMode);
			ui.savetext.setEnabled(false);
			return;
		}
		ui.savetext.setEnabled(newview.canWriteData());
		viewMode = newview;
		ui.setSubViewUI();
		ui.settingsChanged(SettingType.VIEW);
	}
	
	
}

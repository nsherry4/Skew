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
import skew.core.datasource.DataSource;
import skew.core.datasource.DataSourceSelection;
import skew.core.datasource.DummyDataSource;
import skew.core.model.DummyGrid;
import skew.core.model.SkewGrid;
import skew.core.model.SkewPoint;
import skew.core.viewer.SettingType;
import skew.core.viewer.SkewUI;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.DummyView;
import skew.core.viewer.modes.views.MapView;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;

public class SkewController
{

	public DataSource dataSource = null;
	public SkewGrid data = null;
	
	public MapView viewMode = null;
	public MapSubView subView = null;
	
	private File dir;
	
	SkewUI ui;
	
	public SkewController(SkewUI ui)
	{
		this.ui = ui;
		data = new DummyGrid();
		dataSource = new DummyDataSource();
		viewMode = new DummyView();
		
		dir = new File("~");
		
	}
	

	
	public void setData(SkewGrid newdata, DataSource ds)
	{
		if (newdata == null || ds == null) return;
		data = newdata;
		dataSource = ds;
		ui.settingsChanged(SettingType.DATA);
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
			SwidgetIO.saveFile(ui, "Save Data as Text...", "txt", "Text File", ".", is);
			is.close();
			
			tempfile.delete();
		} 
		catch (Exception e)
		{
			TaskDialogs.showException(e);
		}
	}
	
	public void actionOpenData()
	{
		//list of all data formats
		List<DataSource> formats = DataSource.getSources();
		
		//get info for open dialogue
		String[][] exts = new String[formats.size()][1];
		String[] descs = new String[formats.size()];
		for (int i = 0; i < formats.size(); i++)
		{
			exts[i] = new String[]{formats.get(i).extension()};
			descs[i] = formats.get(i).description();
		}
		
		//get a list of filenames from the user
		List<AbstractFile> absfiles = SwidgetIO.openFiles(ui, "Select Data Files to Open", exts, descs, ".");
		if (absfiles == null || absfiles.size() == 0) return;
		
		List<String> files = new ArrayList<String>();
		for (AbstractFile af : absfiles)
		{
			files.add(af.getFileName());
		}
		
		
		//filter for just the working data sources
		List<DataSource> acceptingFormats = new ArrayList<DataSource>();
		List<DataSource> maybeFormats = new ArrayList<DataSource>();
		for (DataSource ds : formats)
		{
			Acceptance acc = ds.accepts(files);
			if (acc == Acceptance.ACCEPT) acceptingFormats.add(ds);
			if (acc == Acceptance.MAYBE) maybeFormats.add(ds);
		}
		
		
		if (acceptingFormats.size() < 1) acceptingFormats = maybeFormats;
		
		DataSource ds = null;
		
		if (acceptingFormats.size() > 1)
		{
			DataSourceSelection selection = new DataSourceSelection();
			ds = selection.pickDSP(ui, acceptingFormats);
			if (ds != null) loadFiles(files, ds);
		}
		else if (acceptingFormats.size() == 0)
		{
			JOptionPane.showMessageDialog(
					ui, 
					"Could not determine the data format of the selected file(s)", 
					"Open Failed", 
					JOptionPane.ERROR_MESSAGE, 
					StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
				);
		}
		else
		{
			ds = acceptingFormats.get(0);
			loadFiles(files, ds);
		}
		
	}
	
	private void loadFiles(List<String> filenames, DataSource ds)
	{
		
		try {
			
			Integer width = Integer.parseInt(JOptionPane.showInputDialog(ui, "Map Width", 1));
			Integer height = Integer.parseInt(JOptionPane.showInputDialog(ui, "Map Height", 1));
		
			Coord<Integer> mapSize = new Coord<Integer>(width, height);
			
			ExecutorSet<SkewGrid> execset = ds.calculate(filenames, mapSize);
			new ExecutorSetView(ui, execset);
			
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
		
		SkewPoint point = data.get(coord.x, coord.y);
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
					ui, ui.graphics, dir.getAbsolutePath()
				);
		}
	}



	public void actionViewChange()
	{
		MapView newview = (MapView)ui.viewSelector.getSelectedItem();
		if (newview == null)
		{
			ui.viewSelector.setSelectedItem(viewMode);
			return;
		}
		viewMode = newview;
		ui.setSubViewUI();
		ui.settingsChanged(SettingType.VIEW);
	}
	
	
}

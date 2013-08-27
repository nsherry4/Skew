package skew.ui;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import skew.DataSources;
import skew.core.datasource.Acceptance;
import skew.core.datasource.DataSourceSelection;
import skew.core.datasource.DummyDataSource;
import skew.core.datasource.IDataSource;
import skew.core.model.DummyGrid;
import skew.core.model.ISkewDataset;
import skew.core.model.SkewDataset;
import skew.core.viewer.SettingType;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.DummyView;
import skew.core.viewer.modes.views.MapView;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.properties.PropertyViewPanel;
import autodialog.controller.SimpleADController;
import autodialog.model.Parameter;
import autodialog.view.AutoDialog;
import autodialog.view.AutoDialog.AutoDialogButtons;
import autodialog.view.editors.IntegerEditor;
import autodialog.view.layouts.FramesADLayout;

import com.ezware.dialog.task.TaskDialogs;
import commonenvironment.AbstractFile;

import fava.functionable.FList;

public class SkewController
{

	public ISkewDataset data = null;
	
	public MapView viewMode = null;
	public MapSubView subView = null;
	
	private File dir;
	
	SkewUI ui;
	SkewTabs window;
	
	public SkewController(SkewTabs window)
	{
		this.window = window;
		data = new SkewDataset("No Dataset", ".", new DummyGrid(), new DummyDataSource());
		viewMode = new DummyView();
		
		dir = new File("~");
		
	}

	
	
	
	public void actionScaleChanged()
	{
		event(SettingType.SCALE);
	}
	

	public void actionSetDataset(ISkewDataset newdata)
	{
		if (newdata == null) return;
		data = newdata;
		viewMode = data.datasource().getViews().get(0);
		
		event(SettingType.DATA);
		event(SettingType.VIEW);
		
		//set up subviews
		if (viewMode.hasSublist()){
			subView = viewMode.getSubList().get(0);
			event(SettingType.SUBVIEW);
		} else {
			subView = null;
		}
		
		//set up runtime parameter controller/listeners
		List<Parameter<?>> params = data.datasource().getRuntimeParameters();
		if (params.size() > 0) {
			new SimpleADController(data.datasource().getRuntimeParameters()){
				
				@Override
				public void parameterUpdated(Parameter<?> param) {
					data.datasource().recalculate();
					event(SettingType.PARAMETER);
				}
				
			};
		}

	}
	
	

	public void actionSaveText()
	{
		
		try
		{
		
			File tempfile = tempfile();
			
			OutputStream os = new FileOutputStream(tempfile);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

			
			List<String> keys = viewMode.getSummaryHeaders();
			
			writer.write("X,  Y");
			for (String key : keys) writer.write(",  " + key);
			writer.write("\n");
			
			Map<String, String> summary;
			for (int y = 0; y < data.height(); y++) {
				for (int x = 0; x < data.width(); x++) {
					
					summary = getCoordInfoMap(x, y);
					writer.write(summary.get("X") + ", ");
					writer.write(summary.get("Y"));
					
					summary = viewMode.getSummaryData(x, y);
					for (String key : keys) {
						writer.write(", ");
						if (summary.containsKey(key)) {
							writer.write(summary.get(key));
						} else {
							writer.write("-");
						}
					}
					
					writer.write("\n");
					
				}
			}
			
			
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
	

	//x and y are pixel positions on the drawing surface, not indicies for a 2d-array model
	public void actionSelection(int px, int py, boolean multiselect, boolean doubleclick)
	{
		Coord<Integer> coord = ui.drawing.getMapCoordinateAtPoint(px, py, false);
		
		if (coord == null) return;
		if (doubleclick) data.setPointSelected(coord.x, coord.y, multiselect);
		
		PropertyViewPanel panel;
		
		ui.sidebarInfoPanel.removeAll();
		
		panel = new PropertyViewPanel(getCoordInfoMap(coord.x, coord.y), null, 0, false, false);
		panel.setBorder(new TitledBorder("Coordinates"));
		ui.sidebarInfoPanel.add(panel);
		
		panel = new PropertyViewPanel(viewMode.getSummaryData(coord.x, coord.y), null, 0, false, false);
		panel.setBorder(new TitledBorder(viewMode.toString()));
		ui.sidebarInfoPanel.add(panel);
		
		
		ui.sidebarScroller.revalidate();
		
		event(SettingType.SELECTION);
	}

	
	
	//x and y are 2d-array-index values
	private Map<String, String> getCoordInfoMap(int x, int y)
	{
		Map<String, String> coordInfo = new LinkedHashMap<>();
		
		coordInfo.put("X", ""+x);
		coordInfo.put("Y", ""+y);
		return coordInfo;
	}
	

	public void actionSavePicture()
	{
		if (data != null) {
			new SavePicture(
					window, ui.graphics, dir.getAbsolutePath()
				);
		}
	}




	public void actionSubviewChange(MapSubView newsubview)
	{
		subView = newsubview;
		event(SettingType.SUBVIEW);
	}
	

	public void actionViewChange(MapView newview)
	{
		if (newview == null)
		{
			ui.viewSelector.setSelectedItem(viewMode);
			ui.savetextButton.setVisible(false);
			return;
		}
		viewMode = newview;
		
		event(SettingType.VIEW);
	}
	
	
	
	
	
	
	
	
	public void setUI(SkewUI ui)
	{
		this.ui = ui;
	}

	private void event(SettingType type)
	{
		if (type != SettingType.SELECTION) viewMode.setUpdateRequired();
		ui.settingsChanged(type);
	}
	
	
	
	public static ISkewDataset loadDataset(SkewTabs window, String path)
	{
		
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
		List<AbstractFile> absfiles = SwidgetIO.openFiles(window, "Select Data Files to Open", exts, descs, path);
		if (absfiles == null || absfiles.size() == 0) return null;
		
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
			if (ds != null) return loadFiles(window, files, ds);
			return null;
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
			return null;
		}
		else
		{
			ds = acceptingFormats.get(0);
			return loadFiles(window, files, ds);
		}
		
	}

	
	private static ISkewDataset loadFiles(SkewTabs window, List<String> filenames, IDataSource ds)
	{
		
		try {
			
			String g = "Map Dimensions";
			Parameter<Integer> paramWidth = new Parameter<>("Width", new IntegerEditor(), 1, g);
			Parameter<Integer> paramHeight = new Parameter<>("Height", new IntegerEditor(), 1, g);
			
			List<Parameter<?>> params = new FList<>();
			params.add(paramWidth);
			params.add(paramHeight);
			params.addAll(ds.getLoadParameters());
			
			SimpleADController dialogController = new SimpleADController(params);
			
			AutoDialog dialog = new AutoDialog(dialogController, AutoDialogButtons.OK_CANCEL, window);
			dialog.setHelpTitle("Additional Dataset Information");
			dialog.setHelpMessage(ds.getLoadParametersInformation());
			dialog.setModal(true);
			dialog.setTitle("Dataset Parameters");
			dialog.initialize(new FramesADLayout());

			//User Cancel/Close
			if (!dialog.okSelected()) return null;
			
			Coord<Integer> mapSize = new Coord<>((Integer)paramWidth.getValue(), (Integer)paramHeight.getValue());
			
			
			ExecutorSet<ISkewDataset> execset = ds.loadDataset(filenames, mapSize);
			new ExecutorSetView(window, execset);
			return execset.getResult();					
			
		}
		catch (Exception ex)
		{
			TaskDialogs.showException(ex);
			ex.printStackTrace();
			return null;
		}
		
	}
	
	
	
	private File tempfile() throws IOException
	{
		final File tempfile = File.createTempFile("Image File - ", " export");
		tempfile.deleteOnExit();
		return tempfile;
	}
	
	
}

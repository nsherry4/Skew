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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;
import plural.swing.ExecutorSetViewPanel;
import plural.swing.stream.StreamDialog;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import skew.DataSources;
import skew.core.datasource.DataSourceDescription;
import skew.core.datasource.DataSourceSelection;
import skew.core.datasource.DummyDataSource;
import skew.core.datasource.DataSource;
import skew.core.datasource.ExecutorDataSource;
import skew.core.datasource.DataSource.FileFormatAcceptance;
import skew.core.datasource.StreamDataSource;
import skew.core.model.DummyGrid;
import skew.core.model.ISkewDataset;
import skew.core.model.SkewDataset;
import skew.core.viewer.SettingType;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.DummyView;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.Summary;
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
import eventful.EventfulListener;
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

			List<Summary> summaries = viewMode.getPointSummary(0, 0);
			
			
			writer.write("X,  Y");
			for (Summary s : summaries) {
				for (String key : s.getCanonicalKeys()) writer.write(",  " + s.getName() + ": " + key);
			}
			writer.write("\n");
			
			Summary summary;
			for (int y = 0; y < data.getHeight(); y++) {
				for (int x = 0; x < data.getWidth(); x++) {
					
					summary = getCoordInfoMap(x, y);
					writer.write(summary.getValues().get("X") + ", ");
					writer.write(summary.getValues().get("Y"));
					
					summaries = viewMode.getPointSummary(x, y);
					for (Summary s : summaries) writeSummary(writer, s);
					
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
	
	private void writeSummary(BufferedWriter w, Summary s) throws IOException
	{
		for (String key : s.getCanonicalKeys()) {
			w.write(", ");
			if (s.getValues().containsKey(key)) {
				w.write(s.getValues().get(key));
			} else {
				w.write("-");
			}
		}
	}
	

	//x and y are pixel positions on the drawing surface, not indicies for a 2d-array model
	public void actionSelection(int px, int py, boolean multiselect, boolean doubleclick)
	{
		Coord<Integer> coord = ui.drawing.getMapCoordinateAtPoint(px, py, false);
		
		if (coord == null) return;
		if (doubleclick) viewMode.setPointSelected(coord.x, coord.y, multiselect);
		
		PropertyViewPanel panel;
		
		ui.sidebarInfoPanel.removeAll();
		
		panel = new PropertyViewPanel(getCoordInfoMap(coord.x, coord.y).getValues(), null, 0, false, false);
		panel.setBorder(new TitledBorder("Coordinates"));
		ui.sidebarInfoPanel.add(panel);
		
		List<Summary> summaries = new ArrayList<>();
		summaries.addAll(viewMode.getPointSummary(coord.x, coord.y));
		summaries.addAll(viewMode.getMapSummary());
		
		for (Summary s : summaries) {
			if (s.getValues().size() == 0) continue;
			panel = new PropertyViewPanel(s.getValues(), null, 0, false, false);
			panel.setBorder(new TitledBorder(s.getName()));
			ui.sidebarInfoPanel.add(panel);
		}
		
		
		ui.sidebarScroller.revalidate();
		
		event(SettingType.SELECTION);
	}

	
	
	//x and y are 2d-array-index values
	private Summary getCoordInfoMap(int x, int y)
	{
		Summary coordInfo = new Summary("Coordinates");
		
		coordInfo.addValue("X", ""+x);
		coordInfo.addValue("Y", ""+y);
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
	
	
	public static List<DataSourceDescription> getDataSourceDescriptions() {
		return DataSources.getSources().stream().map(ds -> ds.getDescription()).collect(Collectors.toList());
	}
	
	public static InputSelection selectFiles(SkewTabs window, String path, SkewUI skewui)
	{
		
		List<DataSource> formats = DataSources.getSources();
		
		//get info for open dialogue
		String[][] exts = new String[formats.size()][1];
		String[] descs = new String[formats.size()];
		for (int i = 0; i < formats.size(); i++)
		{
			exts[i] = new String[]{formats.get(i).getDescription().getExtensions().get(0)};
			descs[i] = formats.get(i).getDescription().getSummary();
		}
		
		//get a list of filenames from the user
		List<AbstractFile> absfiles = SwidgetIO.openFiles(window, "Select Data Files to Open", exts, descs, path, true);
		if (absfiles == null || absfiles.size() == 0) return null;
		
		List<String> files = new ArrayList<String>();
		for (AbstractFile af : absfiles)
		{
			files.add(af.getFileName());
		}
		
		return openFiles(files, window);
		
	}
	
	
	public static InputSelection openFiles(List<String> files, SkewTabs window) {
		
		//filter for just the working data sources
		List<DataSource> acceptingFormats = getViableDataSources(files);
		
		DataSource ds = null;
		
		if (acceptingFormats.size() > 1)
		{
			DataSourceSelection selection = new DataSourceSelection();
			ds = selection.pickDSP(window, acceptingFormats);
			if (ds != null) return new InputSelection(ds, files);
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
			return new InputSelection(ds, files);
		}
		
	}

	public static List<DataSource> getViableDataSources(List<String> files) {
		
		List<DataSource> formats = DataSources.getSources();
		
		//filter for just the working data sources
		List<DataSource> acceptingFormats = new ArrayList<DataSource>();
		List<DataSource> maybeFormats = new ArrayList<DataSource>();
		for (DataSource ds : formats)
		{
			FileFormatAcceptance acc = ds.accepts(files);
			if (acc == FileFormatAcceptance.ACCEPT) acceptingFormats.add(ds);
			if (acc == FileFormatAcceptance.MAYBE) maybeFormats.add(ds);
		}
		
		
		if (acceptingFormats.size() < 1) { 
			return maybeFormats; 
		} else {
			return acceptingFormats;
		}
		
	}
	
	public static void loadFiles(final SkewTabs window, List<String> files, DataSource datasource, final SkewUI skewui) {
		
		Coord<Integer> mapSize = queryLoadParameters(window, datasource);
		if (mapSize == null) {
			window.tabs.closeTab(skewui);
			return;
		}
		
		if (datasource instanceof StreamDataSource) {
			loadFilesStream(window, files, (StreamDataSource) datasource, skewui, mapSize);
		} else if (datasource instanceof ExecutorDataSource) {
			loadFilesExecutor(window, files, (ExecutorDataSource) datasource, skewui, mapSize);
		}
	}
	
	private static Coord<Integer> queryLoadParameters(SkewTabs window, DataSource datasource) {
		
		String g = "Map Dimensions";
		Parameter<Integer> paramWidth = new Parameter<>("Width", new IntegerEditor(), 1, g);
		Parameter<Integer> paramHeight = new Parameter<>("Height", new IntegerEditor(), 1, g);
		
		List<Parameter<?>> params = new FList<>();
		params.add(paramWidth);
		params.add(paramHeight);
		List<Parameter<?>> loadParameters = datasource.getLoadParameters();
		if (loadParameters != null) params.addAll(loadParameters);
		
		SimpleADController dialogController = new SimpleADController(params);
		
		AutoDialog dialog = new AutoDialog(dialogController, AutoDialogButtons.OK_CANCEL, window);
		dialog.setHelpTitle("Additional Dataset Information");
		dialog.setHelpMessage(datasource.getLoadParametersInformation());
		dialog.setModal(true);
		dialog.setTitle("Dataset Parameters");
		dialog.initialize(new FramesADLayout());
		
		
		//User Cancel/Close?
		if (!dialog.okSelected()) return null;
		Coord<Integer> mapSize = new Coord<>((Integer)paramWidth.getValue(), (Integer)paramHeight.getValue());
		return mapSize;
		
	}
	
	private static void loadFilesStream(final SkewTabs window, List<String> files, StreamDataSource datasource, final SkewUI skewui, Coord<Integer> mapSize) {
		
		StreamDialog dialog = new StreamDialog(window, files.size(), "Loading Data...");
		Stream<String> guiStream = dialog.guiStream(files.parallelStream());

		Thread loader = new Thread(() -> datasource.loadDataset(guiStream, mapSize));
		loader.start();
	}
	
	private static void loadFilesExecutor(final SkewTabs window, List<String> files, ExecutorDataSource datasource, final SkewUI skewui, Coord<Integer> mapSize)
	{
		final ExecutorSet<ISkewDataset> execset = getExecutor(window, files, datasource, skewui, mapSize);
		
		execset.addListener(() -> {
			javax.swing.SwingUtilities.invokeLater(() ->
			{
				if (execset.getCompleted() || execset.isAborted()) {
					skewui.setDialog(null);
					skewui.controller.actionSetDataset(execset.getResult());
					window.tabs.setActiveTab(skewui);
				}
			});
		});
		
		
		ExecutorSetViewPanel execPanel = new ExecutorSetViewPanel(execset);
		skewui.setDialog(execPanel);
		
		execset.startWorking();
	}
	
	
	private static ExecutorSet<ISkewDataset> getExecutor(SkewTabs window, List<String> files, ExecutorDataSource datasource, final SkewUI skewui, Coord<Integer> mapSize)
	{
		
		try {
			
			final ExecutorSet<ISkewDataset> execset = datasource.loadDataset(files, mapSize);
			//new ExecutorSetView(window, execset);		
			return execset;				
			
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

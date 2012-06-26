package misorientation.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ca.sciencestudio.process.xrd.calculation.Orientation;

import commonenvironment.AbstractFile;


import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;

import misorientation.calculation.misorientation.Calculation;
import misorientation.datasource.Acceptance;
import misorientation.datasource.DataSource;
import misorientation.datasource.DataSourceSelection;
import misorientation.model.Grain;
import misorientation.model.MisAnglePoint;
import misorientation.model.MisAngleGrid;
import misorientation.viewer.drawing.BoundaryMapPainter;
import misorientation.viewer.drawing.EBSDPalette;
import misorientation.viewer.drawing.GrainPalette;
import misorientation.viewer.drawing.SelectedGrainPainter;
import misorientation.viewer.modes.subviews.GrainMagnitudeSubView;
import misorientation.viewer.modes.subviews.IntraGrainSubView;
import misorientation.viewer.modes.subviews.MisorientationSubView;
import misorientation.viewer.modes.views.GrainLabelView;
import misorientation.viewer.modes.views.GrainMagnitudeView;
import misorientation.viewer.modes.views.InterGrainView;
import misorientation.viewer.modes.views.LocalView;
import misorientation.viewer.modes.views.MisorientationView;
import misorientation.viewer.modes.views.OrientationView;


import eventful.EventfulListener;
import fava.datatypes.Pair;
import fava.functionable.FList;

import scidraw.drawing.DrawingRequest;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.map.MapDrawing;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scidraw.swing.GraphicsPanel;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import scitypes.DirectionVector;
import scitypes.SigDigits;
import scitypes.Spectrum;
import swidget.Swidget;
import swidget.dialogues.fileio.SwidgetIO;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.ZoomSlider;

/**
 * Misorientation viewer GUI displays the results of misorientation calculations as a 2D map.
 * Older XMAS data sets are supported, but misorientation data calculated with this tool
 * uses the south and east boundary values to draw grain boundaries overtop of the map, rather
 * than blacking out pixels on the map to mark boundaries
 * @author Nathaniel Sherry, 2011
 *
 */

public class Misorientation extends JFrame{

	
	private File dir;
	private String title;
	private GraphicsPanel graphics;
	private MisAngleGrid data = null;
	
	MapDrawing drawing;
	MapPainter misorientationPainter, grainPainter;
	BoundaryMapPainter boundaryPainter;
	SelectedGrainPainter selectedGrainPainter;
	//OrientationPainter orientationPainter;
	RasterColorMapPainter orientationPainter;
	
	FList<AbstractPalette> misorientationPalettes, grainPalettes;
	
	DrawingRequest dr;
	
	
	
	
	JSpinner scaleSpinner;
	JLabel scaleLabel;
	JPanel scalePanel;
	
	JComboBox viewSelector;
	MisorientationView viewMode = new LocalView();
	
	JComboBox subViewSelector;
	MisorientationSubView subView = null;
	
	
	//float maxIntensity = 2f;
	
	JScrollPane pane;
	ZoomSlider zoomslider;
	float zoom = 1;
	
	JLabel coords;
	
	public static final Color backgroundGray = new Color(0.1f, 0.1f, 0.1f);
	

	public Misorientation() {
		
		data = new MisAngleGrid(1, 1);
		
		setTitle("Misorientation Viewer");
		
		dir = new File(".");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		
		//////////////////////////
		//Map Drawing
		//////////////////////////
		
		dr = new DrawingRequest();
		drawing = new MapDrawing();
		
		AbstractPalette thermal = new EBSDPalette();
		AbstractPalette grainpalette = new GrainPalette();
		
		
		AbstractPalette greyEmpty = new AbstractPalette() {
						
			@Override
			public Color getFillColour(double intensity, double maximum) {
				if (intensity < 0) return backgroundGray;
				return null;
			}
		};

		
		misorientationPalettes = new FList<AbstractPalette>(greyEmpty, thermal);
		misorientationPainter = new RasterSpectrumMapPainter(misorientationPalettes, null);
		
		grainPalettes = new FList<AbstractPalette>(greyEmpty, grainpalette);
		grainPainter = new RasterSpectrumMapPainter(grainPalettes, null);
		
		boundaryPainter = new BoundaryMapPainter();
		selectedGrainPainter = new SelectedGrainPainter();
		orientationPainter = new RasterColorMapPainter();
		
		drawing.setPainters(new FList<MapPainter>(misorientationPainter, selectedGrainPainter, boundaryPainter));
		
		
		
		
		//////////////////////////
		//UI
		//////////////////////////
		setPreferredSize(new Dimension(500, 650));
		pack();
		setLocationRelativeTo(null);
		
		setLayout(new BorderLayout());
		
		add(createToolbar(), BorderLayout.NORTH);
		
		
		createGraphicsPanel();
		pane = new JScrollPane(graphics);
		pane.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(pane, BorderLayout.CENTER);
		
		
		JPanel statusbar = createBottomControls();
		add(statusbar, BorderLayout.SOUTH);
		
		graphics.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				
				
				Coord<Integer> coord = drawing.getMapCoordinateAtPoint(x, y, false);
				if (coord == null) return;
				
				MisAnglePoint point = data.get(coord.x, coord.y);
				if (point == null) return;
				
				boolean multiselect = e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK;
				if (e.getClickCount() > 1) data.selectGrainAtPoint(point, multiselect);
				
				
				coords.setText("" +
						"(X: " + coord.x + 
						", Y:" + coord.y  + 
						")    " + 
						viewMode.getSummaryText(point, data)
					);
				
				settingsChanged();
			}
		});
		
		
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				setZoom(zoom);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		
		setVisible(true);
		
	}
	
	private JPanel createBottomControls()
	{
		JPanel statusbar = new JPanel();
		
		
		statusbar.setLayout(new BorderLayout());
		zoomslider = new ZoomSlider(100, 500, 50);
		statusbar.add(zoomslider, BorderLayout.EAST);
		zoomslider.addListener(new EventfulListener() {
			
			@Override
			public void change() {
				setZoom(zoomslider.getValue() / 100f);
			}
		});
		
		coords = new JLabel(" ");
		coords.setHorizontalAlignment(JLabel.CENTER);
		statusbar.add(coords, BorderLayout.NORTH);
		
		scalePanel = createScaleControl();
		statusbar.add(scalePanel, BorderLayout.WEST);
		setSubView();
		
		return statusbar;
	}

	
	private void setZoom(float newzoom)
	{
		zoom = newzoom;
		Rectangle r = pane.getVisibleRect();
		if (graphics == null) return;
		
		
		Dimension panesize = pane.getSize();
		
		Dimension currentSize = graphics.getSize();
		graphics.setSize(panesize);

		graphics.setPreferredSize(new Dimension((int)(graphics.getUsedWidth() * zoom), (int)(graphics.getUsedHeight() * zoom)));

		graphics.setSize(currentSize);
		
		
		
		pane.scrollRectToVisible(r);
		
		graphics.revalidate();
	}
	
	private JPanel createScaleControl()
	{
		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		


		scaleSpinner = new JSpinner(viewMode.scaleSpinnerModel(null, null));
		
		panel.add(scaleSpinner, BorderLayout.CENTER);
		
		scaleLabel = new JLabel("Scale ");
		panel.add(scaleLabel, BorderLayout.WEST);
		
		return panel;
		
	}
	
	private JToolBar createToolbar()
	{
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		
		ToolbarImageButton open = new ToolbarImageButton(StockIcon.DOCUMENT_OPEN, "Open IND Folder");
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				actionOpenData();

			}
		});
		
		
		
		
		ToolbarImageButton save = new ToolbarImageButton(StockIcon.DEVICE_CAMERA, "Save Picture");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (data != null) {
					new SavePicture(
							Misorientation.this, 
							Misorientation.this.graphics, 
							Misorientation.this.dir.getAbsolutePath()
						);
				}
				
			}
		});
		
		
		viewSelector = new JComboBox(MisorientationView.getViews().toArray());
		viewSelector.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				viewMode = (MisorientationView)viewSelector.getSelectedItem();
				setSubView();
				settingsChanged();
			}
		});

		
		
		
		toolbar.add(open);
		toolbar.add(save);
		
		toolbar.add(Box.createHorizontalGlue());

		toolbar.add(viewSelector);				
		
		return toolbar;
		
	}
	
	
	private void setSubView()
	{

		if (scaleSpinner != null) scalePanel.remove(scaleSpinner);
		scaleSpinner = null;
		if (subViewSelector != null) scalePanel.remove(subViewSelector);
		subViewSelector = null;
		
		
		if (viewMode.hasSublist())
		{
			List<MisorientationSubView> subviews = viewMode.getSubList();
			subViewSelector = new JComboBox(subviews.toArray());
			scalePanel.add(subViewSelector, BorderLayout.CENTER);
			subView = subviews.get(0);
			
			subViewSelector.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					subView = (MisorientationSubView) subViewSelector.getSelectedItem();
					setScaleSpinner();
					settingsChanged();
				}
			});
			
		}
		else
		{
			subViewSelector = null;
			subView = null;
		}
		
		
		setScaleSpinner();
	}
	
	private void setScaleSpinner()
	{

		if (scaleSpinner != null) scalePanel.remove(scaleSpinner);
		scaleSpinner = null;
		
		SpinnerModel model = viewMode.scaleSpinnerModel(data, subView);
		
		if (model == null) 
		{
			scaleLabel.setEnabled(false);
			return;
		}
		scaleLabel.setEnabled(true);
		
		scaleSpinner = new JSpinner(model);
		scalePanel.add(scaleSpinner, BorderLayout.EAST);
	

		scaleSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				settingsChanged();
			}
		});
		
		
		scaleSpinner.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				settingsChanged();
			}
		});
	}
	
	private void settingsChanged()
	{
		//if (viewMode.hasNumericScale()) maxIntensity = ((Double)scaleSpinner.getValue()).floatValue();
		setZoom(zoom);
		repaint();
	}
	
	private GraphicsPanel createGraphicsPanel()
	{
		graphics = new ScrollableGraphicsPanel() {
			
			
			{
				
				drawing.setDrawingRequest(dr);
				
			}
			
			
			@Override
			public float getUsedWidth() {
				
				setDR();
				
				return drawing.calcTotalSize().x;
			}
			
			@Override
			public float getUsedHeight() {
				
				setDR();
								
				return drawing.calcTotalSize().y;
			}
			
			private void setDR()
			{
				
				try {
					//set the drawing requests dimensions for the data and the screen
					dr.imageWidth = getWidth();
					dr.imageHeight = getHeight();
					
					dr.dataHeight = data.height; //map.height;
					dr.dataWidth = data.width; //map.width;
					dr.uninterpolatedHeight = data.height; //map.height;
					dr.uninterpolatedWidth = data.width; //map.width;
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			protected void drawGraphics(Surface backend, boolean vector) {
							
				if (data == null) return;
				if (data.width == 0 || data.height == 0) return;
				
				double maxValue = getMaxScaleValue();
				dr.maxYIntensity = (float) maxValue;
				
				setDR();

				
				dr.drawToVectorSurface = vector;

								
				Spectrum misorientationData = new Spectrum(data.size(), -1f);
				
				if (viewMode instanceof LocalView) 
				{
					drawing.setPainters(new FList<MapPainter>(misorientationPainter, selectedGrainPainter, boundaryPainter));
					for (int i = 0; i < data.size(); i++)
					{
						double v = data.get(i).average;
						misorientationData.set(i, (float)v);
					}
					
				} 
				
				else if (viewMode instanceof GrainLabelView) 
				{
					drawing.setPainters(new FList<MapPainter>(grainPainter, selectedGrainPainter, boundaryPainter));
					for (int i = 0; i < data.size(); i++)
					{
						int grainIndex = data.get(i).grain;
						if (grainIndex < 0) { misorientationData.set(i, -1f); continue; }
						Grain g = data.grains.get(grainIndex);
						if (g == null) { misorientationData.set(i, -1f); continue; }
						else { misorientationData.set(i, g.colourIndex); }
					}
				} 
				else if (viewMode instanceof GrainMagnitudeView) 
				{
					drawing.setPainters(new FList<MapPainter>(misorientationPainter, selectedGrainPainter, boundaryPainter));
					
					GrainMagnitudeSubView mag = (GrainMagnitudeSubView)subViewSelector.getSelectedItem();
										
					for (int i = 0; i < data.size(); i++)
					{
						int grain = data.get(i).grain;
						double v;
						if (grain == -1)
						{
							v = -1;
						} else {
							Grain g = data.grains.get(grain);
							v = mag.select(new double[]{g.magMin, g.magMax, g.magAvg});
						}
						misorientationData.set(i, (float)v);
					}
				
				} 
				else if (viewMode instanceof OrientationView)
				{
					drawing.setPainters(new FList<MapPainter>(orientationPainter, selectedGrainPainter, boundaryPainter));
					
					List<Color> pixelColours = new FList<Color>(data.width * data.height);
					for (int i = 0; i < data.width * data.height; i++){ pixelColours.add(backgroundGray); }
					
					Color c;
					for (MisAnglePoint point : data.getBackingList())
					{
						if (point.orientationVectors == null)
						{
							c = backgroundGray;
						}
						else
						{
							DirectionVector dv = point.orientationVectors.get(subView.getIndex());
							c = Orientation.directionToColor(dv, 1f);
						}
						pixelColours.set(point.index, c);
					}
					
					orientationPainter.setPixels(pixelColours);
					
					
					
				}
				else if (viewMode instanceof InterGrainView)
				{
					IntraGrainSubView igv = (IntraGrainSubView)subView;
					
					boolean relative = (igv.getIndex() == 0);
					
					if (relative) dr.maxYIntensity = 1;
					
					drawing.setPainters(new FList<MapPainter>(misorientationPainter, selectedGrainPainter, boundaryPainter));
					for (int i = 0; i < data.size(); i++)
					{
						MisAnglePoint p = data.get(i);
						if (p == null)	{ misorientationData.set(i, -1.0f); continue; }

						Grain g = data.getGrainAtPoint(p);
						if (g == null)	{ misorientationData.set(i, -1.0f); continue; }
						
						float v = (float) p.intraGrainMisorientation;
						if (relative) v /= g.intraGrainMax;
						misorientationData.set(i, (float)v);
						
					}
										
				}
				 
				
				List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
				
				axisMarkings.add(  new Pair<Float, String>(0.0f, "" + 0)  );
				axisMarkings.add(  new Pair<Float, String>(0.25f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.25), 3))  );
				axisMarkings.add(  new Pair<Float, String>(0.5f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.5), 3))  );
				axisMarkings.add(  new Pair<Float, String>(0.75f, "" + SigDigits.roundFloatTo((float)(maxValue * 0.75), 3))  );
				axisMarkings.add(  new Pair<Float, String>(1f, "" + SigDigits.roundFloatTo((float)maxValue, 3))  );
				
				
				AxisPainter spectrum = new SpectrumCoordsAxisPainter(
						false, 
						null, 
						null, 
						null, 
						null, 
						null, 
						true, 
						20, 
						256, 
						misorientationPalettes, 
						false, 
						"Misorientation Angle in Degrees", 
						1,
						false,
						axisMarkings);
				
				AxisPainter titlePainter = new TitleAxisPainter(1.0f, null, null, title, null);
				
				if (
						//viewMode == MisorientationViews.LABELS || 
						//viewMode == MisorientationViews.ORIENTATION ||
						//viewMode == MisorientationViews.INTRAGRAIN_MISORIENTATION
						viewMode instanceof GrainLabelView ||
						viewMode instanceof OrientationView ||
						viewMode instanceof InterGrainView
					) 
				{
					drawing.setAxisPainters(new FList<AxisPainter>(titlePainter));
				} else {
					drawing.setAxisPainters(new FList<AxisPainter>(spectrum, titlePainter));
				}

				//set the painter and drawings data, and paint the screen
				boundaryPainter.setData(data);
				selectedGrainPainter.setData(data);
				drawing.needsMapRepaint();
				drawing.setDrawingRequest(dr);
				misorientationPainter.setData(misorientationData);
				grainPainter.setData(misorientationData);
				drawing.setContext(backend);
				drawing.draw();
			}

			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return graphics.getPreferredSize();
			}

			@Override
			public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
				return (int)(25f*zoom);
			}

			@Override
			public boolean getScrollableTracksViewportHeight() {
				return zoom == 1;
			}

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return zoom == 1;
			}

			@Override
			public int getScrollableUnitIncrement(Rectangle arg0, int arg1,	int arg2) {
				return (int)(5f*zoom);
			}
				
		};
		
		return graphics;
	}
	
	private double getMaxScaleValue()
	{
		if (scaleSpinner == null) return -1;
		
		Object o = scaleSpinner.getValue();
		if (o instanceof Double) return (Double)o;
		
		return -1;
	}
	
	
	public static void main(String[] args) {
		
		Swidget.initialize();
		
		new Misorientation();
		
	}
	
	
	private void setData(MisAngleGrid newdata)
	{
		data = newdata;
		repaint();
	}
	

	
	
	/*
	
		private void actionOpenData()
	{

		List<AbstractFile> files;
		List<AbstractDSP> formats =  new ArrayList<AbstractDSP>(dataController.getDataSourcePlugins());
				
		String[][] exts = new String[formats.size()][];
		String[] descs = new String[formats.size()];
		for (int i = 0; i < formats.size(); i++)
		{
			exts[i] = formats.get(i).getFileExtensions().toArray(new String[]{});
			descs[i] = formats.get(i).getDataFormat();
		}

		files = openNewDataset(exts, descs);
		if (files == null) return;
		
		FList<String> filenames = FList.wrap(files).map(new FnMap<AbstractFile, String>() {

			@Override
			public String f(AbstractFile v)
			{
				return v.getFileName();
			}});
		
		
		loadFiles(filenames);
		
		

	}
	
	*/
	
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
		List<AbstractFile> absfiles = SwidgetIO.openFiles(this, "Select Data Files to Open", exts, descs, ".");
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
		
		if (acceptingFormats.size() > 1)
		{
			DataSourceSelection selection = new DataSourceSelection();
			DataSource dsp = selection.pickDSP(this, acceptingFormats);
			if (dsp != null) loadFiles(files, dsp);
		}
		else if (acceptingFormats.size() == 0)
		{
			JOptionPane.showMessageDialog(
					this, 
					"Could not determine the data format of the selected file(s)", 
					"Open Failed", 
					JOptionPane.ERROR_MESSAGE, 
					StockIcon.BADGE_WARNING.toImageIcon(IconSize.ICON)
				);
		}
		else
		{
			loadFiles(files, acceptingFormats.get(0));
		}
		
	}
	
	private void loadFiles(List<String> filenames, DataSource ds)
	{
		
		try {
			
			Integer width = Integer.parseInt(JOptionPane.showInputDialog(Misorientation.this, "Map Width", 1));
			Integer height = Integer.parseInt(JOptionPane.showInputDialog(Misorientation.this, "Map Height", 1));
		
			Coord<Integer> mapSize = new Coord<Integer>(width, height);
			
			ExecutorSet<MisAngleGrid> execset = Calculation.calculate(filenames, ds,  mapSize);
			new ExecutorSetView(Misorientation.this, execset);
			
			setData(execset.getResult());
								
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
	
}



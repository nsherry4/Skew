package misorientation.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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

import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;

import misorientation.calculation.misorientation.Calculation;
import misorientation.model.Grain;
import misorientation.model.MisAnglePoint;
import misorientation.model.MisAngleGrid;


import eventful.EventfulListener;
import fava.datatypes.Pair;
import fava.functionable.FList;
import fava.signatures.FnMap;

import scidraw.drawing.DrawingRequest;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.map.MapDrawing;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scidraw.swing.GraphicsPanel;
import scidraw.swing.SavePicture;
import scitypes.Coord;
import scitypes.SigDigits;
import scitypes.Spectrum;
import swidget.Swidget;
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
	MapPainter blackPainter;
	
	FList<AbstractPalette> misorientationPalettes, grainPalettes;
	
	DrawingRequest dr;
	
	
	
	
	JSpinner scaleSpinner;
	
	JComboBox viewSelector;
	MisorientationViews viewMode = MisorientationViews.MISORIENTATION;

	
	float maxIntensity = 2f;
	
	JScrollPane pane;
	ZoomSlider zoomslider;
	float zoom = 1;
	
	JLabel coords;
	
	

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
		
		final Color backgroundGray = new Color(0.1f, 0.1f, 0.1f);
		AbstractPalette greyEmpty = new AbstractPalette() {
						
			@Override
			public Color getFillColour(double intensity, double maximum) {
				if (intensity < 0) return backgroundGray;
				return null;
			}
		};
		
		AbstractPalette blackStrong = new AbstractPalette() {
			
			@Override
			public Color getFillColour(double intensity, double maximum) {
				double cutoff = 500000;
				if (intensity > Math.max(cutoff, maxIntensity)) return Color.BLACK;
				return null;
			}
		};
		
		misorientationPalettes = new FList<AbstractPalette>(blackStrong, greyEmpty, thermal);
		misorientationPainter = new RasterSpectrumMapPainter(misorientationPalettes, null);
		
		grainPalettes = new FList<AbstractPalette>(blackStrong, greyEmpty, grainpalette);
		grainPainter = new RasterSpectrumMapPainter(grainPalettes, null);
		
		boundaryPainter = new BoundaryMapPainter();
		
		drawing.setPainters(new FList<MapPainter>(misorientationPainter, boundaryPainter));
		
		
		
		
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
		
		
		JPanel statusbar = new JPanel();
		add(statusbar, BorderLayout.SOUTH);
		
		statusbar.setLayout(new BorderLayout());
		zoomslider = new ZoomSlider(100, 500, 50);
		statusbar.add(zoomslider, BorderLayout.EAST);
		zoomslider.addListener(new EventfulListener() {
			
			@Override
			public void change() {
				setZoom(zoomslider.getValue() / 100f);
			}
		});
		
		coords = new JLabel();
		coords.setHorizontalAlignment(JLabel.CENTER);
		statusbar.add(coords, BorderLayout.CENTER);
		
		
		statusbar.add(createScaleControl(), BorderLayout.WEST);
		
		
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
				
				
				
				coords.setText("" +
						"(X: " + coord.x + 
						", Y:" + coord.y  + 
						")    " + 
						viewMode.getSummaryText(point, data)
					);
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
		


		scaleSpinner = new JSpinner(viewMode.scaleSpinnerModel(null));
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
		
		panel.add(scaleSpinner, BorderLayout.CENTER);
		
		panel.add(new JLabel("Scale "), BorderLayout.WEST);
		
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
				
				JFileChooser chooser = new JFileChooser(new File("."));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(Misorientation.this);
				final File f = chooser.getSelectedFile();
				
				if (f == null) return;
				if (!f.isDirectory()) return;
				
				FList<String> filenames = new FList<String>(f.list(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".ind");
					}
				})).map(new FnMap<String, String>() {

					@Override
					public String f(String filename) {
						return f.getPath() + "/" + filename;
					}
				});
				
				
				
				
				try {
					
					Integer width = Integer.parseInt(JOptionPane.showInputDialog(Misorientation.this, "Map Width", 1));
					Integer height = Integer.parseInt(JOptionPane.showInputDialog(Misorientation.this, "Map Height", 1));
				
					Coord<Integer> mapSize = new Coord<Integer>(width, height);
					
					ExecutorSet<MisAngleGrid> execset = Calculation.calculate(filenames, mapSize);
					new ExecutorSetView(Misorientation.this, execset);
					
					setData(execset.getResult());
										
					
				} catch (Exception ex){
					ex.printStackTrace();
				}
				
								
				
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
		
		
		viewSelector = new JComboBox(MisorientationViews.values());
		viewSelector.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				viewMode = (MisorientationViews)viewSelector.getSelectedItem();
				double scale = viewMode.defaultScale(data);
				setSpinnerModel(scaleSpinner, viewMode.scaleSpinnerModel(data));
				setSpinner(scaleSpinner, scale);
				maxIntensity = (float)scale;
				settingsChanged();
			}
		});

		
		
		
		toolbar.add(open);
		toolbar.add(save);
		
		toolbar.add(Box.createHorizontalGlue());

		toolbar.add(viewSelector);				
		
		return toolbar;
		
	}
	
	private void setSpinner(JSpinner spinner, Object object)
	{
		ChangeListener[] listeners = spinner.getChangeListeners();
		
		for (ChangeListener listener : listeners) {
			spinner.removeChangeListener(listener);
		}
		
		spinner.setValue(object);
		
		for (ChangeListener listener : listeners) {
			spinner.addChangeListener(listener);
		}
		
	}
	
	private void setSpinnerModel(JSpinner spinner, SpinnerModel model)
	{
		ChangeListener[] listeners = spinner.getChangeListeners();
		
		for (ChangeListener listener : listeners) {
			spinner.removeChangeListener(listener);
		}
		
		spinner.setModel(model);
		
		for (ChangeListener listener : listeners) {
			spinner.addChangeListener(listener);
		}
		
	}
	
	private void settingsChanged()
	{
		maxIntensity = ((Double)scaleSpinner.getValue()).floatValue();
		setZoom(zoom);
		Misorientation.this.repaint();
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
				
				dr.maxYIntensity = maxIntensity;

				
				setDR();

				
				dr.drawToVectorSurface = vector;

								
				Spectrum spectrumData = new Spectrum(data.size(), -1f);
				
				if (viewMode == MisorientationViews.MISORIENTATION) {
					drawing.setPainters(new FList<MapPainter>(misorientationPainter, boundaryPainter));
					for (int i = 0; i < Math.min(data.size(), data.size()); i++)
					{
						double v = data.get(i).average;
						spectrumData.set(i, (float)v);
					}
				} else if (viewMode == MisorientationViews.GRAINLABELS) {
					drawing.setPainters(new FList<MapPainter>(grainPainter, boundaryPainter));
					for (int i = 0; i < Math.min(data.size(), data.size()); i++)
					{
						int grainIndex = data.get(i).grain;
						if (grainIndex < 0) { spectrumData.set(i, -1f); continue; }
						Grain g = data.grains.get(grainIndex);
						if (g == null) { spectrumData.set(i, -1f); continue; }
						else { spectrumData.set(i, g.colourIndex); }
					}
				} else if (viewMode == MisorientationViews.MAGNITUDE) {
					drawing.setPainters(new FList<MapPainter>(misorientationPainter, boundaryPainter));
					for (int i = 0; i < Math.min(data.size(), data.size()); i++)
					{
						int grain = data.get(i).grain;
						double v;
						if (grain == -1)
						{
							v = -1;
						} else { 
							v = data.grains.get(grain).magnitude;
						}
						spectrumData.set(i, (float)v);
					}
				}
				
				FList<Coord<Double>> boundaryData = data.getBackingList().map(new FnMap<MisAnglePoint, Coord<Double>>(){

					@Override public Coord<Double> f(MisAnglePoint v) {
						return new Coord<Double>(v.east, v.south);
					}});
				
				
				List<Pair<Float, String>> axisMarkings = new FList<Pair<Float,String>>();
				
				axisMarkings.add(  new Pair<Float, String>(0.0f, "" + 0)  );
				axisMarkings.add(  new Pair<Float, String>(0.25f, "" + SigDigits.roundFloatTo((float)(maxIntensity * 0.25), 3))  );
				axisMarkings.add(  new Pair<Float, String>(0.5f, "" + SigDigits.roundFloatTo((float)(maxIntensity * 0.5), 3))  );
				axisMarkings.add(  new Pair<Float, String>(0.75f, "" + SigDigits.roundFloatTo((float)(maxIntensity * 0.75), 3))  );
				axisMarkings.add(  new Pair<Float, String>(1f, "" + maxIntensity)  );
				
				
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
				
				if (viewMode == MisorientationViews.GRAINLABELS) 
				{
					drawing.setAxisPainters(new FList<AxisPainter>(titlePainter));
				} else {
					drawing.setAxisPainters(new FList<AxisPainter>(spectrum, titlePainter));
				}
				
				//set the painter and drawings data, and paint the screen
				boundaryPainter.setPixels(boundaryData);
				drawing.needsMapRepaint();
				drawing.setDrawingRequest(dr);
				misorientationPainter.setData(spectrumData);
				grainPainter.setData(spectrumData);
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
	
	
	public static void main(String[] args) {
		
		Swidget.initialize();
		
		new Misorientation();
		
	}
	
	
	private void setData(MisAngleGrid newdata)
	{
		data = newdata;
		repaint();
	}
	

	
}



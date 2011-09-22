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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plural.executor.ExecutorSet;
import plural.swing.ExecutorSetView;

import misorientation.calculation.Calculation;


import commonenvironment.AbstractFile;
import eventful.EventfulListener;
import fava.datatypes.Pair;
import fava.functionable.FList;
import fava.functionable.FStringInput;
import fava.signatures.FnCondition;
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
import swidget.dialogues.fileio.SwidgetIO;
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
	private FList<Pixel> data = null;
	private int datawidth, dataheight;
	
	MapDrawing drawing;
	MapPainter painter;
	BoundaryMapPainter boundaryPainter;
	MapPainter blackPainter;
	
	FList<AbstractPalette> palettes;
	
	DrawingRequest dr;
	
	
	
	
	JSpinner hspin, wspin, maxspin;

	
	float maxIntensity = 2f;
	
	JScrollPane pane;
	ZoomSlider zoomslider;
	float zoom = 1;
	
	JLabel coords;
	
	

	public Misorientation() {
		
		setTitle("Misorientation Viewer");
		
		dir = new File(".");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		
		//////////////////////////
		//Map Drawing
		//////////////////////////
		
		dr = new DrawingRequest();
		drawing = new MapDrawing();
		
		AbstractPalette thermal = new EBSDPalette();
		
		AbstractPalette greyEmpty = new AbstractPalette() {
						
			@Override
			public Color getFillColour(double intensity, double maximum) {
				if (intensity < 0) return Color.GRAY;
				return null;
			}
		};
		
		AbstractPalette blackStrong = new AbstractPalette() {
			
			@Override
			public Color getFillColour(double intensity, double maximum) {
				double cutoff = 5;
				if (intensity > Math.max(cutoff, maxIntensity)) return Color.BLACK;
				return null;
			}
		};
		
		palettes = new FList<AbstractPalette>(blackStrong, greyEmpty, thermal);
		painter = new RasterSpectrumMapPainter(palettes, null);
		boundaryPainter = new BoundaryMapPainter();
		
		drawing.setPainters(new FList<MapPainter>(painter, boundaryPainter));
		
		
		
		
		//////////////////////////
		//UI
		//////////////////////////
		setPreferredSize(new Dimension(1000, 474));
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
		statusbar.add(coords, BorderLayout.CENTER);
		
		
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
				
				int index = datawidth * coord.y + coord.x;
				
				String avg = formatMisorientationValue(data.get(index).average);
				String east = formatMisorientationValue(data.get(index).borders.x);
				String south = formatMisorientationValue(data.get(index).borders.y);

				coords.setText(" X: " + coord.x + ", Y:" + coord.y  + ",   Average: " + avg + ", East: " + east + ", South: " + south);
				
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
	
	
	private String formatMisorientationValue(double value)
	{
		String valString;
		valString = SigDigits.roundFloatTo((float)value, 3);
		if (value < 0) valString = "No Data";
		
		return valString;
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
	
	private JToolBar createToolbar()
	{
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		ToolbarImageButton open = new ToolbarImageButton(StockIcon.DOCUMENT_OPEN, "Open Misorientation File");
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String[] exts = {"txt"};
				AbstractFile misorientationFile = SwidgetIO.openFile(
						Misorientation.this, 
						"Open Misorientation File", 
						exts, 
						"TXT Files", 
						dir.getAbsolutePath()
					);
				
				try {
					if (misorientationFile != null) {
						
						File moFile = new File(misorientationFile.getFileName());
						dir = moFile.getParentFile();
						title = moFile.getName();
						
						if (title.indexOf(".") != 0)
						{
							title = title.substring(0, title.lastIndexOf(".")).trim();
							Misorientation.this.setTitle(title);
						}
						
						Reader r = misorientationFile.getReader();
						readIntensities(r);
						r.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				repaint();
				
			}
		});
		
		
		ToolbarImageButton importFiles = new ToolbarImageButton(StockIcon.DOCUMENT_IMPORT, "Import IND Folder");
		importFiles.addActionListener(new ActionListener() {
			
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
				
				StringWriter writer = new StringWriter();
				
				
				
				try {
					
					Integer width = Integer.parseInt(JOptionPane.showInputDialog(Misorientation.this, "Map Width", 1));
					Integer height = Integer.parseInt(JOptionPane.showInputDialog(Misorientation.this, "Map Height", 1));
				
					Coord<Integer> mapSize = new Coord<Integer>(width, height);
					
					ExecutorSet<Boolean> execset = Calculation.calculate(filenames, mapSize, writer);
					ExecutorSetView view = new ExecutorSetView(Misorientation.this, execset);
					
					
					readIntensities(new StringReader(writer.toString()));
					
					wspin.setValue(width);
					hspin.setValue(height);
					
				} catch (Exception ex){}
				
								
				
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
		
		
		
		
		
		
		
		hspin = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
		wspin = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
		
		maxspin = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 180.0, 0.1));
		
		//hspin.getEditor().setPreferredSize(new Dimension(100, hspin.getEditor().getPreferredSize().height));
		//wspin.getEditor().setPreferredSize(new Dimension(100, wspin.getEditor().getPreferredSize().height));

		
		hspin.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
						
				settingsChanged();
			}
		});
		
		hspin.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				settingsChanged();
			}
		});
		
		
		wspin.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				settingsChanged();
			}
		});
		
		
		wspin.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				settingsChanged();
			}
		});
		
		
		
		
		maxspin.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				settingsChanged();
			}
		});
		
		
		maxspin.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				settingsChanged();
			}
		});
		
		
		
		
		toolbar.add(open);
		toolbar.add(importFiles);
		toolbar.add(save);
		
		toolbar.addSeparator();
		toolbar.add(Box.createHorizontalGlue());

		
		toolbar.add(new JLabel("  Scale:"));
		toolbar.add(maxspin);
		
		toolbar.add(new JLabel("  Width:"));
		toolbar.add(wspin);
				
		toolbar.add(new JLabel("  Height:"));
		toolbar.add(hspin);
		
		
		return toolbar;
		
	}
	
	private void settingsChanged()
	{
		dataheight = (Integer)hspin.getValue();
		datawidth = (Integer)wspin.getValue();
		maxIntensity = ((Double)maxspin.getValue()).floatValue();
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
				//set the drawing requests dimensions for the data and the screen
				dr.imageWidth = getWidth();
				dr.imageHeight = getHeight();
				
				dr.dataHeight = dataheight; //map.height;
				dr.dataWidth = datawidth; //map.width;
				dr.uninterpolatedHeight = dataheight; //map.height;
				dr.uninterpolatedWidth = datawidth; //map.width;
			}
			
			@Override
			protected void drawGraphics(Surface backend, boolean vector) {
							
				if (data == null) return;
				if (datawidth == 0 || dataheight == 0) return;
				
				dr.maxYIntensity = maxIntensity;

				
				setDR();

				
				dr.drawToVectorSurface = vector;

								
				Spectrum spectrumData = new Spectrum(datawidth*dataheight, -1f);
				for (int i = 0; i < Math.min(datawidth*dataheight, data.size()); i++)
				{
					double v = data.get(i).average;
					spectrumData.set(i, (float)v);
				}
				
				FList<Coord<Double>> boundaryData = data.map(new FnMap<Pixel, Coord<Double>>(){

					@Override public Coord<Double> f(Pixel v) {
						return v.borders;
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
						palettes, 
						false, 
						"Misorientation Angle in Degrees", 
						1,
						false,
						axisMarkings);
				
				AxisPainter titlePainter = new TitleAxisPainter(1.0f, null, null, title, null);
				
				drawing.setAxisPainters(new FList<AxisPainter>(spectrum, titlePainter));
				
				//set the painter and drawings data, and paint the screen
				boundaryPainter.setPixels(boundaryData);
				drawing.needsMapRepaint();
				drawing.setDrawingRequest(dr);
				painter.setData(spectrumData);
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
	
	
	private void readIntensities(Reader reader) throws IOException
	{
		
		FList<Pixel> olddata = data;
		data = FStringInput.lines(reader).toSink().map(new FnMap<String, String>() {

			@Override
			public String f(String v) {
				if (!v.contains("#")) return v.trim();
				return v.substring(0, v.indexOf("#")).trim();
			}
		}).filter(new FnCondition<String>() {

			@Override
			public Boolean f(String v) {
				return v.length() > 0;
			}
		}).map(new FnMap<String, Pixel>() {

			@Override
			public Pixel f(String line) {

				Pixel p = new Pixel();
				double x = 0d;
				double y = 0d;
				
				FList<String> words = FStringInput.words(line.trim()).toSink();
				
				
				p.average = Double.parseDouble(words.get(2).trim());
				if (words.size() > 3) x = Double.parseDouble(words.get(3).trim());
				if (words.size() > 3) y = Double.parseDouble(words.get(4).trim());
				
				Coord<Double> coord = new Coord<Double>(x, y);
				p.borders = coord;
				
				return p;

			}
		}).toSink();
		
		if (olddata == null || olddata.size() != data.size())
		{
			
			int new_w = data.size();
			int new_h = 1;
			
			hspin.setValue(new_h);
			wspin.setValue(new_w);
			
		}
		
		repaint();
		
		
	}
}



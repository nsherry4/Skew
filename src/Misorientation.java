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
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


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
import scidraw.drawing.map.painters.RasterColorMapPainter;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.map.palettes.ThermalScalePalette;
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
	int zoom = 1;
	
	JLabel coords;
	
	

	public Misorientation() {
		
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
		setPreferredSize(new Dimension(750, 350));
		pack();
		
		setLayout(new BorderLayout());
		
		add(createToolbar(), BorderLayout.NORTH);
		
		
		createGraphicsPanel();
		pane = new JScrollPane(graphics);
		pane.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(pane, BorderLayout.CENTER);
		
		
		JPanel statusbar = new JPanel();
		add(statusbar, BorderLayout.SOUTH);
		
		statusbar.setLayout(new BorderLayout());
		zoomslider = new ZoomSlider(1, 10, 1);
		statusbar.add(zoomslider, BorderLayout.EAST);
		zoomslider.addListener(new EventfulListener() {
			
			@Override
			public void change() {
				setZoom(zoomslider.getValue());
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
		valString = SigDigits.roundFloatTo((float)value, 4);
		if (value < 0) valString = "No Data";
		if (value > 5) valString = "Grain Boundary";
		
		return valString;
	}
	
	private void setZoom(int newzoom)
	{
		zoom = newzoom;
		Rectangle r = pane.getVisibleRect();
		if (graphics == null) return;
		
		
		//float oldwidth = graphics.getUsedWidth();
		//float oldheight = graphics.getUsedHeight();

		
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
		
		ToolbarImageButton open = new ToolbarImageButton(StockIcon.DOCUMENT_OPEN, "Open");
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
						
						readIntensities(misorientationFile);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				repaint();
				
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
		
		
		
		
		
		
		
		hspin = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));
		wspin = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));
		
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
		toolbar.add(save);
		
		toolbar.addSeparator();
		toolbar.add(Box.createHorizontalGlue());

		
		toolbar.add(new JLabel("  Cutoff:"));
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
				axisMarkings.add(  new Pair<Float, String>(0.25f, "" + SigDigits.roundFloatTo((float)(maxIntensity * 0.25), 4))  );
				axisMarkings.add(  new Pair<Float, String>(0.5f, "" + SigDigits.roundFloatTo((float)(maxIntensity * 0.5), 4))  );
				axisMarkings.add(  new Pair<Float, String>(0.75f, "" + SigDigits.roundFloatTo((float)(maxIntensity * 0.75), 4))  );
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
				return 25*zoom;
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
				return 5*zoom;
			}
				
		};
		
		return graphics;
	}
	
	
	public static void main(String[] args) {
		
		Swidget.initialize();
		
		new Misorientation();
		
	}
	
	
	private void readIntensities(AbstractFile file) throws IOException
	{
		
		FList<Pixel> olddata = data;
		data = FStringInput.lines(file.getInputStream()).map(new FnMap<String, String>() {

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



package skew.core.viewer;

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
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import scidraw.drawing.DrawingRequest;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.map.MapDrawing;
import scidraw.swing.GraphicsPanel;
import skew.core.Version;
import skew.core.controller.SkewController;
import skew.core.model.ISkewDataset;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.MapView;
import skew.core.viewer.modes.views.impl.DummyView;
import swidget.Swidget;
import swidget.dialogues.AboutDialogue;
import swidget.icons.IconFactory;
import swidget.icons.StockIcon;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.ZoomSlider;

import com.ezware.dialog.task.TaskDialogs;
import commonenvironment.IOOperations;

import eventful.EventfulListener;

/**
 * Misorientation viewer GUI displays the results of misorientation calculations as a 2D map.
 * Older XMAS data sets are supported, but misorientation data calculated with this tool
 * uses the south and east boundary values to draw grain boundaries overtop of the map, rather
 * than blacking out pixels on the map to mark boundaries
 * @author Nathaniel Sherry, 2011
 *
 */

@SuppressWarnings("serial")
public class SkewUI extends JPanel {

	SkewTabs parent;
	SkewController controller;
	
	boolean dummy= true;
	
	public GraphicsPanel graphics;
	
	
	public MapDrawing drawing;
	DrawingRequest dr;
	
	JSpinner scaleSpinner;
	JLabel scaleLabel;
	JPanel scalePanel;

	public JComboBox<MapView> viewSelector;
	JComboBox<MapSubView> subViewSelector;
	

	JScrollPane pane;
	ZoomSlider zoomslider;
	float zoom = 1;
	
	public ToolbarImageButton savetext;
	private ToolbarImageButton save;
	
	public JLabel coords;
	
	
	
	public SkewUI(SkewTabs parent)
	{
		this(parent, new SkewController(parent));
	}
	

	public SkewUI(SkewTabs parent, final SkewController controller) 
	{
		
		this.parent = parent;
		
		this.controller = controller;
		controller.setUI(this);
		
		
		
		
		//////////////////////////
		//Map Drawing
		//////////////////////////
		
		dr = new DrawingRequest();
		drawing = new MapDrawing();

		
		
		//////////////////////////
		//UI
		//////////////////////////
		
		setLayout(new BorderLayout());
		
		add(createToolbar(), BorderLayout.NORTH);
		
		
		createGraphicsPanel();
		graphics.setPreferredSize(new Dimension(1000, 473));
		
		pane = new JScrollPane(graphics);
		pane.setBorder(new EmptyBorder(0, 0, 0, 0));
		pane.setBackground(Color.white);
		pane.getViewport().setBackground(Color.white);
		
		add(pane, BorderLayout.CENTER);
		

		
		
		JPanel statusbar = createBottomControls();
		add(statusbar, BorderLayout.SOUTH);
		
		viewsUpdated();
		
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
				boolean multiselect = e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK;
				boolean doubleclick = e.getClickCount() > 1;
				
				controller.actionSelection(x, y, multiselect, doubleclick);
				
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
		setSubViewUI();
		
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
		
		scaleLabel = new JLabel("Scale ");
		panel.add(scaleLabel, BorderLayout.WEST);
		
		return panel;
		
	}
	
	private JToolBar createToolbar()
	{
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		
			
		ToolbarImageButton open = new ToolbarImageButton(StockIcon.DOCUMENT_OPEN, "Open File(s)");
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				ISkewDataset dataset = SkewController.loadDataset(parent, controller.data.path());
				if (dataset != null) {
					//create new UI
					SkewUI ui = new SkewUI(parent);
					ui.dummy = false;
					parent.addTab(ui);
					ui.controller.actionSetDataset(dataset);
					parent.tabs.setActiveTab(ui);
					
					//remove this one, if a placeholder
					if (dummy) parent.tabs.closeTab(SkewUI.this);
				}
				
			}
		});

		ToolbarImageButton tab = new ToolbarImageButton(StockIcon.WINDOW_TAB_NEW, "New Tab");
		tab.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				parent.newTab();

			}
		});
		
		save = new ToolbarImageButton(StockIcon.DEVICE_CAMERA, "Save Picture");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				controller.actionSavePicture();

			}
		});
		save.setVisible(false);
		
		
		viewSelector = new JComboBox<MapView>(new Vector<MapView>(controller.data.datasource().getViews()));
		
		viewSelector.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				controller.actionViewChange((MapView)viewSelector.getSelectedItem());
			}
		});
		
		
		savetext = new ToolbarImageButton(StockIcon.DOCUMENT_EXPORT, "Export Map Data");
		savetext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				controller.actionSaveText();
			}
		});
		savetext.setVisible(false);
		
		
		ToolbarImageButton about = new ToolbarImageButton(StockIcon.MISC_ABOUT, "About");
		about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new AboutDialogue(
						parent,
						Version.name + " " + Version.short_version,
						Version.description,
						"www.sciencestudioproject.com",
						"Copyright (c) 2012 by<br>The University of Western Ontario<br>and<br>The Canadian Light Source Inc.",
						IOOperations.readTextFromJar("/skew/licence.txt"),
						IOOperations.readTextFromJar("/skew/credits.txt"),
						"skew",
						"",
						Version.long_version,
						"",
						Version.date,
						true
				);
			}
		});
		
		
		
		toolbar.add(open);
		//toolbar.add(tab);
		toolbar.add(save);
		
		
		toolbar.add(Box.createHorizontalGlue());

		toolbar.add(viewSelector);	
		toolbar.add(savetext);
		
		toolbar.add(Box.createHorizontalGlue());
		
		toolbar.add(about);
		
		return toolbar;
		
	}
	
	
	public void setSubViewUI()
	{

		if (scaleSpinner != null) scalePanel.remove(scaleSpinner);
		scaleSpinner = null;
		if (subViewSelector != null) scalePanel.remove(subViewSelector);
		subViewSelector = null;
		
		if (controller.viewMode.hasSublist())
		{
			List<MapSubView> subviews = controller.viewMode.getSubList();
			subViewSelector = new JComboBox<MapSubView>(new Vector<MapSubView>(subviews));
			scalePanel.add(subViewSelector, BorderLayout.CENTER);
			controller.subView = subviews.get(0);
			
			subViewSelector.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					controller.actionSubviewChange((MapSubView) subViewSelector.getSelectedItem());
				}
			});
			
		}
		else
		{
			subViewSelector = null;
			controller.subView = null;
		}
		
		
		setScaleSpinner();
	}
	
	private void setScaleSpinner()
	{

		if (scaleSpinner != null) scalePanel.remove(scaleSpinner);
		scaleSpinner = null;
		
		SpinnerModel model = controller.viewMode.scaleSpinnerModel(controller.subView);
		
		if (model == null) 
		{
			scaleLabel.setVisible(false);
			return;
		}
		scaleLabel.setVisible(true);
		
		scaleSpinner = new JSpinner(model);
		scalePanel.add(scaleSpinner, BorderLayout.EAST);
	

		scaleSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				controller.actionScaleChanged();
			}
		});
		
		
		scaleSpinner.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				settingsChanged(SettingType.SCALE);
			}
		});
	}
	
	public void settingsChanged(SettingType type)
	{
				
		switch (type) {
			
			case DATA:
				parent.setTabTitle(this, controller.data.name());
				break;
				
			case VIEW:
				viewsUpdated();
				savetext.setVisible(controller.viewMode.canWriteData());
				save.setVisible(true);
				break;
	
			case SUBVIEW:
				setScaleSpinner();
				
			default:
				break;
		}
		
		setZoom(zoom);
		repaint();
	}
	
	
	private void viewsUpdated()
	{
		List<MapView> views = controller.data.datasource().getViews();
		
		viewSelector.removeAllItems();
		if (views.size() == 0) {
			viewSelector.setVisible(false);
			zoomslider.setVisible(false);
			graphics.setBackground(this.getBackground());
		} else if (views.size() == 1) {
			viewSelector.setVisible(false);
			zoomslider.setVisible(true);
			graphics.setBackground(Color.white);
			setSubViewUI();
		} else {
		
			viewSelector.setVisible(true);
			zoomslider.setVisible(true);
			graphics.setBackground(Color.white);
			setSubViewUI();
			for (MapView view : controller.data.datasource().getViews())
			{
				viewSelector.addItem(view);
			}
		}
		
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
					
					dr.dataHeight = controller.data.height(); //map.height;
					dr.dataWidth = controller.data.width(); //map.width;
					dr.uninterpolatedHeight = controller.data.height(); //map.height;
					dr.uninterpolatedWidth = controller.data.width(); //map.width;
				} catch (NullPointerException e) {
					TaskDialogs.showException(e);
					e.printStackTrace();
				}
			}
			
			@Override
			protected void drawGraphics(Surface backend, boolean vector) {
				
				if (controller.data == null) return;
				if (controller.data.width() == 0 || controller.data.height() == 0) return;
				if (controller.viewMode instanceof DummyView) return;
				
				
				float maxValue = (float)getMaxScaleValue();
				if (maxValue == -1) maxValue = controller.viewMode.getMaximumIntensity(controller.subView);		
				dr.maxYIntensity = (float) maxValue;
				
				drawing.setPainters(controller.viewMode.getPainters(controller.subView, maxValue));
				drawing.setAxisPainters(controller.viewMode.getAxisPainters(controller.subView, maxValue));
				
				dr.drawToVectorSurface = vector;
				setDR();

				
				drawing.needsMapRepaint();
				drawing.setDrawingRequest(dr);
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
	
}


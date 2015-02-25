package skew.ui;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.OverlayLayout;
import javax.swing.SpinnerModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.ext.swing.GridBagConstants;

import scidraw.drawing.DrawingRequest;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.map.MapDrawing;
import scidraw.swing.GraphicsPanel;
import scitypes.Coord;
import skew.Version;
import skew.core.datasource.DataSource;
import skew.core.datasource.DataSourceSelection;
import skew.core.model.ISkewDataset;
import skew.core.viewer.ScrollableGraphicsPanel;
import skew.core.viewer.SettingType;
import skew.core.viewer.modes.subviews.MapSubView;
import skew.core.viewer.modes.views.DummyView;
import skew.core.viewer.modes.views.MapView;
import swidget.dialogues.AboutDialogue;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.DraggingScrollPaneListener;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.ZoomSlider;
import autodialog.model.Parameter;
import autodialog.view.AutoPanel;
import autodialog.view.layouts.FramesADLayout;

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
	
	CardLayout cards = new CardLayout();
	JPanel mainLayer = new JPanel();
	JPanel dialogLayer = new JPanel();
	
	
	boolean dummy= true;
	
	public GraphicsPanel graphics;
	
	
	public MapDrawing drawing;
	DrawingRequest dr;
	
	JSpinner scaleSpinner;
	JLabel scaleLabel;
	JPanel scalePanel;

	public JComboBox<MapView> viewSelector;
	JComboBox<MapSubView> subViewSelector;
	

	JScrollPane graphicsScroller;
	ZoomSlider zoomslider;
	float zoom = 1;
	
	JPanel sidebarParameterPanel;
	JPanel sidebarInfoPanel;
	JScrollPane sidebarScroller;
	
	JSplitPane mainPanel;
	
	public ToolbarImageButton savetextButton;
	private ToolbarImageButton savepictureButton;
			
	
	public SkewUI(SkewTabs parent)
	{
		this(parent, new SkewController(parent));
	}
	

	public SkewUI(SkewTabs parent, final SkewController controller) 
	{
		
		this.parent = parent;
		
		this.controller = controller;
		controller.setUI(this);
		

		setLayout(cards);
		add(dialogLayer, "dialog");
		add(mainLayer, "main");
		dialogLayer.setBackground( new Color(0, 0, 0, 128) );
		dialogLayer.setOpaque(true);
		dialogLayer.setLayout(new GridBagLayout());
		cards.show(this, "main");
		
		setDialog(null);
		
		//////////////////////////
		//Map Drawing
		//////////////////////////
		
		dr = new DrawingRequest();
		drawing = new MapDrawing();

		
		
		//////////////////////////
		//UI
		//////////////////////////
		
		mainLayer.setLayout(new BorderLayout());
		mainLayer.add(createToolbar(), BorderLayout.NORTH);
		
		
		createGraphicsPanel();
		graphics.setPreferredSize(new Dimension(1000, 473));
		
		graphicsScroller = new JScrollPane(graphics);
		graphicsScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		graphicsScroller.setBackground(Color.white);
		graphicsScroller.getViewport().setBackground(Color.white);
		new DraggingScrollPaneListener(graphicsScroller.getViewport(), graphics);
		
		
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new GridBagLayout());
		
		
		
		sidebarParameterPanel = new JPanel();
		sidebarParameterPanel.setLayout(new BoxLayout(sidebarParameterPanel, BoxLayout.Y_AXIS));
				
		sidebarInfoPanel = new JPanel();
		sidebarInfoPanel.setLayout(new BoxLayout(sidebarInfoPanel, BoxLayout.Y_AXIS));
		
				
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0;
		c.weightx = 1f;
		c.anchor = GridBagConstants.NORTH;
		c.fill = GridBagConstants.BOTH;
		c.gridx = 0;
		
		
		c.gridy = 0;
		sidebar.add(sidebarParameterPanel, c);
		
		c.gridy = 1;
		sidebar.add(sidebarInfoPanel, c);
		
		c.gridy = 2;
		c.weighty = 1f;
		sidebar.add(new JPanel(), c);
		
		
		sidebarScroller = new JScrollPane(sidebar);
		sidebarScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
		sidebarScroller.setPreferredSize(new Dimension(200, 1));
		
		mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, sidebarScroller, graphicsScroller);
		mainPanel.setResizeWeight(0f);
		mainPanel.setDividerSize(8);
		mainPanel.setOneTouchExpandable(true);
		
		mainLayer.add(mainPanel, BorderLayout.CENTER);
		

		
		
		JPanel statusbar = createBottomControls();
		mainLayer.add(statusbar, BorderLayout.SOUTH);
		
		availableViewsChanged();
		selectedViewChanged();
		
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
		
		
		
		mainLayer.addComponentListener(new ComponentListener() {
			
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
		zoomslider.addListener(() -> setZoom(zoomslider.getValue() / 100f));
				
		scalePanel = createScaleControl();
		statusbar.add(scalePanel, BorderLayout.WEST);
		setSubViewUI();
		
		return statusbar;
	}

	
	private void setZoom(float newzoom)
	{
		zoom = newzoom;
		Rectangle r = graphicsScroller.getVisibleRect();
		if (graphics == null) return;
		
		Dimension panesize = graphicsScroller.getSize();
		
		Dimension currentSize = graphics.getSize();
		graphics.setSize(panesize);
		graphics.setPreferredSize(new Dimension((int)(graphics.getUsedWidth(zoom)), (int)(graphics.getUsedHeight(zoom))));
		graphics.setSize(currentSize);
		
		graphicsScroller.scrollRectToVisible(r);
		
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
		
		open.addActionListener((e) -> {
				
			//create new UI
			SkewUI ui = new SkewUI(parent);
			ui.dummy = false;
			parent.addTab(ui);
			
			
			List<String> files = SkewController.selectFiles(parent, controller.data.path(), ui);
			InputSelection selection = openFiles(files, parent);

			if (selection != null && selection.datasource != null) {

				parent.setTabTitle(ui, "Loading...");
				SkewController.loadFiles(parent, selection.files, selection.datasource, ui);
				parent.setActiveTab(ui);

				//remove this one, if a placeholder
				if (dummy) parent.tabs.closeTab(SkewUI.this);
			} else {
				parent.closeTab(ui);
			}
			
		});

		ToolbarImageButton tab = new ToolbarImageButton(StockIcon.WINDOW_TAB_NEW, "New Tab");
		
		tab.addActionListener((e) -> parent.newTab());
		
		savepictureButton = new ToolbarImageButton(StockIcon.DEVICE_CAMERA, "Save Picture");
		savepictureButton.addActionListener((e) -> controller.actionSavePicture());
		savepictureButton.setVisible(false);
		
		
		viewSelector = new JComboBox<MapView>(new Vector<MapView>(controller.data.datasource().getViews()));
		viewSelector.addActionListener((e) -> controller.actionViewChange((MapView)viewSelector.getSelectedItem()));
		
		
		savetextButton = new ToolbarImageButton(StockIcon.DOCUMENT_EXPORT, "Export Map Data");
		savetextButton.addActionListener((a) -> controller.actionSaveText());
		savetextButton.setVisible(false);
		
		
		ToolbarImageButton about = new ToolbarImageButton(StockIcon.MISC_ABOUT, "About");
		about.addActionListener((e) ->
		{
			new AboutDialogue(
					parent,
					Version.name + " " + Version.short_version,
					Version.description,
					"www.sciencestudioproject.com",
					"Copyright (c) 2012-2014 by<br>The University of Western Ontario",
					IOOperations.readTextFromJar("/skew/licence.txt"),
					IOOperations.readTextFromJar("/skew/credits.txt"),
					"skew",
					"",
					Version.long_version,
					"",
					Version.date
				);
		});
		
		
		
		toolbar.add(open);
		//toolbar.add(tab);
		toolbar.add(savepictureButton);
		
		
		toolbar.add(Box.createHorizontalGlue());

		toolbar.add(viewSelector);	
		toolbar.add(savetextButton);
		
		toolbar.add(Box.createHorizontalGlue());
		
		toolbar.add(about);
		
		return toolbar;
		
	}
	
	public static InputSelection openFiles(List<String> files, SkewTabs window) {
		
		//filter for just the working data sources
		List<DataSource> acceptingFormats = SkewController.getViableDataSources(files);
		
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
			
			subViewSelector.addActionListener((e) -> {
				controller.actionSubviewChange((MapSubView) subViewSelector.getSelectedItem());
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
		scaleSpinner.addChangeListener((e) -> controller.actionScaleChanged());
		
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
				runtimeParametersChanged();
				availableViewsChanged();
				break;
				
			case VIEW:
				selectedViewChanged();
				savetextButton.setVisible(true);
				savepictureButton.setVisible(true);
				break;
	
			case SUBVIEW:
				setScaleSpinner();
				
			default:
				break;
		}
		
		setZoom(zoom);
		mainLayer.repaint();
	}
	
	
	public void setDialog(JPanel dialog) {
		dialogLayer.removeAll();
		
		if (dialog != null) {
			cards.show(this, "dialog");
			
			JPanel containerPanel = new JPanel();
			containerPanel.setBorder(
					new CompoundBorder(
							new LineBorder(Color.BLACK),
							new LineBorder(Color.WHITE)
						)
					);
			containerPanel.add(dialog);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.CENTER;
			dialogLayer.add(containerPanel, c);
			dialogLayer.setVisible(true);
			
		} else {
			dialogLayer.setVisible(false);			
			cards.show(this, "main");
		}
	}
	
	
	private void availableViewsChanged()
	{
		List<MapView> views = controller.data.datasource().getViews();
		viewSelector.removeAllItems();
		
		for (MapView view : views)
		{
			viewSelector.addItem(view);
		}
		
		
		graphics.setBackground(Color.white);
		zoomslider.setVisible(true);
		if (views.size() == 0) {
			viewSelector.setVisible(false);
			zoomslider.setVisible(false);
			graphics.setBackground(this.mainLayer.getBackground());
		} else if (views.size() == 1) {
			viewSelector.setVisible(false);
		} else {
			viewSelector.setVisible(true);
		}


		
	}
	
	private void runtimeParametersChanged()
	{
		sidebarParameterPanel.removeAll();
		
		List<Parameter<?>> params = controller.data.datasource().getRuntimeParameters();
		if (params == null) params = new ArrayList<>();
		
		AutoPanel panel = new AutoPanel(params, new FramesADLayout(), 0);
		sidebarParameterPanel.add(panel);
		sidebarParameterPanel.setVisible(params.size() > 0);
		
		sidebarScroller.setPreferredSize(new Dimension(
				Math.max(200, sidebarParameterPanel.getPreferredSize().width + 30), 
				0
			));
		mainPanel.resetToPreferredSizes();
		
				
	}
	
	private void selectedViewChanged()
	{
		setSubViewUI();
		sidebarInfoPanel.removeAll();
	}
	
	
	private GraphicsPanel createGraphicsPanel()
	{
		graphics = new ScrollableGraphicsPanel() {
			
			
			{
				drawing.setDrawingRequest(dr);
			}
			
			
			@Override
			public float getUsedWidth() {
				return getUsedWidth(1);				
			}
			
			@Override
			public float getUsedHeight() {
				return getUsedHeight(1);				
			}
			
			@Override
			public float getUsedHeight(float zoom) {
				setDR();
				Coord<Float> borders = drawing.calcBorderSize();
				Coord<Float> map = drawing.calcMapSize();
				return map.y * zoom + borders.y;
			}

			@Override
			public float getUsedWidth(float zoom) {
				setDR();
				Coord<Float> borders = drawing.calcBorderSize();
				Coord<Float> map = drawing.calcMapSize();
				return map.x * zoom + borders.x;
			}
			
			private void setDR()
			{
				
				try {
					//set the drawing requests dimensions for the data and the screen
					dr.imageWidth = getWidth();
					dr.imageHeight = getHeight();
					
					dr.dataHeight = controller.data.getHeight(); //map.height;
					dr.dataWidth = controller.data.getWidth(); //map.width;
					dr.uninterpolatedHeight = controller.data.getHeight(); //map.height;
					dr.uninterpolatedWidth = controller.data.getWidth(); //map.width;
				} catch (NullPointerException e) {
					TaskDialogs.showException(e);
					e.printStackTrace();
				}
			}
			
			@Override
			protected void drawGraphics(Surface backend, boolean vector) {
				
				if (controller.data == null) return;
				if (controller.data.getWidth() == 0 || controller.data.getHeight() == 0) return;
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


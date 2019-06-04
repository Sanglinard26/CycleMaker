/*
 * Creation : 16 août 2018
 */
package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import form.Cycle;
import form.Element;
import observer.Observateur;
import utils.Utilitaire;

public final class Ihm extends JFrame implements Observateur {

    private static final long serialVersionUID = 1L;

    private final JList<Cycle> listCycle;
    private final DefaultListModel<Cycle> dataModel;
    private final ChartPanel chartPanel;
    private final PanelCreation panelCreation;
    private final ButtonGroup group = new ButtonGroup();

    private boolean designMode = false;
    
    private PropertyChangeSupport pcsBtDesign;

    public Ihm() {
        super("Cycle Maker");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setJMenuBar(createMenu());

        final Container content = getContentPane();

        content.add(createToolBar(), BorderLayout.NORTH);

        dataModel = new DefaultListModel<>();
        listCycle = new JList<Cycle>(dataModel);
        listCycle.setFixedCellWidth(200);
        listCycle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCycle.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !listCycle.isSelectionEmpty()) {
                    panelCreation.setCycle(listCycle.getSelectedValue());
                    panelCreation.fillDataset();
                    createCombinedChart();
                }
            }
        });

        listCycle.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127 && listCycle.getSelectedIndex() > -1) // touche suppr
                {
                    for (int idx : listCycle.getSelectedIndices()) {
                        dataModel.get(idx).delObservateur();
                        dataModel.remove(idx);
                    }

                    listCycle.clearSelection();

                    chartPanel.setChart(null);
                    panelCreation.setCycle(null);
                    
                    
                    
                    pcsBtDesign.firePropertyChange(JToggleButton.MODEL_CHANGED_PROPERTY, designMode, false);
                    
                    designMode = false;
                    
                    Enumeration<AbstractButton> enumBt = group.getElements();
                    while (enumBt.hasMoreElements()) {
						AbstractButton ab = enumBt.nextElement();
						ab.setSelected(designMode);
						ab.setEnabled(designMode);
					}
                    
                    panelCreation.setVisible(designMode);
                }
            }
        });

        content.add(new JScrollPane(listCycle, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.WEST);

        chartPanel = new ChartPanel(null);
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        content.add(chartPanel, BorderLayout.CENTER);

        panelCreation = new PanelCreation();
        panelCreation.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selectedElement")) {

                    int idxDataset = panelCreation.getIndexDataset();

                    Element element = (Element) evt.getNewValue();

                    XYSeries series = new XYSeries("Element");
                    for (int i = element.getFirstIndex(); i <= element.getLastIndex(); i++) {
                        Double val = listCycle.getSelectedValue().getDatasets().get(idxDataset).getDatas().get(i);
                        series.add(listCycle.getSelectedValue().getTime().get(i), val);
                    }
                    XYPlot plot = ((XYPlot) ((CombinedDomainXYPlot) chartPanel.getChart().getPlot()).getSubplots().get(idxDataset));
                    XYSeriesCollection collection = ((XYSeriesCollection) plot.getDataset());
                    int idx = collection.getSeriesIndex("Element");
                    if (idx > -1) {
                        collection.removeSeries(idx);
                    }
                    collection.addSeries(series);

                    XYLineAndShapeRenderer renderer = ((XYLineAndShapeRenderer) plot.getRenderer(0));
                    Paint serieColor = renderer.getSeriesPaint(0);

                    renderer.setSeriesStroke(1, new BasicStroke(4));
                    renderer.setSeriesPaint(1, serieColor);
                    renderer.setSeriesShapesVisible(1, false);

                }

            }
        });
        content.add(panelCreation, BorderLayout.EAST);

        pack();

        panelCreation.setVisible(false);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private final JToolBar createToolBar() {

        final String ICON_NEW = "/icon_newCycle_24.png";
        final String ICON_OPEN = "/icon_openCycle_24.png";
        final String ICON_SAVE = "/icon_saveCycle_24.png";
        final String ICON_DESIGN = "/icon_design_24.png";

        final String ICON_POINT = "/icon_point_24.png";
        final String ICON_CRENEAU = "/icon_creneau_24.png";
        final String ICON_SINUS = "/icon_sinus_24.png";
        final String ICON_RAMPE = "/icon_rampe_24.png";
        final String ICON_STATIONNAIRE = "/icon_stationnaire_24.png";
        final String ICON_TRAPEZE = "/icon_trapeze_24.png";

        final JToolBar toolBar = new JToolBar("Barre d'outils");
        toolBar.setFloatable(false);

        final JButton btNew = new JButton(new ImageIcon(getClass().getResource(ICON_NEW)));
        btNew.setToolTipText("Create a new cycle");
        btNew.addActionListener(new NewCycle());
        toolBar.add(btNew);

        final JButton btOpen = new JButton(new ImageIcon(getClass().getResource(ICON_OPEN)));
        btOpen.setToolTipText("Open a existing cycle");
        btOpen.addActionListener(new OpenCycle());
        toolBar.add(btOpen);

        final JButton btSave = new JButton(new ImageIcon(getClass().getResource(ICON_SAVE)));
        btSave.setToolTipText("Save the current cycle");
        btSave.addActionListener(new SaveCycle());
        toolBar.add(btSave);

        toolBar.addSeparator(new Dimension(15, 0));

        final JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setMaximumSize(new Dimension(15, 30));
        toolBar.add(separator);

        final JToggleButton btPoint = new JToggleButton(new ImageIcon(getClass().getResource(ICON_POINT)));
        btPoint.setBorder(new LineBorder(Color.BLACK, 1));
        btPoint.setActionCommand(Element.POINT);
        btPoint.setToolTipText(Element.POINT);
        btPoint.setEnabled(designMode);
        btPoint.addActionListener(new SelectionElement());

        final JToggleButton btCreneau = new JToggleButton(new ImageIcon(getClass().getResource(ICON_CRENEAU)));
        btCreneau.setBorder(new LineBorder(Color.BLACK, 1));
        btCreneau.setActionCommand(Element.CRENEAU);
        btCreneau.setToolTipText(Element.CRENEAU);
        btCreneau.setEnabled(designMode);
        btCreneau.addActionListener(new SelectionElement());

        final JToggleButton btStationnaire = new JToggleButton(new ImageIcon(getClass().getResource(ICON_STATIONNAIRE)));
        btStationnaire.setBorder(new LineBorder(Color.BLACK, 1));
        btStationnaire.setActionCommand(Element.STATIONNAIRE);
        btStationnaire.setToolTipText(Element.STATIONNAIRE);
        btStationnaire.setEnabled(designMode);
        btStationnaire.addActionListener(new SelectionElement());

        final JToggleButton btRampe = new JToggleButton(new ImageIcon(getClass().getResource(ICON_RAMPE)));
        btRampe.setBorder(new LineBorder(Color.BLACK, 1));
        btRampe.setActionCommand(Element.RAMPE);
        btRampe.setToolTipText(Element.RAMPE);
        btRampe.setEnabled(designMode);
        btRampe.addActionListener(new SelectionElement());

        final JToggleButton btSinus = new JToggleButton(new ImageIcon(getClass().getResource(ICON_SINUS)));
        btSinus.setBorder(new LineBorder(Color.BLACK, 1));
        btSinus.setActionCommand(Element.SINUS);
        btSinus.setToolTipText(Element.SINUS);
        btSinus.setEnabled(designMode);
        btSinus.addActionListener(new SelectionElement());

        final JToggleButton btTrapeze = new JToggleButton(new ImageIcon(getClass().getResource(ICON_TRAPEZE)));
        btTrapeze.setBorder(new LineBorder(Color.BLACK, 1));
        btTrapeze.setActionCommand(Element.TRAPEZE);
        btTrapeze.setToolTipText(Element.TRAPEZE);
        btTrapeze.setEnabled(designMode);
        btTrapeze.addActionListener(new SelectionElement());

        group.add(btPoint);
        group.add(btCreneau);
        group.add(btStationnaire);
        group.add(btRampe);
        group.add(btSinus);
        group.add(btTrapeze);

        final JToggleButton btDesign = new JToggleButton(new ImageIcon(getClass().getResource(ICON_DESIGN)));
        btDesign.setToolTipText("Design mode");
        btDesign.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (listCycle.getSelectedValue() != null) {
                    designMode = btDesign.isSelected();
                    panelCreation.setVisible(designMode);

                    btPoint.setEnabled(designMode);
                    btCreneau.setEnabled(designMode);
                    btStationnaire.setEnabled(designMode);
                    btRampe.setEnabled(designMode);
                    btSinus.setEnabled(designMode);
                    btTrapeze.setEnabled(designMode);
                } else {
                    btDesign.setSelected(false);
                }
            }
        });
        
        pcsBtDesign = new PropertyChangeSupport(btDesign);
        pcsBtDesign.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				btDesign.setSelected((boolean) event.getNewValue());
			}
		});
        
        
        toolBar.add(btDesign);
        toolBar.addSeparator();
        toolBar.add(btPoint);
        toolBar.addSeparator();
        toolBar.add(btCreneau);
        toolBar.addSeparator();
        toolBar.add(btStationnaire);
        toolBar.addSeparator();
        toolBar.add(btRampe);
        toolBar.addSeparator();
        toolBar.add(btSinus);
        toolBar.addSeparator();
        toolBar.add(btTrapeze);

        return toolBar;
    }

    private final JMenuBar createMenu() {

        final JMenuBar menuBar = new JMenuBar();

        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");

        menuItem = new JMenuItem("New");
        menuItem.addActionListener(new NewCycle());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem("Open");
        menuItem.addActionListener(new OpenCycle());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem("Save");
        menuItem.addActionListener(new SaveCycle());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menu.add(menuItem);

        menuBar.add(menu);

        return menuBar;
    }

    private final class NewCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            final JTextField txtCycleName = new JTextField("New_cycle", 30);
            final JTextField txtDatasets = new JTextField("LOOP40", 30);
            txtDatasets.setToolTipText("Enter dataset separate with comma (Ex : C_REG,LOOP40)");
            final JComponent[] inputs = new JComponent[] { new JLabel("Name of cycle"), txtCycleName, new JLabel("Dataset(s)"), txtDatasets };
            int result = JOptionPane.showConfirmDialog(null, inputs, "Cycle properties", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                if (txtDatasets.getText().trim().length() > 0) {
                    final Cycle cycle = new Cycle(txtCycleName.getText(), formatDataset(txtDatasets.getText()));
                    cycle.addObservateur(Ihm.this);
                    dataModel.addElement(cycle);
                } else {
                    JOptionPane.showMessageDialog(Ihm.this, "You must enter at least one dataset", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    private final List<String> formatDataset(String datasets) {
        boolean duplicateDataset = false;

        List<String> setDataset = new ArrayList<String>();

        for (String dataset : datasets.split(",")) {
            if (!setDataset.contains(dataset) && dataset.length() > 0) {
                setDataset.add(dataset);
            } else {
                duplicateDataset = true;
            }
        }

        Collections.sort(setDataset);

        if (duplicateDataset) {
            JOptionPane.showMessageDialog(this, "Duplicate dataset are present", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        return setDataset;
    }

    private final class OpenCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            final JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setMultiSelectionEnabled(true);
            jFileChooser.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return "Cycle file (*.txt, *.cycle)";
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    final String extension = Utilitaire.getExtension(f);
                    if (extension.equals(Utilitaire.TXT) || extension.equals(Utilitaire.CYCLE)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(Ihm.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {

                Cycle cycle;

                for (File file : jFileChooser.getSelectedFiles()) {

                    cycle = new Cycle(file);
                    cycle.addObservateur(Ihm.this);
                    dataModel.addElement(cycle);
                }
            }

        }
    }

    private final void createCombinedChart() {

        final Cycle selectedCycle = listCycle.getSelectedValue();

        if (selectedCycle != null) {
            final int nbPlot = selectedCycle.getDatasets().size();

            final XYSeries[] series = new XYSeries[nbPlot];
            final XYSeriesCollection[] collections = new XYSeriesCollection[nbPlot];
            final XYItemRenderer[] renderers = new XYLineAndShapeRenderer[nbPlot];
            Shape diamondShape = ShapeUtilities.createDiamond(1);
            final NumberAxis[] rangeAxiss = new NumberAxis[nbPlot];
            final XYPlot[] subPlots = new XYPlot[nbPlot];

            final CombinedDomainXYPlot plot = new CombinedDomainXYPlot();

            final List<Double> temps = selectedCycle.getTime();
            final int nbPoint = temps.size();

            for (int nPlot = 0; nPlot < nbPlot; nPlot++) {

                series[nPlot] = new XYSeries(selectedCycle.getDatasets().get(nPlot).getName());
                for (int n = 0; n < nbPoint; n++) {

                    int sizeData = listCycle.getSelectedValue().getDatasets().get(nPlot).getDatas().size();

                    if (n < sizeData) {
                        series[nPlot].add(temps.get(n), selectedCycle.getDatasets().get(nPlot).getDatas().get(n));
                    }
                }

                collections[nPlot] = new XYSeriesCollection(series[nPlot]);
                rangeAxiss[nPlot] = new NumberAxis(selectedCycle.getDatasets().get(nPlot).getName());
                renderers[nPlot] = new XYLineAndShapeRenderer(true, true);
                renderers[nPlot].setSeriesShape(0, diamondShape);
                subPlots[nPlot] = new XYPlot(collections[nPlot], null, rangeAxiss[nPlot], renderers[nPlot]);

                plot.add(subPlots[nPlot], 1);
            }

            plot.setDomainPannable(true);
            plot.setOrientation(PlotOrientation.VERTICAL);
            plot.setGap(20);
            plot.setDomainCrosshairVisible(true);

            JFreeChart chart = new JFreeChart(plot);

            chartPanel.setChart(chart);
            chartPanel.setRangeZoomable(false);
        }
    }

    private final class SaveCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            final Cycle selectedCycle = listCycle.getSelectedValue();

            if (selectedCycle != null) {
                final JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Save file");
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Text file (*.txt)", "txt"));
                jFileChooser.setSelectedFile(new File(selectedCycle.getName() + ".txt"));
                final int rep = jFileChooser.showSaveDialog(Ihm.this);

                if (rep == JFileChooser.APPROVE_OPTION) {

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if (selectedCycle.save(jFileChooser.getSelectedFile())) {
                                JOptionPane.showMessageDialog(Ihm.this, "Saving done !", "Message", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });

                    thread.start();

                }
            }
        }
    }

    private final class SelectionElement implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            final Enumeration<AbstractButton> itBt = group.getElements();

            while (itBt.hasMoreElements()) {
                AbstractButton bt = itBt.nextElement();
                if (bt.getActionCommand().equals(e.getActionCommand())) {
                    bt.setBorder(BorderFactory.createLoweredBevelBorder());
                } else {
                    bt.setBorder(new LineBorder(Color.BLACK, 1));
                }
            }

            panelCreation.configure(listCycle.getSelectedValue(), e.getActionCommand());
        }

    }

    @Override
    public void update(String property) {
        createCombinedChart();

    }

}

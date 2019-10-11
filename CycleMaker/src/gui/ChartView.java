package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

import form.Cycle;
import observer.Observateur;

public final class ChartView extends JPanel implements ChartMouseListener, MouseMotionListener, MouseListener, Observateur {

    private static final long serialVersionUID = 1L;

    private final ChartPanel chartPanel;
    private JFreeChart chart = null;
    private XYItemEntity xyItemEntity = null;
    private float initialMovePointY = Float.NaN;
    private float finalMovePointY = Float.NaN;

    private final BoundedRangeModel boundedRangeModel;
    private final JSlider sliderTime;
    private final JLabel cycleName;

    private Cycle selectedCycle;

    public ChartView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        cycleName = new JLabel();
        cycleName.setHorizontalAlignment(SwingConstants.CENTER);
        cycleName.setFont(new Font(null, Font.BOLD, 14));
        cycleName.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(cycleName, BorderLayout.NORTH);

        chartPanel = new ChartPanel(null, 680, 420, 300, 200, 1920, 1080, true, true, false, false, true, false);
        chartPanel.setPopupMenu(null);
        chartPanel.addChartMouseListener(this);
        chartPanel.addMouseMotionListener(this);
        chartPanel.addMouseListener(this);
        add(chartPanel, BorderLayout.CENTER);

        boundedRangeModel = new DefaultBoundedRangeModel(0, 0, 0, 1);
        boundedRangeModel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                // updateCursor(e);
            }
        });

        sliderTime = new JSlider(boundedRangeModel);
        sliderTime.setVisible(false);
        add(sliderTime, BorderLayout.SOUTH);
    }

    public final ChartPanel getChartPanel() {
        return chartPanel;
    }

    private final void updateCursor(EventObject event) {
        CombinedDomainXYPlot plot = (CombinedDomainXYPlot) chartPanel.getChart().getPlot();

        @SuppressWarnings("unchecked")
        List<XYPlot> subPlots = plot.getSubplots();

        double xVal = Double.NaN;
        double yVal = Double.NaN;

        for (XYPlot subplot : subPlots) {

            if (event.getSource() instanceof JFreeChart) {
                xVal = subplot.getDomainCrosshairValue();
                yVal = DatasetUtilities.findYValue(subplot.getDataset(), 0, xVal);
            } else {
                int xIndex = boundedRangeModel.getValue();
                if (selectedCycle.getTime().isEmpty()) {
                    return;
                }
                xVal = selectedCycle.getTime().get(xIndex);
                subplot.setDomainCrosshairValue(xVal);
                if (xIndex < subplot.getDataset().getItemCount(0)) {
                    yVal = subplot.getDataset().getYValue(0, xIndex);
                }
            }

            XYTextAnnotation txtAnnot = (XYTextAnnotation) subplot.getAnnotations().get(0);
            txtAnnot.setX(xVal);

            String txtVal = String.format("%.2f", yVal);
            txtAnnot.setText(txtVal);

            txtAnnot.setY(yVal);
            txtAnnot.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
            txtAnnot.setOutlineVisible(true);
            txtAnnot.setBackgroundPaint(Color.WHITE);
        }
    }

    public final void createCombinedChart(Cycle cycle) {

        if (cycle != null) {
            this.selectedCycle = cycle;

            this.cycleName.setText(this.selectedCycle.getName());

            final int nbPlot = selectedCycle.getDatasets().size();

            final XYSeries[] series = new XYSeries[nbPlot];
            final XYSeriesCollection[] collections = new XYSeriesCollection[nbPlot];
            final XYItemRenderer[] renderers = new XYLineAndShapeRenderer[nbPlot];
            Shape diamondShape = ShapeUtilities.createDiamond(2);
            final NumberAxis[] rangeAxiss = new NumberAxis[nbPlot];
            final XYPlot[] subPlots = new XYPlot[nbPlot];

            final CombinedDomainXYPlot plot = new CombinedDomainXYPlot();

            final List<Float> temps = selectedCycle.getTime();
            final int nbPoint = temps.size();

            for (int nPlot = 0; nPlot < nbPlot; nPlot++) {

                series[nPlot] = new XYSeries(selectedCycle.getDatasets().get(nPlot).getName());
                for (int n = 0; n < nbPoint; n++) {

                    int sizeData = selectedCycle.getDatasets().get(nPlot).getDatas().size();

                    if (n < sizeData) {
                        series[nPlot].add(temps.get(n), selectedCycle.getDatasets().get(nPlot).getDatas().get(n));
                    }
                }

                collections[nPlot] = new XYSeriesCollection(series[nPlot]);
                rangeAxiss[nPlot] = new NumberAxis(selectedCycle.getDatasets().get(nPlot).getName());
                renderers[nPlot] = new XYLineAndShapeRenderer(true, true);
                renderers[nPlot].setSeriesShape(0, diamondShape);
                subPlots[nPlot] = new XYPlot(collections[nPlot], null, rangeAxiss[nPlot], renderers[nPlot]);
                subPlots[nPlot].setDomainCrosshairVisible(true);
                subPlots[nPlot].setDomainCrosshairStroke(
                        new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f));
                subPlots[nPlot].setDomainCrosshairPaint(Color.BLACK);
                subPlots[nPlot].addAnnotation(new XYTextAnnotation("", Double.NaN, Double.NaN));
                plot.add(subPlots[nPlot], 1);
            }

            plot.setDomainPannable(true);
            plot.setOrientation(PlotOrientation.VERTICAL);
            plot.setGap(20);

            chart = new JFreeChart(plot);

            chartPanel.setChart(chart);
            chartPanel.setRangeZoomable(false);

            updateSlider();
            sliderTime.setVisible(true);
        } else {
            chartPanel.setChart(null);
            this.cycleName.setText(null);
        }
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent paramChartMouseEvent) {
        ChartEntity entity = paramChartMouseEvent.getEntity();

        if (entity instanceof PlotEntity) {
            PlotEntity plotEntity = (PlotEntity) entity;
            XYPlot xyPlot = (XYPlot) plotEntity.getPlot();
            xyPlot.handleClick(paramChartMouseEvent.getTrigger().getX(), paramChartMouseEvent.getTrigger().getY(),
                    chartPanel.getChartRenderingInfo().getPlotInfo());

            double crossHairValue = xyPlot.getDomainCrosshairValue();
            int[] xIndex = DatasetUtilities.findItemIndicesForX(xyPlot.getDataset(), 0, crossHairValue);
            if (xIndex[0] > -1 && xIndex[1] > -1) {
                double xValue1 = xyPlot.getDataset().getXValue(0, xIndex[0]);
                double xValue2 = xyPlot.getDataset().getXValue(0, xIndex[1]);
                double diffXvalue1 = Math.abs(xValue1 - crossHairValue);
                double diffXvalue2 = Math.abs(xValue2 - crossHairValue);
                if (diffXvalue1 < diffXvalue2) {
                    boundedRangeModel.setValue(xIndex[0]);
                } else {
                    boundedRangeModel.setValue(xIndex[1]);
                }
            }
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent paramChartMouseEvent) {

        Point pt = paramChartMouseEvent.getTrigger().getPoint();
        XYPlot plot = ((CombinedDomainXYPlot) chart.getPlot()).findSubplot(chartPanel.getChartRenderingInfo().getPlotInfo(), pt);

        if (plot != null && plot.getDatasetCount() > 0) {

            Point2D p2d = chartPanel.translateScreenToJava2D(pt);

            EntityCollection entities = chartPanel.getChartRenderingInfo().getEntityCollection();

            ChartEntity entity = entities.getEntity(p2d.getX(), p2d.getY());

            if ((entity != null) && (entity instanceof XYItemEntity)) {
                xyItemEntity = (XYItemEntity) entity;
            } else if (!(entity instanceof XYItemEntity)) {
                xyItemEntity = null;
                chartPanel.setDomainZoomable(true);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            if (xyItemEntity == null) {
                chartPanel.setDomainZoomable(true);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return; // return if not pressed on any series point
            }

            initialMovePointY = xyItemEntity.getDataset().getY(0, xyItemEntity.getItem()).floatValue();

            this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (this.getCursor().getType() != Cursor.DEFAULT_CURSOR) {

            this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));

            chartPanel.setDomainZoomable(false);

            int itemIndex = xyItemEntity.getItem();

            Point pt = e.getPoint();
            XYPlot xy = ((CombinedDomainXYPlot) chart.getPlot()).findSubplot(chartPanel.getChartRenderingInfo().getPlotInfo(), pt);
            if (xy == null) {
                return;
            }
            XYSeries series = ((XYSeriesCollection) xyItemEntity.getDataset()).getSeries(0);
            Point2D p = chartPanel.translateScreenToJava2D(pt);
            int subPlotIndex = chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotIndex(p);
            if (subPlotIndex < 0) {
                return;
            }
            Rectangle2D dataArea = chartPanel.getChartRenderingInfo().getPlotInfo().getSubplotInfo(subPlotIndex).getDataArea();

            // Test
            ValueAxis ax = xy.getRangeAxis();
            // ax.setAutoRange(true);

            finalMovePointY = (float) xy.getRangeAxis().java2DToValue(p.getY(), dataArea, xy.getRangeAxisEdge());

            float difference = finalMovePointY - initialMovePointY;

            float targetPoint = series.getY(itemIndex).floatValue() + difference;

            series.updateByIndex(itemIndex, Float.valueOf(targetPoint));

            initialMovePointY = finalMovePointY;

            selectedCycle.getDataset(series.getKey().toString()).getDatas().set(itemIndex, targetPoint);

            boundedRangeModel.setValue(itemIndex);
            // updateCursor(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub

    }

    public final void updateSlider() {
        int nbPoint = selectedCycle.getNbPoint() - 1;
        int sliderValue = Math.min(nbPoint, boundedRangeModel.getValue());
        boundedRangeModel.setRangeProperties(sliderValue, 0, 0, nbPoint, true);
    }

    @Override
    public void update(String property) {
        this.createCombinedChart(selectedCycle);
        updateSlider();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.getCursor().getType() == 12 && e.getClickCount() > 1) {
            double currentYval = xyItemEntity.getDataset().getY(0, xyItemEntity.getItem()).doubleValue();

            final JTextField txtYval = new JTextField("", 30);
            final JComponent[] inputs = new JComponent[] { new JLabel("New Yvalue (current = " + currentYval + ")"), txtYval };
            int result = JOptionPane.showConfirmDialog(null, inputs, "Dataset modification", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    float newYval = Float.parseFloat(txtYval.getText());
                    XYSeries series = ((XYSeriesCollection) xyItemEntity.getDataset()).getSeries(0);
                    series.updateByIndex(xyItemEntity.getItem(), newYval);
                    selectedCycle.getDataset(series.getKey().toString()).getDatas().set(xyItemEntity.getItem(), newYval);
                    // updateCursor(e);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(ChartView.this.getParent(), "You must enter a number!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}

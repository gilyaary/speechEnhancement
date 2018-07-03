package com.lg.audio.base;


import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineViewer extends ApplicationFrame {
    
    double ad[] = { 4,8,4,1,4,1,4,1,4,1,4,1}; // y axis
    double ad1[] = {2,4,2,4,2,4,2,4,2,4,2,4}; //x axis
    double ad2[] = {.1,.1,.1,.1,.1,.1,.1,.1,.1,.1,.1,.1}; //sizes
    DefaultXYDataset dataset = new DefaultXYDataset();
    PlotOrientation orientation = PlotOrientation.HORIZONTAL;
    private XYSplineRenderer lineRenderer = new XYSplineRenderer();
    CandlestickRenderer candlesRenderer = new CandlestickRenderer();
    private JFreeChart jfreechart;
    
    public LineViewer(String chartTitle) {
        super(chartTitle);
        this.jfreechart = ChartFactory.createXYLineChart(chartTitle, "X", "Y", dataset, orientation , true, true, false);
        
        XYPlot xyplot = jfreechart.getXYPlot();
        Plot plot = jfreechart.getPlot();
        xyplot.setRenderer(jfreechart.getXYPlot().getRendererCount(), lineRenderer);
        xyplot.setRenderer(jfreechart.getXYPlot().getRendererCount(), candlesRenderer);
        
        
        
        xyplot.setForegroundAlpha(0.65F);
        //NumberAxis numberaxis = (NumberAxis) xyplot.getDomainAxis();
        //numberaxis.setLowerMargin(0.14999999999999999D);
        //numberaxis.setUpperMargin(0.14999999999999999D);
        //NumberAxis numberaxis1 = (NumberAxis) xyplot.getRangeAxis();
        //numberaxis1.setLowerMargin(0.14999999999999999D);
        //numberaxis1.setUpperMargin(0.14999999999999999D);
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setDomainZoomable(true);
        chartpanel.setRangeZoomable(true);
        chartpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartpanel);
    }
    
    public void addPoints(List <TwoDimensionalPoint> points, Color color, String title, double pointSize){
        int size = points.size();
        ad = new double [size];
        ad1 = new double [size];
        for(int i=0; i<size; i++){
            TwoDimensionalPoint p = points.get(i);
            ad1[i] = p.getX();
            ad[i] = p.getY();            
        }
        double[][]data = {ad, ad1};
        dataset.addSeries(title, data);
        lineRenderer.setSeriesPaint(dataset.getSeriesCount()-1, color);        
    }
    
    public static void main(String args[]) {
        List <TwoDimensionalPoint> points = new ArrayList();
        TwoDimensionalPoint p = new TwoDimensionalPoint(2, 3); 
        points.add(p);
        p = new TwoDimensionalPoint(3, 5); 
        points.add(p);
        p = new TwoDimensionalPoint(4, 6);
        points.add(p);
        LineViewer bv = new LineViewer("");
        bv.addPoints(points, Color.blue, "Test Point Series 1",0.01);
        bv.pack();
        RefineryUtilities.centerFrameOnScreen(bv);
        bv.setVisible(true);
    }
    
/*
// create subplot 1...
IntervalXYDataset data1 = this.createDataset1();
XYItemRenderer renderer1 = new VerticalXYBarRenderer(0.20);
renderer1.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
XYPlot subplot1 = new XYPlot(data1, null, null, renderer1);

// create subplot 2...
XYDataset data2 = this.createDataset2();
XYItemRenderer renderer2 = new StandardXYItemRenderer();
renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
XYPlot subplot2 = new XYPlot(data2, null, null, renderer2);

//Next, create a new OverlaidXYPlot and add the subplots:
// make an overlaid plot and add the subplots...
ValueAxis domainAxis = new HorizontalDateAxis("Date");
ValueAxis rangeAxis = new VerticalNumberAxis("Value");
OverlaidXYPlot plot = new OverlaidXYPlot(domainAxis, rangeAxis);
plot.add(subplot1);
plot.add(subplot2);

// return a new chart containing the overlaid plot...
return new JFreeChart("Overlaid Plot Example", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
*/
}
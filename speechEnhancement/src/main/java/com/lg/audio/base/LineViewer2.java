package com.lg.audio.base;


import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineViewer2 extends ApplicationFrame {
    
    double ad[] = { 4,8,4,1,4,1,4,1,4,1,4,1}; // y axis
    double ad1[] = {2,4,2,4,2,4,2,4,2,4,2,4}; //x axis
    double ad2[] = {.1,.1,.1,.1,.1,.1,.1,.1,.1,.1,.1,.1}; //sizes
    
    PlotOrientation orientation = PlotOrientation.HORIZONTAL;
        
    private JFreeChart jfreechart;
    private XYPlot plot = new XYPlot();
    
    public LineViewer2(String chartTitle) {
        super(chartTitle);
        
        //final DateAxis domainAxis = new DateAxis("Date");
        //domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        
        final DateAxis  domainAxis = new DateAxis ("Dates");
        domainAxis.setAutoRange(true);
        final ValueAxis rangeAxis = new NumberAxis("Value");
        rangeAxis.setAutoRange(true);
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        
        this.jfreechart = new JFreeChart("Overlaid Plot Example", JFreeChart.DEFAULT_TITLE_FONT, plot , true);
        
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setDomainZoomable(true);
        chartpanel.setRangeZoomable(true);
        chartpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartpanel);
    }
    
    public void addCandles(OHLCDataItem[] candles){
        OHLCDataset ohlcDataset = new DefaultOHLCDataset("", candles );
        CandlestickRenderer candlesRenderer = new CandlestickRenderer();
        plot.setDataset(plot.getRendererCount()+1, ohlcDataset);
        plot.setRenderer(plot.getRendererCount()+1, candlesRenderer);
    }
    
    public void addLines(IntervalXYDataset lines){
        IntervalXYDataset lineDataset = lines;
        plot.setDataset(plot.getRendererCount()+1, lineDataset);
        XYSplineRenderer lineRenderer = new XYSplineRenderer();
        plot.setRenderer(plot.getRendererCount()+1, lineRenderer);
    }
    
    public static void main(String args[]) {
        
        LineViewer2 bv = new LineViewer2("");
        
        OHLCDataItem[] candles = createCandles();
        bv.addCandles(candles);
        
        IntervalXYDataset lines = createDataset1();
        bv.addLines(lines);
        
        IntervalXYDataset lines2 = createDataset2();
        bv.addLines(lines2);
                
        bv.pack();
        RefineryUtilities.centerFrameOnScreen(bv);
        bv.setVisible(true);
    }
    
    private static OHLCDataItem[] createCandles() {
        OHLCDataItem[] candles = new OHLCDataItem[10];
        for(int i=0; i<candles.length; i++){
            long time = new Date().getTime() - i * 1000*60*60*24;
            Date date = new Date(time);            
            candles[i] = new OHLCDataItem(date,50-i*10,80-i*10,0,90-i*10,50);
        }
        return candles;
    }

    
    private static IntervalXYDataset createDataset1() {

        final TimeSeries ts = new TimeSeries("Series 1", Day.class);
        Date date1 = new Date( new Date().getTime() - 0 * 1000*60*60*24) ;
        Date date2 = new Date( new Date().getTime() - 3 * 1000*60*60*24) ;
        Date date3 = new Date( new Date().getTime() - 4 * 1000*60*60*24) ;
        Date date4 = new Date( new Date().getTime() - 5 * 1000*60*60*24) ;
        ts.add(new Day(date1), 100);
        ts.add(new Day(date2), 80);
        ts.add(new Day(date3), 70);
        ts.add(new Day(date4), 60);

        return new TimeSeriesCollection(ts);

    }
    
    private static IntervalXYDataset createDataset2() {

        final TimeSeries ts = new TimeSeries("Series 2", Day.class);
        Date date1 = new Date( new Date().getTime() - 0 * 1000*60*60*24) ;
        Date date2 = new Date( new Date().getTime() - 3 * 1000*60*60*24) ;
        Date date3 = new Date( new Date().getTime() - 4 * 1000*60*60*24) ;
        Date date4 = new Date( new Date().getTime() - 5 * 1000*60*60*24) ;
        ts.add(new Day(date1), 50);
        ts.add(new Day(date2), 10);
        ts.add(new Day(date3), 50);
        ts.add(new Day(date4), 10);

        return new TimeSeriesCollection(ts);

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
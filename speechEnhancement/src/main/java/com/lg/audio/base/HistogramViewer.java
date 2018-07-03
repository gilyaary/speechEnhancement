package com.lg.audio.base;

import java.awt.Dimension;
import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.*;

public class HistogramViewer {
	public ChartPanel panel;
	private XYPlot plot = new XYPlot();
    final NumberAxis  domainAxis = new NumberAxis ("Period");
    final ValueAxis rangeAxis = new NumberAxis("Value");
	
    public HistogramViewer(String title) {
    	JFreeChart jfreechart = ChartFactory.createHistogram(title, null, null, null, PlotOrientation.VERTICAL, true, true, false);
        this.plot = (XYPlot)jfreechart.getPlot();
        plot.setForegroundAlpha(0.85F);
        XYBarRenderer xybarrenderer = (XYBarRenderer)plot.getRenderer();
        xybarrenderer.setDrawBarOutline(false);
    	this.panel = new ChartPanel(jfreechart);
        this.panel.setPreferredSize(new Dimension(500, 800));
        
	}
    
    public void addHistogram(int index, IntervalXYDataset ds) {
       this.plot.setDataset(index, ds);
       if(plot.getRenderer(index) == null){
    	   XYBarRenderer barRenderer = new XYBarRenderer();
    	   plot.setRenderer(index, barRenderer); 
       }
       
    }
    
    public static void main(String[] args) {
    	
    	HistogramViewer hv = new HistogramViewer("Periodogram");
    	//IntervalXYDataset ds = createDemoDataset();
    	int[] x = new int [] {5,6,7,8,9,10,11,12,13,14,15};
    	double[] y = new double [] {10.5,14.5,25,20,17.5,21.3,35.6,32.0,29,27,30};
    	IntervalXYDataset ds = new HistogramResultsDataSet(x,y,true,true);
		hv.addHistogram(1, ds);
    	
    	
    	
    	JFrame frame = new JFrame();
    	frame.setContentPane(hv.panel);
    	frame.pack();
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}    
        
    private static IntervalXYDataset createDataset2(final int[]x, final double[]y) {
    	SimpleHistogramDataset simplehistogramdataset = new SimpleHistogramDataset("Series 1");
        
    	for(int i=0; i<x.length; i++){
	    	double startX = x[i];
	    	double endX = startX+1;
    		SimpleHistogramBin simplehistogrambin = new SimpleHistogramBin(startX, endX, true, false);
	        simplehistogrambin.setItemCount((int)y[i]);
	        simplehistogramdataset.addBin(simplehistogrambin);
    	}
        return simplehistogramdataset;
        
	}

    
    private static IntervalXYDataset createDatasetFromSamples(String seriesTitle, double[]values, int binCount, double minValue, double maxValue){
    	HistogramDataset histogramdataset = new HistogramDataset();
    	histogramdataset.addSeries(seriesTitle, values, binCount, minValue, maxValue);
    	return histogramdataset;
    }
    
    private static IntervalXYDataset createDemoDataset(){
        HistogramDataset histogramdataset = new HistogramDataset();
        double ad[] = new double[1000];
        Random random = new Random(0xbc614eL);
        for(int i = 0; i < 1000; i++)
            ad[i] = random.nextGaussian() + 5D;

        histogramdataset.addSeries("H1", ad, 100, 2D, 8D);
        ad = new double[1000];
        for(int j = 0; j < 1000; j++)
            ad[j] = random.nextGaussian() + 7D;

        histogramdataset.addSeries("H2", ad, 100, 4D, 10D);
        return histogramdataset;
    }

	public XYPlot getPlot() {
		return this.plot;
	}
    
}

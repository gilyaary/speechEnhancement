package com.lg.audio.base;

import java.util.List;

import org.jfree.data.xy.AbstractIntervalXYDataset;


public class HistogramResultsDataSet extends AbstractIntervalXYDataset{
	
	double[]x;
	double[]y;
	double xRange = 0;
	double yRange = 0;
	private double binWidth;
	private double minX;
	
	public HistogramResultsDataSet(double[] x, double[] y, boolean minXZero, boolean minYZero) {
		int itemCount = Math.min(x.length, y.length);
    	double minX = Double.MAX_VALUE;
    	double maxX = Double.MIN_VALUE;
    	double minY = Double.MAX_VALUE;
    	double maxY = Double.MIN_VALUE;
    	
    	for(int i=0; i<itemCount; i++){
    		if(x[i] <= minX){
    			minX = x[i];
    		}
    		if(x[i] >= maxX){
    			maxX = x[i];
    		}
    		if(y[i] <= minY){
    			minY = y[i];
    		}
    		if(y[i] >= maxY){
    			maxY = y[i];
    		}
    	}
    	
    	this.x = x;
		this.y = y;
		this.xRange = maxX - minX;
		this.yRange = maxY - minY;
		this.binWidth = xRange / (double)itemCount;		
		
		//this.minX = 0;
	}
	
	public HistogramResultsDataSet(int[] x, double[] y, boolean minXZero, boolean minYZero) {
		int itemCount = Math.min(x.length, y.length);
    	double minX = Double.MAX_VALUE;
    	double maxX = Double.MIN_VALUE;
    	double minY = Double.MAX_VALUE;
    	double maxY = Double.MIN_VALUE;
    	
    	for(int i=0; i<itemCount; i++){
    		if(x[i] <= minX){
    			minX = x[i];
    		}
    		if(x[i] >= maxX){
    			maxX = x[i];
    		}
    		if(y[i] <= minY){
    			minY = y[i];
    		}
    		if(y[i] >= maxY){
    			maxY = y[i];
    		}
    	}
    	
    	this.x = toInts(x);
		this.y = y;
		this.xRange = maxX - minX;
		this.yRange = maxY - minY;
		this.binWidth = xRange / (double)itemCount;		
		
		//this.minX = 0;
	}
	
	public HistogramResultsDataSet(int[] x, double[] y, double minX, double maxX, double minY, double maxY) {
		int itemCount = Math.min(x.length, y.length);
		
		this.x = toInts(x);
		this.y = y;
		this.xRange = maxX - minX;
		this.yRange = maxY - minY;
		this.binWidth = xRange / (double)itemCount;
		this.minX = minX;
	}
	
	public HistogramResultsDataSet(List<double[]> periodAmplitude) {
		int itemCount = periodAmplitude.size();
		double minX = Double.MAX_VALUE;
    	double maxX = Double.MIN_VALUE;
    	double minY = Double.MAX_VALUE;
    	double maxY = Double.MIN_VALUE;
    	
    	int [] x = new int[itemCount];
		double [] y = new double[itemCount];
		for(int i=0; i<itemCount; i++){
			x[i] = (int) periodAmplitude.get(i)[0];
			y[i] = periodAmplitude.get(i)[1];
		}
		
		for(int i=0; i<itemCount; i++){
    		if(x[i] <= minX){
    			minX = x[i];
    		}
    		if(x[i] >= maxX){
    			maxX = x[i];
    		}
    		if(y[i] <= minY){
    			minY = y[i];
    		}
    		if(y[i] >= maxY){
    			maxY = y[i];
    		}
    	}
		
		this.x = toInts(x);
		this.y = y;
		this.xRange = maxX - minX;
		this.yRange = maxY - minY;
		this.binWidth = xRange / (double)itemCount;
		this.minX = minX;
	}

	private double[] toInts(int[] x2) {
		double [] xx = new double[x2.length];
		for (int i = 0; i < x2.length; i++) {
			xx[i] = x2[i];
		}
		return xx;
	}

	
	public Number getStartX(int series, int item) {
		return x[item] - this.binWidth/2;
	}

	
	public Number getEndX(int series, int item) {
		return x[item] + this.binWidth/2;
	}

	
	public Number getStartY(int series, int item) {
		return y[item];
	}

	
	public Number getEndY(int series, int item) {
		return y[item];
	}

	
	public int getItemCount(int series) {
		return Math.min(x.length, y.length);
	}

	
	public Number getX(int series, int item) {
		return x[item];
	}

	
	public Number getY(int series, int item) {
		return y[item];
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}

	@Override
	public Comparable getSeriesKey(int series) {
		return "P";
	}
}
package com.lg.audio.base;

import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.math.complex.Complex;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;

public class HistogramHelper {
	
	public static void calculateAndShowHistogram(List<TransformResult[]>allTransformResults) {
		calculateAndShowHistogram(allTransformResults,0, 8000, 1200, 800, true);
	}
	public static double[][] calculateAndShowHistogram(List<TransformResult[]>allTransformResults, double minFreq, double maxFreq, int width, int height, boolean smooth) {
		
		//define these
		int bin_count = 40;
		double minX = -0.5;
		double maxX = 0.5;
		
		String title = "3DHistogram " + minFreq + "-" + maxFreq ;
		
		double range = maxX - minX;
		double binSize = range / bin_count;
		double [] x = new double [bin_count];
		double [] y = new double [bin_count];
		for(int i=0; i<bin_count; i++){
			x[i] = minX + binSize * ( i + 0.5);
		}
		
		double minXTimeDiff = -200;
		double maxXTimeDiff = 200;
		double rangeTimeDiff = maxXTimeDiff - minXTimeDiff;
		int bin_count_time_diff = 40;
		double binSizeTimeDiff = rangeTimeDiff / bin_count_time_diff;
		double [] xTimeDiff = new double [bin_count_time_diff];
		double [] yTimeDiff = new double [bin_count_time_diff];
		for(int i=0; i<bin_count_time_diff; i++){
			xTimeDiff[i] = minXTimeDiff + binSizeTimeDiff * ( i + 0.5);
		}
		double[][]yAmpRatioAndTime = new double[bin_count][bin_count_time_diff];
		
		for(TransformResult[] transformResults : allTransformResults){
			List<SpectralElement> spectralElementsR = transformResults[0].getSpectralElements();
			List<SpectralElement> spectralElementsL = transformResults[1].getSpectralElements();
			Complex[] complexR = transformResults[0].getComplex();
			Complex[] complexL = transformResults[1].getComplex();
			double [] yy = new double[spectralElementsR.size()];
			double [] yyTimeDiff = new double[spectralElementsR.size()];
			
			for(int i=0; i<spectralElementsR.size(); i++){
				double ampR = Double.MIN_VALUE+Math.sqrt(complexR[i].getReal()*complexR[i].getReal()+complexR[i].getImaginary()*complexR[i].getImaginary());
				double ampL = Double.MIN_VALUE+Math.sqrt(complexL[i].getReal()*complexL[i].getReal()+complexL[i].getImaginary()*complexL[i].getImaginary());
				double ampRatio = Math.abs(ampR/ampL) - Math.abs(ampL/ampR);

				int freq = (int) spectralElementsR.get(i).getFrequency();
				int binIndex = (int) ( (ampRatio - minX) / binSize );
				
				
				double angle = (complexL[i].divide(complexR[i])).log().getImaginary();
				angle *= -1;
				double cycleTime  = 1D/(double)freq;
				double timeDiff = (angle/(2*Math.PI)) * cycleTime;
				int relativeTimeDiff = (int)(timeDiff * 1000000 );
				int binIndexTimeDiff = (int) (  (relativeTimeDiff-minXTimeDiff) / binSizeTimeDiff );
				
				if( freq >= minFreq && freq <= maxFreq){
					if( binIndex >= 0 && binIndex < bin_count){ // 
						if(binIndexTimeDiff >= 0 && binIndexTimeDiff < bin_count_time_diff ){ 
							double weight = Math.abs(ampR * ampL) ;
							y[binIndex] += weight*weight ;
							yy[binIndex] += weight*weight ;
							double weightTimeDiff = Math.abs(ampR * ampL) * freq;
							yTimeDiff[binIndexTimeDiff] += weightTimeDiff;
							yyTimeDiff[binIndexTimeDiff] += weightTimeDiff;
							if( binIndex >=0 && binIndex < bin_count ){
								yAmpRatioAndTime[binIndex][binIndexTimeDiff] += weight*weight ;
							}
						}
					}
				}
			}
			//displayHist(x, yy, "Amplitude Ratios");
			//displayHist(xTimeDiff, yyTimeDiff, "TimeDiff");
		}
		
		displayHist(x, y, "Total Amplitude Ratios");
		displayHist(xTimeDiff, yTimeDiff, "Total TimeDiff");
		display3dHist(yAmpRatioAndTime, smooth, title, width, height);
		
		histogramCount ++;
		//System.out.printf("%.2f, %.2f, %.2f%n", totalR, totalL, totalR/totalL);
		return yAmpRatioAndTime;
	
	}
	
	public static void display3dHist(double[][] xy, boolean smooth) {
		display3dHist( xy,  smooth, "3D Histogram", 1200, 800);
	}
	public static void display3dHist(final double[][] xy, boolean smooth, String title, int width, int height) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for(int i=0; i<xy.length; i++){
			for(int j=0; j<xy[i].length; j++){
				double value = xy[i][j];
				if(value > max){max = value;}
				if(value < min){min = value;}
			}
		}
		if(smooth){
			smooth(xy);
		}
		
		_3dTest _3dHist = new _3dTest();
		Range xrange = new Range(0, xy.length);
		int xsteps = xy.length;
		Range yrange = new Range(0, xy[0].length);
		int ysteps = xy[0].length;
		Mapper mapper = new Mapper() {
		    public double f(double x, double y) {
		        int xIndex = (int)x;
		        int yIndex  =(int)y;
		        if(xIndex >=0 && xIndex < xy.length && yIndex >=0 && yIndex < xy[0].length ){
		        	return xy[xIndex][yIndex];
		        }
		        return 0;
		    }
		};
		_3dHist.buildChart(mapper, yrange, ysteps, yrange, ysteps, min, max, title, width, height);
	}

	public static void smooth(double[][] xy) {
		for(int iter = 0; iter < 15; iter ++){
			for(int row=1; row<xy.length-1;row++){
				for(int col=1; col<xy[row].length-1;col++){
					//we need to get the value of each point from a circle
					double up = xy[row-1][col];
					double down = xy[row+1][col];
					double left = xy[1][col-1];
					double right = xy[row][col+1];
					double avg = (up+down+left+right)/4;
					xy[row][col] += avg * 0.25;
				}
			}
		}
	}

	public static void displayHist(double[] x, double[] y, String histTitle) {
		HistogramViewer hv = new HistogramViewer(histTitle);
		HistogramResultsDataSet ds = new HistogramResultsDataSet(x, y, true, true);
		hv.addHistogram(1, ds);
		JFrame frame = new JFrame();
		frame.setContentPane(hv.panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	static int histogramCount = 0;

}

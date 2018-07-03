package com.lg.audio.duet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;

import com.lg.audio.base.HistogramResultsDataSet;
import com.lg.audio.base.HistogramViewer;
import com.lg.audio.base.SpectralElement;
import com.lg.audio.base.SpectrumAnalizer;
import com.lg.audio.base.StdAudio88k;
import com.lg.audio.base.TransformResult;
import com.lg.audio.base._3dTest;




public class NonMicrophoneSeparationTestB {
	
	static final double GAIN = 5;
	//static SampleGenerator sg = new SampleGenerator(); 
	static boolean exitEarly = true;
	static int windowSize = (int)( 4096 / 4 ) ;
	static int skipSize =   (int)( 4096 / 16 );
	//static int MIC2DELAY = 100;
	static int MIC2DELAY = 0;
	static String PATH = "/AudioSamples/";	
	
	static String file1 = PATH + "amy.wav";
	static String file2 = PATH + "eric.wav";
	
	
	
	
	public static void main(String[] args) throws Exception {
		ConvertFile(file1, file2);
	}

	
	
	static List<TransformResult> spectrumsRight = new ArrayList<TransformResult>();
	static List<TransformResult> spectrumsLeft = new ArrayList<TransformResult>();
	

	private static void separateMics(double[] bothMics, double[] micR, double[] micL) {
		int leftIndex = 0;
		int rightIndex = 0;
		for(int i=0; i<bothMics.length; i++){
			boolean even = i%2 == 0;
			if(even){
				micL[leftIndex] = bothMics[i];
				leftIndex += 1;
			}
			else{
				micR[rightIndex] = bothMics[i];
				rightIndex += 1;
			}
		}
	}

	

	private static double[] hamming(double[] values) {
		double [] hammingWeightedValues = new double[values.length];
		for(int index=0; index<values.length; index++){
			double hammingWeight = 0.54f - 0.46f * (double) Math.cos(Math.PI*2 * index / (values.length - 1));
			//hammingWeight = 1;
			hammingWeightedValues[index] = hammingWeight * values[index];
		}
		return hammingWeightedValues;
	}

	static SpectrumAnalizer sa = new SpectrumAnalizer(44000);
	
	private static int getClosestPowerOf2(int size) {
		int log2 = (int) (Math.log(size) / Math.log(2));
		return(int) Math.pow(2, log2+1);
	}	
	
	private static void showHistogram(List<TransformResult[]>allTransformResults) {
		if(true){
			//define these
			int bin_count = 40;
			double minX = -3;
			double maxX = 3;
			
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
			
			double totalR = 0;
			double totalL = 0;
			
			int iter = 0;
			for(TransformResult[] transformResults : allTransformResults){
				List<SpectralElement> spectralElementsR = transformResults[0].getSpectralElements();
				List<SpectralElement> spectralElementsL = transformResults[1].getSpectralElements();
				Complex[] complexR = transformResults[0].getComplex();
				Complex[] complexL = transformResults[1].getComplex();
				double [] yy = new double[spectralElementsR.size()];
				double [] yyTimeDiff = new double[spectralElementsR.size()];
				
				for(int i=0; i<spectralElementsR.size(); i++){
					
					//double ampR = spectralElementsR.get(i).getAmplitude(); totalR += ampR;
					//double ampL = spectralElementsL.get(i).getAmplitude(); totalL += ampL;
					double ampR = Double.MIN_VALUE+Math.sqrt(complexR[i].getReal()*complexR[i].getReal()+complexR[i].getImaginary()*complexR[i].getImaginary());
					double ampL = Double.MIN_VALUE+Math.sqrt(complexL[i].getReal()*complexL[i].getReal()+complexL[i].getImaginary()*complexL[i].getImaginary());
					double ampRatio = Math.abs(ampR/ampL) - Math.abs(ampL/ampR);

					int freq = (int) spectralElementsR.get(i).getFrequency();
					int binIndex = (int) ( (ampRatio - minX) / binSize );
					
					
					
					
					
					//FOR TIME DIFFERENCE
					/*
					double phaseR = Math.atan(complexR[i].getImaginary()/complexR[i].getReal());
					double phaseL = Math.atan(complexL[i].getImaginary()/complexL[i].getReal());
					double phaseDiffRad = phaseR - phaseL;
					double phaseDiffDecimal = phaseDiffRad / (Math.PI*2);
					double timeDiff = phaseDiffDecimal / freq;
					int diffMicroSec = (int)(timeDiff * 1000 * 1000); 
					int binIndexTimeDiff = (int) (  (diffMicroSec-minXTimeDiff) / binSizeTimeDiff );
					*/
					
					double angle = (complexL[i].divide(complexR[i])).log().getImaginary();
					angle *= -1;
					double cycleTime  = 1D/(double)freq;
					double timeDiff = (angle/(2*Math.PI)) * cycleTime;
					int relativeTimeDiff = (int)(timeDiff * 1000000 );
					int binIndexTimeDiff = (int) (  (relativeTimeDiff-minXTimeDiff) / binSizeTimeDiff );
					
					
					
					//System.out.println(diffMicroSec);
					
					if(   freq > 300 && freq < 4000 ){
						if(  binIndex >= 0 && binIndex < bin_count){ //freq > 1400 && freq < 1500 && 
							if(binIndexTimeDiff >= 0 && binIndexTimeDiff < bin_count_time_diff){ // &&  freq > 250 && freq < 2000
								double weight = Math.abs(ampR * ampL) ;
								y[binIndex] += weight*weight ;
								yy[binIndex] += weight*weight ;
								double weightTimeDiff = Math.abs(ampR * ampL) * freq;
								yTimeDiff[binIndexTimeDiff] += weightTimeDiff;
								yyTimeDiff[binIndexTimeDiff] += weightTimeDiff;
								if( binIndex >=0 && binIndex < bin_count ){
									yAmpRatioAndTime[binIndex][binIndexTimeDiff] += weight*weight*0.00001 ;
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
			display3dHist(yAmpRatioAndTime, true);
			
			histogramCount ++;
			//System.out.printf("%.2f, %.2f, %.2f%n", totalR, totalL, totalR/totalL);
		}
		
	}

	private static void display3dHist(final double[][] xy, boolean smooth) {
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
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for(int i=0; i<xy.length; i++){
			for(int j=0; j<xy[i].length; j++){
				double value = xy[i][j];
				if(value > max){max = value;}
				if(value < min){min = value;}
			}
		}
		_3dHist.buildChart(mapper, yrange, ysteps, yrange, ysteps, min, max);
	}

	private static void smooth(double[][] xy) {
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

	

	private static void displayHist(double[] x, double[] y, String histTitle) {
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
	
	private static double diff(double v1, double v2) {
		return Math.abs(v1 - v2);
	}


	

	private static double[] toArray(List<Double> list) {
		double [] array = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	
	//static SampleGenerator gen = new SampleGenerator();
	
	private static void ConvertFile(String file1, String file2) throws Exception {
		double[] bothMics = mix(file1, file2);
		double[]micR = new double [bothMics.length/2];
		double[]micL = new double [bothMics.length/2];
		separateMics(bothMics, micR, micL);
		final List<TransformResult[] > allTransformerResults = new ArrayList<TransformResult[] >();
		Consumer<TransformResult[]> processor = ( TransformResult[] transformResults) -> {
			allTransformerResults.add(transformResults);
			histogramCount ++;
		};
		
		extractSpectrums(micR, micL, windowSize, skipSize, processor );
		showHistogram(allTransformerResults);
	}

	private static double[] mix(String file1, String file2) {
		
		double  MIC1SRC1 = 1, 
				MIC1SRC2 = 0.7, 
				MIC2SRC1 = 0.7, 
				MIC2SRC2 = 1;
		
		double[] streoSource1 = StdAudio88k.read(file1);
		double[] streoSource2 = StdAudio88k.read(file2);
		
		//create a delay
		double[] streoSource1Delayed = Arrays.copyOfRange(streoSource1, 0, streoSource1.length); //delay source 1 by 0.5 ms
		double[] streoSource2Delayed = Arrays.copyOfRange(streoSource2, 0, streoSource2.length);
		
		double[]mix = new double[Math.min(streoSource1Delayed.length, streoSource2Delayed.length) - 40];
		for(int i=0; i<mix.length; i+=2){
			mix[i] += streoSource1[i+10] * 0.4;
			mix[i+1] += streoSource1[i] * 0.6;
		}
		for(int i=0; i<mix.length; i+=2){
			mix[i] += streoSource2[i] * 0.6;
			mix[i+1] += streoSource2[i+10] * 0.4;
		}
		return mix;
	}

	
	private static double[] extract(String file1, String file2) {
		double[] stereoSource1 = StdAudio88k.read(file1);
		double[] stereoSource2 = StdAudio88k.read(file2);
		for (int i = 0; i < stereoSource1.length; i++) {
			stereoSource1[i] = stereoSource1[i] * 3.5;
		}
		//findIntersection(stereoSource1,stereoSource2);
		double src1TotalSqr = 0;
		double src2TotalSqr = 0;
		for (int i = 0; i < stereoSource1.length; i++) {
			src1TotalSqr += stereoSource1[i] * stereoSource1[i];
			src2TotalSqr += stereoSource2[i] * stereoSource2[i];
		}
		System.out.printf("AvgSrc1: %.8f%n", src1TotalSqr/stereoSource1.length);	
		System.out.printf("AvgSrc2: %.8f%n", src2TotalSqr/stereoSource2.length);	
		
		double[]mix =  new double[Math.min(stereoSource1.length, stereoSource2.length)/2 + 1];
		for(int i=0; i<mix.length; i+=4){
			double src1 = stereoSource1[i];
			double src2 = stereoSource2[i + MIC2DELAY] ;
			mix[i] = src1;
			mix[i+1] = src2;
			
		}
		return mix;
	}

	
	private static void findIntersection(double[] src1, double[] src2) {
		
		double bestCorr = -1;
		int bestLag = 0;
		for(int startPoint = 1000; startPoint<300000; startPoint+=1000){
			int endPoint = startPoint + 1000;
			
			for(int lag=-200; lag<200; lag++){
				
				double [] a = Arrays.copyOfRange(src1, startPoint, endPoint);
				double [] b = Arrays.copyOfRange(src2, startPoint+lag, endPoint+lag);
				PearsonsCorrelation corrFinder = new PearsonsCorrelation();
				double corr = corrFinder.correlation(a, b);
				if(corr > bestCorr){
					bestCorr = corr;
					bestLag = lag;
				}
			}
			System.out.printf("BestLag: %s%n ", bestLag);
		}
		
	}

	private static boolean correctAmplitudeRatio(double ampRatio, double timeDiff) {
		return 
				true &&
				//ampRatio >= -0.5 && ampRatio <= -0.1 &&
				//ampRatio <= 4  &&
				//ampRatio >= 3.5  &&
				//Math.abs(timeDiff) > 400 && Math.abs(timeDiff) < 500 && 
				//timeDiff > 100 && timeDiff < 200 &&
				
				//ampRatio > -0.2 && ampRatio < -0.1 &&
				//timeDiff > -100 && timeDiff < 100 &&
				//ampRatio > 3.4 && ampRatio < 3.6 &&
				//ampRatio > 3.6 && ampRatio < 4.0 &&
				//timeDiff > -100 && timeDiff < 100 &&
				//timeDiff > -100 && timeDiff < 100 &&
				//(timeDiff < -150 && timeDiff < -100) &&
				//timeDiff < 50 && timeDiff > -50 &&
				//ampRatio > 0.1 && ampRatio < 0.5 &&
				//ampRatio > -0.4 && ampRatio < 0.1 &&
				//timeDiff > -50 && timeDiff < 50 &&
				//timeDiff > -15 && timeDiff< -0.1 &&
				//timeDiff > 10 && timeDiff< 20 &&
				//timeDiff > -25 && timeDiff< 25 &&
				//ampRatio > -0.5 && ampRatio < 0.5 &&
				//ampRatio > 0.50 && ampRatio < 0.90 &&
				//ampRatio > -1 && ampRatio < -0.50 &&
				//(timeDiff >= 0 && timeDiff <= 60 ||  ampRatio < 0) &&
				//ampRatio < 0 &&
				timeDiff > -120 && timeDiff < -80 &&
				true;
	}
	
	private static List<Double> recreateSources(TransformResult[] transformResults) {
		FastFourierTransformer fft = new FastFourierTransformer();
		Complex[] complexR = transformResults[0].getComplex();
		Complex[] complexL = transformResults[1].getComplex();
		Complex[] selected = new Complex[complexR.length] ;
		for (int i = 0; i < selected.length; i++) {
			selected[i] = new Complex(0,0);
		}
		for (int i = 1; i < selected.length/2; i++) {
			double frequency = ((double)i)* ( ((double)44000)/ ((double)complexR.length) );
			SpectralElement seR = transformResults[0].getSpectralElements().get(i-1);
			SpectralElement seL = transformResults[1].getSpectralElements().get(i-1);
			double freq = seR.getFrequency();
			double ampR = seR.getAmplitude();
			double ampL = seL.getAmplitude();
			double ampRatio = Math.abs(ampR/ampL) -  Math.abs(ampL/ampR);
			
			double phaseR = Math.atan(complexR[i].getImaginary()/complexR[i].getReal());
			double phaseL = Math.atan(complexL[i].getImaginary()/complexL[i].getReal());
			double phaseDiffRad = phaseR - phaseL;
			double phaseDiffDecimal = phaseDiffRad / (Math.PI*2);
			double timeDiff = phaseDiffDecimal / freq;
			double timeDiffMicrosec = timeDiff * 1000000;
			
			if( correctAmplitudeRatio(ampRatio, timeDiffMicrosec) ){
				selected[i] = complexR[i];
				selected[selected.length - i - 1] = complexR[selected.length -i -1];
			}
		}
		
		//DO Inverse FFT
		Complex[] trans = fft.inversetransform(selected);
		double [] composed = new double [trans.length] ;
		List<Double> composedList = new ArrayList<Double>();
		for(int i=0; i<trans.length; i++){
			double real = trans[i].getReal() * 1;
			composed[i] = real * GAIN;
			composedList.add(composed[i]);
		}
		
		return composedList;
	}
	
	
	private static void extractSpectrums(double[] micR, double[] micL, int windowSize, int skipSize,
			Consumer<TransformResult[]> consumer) {
		
		double [] output= new double [micR.length];
		
		for(int i=windowSize; i<micR.length; i+=skipSize){
			int startIndex = i-windowSize;
			int endIndex = i;
			double [] usedValuesR = Arrays.copyOfRange(micR, startIndex, endIndex);
			TransformResult spectrumR = sa.analize3(hamming(usedValuesR));
			spectrumsRight.add(spectrumR);
			double [] usedValuesL = Arrays.copyOfRange(micL, startIndex, endIndex);
			TransformResult spectrumL = sa.analize3(hamming(usedValuesL));
			spectrumsLeft.add(spectrumL);
			
			List<Double> synthesized = recreateSources(new TransformResult[]{spectrumR,spectrumL});
			//output
			for(int j=0; j<synthesized.size(); j++){
				output[startIndex + j] += synthesized.get(j)/(windowSize/skipSize);
			}
		}
		//ChartViewer viewer2 = new ChartViewer("Sound WAV Before ");
		//ChartHelper.displaySignal( output, viewer2, 1, "Source 1", 1, Color.GREEN);
		//StdAudio88k.SAMPLE_RATE = 8000;
		StdAudio88k.SAMPLE_RATE = 44100;
		StdAudio88k.init();
		StdAudio88k.play(output);
		
		
		//do this to see histograms
		for(int i=0; i<spectrumsRight.size(); i++){
			TransformResult analizedSpectrumR = spectrumsRight.get(i);
			TransformResult analizedSpectrumL = spectrumsLeft.get(i);
			consumer.accept(new TransformResult[]{analizedSpectrumR,analizedSpectrumL});
		}
	}
	

	
	
	
	

	
}

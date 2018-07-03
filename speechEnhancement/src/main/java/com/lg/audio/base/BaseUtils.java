package com.lg.audio.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;



public class BaseUtils {
	
	public static void separateMics(double[] bothMics, double[] micR, double[] micL) {
		int leftIndex = 0;
		int rightIndex = 0;
		for(int i=0; i<micR.length-1 && i<micL.length-1; i++){
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
	
	public static List<TransformResult[]> extractSpectrums(double[] micR, double[] micL, int windowSize, int skipSize, int samplingFreq) {
		SpectrumAnalizer sa = new SpectrumAnalizer(samplingFreq);
		List<TransformResult[]> transResults = new ArrayList<TransformResult[]>();
		
		for(int i=windowSize; i<micR.length; i+=skipSize){
			int startIndex = i-windowSize;
			int endIndex = i;
			double [] usedValuesR = Arrays.copyOfRange(micR, startIndex, endIndex);
			TransformResult spectrumR = sa.analize3(hamming(usedValuesR));
			double [] usedValuesL = Arrays.copyOfRange(micL, startIndex, endIndex);
			TransformResult spectrumL = sa.analize3(hamming(usedValuesL));
			transResults.add(new TransformResult[]{spectrumR,spectrumL});
		}
		return transResults;
	}
	
	public static double[] hamming(double[] values) {
		double [] hammingWeightedValues = new double[values.length];
		for(int index=0; index<values.length; index++){
			double hammingWeight = 0.54f - 0.46f * (double) Math.cos(Math.PI*2 * index / (values.length - 1));
			hammingWeightedValues[index] = hammingWeight * values[index];
		}
		return hammingWeightedValues;
	}
	
	public static double[] inverseHamming(double[] values) {
		double [] hammingWeightedValues = new double[values.length];
		for(int index=0; index<values.length; index++){
			double hammingWeight = 0.54f - 0.46f * (double) Math.cos(Math.PI*2 * index / (values.length - 1));
			hammingWeightedValues[index] = values[index] / (hammingWeight + Double.MIN_VALUE);
		}
		return hammingWeightedValues;
	}
	public static List<Double> inverseHamming(List<Double> values) {
		List<Double> hammingWeightedValues = new ArrayList<Double>(values.size());
		for(int index=0; index<values.size(); index++){
			double hammingWeight = 0.54f - 0.46f * (double) Math.cos(Math.PI*2 * index / (values.size() - 1));
			hammingWeightedValues.add( values.get(index) / hammingWeight );
		}
		return hammingWeightedValues;
	}
	
	
	
	public static void play( List<TransformResult[]> spectrumsList, int windowSize, int skipSize, int length, int samplingRate, int GAIN, Predicate<double[]> p) {
		double[] output = inverseFFTOverlapAdd(spectrumsList, windowSize, skipSize, length, GAIN, p);
		//ChartViewer viewer2 = new ChartViewer("Sound WAV Before ");
		//ChartHelper.displaySignal( output, viewer2, 1, "Source 1", 1, Color.GREEN);
		System.out.println("!!!!  PROCESSING SIGNAL COMPLETED !!!!");
		StdAudio88k.init(samplingRate);
		StdAudio88k.play(output);
	}

	public static double[] inverseFFTOverlapAdd(List<TransformResult[]> spectrumsList, int windowSize, int skipSize,
			int length, int GAIN, Predicate<double[]> p) {
		double [] output= new double [length];
		int startIndex = 0;
		for(TransformResult[] lrSpectrums : spectrumsList){
			List<Double> synthesized = recreateSources(lrSpectrums, GAIN, p);
			//List<Double> weightedSynthesized = inverseHamming(synthesized);
			List<Double> weightedSynthesized = synthesized;
			//output
			for(int j=0; j<weightedSynthesized.size(); j++){
				output[startIndex + j] += weightedSynthesized.get(j)/(windowSize/skipSize);
			}
			startIndex += skipSize;
		}
		return output;
	}
	
	public static List<List<Double>[]> inverseLeftRightFft( List<TransformResult[]> spectrumsList, double minFreq, double maxFreq){
		FastFourierTransformer fft = new FastFourierTransformer();
		List<List<Double>[]> framedSignalSegments = new ArrayList<List<Double>[]>();
		//each element is a frequency spectrum of left and right
		for(TransformResult[] lrSpectrums : spectrumsList){
			
			List<SpectralElement> spectralElements = lrSpectrums[0].getSpectralElements();
			Complex[] complexR = lrSpectrums[0].getComplex();
			Complex[] complexL = lrSpectrums[1].getComplex();
			Complex[] complexRSelected = new Complex[complexR.length];
			Complex[] complexLSelected = new Complex[complexL.length];
			for(int i=0; i<complexL.length; i++){
				complexRSelected[i] = new Complex(0,0);
				complexLSelected[i] = new Complex(0,0);
			}
			for(int i=1; i<complexL.length/2; i++){
				SpectralElement se = spectralElements.get(i-1);
				double frequency = se.getFrequency();
				if(frequency >= minFreq && frequency <= maxFreq){
					complexRSelected[i] = complexR[i];
					complexLSelected[i] = complexL[i];
				}
			}
			
			List<Double> reconstractedTimeDomainSignalR = inverseFft(1, fft, complexRSelected);
			List<Double> reconstractedTimeDomainSignalL = inverseFft(1, fft, complexLSelected);
			List<Double>[] lrSignals = new List[]{reconstractedTimeDomainSignalR, reconstractedTimeDomainSignalL};
			framedSignalSegments.add(lrSignals);
		}
		return framedSignalSegments;
	}
	
	public static List<Double> recreateSources(TransformResult[] transformResults, int GAIN, Predicate<double[]> separator) {
		FastFourierTransformer fft = new FastFourierTransformer();
		Complex[] complexR = transformResults[0].getComplex();
		Complex[] complexL = transformResults[1].getComplex();
		Complex[] selected = selectComponents(transformResults, separator, complexR, complexL);
		
		//DO Inverse FFT
		List<Double> composedList = inverseFft(GAIN, fft, selected);
		
		return composedList;
	}

	private static Complex[] selectComponents(TransformResult[] transformResults, Predicate<double[]> separator,
			Complex[] complexR, Complex[] complexL) {
		Complex[] selected = new Complex[complexR.length] ;
		for (int i = 0; i < selected.length; i++) {
			selected[i] = new Complex(0,0);
		}
		for (int i = 1; i < selected.length/2; i++) {
			//double frequency = ((double)i)* ( ((double)44000)/ ((double)complexR.length) );
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
			
			//correctAmplitudeRatio(ampRatio, timeDiffMicrosec)
			if( separator.test( new double [] {ampRatio, timeDiffMicrosec, freq, ampR*ampR}) ){
				selected[i] = complexR[i];
				selected[selected.length - i - 1] = complexR[selected.length -i -1];
			}
		}
		return selected;
	}

	private static List<Double> inverseFft(int GAIN, FastFourierTransformer fft, Complex[] selected) {
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

	public static double crossCorrelation(List<Double> valuesRight, List<Double> valuesLeft) {
		double meanRight = mean(valuesRight);
		double meanLeft = mean(valuesLeft);
		double sigmaLeftRightCoVariance = 0;
		double sigmaLeftVariance = 0;
		double sigmaRightVariance = 0;
		for( int i = 0; i<valuesRight.size() && i < valuesLeft.size(); i++){
			sigmaRightVariance += (valuesRight.get(i) - meanRight) * (valuesRight.get(i) - meanRight);
			sigmaLeftVariance += (valuesLeft.get(i) - meanLeft) * (valuesLeft.get(i) - meanLeft);
			sigmaLeftRightCoVariance += (valuesRight.get(i) - meanRight) * (valuesLeft.get(i) - meanLeft);
		}
		double crossCorrelation = sigmaLeftRightCoVariance / ( Math.sqrt(sigmaRightVariance) * Math.sqrt(sigmaLeftVariance) );
		return crossCorrelation;
	}

	private static double mean(List<Double> values) {
		double count = values.size();
		double total = 0;
		for(Double value : values){
			total += value;
		}
		return total / count;
	}
	
	/*
	 * //List<Double> synthesized = recreateSources(new TransformResult[]{spectrumR,spectrumL});
		//output
		for(int j=0; j<synthesized.size(); j++){
			output[startIndex + j] += synthesized.get(j)/(windowSize/skipSize);
		}
	 */
}

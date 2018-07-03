

package com.lg.audio.base;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.jfree.ui.RefineryUtilities;


public class SpectrumAnalizer {

    private static double y;
    int sampleFrequency = 0; //number of samples per time unit (seconds, minutes, hours, days. weeks, months, years)
    
    public SpectrumAnalizer(int sampleFrequency2) {
        sampleFrequency = sampleFrequency2;
    }


    public List<SpectralElement> analizePrev(double [] samples){
        
    	List<SpectralElement>spectrum=new ArrayList<SpectralElement>();
        FastFourierTransformer fft = new FastFourierTransformer();
        
        
        Complex[] complex = fft.transform(samples);
        for(int i=0; i<complex.length/2;i+=1){
            
            Complex c1  = complex[i]; 
            double imaginary1 = c1.getImaginary();
            double real1 = c1.getReal();
            double amplitude1 =  Math.sqrt( Math.pow(real1, 2) + Math.pow(imaginary1, 2) ) / ((double)complex.length);
            double phaseShift1 = calculatePhaseShift(imaginary1, real1);            
            
            Complex c2  = complex[complex.length-i-1]; 
            double imaginary2 = c2.getImaginary();
            double real2 = c2.getReal();
            double amplitude2 =  Math.sqrt( Math.pow(real2, 2) + Math.pow(imaginary2, 2) ) / ((double)complex.length);
            double phaseShift2 = calculatePhaseShift(imaginary2, real2);
            
            
            double phaseShift = (phaseShift1);
            
            
            if(true){
                
                double frequency = ((double)i)* ( ((double)sampleFrequency)/ ((double)complex.length) );
                                
                //System.out.printf("(%.2f) Amplitude: %.2f, PhaseShift: %.2f%n", frequency, (amplitude1+amplitude2), ((phaseShift1+phaseShift2)/(Math.PI*2))*360);
                                
                SpectralElement spectralElement = new SpectralElement(frequency , (amplitude1 + amplitude2), (phaseShift/(Math.PI*2))*360); 
                spectrum.add(spectralElement);
            }
        }
        return spectrum;
    }

    
    public List<double[]> analizeAsPeriods(double [] samples){
        List<double[]>spectrum=new ArrayList<double[]>();
        FastFourierTransformer fft = new FastFourierTransformer();
        Complex[] complex = fft.transform(samples);
        for(int i=1; i<complex.length/2;i+=1){
            
            Complex c1  = complex[i]; 
            double imaginary1 = c1.getImaginary();
            double real1 = c1.getReal();
            double amplitude1 =  Math.sqrt( Math.pow(real1, 2) + Math.pow(imaginary1, 2) ) / ((double)complex.length);
            double phaseShift1 = calculatePhaseShift(imaginary1, real1);            
            
            Complex c2  = complex[complex.length-i-1]; 
            double imaginary2 = c2.getImaginary();
            double real2 = c2.getReal();
            double amplitude2 =  Math.sqrt( Math.pow(real2, 2) + Math.pow(imaginary2, 2) ) / ((double)complex.length);
            double phaseShift2 = calculatePhaseShift(imaginary2, real2);
            
            
            double phaseShift = (phaseShift1);
            
            
            if(true){
            	double frequency = ((double)i)* ( ((double)sampleFrequency)/ ((double)complex.length) );
                double period = 1/frequency;
                double amplitude = amplitude1 + amplitude2;
                spectrum.add(new double[]{period, amplitude});
                
            }
        }
        
        return spectrum;
    }
    

    public List<SpectralElement> analize(double [] samples){
        
    	List<SpectralElement>spectrum=new ArrayList<SpectralElement>();
        FastFourierTransformer fft = new FastFourierTransformer();
        
        Complex[] complex = fft.transform(samples);
        
        
        
        for(int i=1; i<complex.length/2;i+=1){
            
            Complex c1  = complex[i]; 
            double imaginary1 = c1.getImaginary();
            double real1 = c1.getReal();
            double amplitude1 =  Math.sqrt( Math.pow(real1, 2) + Math.pow(imaginary1, 2) ) / ((double)complex.length) * 2;
            double phaseShift1 = calculatePhaseShift(imaginary1, real1);
            
            
            Complex c2  = complex[complex.length-i];
            double imaginary2 = c2.getImaginary();
            double real2 = c2.getReal();
            double amplitude2 =  Math.sqrt( Math.pow(real2, 2) + Math.pow(imaginary2, 2) ) / ((double)complex.length) * 2;
            double phaseShift2 = calculatePhaseShift(imaginary2, real2);
            
            
            double amplitude = amplitude1; 
            double phaseShift = phaseShift2; 
            
            if(true){
                
                double frequency = ((double)i)* ( ((double)sampleFrequency)/ ((double)complex.length) );
                                
                //System.out.printf("(%.2f) Amplitude: %.2f, PhaseShift: %.2f%n", frequency, amplitude, (phaseShift/(Math.PI*2))*360);
                                
                SpectralElement spectralElement = new SpectralElement(frequency , amplitude, phaseShift, real1,imaginary1); 
                spectrum.add(spectralElement);
            }
        }
        return spectrum;
    }
    
public List<SpectralElement> analize2(double [] samples){
        
    	List<SpectralElement>spectrum=new ArrayList<SpectralElement>();
        FastFourierTransformer fft = new FastFourierTransformer();
        
        Complex[] complex = fft.transform(samples);
        
        
        
        for(int i=1; i<complex.length/2;i+=1){
            
            Complex c1  = complex[i]; 
            double imaginary1 = c1.getImaginary();
            double real1 = c1.getReal();
            double amplitude1 =  Math.sqrt( Math.pow(real1, 2) + Math.pow(imaginary1, 2) );
            double phaseShift1 = calculatePhaseShift(imaginary1, real1);
            
            
            Complex c2  = complex[complex.length-i];
            double imaginary2 = c2.getImaginary();
            double real2 = c2.getReal();
            double amplitude2 =  Math.sqrt( Math.pow(real2, 2) + Math.pow(imaginary2, 2) ) ;
            double phaseShift2 = calculatePhaseShift(imaginary2, real2);
            
            
            double amplitude = amplitude1; 
            double phaseShift = phaseShift1; 
            
            if(true){
                
                double frequency = ((double)i)* ( ((double)sampleFrequency)/ ((double)complex.length) );
                                
                //System.out.printf("(%.2f) Amplitude: %.2f, PhaseShift: %.2f%n", frequency, amplitude, (phaseShift/(Math.PI*2))*360);
                                
                SpectralElement spectralElement = new SpectralElement(frequency , amplitude, phaseShift, real1,imaginary1); 
                spectrum.add(spectralElement);
            }
        }
        return spectrum;
    }


public TransformResult analize3(double [] samples){
    
	List<SpectralElement>spectralElements=new ArrayList<SpectralElement>();
    FastFourierTransformer fft = new FastFourierTransformer();
    
    Complex[] complex = fft.transform(samples);
    
    
    
    for(int i=1; i<complex.length/2;i+=1){
        
        Complex c1  = complex[i]; 
        double imaginary1 = c1.getImaginary();
        double real1 = c1.getReal();
        double amplitude1 =  Math.sqrt( Math.pow(real1, 2) + Math.pow(imaginary1, 2) );
        double phaseShift1 = calculatePhaseShift(imaginary1, real1);
        
        
        Complex c2  = complex[complex.length-i];
        double imaginary2 = c2.getImaginary();
        double real2 = c2.getReal();
        double amplitude2 =  Math.sqrt( Math.pow(real2, 2) + Math.pow(imaginary2, 2) ) ;
        double phaseShift2 = calculatePhaseShift(imaginary2, real2);
        
        
        double amplitude = amplitude1; 
        double phaseShift = phaseShift1; 
        
        if(true){
            
            double frequency = ((double)i)* ( ((double)sampleFrequency)/ ((double)complex.length) );
                            
            //System.out.printf("(%.2f) Amplitude: %.2f, PhaseShift: %.2f%n", frequency, amplitude, (phaseShift/(Math.PI*2))*360);
                            
            SpectralElement spectralElement = new SpectralElement(frequency , amplitude, phaseShift, real1,imaginary1); 
            spectralElements.add(spectralElement);
        }
    }
    return new TransformResult(spectralElements, complex);
}
    
    public static void main(String[] args) {
        int sampleFrequency = (int)Math.pow(2, 10); //1000 samples per second
        int valuesInArray = 2000; //2000 samples
        double [] samples = createValues(sampleFrequency, valuesInArray);
        double [] samplesToAnalize = Arrays.copyOf(samples, 2048 );
        
        SpectrumAnalizer sa = new SpectrumAnalizer(sampleFrequency);
        
        List<SpectralElement> spectrum = sa.analize(samplesToAnalize);
        
        for(int i=0; i<spectrum.size(); i++){
            SpectralElement element = spectrum.get(i);
            
            double absAmplitude = element.getAmplitude();
            double frequency = element.getFrequency();
            double phaseShift = element.getPhaseShift();
            double cycleTime = 1/frequency;
            //double relativeAmplitude = (absAmplitude / cycleTime)  * 100 ; 
            
            //System.out.printf("(%.2f) Amplitude: %.2f, PhaseShift: %.2f%n", cycleTime, amplitude, phaseShift);
            if(absAmplitude >= 0.1){
                System.out.printf("%.2f,%.3f,%.2f%n", frequency, absAmplitude, phaseShift);
            }   
        }
        
        List <TwoDimensionalPoint> origSinePoints = new ArrayList();
        for(int i=0; i<samples.length; i++){
            double x = i;            
            double y = samples[i];
            origSinePoints.add(new TwoDimensionalPoint(x, y));
        }
        
        List <TwoDimensionalPoint> compositeSinePoints = new ArrayList();
        for(int i=0; i<samples.length; i++){
            double x = i;            
            double y = getSumOfValues(x, spectrum,sampleFrequency);
            compositeSinePoints.add(new TwoDimensionalPoint(x, y));
        }
        
        LineViewer bv = new LineViewer("");
        bv.addPoints(origSinePoints, Color.blue, "Test Point Series 1", 0.01);
        bv.addPoints(compositeSinePoints, Color.blue, "Test Point Series 2", 0.01);
        
        
        
        bv.pack();
        RefineryUtilities.centerFrameOnScreen(bv);
        bv.setVisible(true);
        
        

    }
    
    public static double getSumOfValues(double x, List<SpectralElement> spectrum, int sampleFrequency) {
        
        double sumOfFrequencies = 0;
        double t = x;
        
        for(int i=0; i<spectrum.size(); i++){
            SpectralElement element = spectrum.get(i);
            
            double absAmplitude = element.getAmplitude();
            double frequency = element.getFrequency();
            double phaseShift = element.getPhaseShift();
            double cycleTime = 1/frequency;
            
            double frequencyValue = absAmplitude * Math.sin(   phaseShift + (t/sampleFrequency * frequency * 2 * Math.PI)   );
            sumOfFrequencies += frequencyValue;
        }
        return sumOfFrequencies;
    }


    private static double[] createValues2(double sampleFrequency, int valuesInArray) {
        // TODO Auto-generated method stub
        //5000 msec
        double amplitude1 = 30;
        double amplitude2 = 30;
        double amplitude3 = 30;
        
        double frequency1 = 1; //50 HZ
        double frequency2 = 4; //120 HZ
        double frequency3 = 12; //360 HZ
        
        //the array contains 1 value each millisecond
        double[]values = new double [valuesInArray]; 
        
        for(int i=0; i<values.length; i++){
            
            //i is in  milliseconds, divide to get time in seconds
            double sampleTime = 1.0D/(double)sampleFrequency;
            double t = (double) i * sampleTime;
            
            double value1 = amplitude1 * Math.sin(   (0.60 + t * frequency1) * (2 * Math.PI)   );
            double value2 = amplitude2 * Math.sin(   (0.60 + t * frequency2) * (2 * Math.PI)   );
            double value3 = amplitude3 * Math.sin(   (0.60 + t * frequency3) * (2 * Math.PI)   );
            
            values[i] = value1 + value2 + value3;
            //values[i] = value1;
            //values[i] = value2;
            
        }
        
        return values;
    }
    
    private double calculatePhaseShift(double imaginary, double real) {
        double phaseShift = Math.atan(Math.abs(real)/Math.abs(imaginary));
        if(real >=0){
            if( imaginary >=0 ){
                return phaseShift;
            }
            else{
                return Math.PI - phaseShift;
            }
        }
        else{
            if( imaginary >=0 ){
                return 2*Math.PI - phaseShift;
            }
            else{
                return Math.PI + phaseShift;
            }
        }        
    }


	public List<SpectralElement> analize(List<Integer> samples) {
		int totalSamplesLog2 = (int) (Math.log(samples.size()) / Math.log(2)) + 1;
		int spacesInArray = (int) Math.pow(2, totalSamplesLog2);
		double [] values = new double [spacesInArray]; 
		for(int i=0; i<samples.size(); i++){
			values[i] = samples.get(i).doubleValue();
		}
		return analize(values);
	}
	
	 public static double getSumOfValues2(double sampleIndex, List<SpectralElement> spectrum, int samplesPerCycle) {
	        
	        
	    	
	    	
	    	double sumOfFrequencies = 0;
	        
	        for(int i=0; i<spectrum.size(); i++){
	            SpectralElement element = spectrum.get(i);
	            
	            double absAmplitude = element.getAmplitude();
	            double frequency = element.getFrequency();
	            double phaseShift = element.getPhaseShift();
	            double cycleTime = 1/frequency;
	            
	            double t = cycleTime * (sampleIndex / samplesPerCycle);
	            double frequencyValue = absAmplitude * 2 * Math.PI * frequency * t; //v = V * 2*PI*f*t
	            sumOfFrequencies += frequencyValue;
//	            double frequencyValue = absAmplitude * Math.sin(   phaseShift + (t/sampleFrequency * frequency * 2 * Math.PI)   );
//	            sumOfFrequencies += frequencyValue;
	        }
	        return sumOfFrequencies;
	    }
	
	 static LineViewer bv = new LineViewer("");
	 static{ 
		 bv.pack();
	     RefineryUtilities.centerFrameOnScreen(bv);
	     bv.setVisible(true);
	 }
	 public static void displaySpectrum(List<SpectralElement> spectrum, double totalDurationToShow, List <TwoDimensionalPoint> origSinePoints) {
	        
			int points = 200;
			double timePerPoint = totalDurationToShow / ((double)points);
			List <TwoDimensionalPoint> compositeSinePoints = null;
			
			if(spectrum != null){
				compositeSinePoints = new ArrayList<TwoDimensionalPoint>();
		        for(int i=1; i<=points; i++){
		        	double t = ((double)i) * timePerPoint;
		            double y = getSumOfValues2(t, spectrum);
		            compositeSinePoints.add(new TwoDimensionalPoint(i-1, y));
		        }
			}
	        
	        if(origSinePoints != null){
	        	bv.addPoints(origSinePoints, Color.red, "Test Point Series 1", 0.01);
	        }
	        
	        if(compositeSinePoints != null){
	        	bv.addPoints(compositeSinePoints, Color.blue, "Test Point Series 2", 0.01);
	        }
	        

	    }
	 
	 
	 	 public static double getSumOfValues2(double t, List<SpectralElement> spectrum) {

		 
	    	double sumOfFrequencies = 0;
	        
	        for(int i=0; i<spectrum.size(); i++){
	            SpectralElement element = spectrum.get(i);
	            
	            double absAmplitude = element.getAmplitude();
	            double frequency = element.getFrequency();
	            double phaseShiftRad =element.getPhaseShift() ;
	            double cycleTime = 1/frequency;
	            
	            double currentCyclicAngleRad = frequency * t * 2 * Math.PI + phaseShiftRad;
	            
	            double currentVerticalValue = absAmplitude * Math.sin(currentCyclicAngleRad);
	            sumOfFrequencies += currentVerticalValue;          
	        }
	        return sumOfFrequencies;
	    }
	 
	 
	 private static double[] createValues(double sampleFrequency, int valuesInArray) {
	        // TODO Auto-generated method stub
	        //5000 msec
	        double amplitude1 = 30;
	        double amplitude2 = 15;
	        double amplitude3 = 7.5;
	        
	        double frequency1 = 1; //50 HZ
	        double frequency2 = 2; //120 HZ
	        double frequency3 = 4; //360 HZ
	        
	        //the array contains 1 value each millisecond
	        double[]values = new double [valuesInArray]; 
	        
	        for(int i=0; i<values.length; i++){
	            
	            //i is in  milliseconds, divide to get time in seconds
	            double sampleTime = 1.0D/(double)sampleFrequency;
	            double t = (double) i * sampleTime;
	            
	            double value1 = amplitude1 * Math.sin(   ( t * frequency1) * (2 * Math.PI)   );
	            double value2 = amplitude2 * Math.sin(   ( t * frequency2) * (2 * Math.PI)   );
	            double value3 = amplitude3 * Math.sin(   ( t * frequency3) * (2 * Math.PI)   );
	            
	            values[i] = value1 + value2 + value3;
	            //values[i] = value1;
	            //values[i] = value2;
	            
	        }
	        
	        return values;
	    }
	 
}

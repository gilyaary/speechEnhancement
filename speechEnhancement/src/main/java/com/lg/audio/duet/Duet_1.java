package com.lg.audio.duet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.lg.audio.base.BaseUtils;
import com.lg.audio.base.HistogramHelper;
import com.lg.audio.base.Mixer;
import com.lg.audio.base.StdAudio88k;
import com.lg.audio.base.TransformResult;

public class Duet_1 {
	
	static final int SAMPLING_RATE = 50000;
	static int windowSize = (int)( 4096 /2 ) ;
	static int skipSize =   (int)( 4096 /2 );
	
	public static final int GAIN = 5;
	static String PATH = "/AudioSamples/";
	//static String file1 = PATH + "GIL_AMIT.wav";
	static String file1 = PATH + "amy.wav";
	static String file2 = PATH + "eric.wav";
	
	public static void main (String [] args){
		Duet_1 duet = new Duet_1();
		duet.analize(file1, file2);
	}

	private void analize(String file1, String file2) {
		
		double[] bothMics = Mixer.mix(file1, file2);
		double[]micR = new double [bothMics.length/2];
		double[]micL = new double [bothMics.length/2];
		micR = Arrays.copyOfRange(micR, 0, 400000);
		micL = Arrays.copyOfRange(micL, 0, 400000);
		
		//separate each channel of the stereo wav file
		BaseUtils.separateMics(bothMics, micR, micL);
		long start = System.currentTimeMillis();
		List<TransformResult[]> spectrums = BaseUtils.extractSpectrums(micR, micL, windowSize, skipSize, SAMPLING_RATE); //STFT
		long end = System.currentTimeMillis();
		System.out.println("FFT ProcessTime/Sec: =" + (end - start));
		
		//here we calculate for each spectral element (FFT Component) the time shift and amplitude difference
		//we then plot the results for all spectral elements on a 3D plot
		HistogramHelper.calculateAndShowHistogram(spectrums, 0, 8000, 1200, 800, true);
		//{ampRatio, timeDiffMicrosec, freq}
		
		//use this predicate to define the samples in each frame as belonging to a cluster 
		Predicate<double[]> separator = args -> {
			
			double ampRatio = args[0];
			int timeDiffMicrosec = (int) args[1];
			int freq = (int)args[2];
			//timeDiffMicrosec > 150 && 
			
			return  ampRatio >= -0.30 && ampRatio <=  -0.1 && timeDiffMicrosec < -50 && timeDiffMicrosec > -200 ; //timeDiffMicrosec > 0;
		};
		
		//play the separated channel
		BaseUtils.play( spectrums, windowSize, skipSize,  micR.length, SAMPLING_RATE, GAIN, separator);
	}
}

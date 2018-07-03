package com.lg.audio.base;

import java.util.Arrays;

public class Mixer {
	public static double[] mix(String file1, String file2) {
		
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
}

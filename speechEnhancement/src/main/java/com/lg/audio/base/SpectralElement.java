package com.lg.audio.base;

public class SpectralElement {
    double frequency;
    double amplitude;
    double phaseShift;
	private double real;
	private double imaginary;
    public double getFrequency() {
        return frequency;
    }
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
    public double getAmplitude() {
        return amplitude;
    }
    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }
    public double getPhaseShift() {
        return phaseShift;
    }
    public void setPhaseShift(double phaseShift) {
        this.phaseShift = phaseShift;
    }
    public SpectralElement(double frequency2, double amplitude, double phaseShift) {
        super();
        this.frequency = frequency2;
        this.amplitude = amplitude;
        this.phaseShift = phaseShift;
    }
	public SpectralElement(double frequency2, double amplitude2, double phaseShift2, double real1, double imaginary1) {
		 super();
	        this.frequency = frequency2;
	        this.amplitude = amplitude2;
	        this.phaseShift = phaseShift2;
	        this.real = real1;
	        this.imaginary = imaginary1;
	}
	public double getReal() {
		return real;
	}
	public void setReal(double real) {
		this.real = real;
	}
	public double getImaginary() {
		return imaginary;
	}
	public void setImaginary(double imaginary) {
		this.imaginary = imaginary;
	}
	
}
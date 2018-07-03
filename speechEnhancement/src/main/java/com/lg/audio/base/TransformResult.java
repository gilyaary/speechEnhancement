package com.lg.audio.base;

import java.util.List;
import org.apache.commons.math.complex.Complex;

public class TransformResult{
	List<SpectralElement> spectralElements;
	Complex[] complex;
	public List<SpectralElement> getSpectralElements() {
		return spectralElements;
	}
	public void setSpectralElements(List<SpectralElement> spectralElements) {
		this.spectralElements = spectralElements;
	}
	public Complex[] getComplex() {
		return complex;
	}
	public void setComplex(Complex[] complex) {
		this.complex = complex;
	}
	public TransformResult(List<SpectralElement> spectralElements, Complex[] complex) {
		super();
		this.spectralElements = spectralElements;
		this.complex = complex;
	}
	
}
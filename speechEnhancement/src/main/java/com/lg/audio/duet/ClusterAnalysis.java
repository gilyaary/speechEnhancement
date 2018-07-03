package com.lg.audio.duet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.complex.Complex;

import com.lg.audio.base.SpectralElement;
import com.lg.audio.base.TransformResult;

import weka.clusterers.Clusterer;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class ClusterAnalysis {
	public static void main(String[] args) throws Exception {
		EM clusterer = new EM();
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("att1", 0));
		attributes.add(new Attribute("att2", 1));
		Instances data = new Instances("Test dataset", attributes, 100);

		addInstance(data, 100, 100);
		addInstance(data, 101, 99);
		addInstance(data, 98, 100);
		addInstance(data, 100, 98);
		addInstance(data, 99, 101);
		addInstance(data, 101, 99);
		addInstance(data, 100, 100);
		addInstance(data, 101, 99);
		addInstance(data, 98, 100);
		addInstance(data, 100, 98);
		addInstance(data, 99, 101);
		addInstance(data, 101, 99);

		addInstance(data, 10, 10);
		addInstance(data, 11, 9);
		addInstance(data, 8, 10);
		addInstance(data, 10, 8);
		addInstance(data, 9, 11);
		addInstance(data, 11, 9);
		addInstance(data, 10, 10);
		addInstance(data, 11, 9);
		addInstance(data, 8, 10);
		addInstance(data, 10, 8);
		addInstance(data, 9, 11);
		addInstance(data, 11, 9);

		clusterer.setNumClusters(2);
		clusterer.buildClusterer(data);

		double[] dist1 = clusterer.distributionForInstance(createInstance(10, 10));
		System.out.printf("%s%n", Arrays.toString(dist1));
		double[] dist2 = clusterer.distributionForInstance(createInstance(101, 99));
		System.out.printf("%s%n", Arrays.toString(dist2));
		double[] dist3 = clusterer.distributionForInstance(createInstance(55, 55));
		System.out.printf("%s%n", Arrays.toString(dist3));

	}

	private static void addInstance(Instances data, double x, double y) {
		Instance i1 = createInstance(x, y);
		data.add(i1);
	}

	private static Instance createInstance(double x, double y) {
		Instance i1 = new SparseInstance(2);
		i1.setValue(0, x);
		i1.setValue(1, y);
		// i1.setWeight(weight);
		return i1;
	}

	
	
	public Instances processHistogram(List<TransformResult[]> allTransformResults, double minFreq,
			double maxFreq) {

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("AmplitudeRatio", 0));
		attributes.add(new Attribute("TimeDiff", 1));
		Instances instances = new Instances("LeftRightRatios", attributes, 1000000);

		for (TransformResult[] transformResults : allTransformResults) {
			Instance instance = new SparseInstance(2);
			List<SpectralElement> spectralElementsR = transformResults[0].getSpectralElements();
			List<SpectralElement> spectralElementsL = transformResults[1].getSpectralElements();
			Complex[] complexR = transformResults[0].getComplex();
			Complex[] complexL = transformResults[1].getComplex();
			for (int i = 0; i < spectralElementsR.size(); i++) {
				double ampR = Double.MIN_VALUE + Math.sqrt(complexR[i].getReal() * complexR[i].getReal()
						+ complexR[i].getImaginary() * complexR[i].getImaginary());
				double ampL = Double.MIN_VALUE + Math.sqrt(complexL[i].getReal() * complexL[i].getReal()
						+ complexL[i].getImaginary() * complexL[i].getImaginary());
				double ampRatio = Math.abs(ampR / ampL) - Math.abs(ampL / ampR);
				int freq = (int) spectralElementsR.get(i).getFrequency();
				double angle = (complexL[i].divide(complexR[i])).log().getImaginary();
				angle *= -1;
				double cycleTime = 1D / (double) freq;
				double timeDiff = (angle / (2 * Math.PI)) * cycleTime;
				int relativeTimeDiff = (int) (timeDiff * 1000000);
				if (freq >= minFreq && freq <= maxFreq) {
					double weight = Math.abs(ampR * ampL);
					// REPORT: ampRatio, relativeTimeDiff, weight
					instance.setValue(0, ampRatio);
					instance.setValue(1, relativeTimeDiff);
					instance.setWeight(weight);
					instances.add(instance);
				}
			}
		}
		return instances;
	}
	
	public Clusterer em(Instances instances, int numberOfClusters) throws Exception{
		EM clusterer = new EM();
		clusterer.setNumClusters(numberOfClusters);
		clusterer.buildClusterer(instances);
		return clusterer;
	}
	
	public Clusterer kmeans(Instances instances, int numberOfClusters) throws Exception{
		SimpleKMeans clusterer = new SimpleKMeans();
		clusterer.setNumClusters(numberOfClusters);
		clusterer.buildClusterer(instances);
		clusterer.setSeed(1);
		return clusterer;
	}
}

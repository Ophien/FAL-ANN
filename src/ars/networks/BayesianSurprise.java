/*
 * Copyright(C) 2018 Alysson Ribeiro da Silva
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.If not, see<https://www.gnu.org/licenses/>.
 *
*/
package ars.networks;

import java.util.ArrayList;
import java.util.List;

public class BayesianSurprise {
	// -------------------------------------------------------------------------------------------------------
	private double[] varianceVector;
	private double[] sumVector;
	private double[] meanVector;

	private boolean useScale;
	
	public void setScaling(boolean value){
		useScale = value;
	}
	
	private List<double[]> bayesMatrix;

	private int bayesFeatureSize;
	private double elementInBase;
	// -------------------------------------------------------------------------------------------------------
	public BayesianSurprise(int featureSize) {
		bayesFeatureSize = featureSize;
		sumVector = new double[featureSize];
		meanVector = new double[featureSize];
		varianceVector = new double[featureSize];
		bayesMatrix = new ArrayList<>();
		elementInBase = 0;
	}
	// -------------------------------------------------------------------------------------------------------
	public void insertIntoMem(double[] stimulus) {
		bayesMatrix.add(stimulus);
		elementInBase += 1;
		CalculateStatistics(stimulus);
	}
	// -------------------------------------------------------------------------------------------------------
	public double calculateSurprise(double[] input, boolean updateBayes) {
		double surprise = 0.0;
		double N = 1.0;

		double[] surpriseVector = new double[varianceVector.length];
		
		for (int i = 0; i < varianceVector.length; i++) {
			double scalingTerm = N / (0.001 + (2.0 * varianceVector[i]));
			double surp = varianceVector[i] + Math.pow((input[i] - meanVector[i]), 2.0);
			
			if(useScale)
				surpriseVector[i] = scalingTerm	* surp;
			else{
				surpriseVector[i] = surp;	
			}
			
			surprise += surpriseVector[i];
		}

		if (updateBayes) {
			bayesMatrix.add(input);
			elementInBase += 1;
			CalculateStatistics(input);
		}

		return surprise;
	}
	// -------------------------------------------------------------------------------------------------------
	public double CalculateSurpriseSingleFeature(int feature, double[] input, int N, boolean updateBayes) {
		double surprise = 0.0;
		
		if (varianceVector[feature] > 0.0)
			surprise = (N / (2 * varianceVector[feature])) 
			* (varianceVector[feature] + Math.pow((input[feature] - meanVector[feature]), 2.0));

		if (updateBayes) {
			bayesMatrix.add(input);
			elementInBase += 1;
			CalculateStatistics(input);
		}

		return surprise;
	}
	// -------------------------------------------------------------------------------------------------------
	private void CalculateStatistics(double[] input) {
		// Sum
		for (int element = 0; element < bayesFeatureSize; element++)
			sumVector[element] += input[element];

		// Mean
		System.arraycopy(sumVector, 0, meanVector, 0, bayesFeatureSize);

		for (int i = 0; i < bayesFeatureSize; i++)
			meanVector[i] /= elementInBase;

		// Variance
		for (int line = 0; line < bayesMatrix.size(); line++)
			for (int element = 0; element < bayesFeatureSize; element++)
				varianceVector[element] += Math.pow(bayesMatrix.get(line)[element] - meanVector[element], 2.0);
		for (int element = 0; element < bayesFeatureSize; element++)
			varianceVector[element] /= elementInBase;
	}
	// -------------------------------------------------------------------------------------------------------
}

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

package ars.featureselection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FeatureVectorScaler {
	// -------------------------------------------------------------------------------------------------------
	int inputSize;
	double[] max;
	double[] min;
	// -------------------------------------------------------------------------------------------------------
	double clamp(double value, double min, double max) {
		if (value < min)
			value = min;
		if (value > max)
			value = max;
		return value;
	}
	// -------------------------------------------------------------------------------------------------------
	public FeatureVectorScaler(int inputSize){
		this.inputSize = inputSize;
		
		max = new double[inputSize];
		min = new double[inputSize];
		
		for(int i = 0; i < inputSize; i++){
			max[i] = Double.NEGATIVE_INFINITY;
			min[i] = Double.POSITIVE_INFINITY;
		}
	}
	// -------------------------------------------------------------------------------------------------------
	public void updateFeatures(double[] stimulus){
		for(int i = 0; i < inputSize; i++){
			if(stimulus[i] > max[i])
				max[i] = stimulus[i];
			if(stimulus[i] < min[i])
				min[i] = stimulus[i];
		}
	}
	// -------------------------------------------------------------------------------------------------------
	public void featureScaleStimulus(double[] stimulus) {
		for (int i = 0; i < stimulus.length; i++) {
			stimulus[i] = clamp(stimulus[i], min[i], max[i]);

			double numerator = stimulus[i] - min[i];
			if (numerator <= 0.0)
				stimulus[i] = 0.0;
			else {
				double norm = (stimulus[i] - min[i]) / (max[i] - min[i]);
				stimulus[i] = norm;
			}
		}
	}
	// -------------------------------------------------------------------------------------------------------
	public void writeMaxMin(String file) {
		try {
			File f = new File(file);
			BufferedWriter b = new BufferedWriter(new FileWriter(f));

			for (int i = 0; i < inputSize - 1; i++)
				b.write(Double.toString(max[i]) + " ");
			b.write(Double.toString(max[inputSize - 1]) + "\n");
			for (int i = 0; i < inputSize - 1; i++)
				b.write(Double.toString(min[i]) + " ");
			b.write(Double.toString(min[inputSize - 1]));

			b.close();
		} catch (Exception E) {

		}
	}
	// -------------------------------------------------------------------------------------------------------
	public void readMaxMin(String file) {
		try {
			File f = new File(file);
			BufferedReader b = new BufferedReader(new FileReader(f));

			String[] maxS = b.readLine().split(" ");
			String[] minS = b.readLine().split(" ");

			for (int i = 0; i < maxS.length; i++) {
				max[i] = Double.parseDouble(maxS[i]);
				min[i] = Double.parseDouble(minS[i]);
			}

			b.close();
		} catch (Exception E) {

		}
	}
	// -------------------------------------------------------------------------------------------------------
}

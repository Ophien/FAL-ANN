/*MIT License
*
*Copyright (c) 2018 Alysson Ribeiro da Silva
*
*Permission is hereby granted, free of charge, to any person obtaining a copy 
*of this software and associated documentation files (the "Software"), to deal 
*in the Software without restriction, including *without limitation the rights 
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
*copies of the Software, and to permit persons to whom the Software is furnished 
*to do so, subject *to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
*EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. *IN NO EVENT SHALL THE AUTHORS 
*OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
*AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH 
*THE *SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

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
public class GPUWrapper {
	static {
		System.loadLibrary("ARS_FFS");
	}
	
	private static GPUWrapper _singleton;

	public static GPUWrapper Singleton(){
		if(_singleton == null) _singleton = new GPUWrapper();
		return _singleton;
	}
	
	public native double[] prediction(double[] darray);

	public native void loadConfig(String file);

	public native void loadNetwork(String file);

	public native void saveNetwork(String file);
	
	public native void setLearning(boolean bool);
	
	public native int getNeuronCount();
	
	// MLP JNI para utilizar a nova estrutura
	
	public native double MLPprediction(double[] darray);
	
	public native void MLPloadConfig(String file);
	
	public native int MLPgetNeuronCount();
	
	public native void MLPsetLearning(boolean bool);
	
	// Action explorer
	
	public native void insertNewFeatureSet(String featureSetName, int featureSize);
	public native void insertFeatureIntoFeatureSet(String featureSetName, double[] input, int featureSize);
	public native void calculatedStatistics(String featureSetName);
	public native void saveFeatures(String featureSetName, String fileName);
	public native void loadFeatures(String featureSetName, String fileName);
	public native void  selectFeatures(String featureSetName);
	
	public native double[] getFeaturesPositiveMean(String featureSetName, double[] darray);
	public native double[] getFeaturesVarianceMeanThreshold(String featureSetName, double[] darray);
	public native double[] getFeaturesTopKVarianceReturn(String featureSetName, double[] darray);
	public native double[] getFeaturesVariationPercentage(String featureSetName, double[] darray);
}

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

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

package ars.datastructures;

import ars.datastructures.ChannelDescription;
import ars.featureselection.EnvironmentBuilder;

public class ChannelDescription {
	public EnvironmentBuilder[] featureBuilders;
	public int[] featuresSizes;
	public int[] fieldsClass;
	public boolean[] adaptiveVigilanceRaising;
	public boolean[] activeFields;
	public int[] temperatureOp;
	public int[] learningOp;
	public double[] learningRate;
	public double[] learnVigilances;
	public double[] performVigilances;
	public double[] gammas;
	public double[] alphas;
	public int bayesSize;
	
	public boolean fuzzyReadout = false;
	public boolean resonanceToPredict = false;
	public boolean useDirectReward = false;
	public boolean useQLearning = true;
	public int totalFields;
	
	public double[][] generateFullStimulus(){
		double[][] fullStimulus = new double[totalFields][];
		for(int field = 0; field < totalFields; field++)
			fullStimulus[field] = featureBuilders[field].buildEnvironment();
		return fullStimulus;
	}
	
	public String getStimulusKey(){
		String key = "";
		for(int field = 0; field < totalFields - 1; field++)
			key += featureBuilders[field].generateFieldKey() + "|";
		key += featureBuilders[totalFields - 1].generateFieldKey();
		return key;
	}

	public ChannelDescription(EnvironmentBuilder[] featureBuilders, 
			int[] featureSizes, int[] fieldsClass, boolean[] adaptiveVigilanceRaising, 
			boolean[] activeFields, int[] temperatureOp, int[] learningOp, 
			double[] learningRate, double[] learningVigilances, double[] performVigilances,
			double[] gammas, double[] alphas, boolean fuzzyReadout,
			boolean resonanceToPredict, boolean useDirectReward, boolean useQLearning) {
		
		this.featureBuilders = featureBuilders;
		this.featuresSizes = featureSizes;
		this.fieldsClass = fieldsClass;
		this.adaptiveVigilanceRaising = adaptiveVigilanceRaising;
		this.activeFields = activeFields;
		this.temperatureOp = temperatureOp;
		this.learningOp = learningOp;
		this.learningRate = learningRate;
		this.learnVigilances = learningVigilances;
		this.performVigilances = performVigilances;
		this.gammas = gammas;
		this.alphas = alphas;
		
		this.fuzzyReadout = fuzzyReadout;
		this.resonanceToPredict = resonanceToPredict;
		this.useDirectReward = useDirectReward;
		this.useQLearning = useQLearning;
		
		totalFields = featureSizes.length;
	}
}

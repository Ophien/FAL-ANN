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
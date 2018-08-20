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
package ars.minefield.bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ars.datastructures.ChannelDescription;
import ars.datastructures.Pair;
import ars.datastructures.UAMANN;
import ars.networks.AdaptiveNeuralSystem;
import statistics.StatisticsHandler;

public class Program {
	// -------------------------------------------------------------------------------------------------------
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static AdaptiveNeuralSystem activeNet = null;

	public void FALCON_ARAM(int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines, 
			double[][] successRate,
			double[][] iter,
			double[][] size,
			double[][] time) {
		// data structure to hold results
		double[] performanceResults = new double[trials / trialSize];
		
		// data structure to hold the iterations spent by the agent
		double[] iterationResults = new double[trials / trialSize];
		
		// data structure to hold the network size
		double[] networkSize = new double[trials / trialSize];
		
		// data structure to hold the execution time
		double[] executionTime = new double[trials / trialSize];
		
		// TODO code application logic here
		int[] featuresSizes = new int[] { 5 + 8, 5, 2 };
		boolean[] vr = new boolean[] { true, false, false };
		boolean[] activeFields = new boolean[] { true, true, true };
		int[] temperatureOp = new int[] { 1, 1, 2 };
		int[] learningOp = new int[] { 1, 1, 2 };
		int[] fieldsClass = new int[] { AdaptiveNeuralSystem.STATE, AdaptiveNeuralSystem.ACTION,
				AdaptiveNeuralSystem.REWARD };
		double[] learningRate = new double[] { 1.0, 1.0, 1.0 };
		double[] learnVigilances = new double[] { 0.2, 0.2, 0.5 };
		double[] performVigilances = new double[] { 0.0, 0.0, 0.0 };
		double[] gammas = new double[] { 0.5, 0.5, 0.0 };
		double[] alphas = new double[] { 0.1, 0.1, 0.1 };
		boolean fuzzyReadout = false;
		boolean resonanceToPredict = false;
		boolean useDirectReward = false;
		boolean useQLearning = true;
		AdaptiveNeuralSystem net = new AdaptiveNeuralSystem(featuresSizes, vr, activeFields, temperatureOp, learningOp,
				fieldsClass, learningRate, learnVigilances, performVigilances, gammas, alphas, fuzzyReadout,
				resonanceToPredict, useDirectReward, useQLearning, false);
		activeNet = net;

		double hits = 0.0;
		double cumulativeHits = 0.0;
		double counter = 0.0;
		double iterCounter = 0.0;
		double sizeCounter = 0.0;
		double timeCounter = 0.0;
		int totalSims = trials;
		int currentEpoch = 0;
		for (int i = 1; i <= totalSims; i++) {
			MineFieldSimulator mfs = new MineFieldSimulator(baseStepSize, baseSize, baseSize, baseTotalMines);
			Agent agent = new Agent(5, mfs);
			agent.setBrain(net);
			mfs.setAgent(agent);
			
			// spended iterations
			Integer[] iterations = new Integer[1];
			
			// compute the simulation time here
			
			long startTime = System.currentTimeMillis();
			mfs.simulateAll(false, false, iterations);
			long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    
			//mfs.simulateAll(i > totalSims - 20, false);

			if (mfs.success()) {
				hits += 1.0;
				cumulativeHits += 1.0;
			}
			
			sizeCounter += net.getNeuronsSize();
			iterCounter += iterations[0];
			timeCounter += elapsedTime;

			counter += 1.0;
			if (i > 0 && i % trialSize == 0) {
				double stepAccuracy = hits / counter;
				performanceResults[currentEpoch] = stepAccuracy;
				currentEpoch++;
				System.out.println("Accuracy: " + stepAccuracy);

				// set variables
				performanceResults[currentEpoch] = hits / counter;
				
				// set iterations as a result
				iterationResults[currentEpoch] = (double)iterCounter / counter;
				
				// set network size
				networkSize[currentEpoch] = (double)sizeCounter / counter;
				
				// set execution time for this simulation
				executionTime[currentEpoch] = (double)timeCounter / counter;
				
				System.out.println("Accuracy: " + performanceResults[currentEpoch]);
				currentEpoch++;
				counter = 0.0;
				hits = 0.0;
				iterCounter = 0.0;
				sizeCounter = 0.0;
				timeCounter = 0.0;
			}

			net.updateEpsilon();
		}
		
		System.out.println("Cumulative accuracy: " + cumulativeHits / totalSims);
		
		// set parameters reference
 		successRate[0] = performanceResults;
		iter[0] = iterationResults;
		size[0] = networkSize;
		time[0] = executionTime;
	}

	public void FALCON_UAM(int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines,
			double[][] successRate,
			double[][] iter,
			double[][] size1,
			double[][] size2,
			double[][] size3,
			double[][] time) {
		// data structure to hold results
		double[] performanceResults = new double[trials / trialSize];
		
		// data structure to hold the iterations spent by the agent
		double[] iterationResults = new double[trials / trialSize];
		
		// data structure to hold the network size
		double[] networkSize1 = new double[trials / trialSize];
		
		// data structure to hold the network size
		double[] networkSize2 = new double[trials / trialSize];
		
		// data structure to hold the network size
		double[] networkSize3 = new double[trials / trialSize];
		
		// data structure to hold the execution time
		double[] executionTime = new double[trials / trialSize];

		UAMANN BRAIN = new UAMANN();
		int[] featuresSizes = new int[] { 5 };
		boolean[] adaptiveVigilanceRaising = new boolean[] { true };
		boolean[] activeFields = new boolean[] { true };
		int[] temperatureOp = new int[] { 1 };
		int[] learningOp = new int[] { 2 };
		int[] fieldsClass = new int[] { AdaptiveNeuralSystem.STATE };
		double[] learningRate = new double[] { 1.0 };
		double[] learningVigilances = new double[] { 1.0 };
		double[] performVigilances = new double[] { 1.0 };
		double[] gammas = new double[] { 1.0 };
		double[] alphas = new double[] { 0.1 };
		boolean fuzzyReadout = false;
		boolean resonanceToPredict = true;
		boolean useDirectReward = false;
		boolean useQLearning = true;

		ChannelDescription actionChannelConfig = new ChannelDescription(null, featuresSizes, fieldsClass,
				adaptiveVigilanceRaising, activeFields, temperatureOp, learningOp, learningRate, learningVigilances,
				performVigilances, gammas, alphas, fuzzyReadout, resonanceToPredict, useDirectReward, useQLearning);

		int[] featuresSizesB = new int[] { 5 };
		boolean[] adaptiveVigilanceRaisingB = new boolean[] { true };
		boolean[] activeFieldsB = new boolean[] { true };
		int[] temperatureOpB = new int[] { 1 };
		int[] learningOpB = new int[] { 2 };
		int[] fieldsClassB = new int[] { AdaptiveNeuralSystem.STATE };
		double[] learningRateB = new double[] { 0.1 };
		double[] learningVigilancesB = new double[] { 0.2 };
		double[] performVigilancesB = new double[] { 0.0 };
		double[] gammasB = new double[] { 1.0 };
		double[] alphasB = new double[] { 0.1 };
		boolean fuzzyReadoutB = false;
		boolean resonanceToPredictB = false;
		boolean useDirectRewardB = true;
		boolean useQLearningB = true;

		ChannelDescription environmentChannel = new ChannelDescription(null, featuresSizesB, fieldsClassB,
				adaptiveVigilanceRaisingB, activeFieldsB, temperatureOpB, learningOpB, learningRateB,
				learningVigilancesB, performVigilancesB, gammasB, alphasB, fuzzyReadoutB, resonanceToPredictB,
				useDirectRewardB, useQLearningB);

		int[] featuresSizesC = new int[] { 8, 2 };
		boolean[] adaptiveVigilanceRaisingC = new boolean[] { true, true };
		boolean[] activeFieldsC = new boolean[] { true, true };
		int[] temperatureOpC = new int[] { 1, 2 };
		int[] learningOpC = new int[] { 2, 2 };
		int[] fieldsClassC = new int[] { AdaptiveNeuralSystem.STATE, AdaptiveNeuralSystem.REWARD };
		double[] learningRateC = new double[] { 1.0, 0.1 };
		double[] learningVigilancesC = new double[] { 1.0, 0.0 };
		double[] performVigilancesC = new double[] { 1.0, 0.0 };
		double[] gammasC = new double[] { 1.0, 0.0 };
		double[] alphasC = new double[] { 0.1, 0.1 };
		boolean fuzzyReadoutC = false;
		boolean resonanceToPredictC = true;
		boolean useDirectRewardC = true;
		boolean useQLearningC = true;

		ChannelDescription dirChannel = new ChannelDescription(null, featuresSizesC, fieldsClassC,
				adaptiveVigilanceRaisingC, activeFieldsC, temperatureOpC, learningOpC, learningRateC,
				learningVigilancesC, performVigilancesC, gammasC, alphasC, fuzzyReadoutC, resonanceToPredictC,
				useDirectRewardC, useQLearningC);

		BRAIN.insertChannel(actionChannelConfig);
		BRAIN.insertChannel(environmentChannel);
		BRAIN.insertChannel(dirChannel);
		BRAIN.initialize();

		UAM_Agent agent = new UAM_Agent(5);
		agent.setBrain(BRAIN);

		double hits = 0.0;
		double cumulativeHits = 0.0;
		double counter = 0.0;
		int totalSims = trials;
		
		// variables to check the performance
		Integer[] iterations = new Integer[1];
		int iterCounter = 0;
		long elapsedTime = 0;
		double sizeCounter1 = 0;
		double sizeCounter2 = 0;
		double sizeCounter3 = 0;
		
		int currentEpoch = 0;
		for (int i = 1; i <= totalSims; i++) {
			UAMMineFieldSimulator mfs = new UAMMineFieldSimulator(baseStepSize, baseSize, baseSize, baseTotalMines);
			mfs.setAgent(agent);

			long startTime = System.currentTimeMillis();
			mfs.simulateAll(false, false, iterations);
			long stopTime = System.currentTimeMillis();
		    elapsedTime += stopTime - startTime;
			// mfs.simulateAll(i > totalSims - 20, false);
		    
		    iterCounter += iterations[0];

			HashMap<Integer,Pair>[] sizes = new HashMap[1];
			BRAIN.getAverageNeuronSize(sizes);
			sizeCounter1 += (double)sizes[0].get(0).first / (double)sizes[0].get(0).second;
			sizeCounter2 += (double)sizes[0].get(1).first / (double)sizes[0].get(1).second;
			sizeCounter3 += (double)sizes[0].get(2).first / (double)sizes[0].get(2).second;
		    
			if (mfs.success()) {
				hits += 1.0;
				cumulativeHits += 1.0;
			}

			// always increment counter
			counter += 1.0;
			if (i > 0 && i % trialSize == 0) {
				// set variables
				performanceResults[currentEpoch] = hits / counter;
				
				// set iterations as a result
				iterationResults[currentEpoch] = (double)iterCounter / counter;
				
				// set network size
				networkSize1[currentEpoch] = (double)sizeCounter1 / counter;
				networkSize2[currentEpoch] = (double)sizeCounter2 / counter;
				networkSize3[currentEpoch] = (double)sizeCounter3 / counter;
				
				// set execution time for this simulation
				executionTime[currentEpoch] = (double)elapsedTime / counter;
				
				System.out.println("Accuracy: " + performanceResults[currentEpoch]);
				currentEpoch++;
				counter = 0.0;
				hits = 0.0;
				iterCounter = 0;
				sizeCounter1 = 0;
				sizeCounter2 = 0;
				sizeCounter3 = 0;
				elapsedTime = 0;
			}

			BRAIN.getRoot().updateEpsilon();
		}
		
		System.out.println("Cumulative accuracy: " + cumulativeHits / totalSims);
		
		// set output parameters
 		successRate[0] = performanceResults;
		iter[0] = iterationResults;
		size1[0] = networkSize1;
		size2[0] = networkSize2;
		size3[0] = networkSize3;
		time[0] = executionTime;
	}
	
	public void FALCON_UAM_TWOWAY(int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines,
			double[][] successRate,
			double[][] iter,
			double[][] size1,
			double[][] size2,
			double[][] time) {
		// data structure to hold results
		double[] performanceResults = new double[trials / trialSize];
		
		// data structure to hold the iterations spent by the agent
		double[] iterationResults = new double[trials / trialSize];
		
		// data structure to hold the network size
		double[] networkSize1 = new double[trials / trialSize];
		double[] networkSize2 = new double[trials / trialSize];
		
		// data structure to hold the execution time
		double[] executionTime = new double[trials / trialSize];

		UAMANN BRAIN = new UAMANN();
		int[] featuresSizes = new int[] { 5 };
		boolean[] adaptiveVigilanceRaising = new boolean[] { true };
		boolean[] activeFields = new boolean[] { true };
		int[] temperatureOp = new int[] { 1 };
		int[] learningOp = new int[] { 2 };
		int[] fieldsClass = new int[] { AdaptiveNeuralSystem.STATE };
		double[] learningRate = new double[] { 1.0 };
		double[] learningVigilances = new double[] { 1.0 };
		double[] performVigilances = new double[] { 1.0 };
		double[] gammas = new double[] { 1.0 };
		double[] alphas = new double[] { 0.1 };
		boolean fuzzyReadout = false;
		boolean resonanceToPredict = true;
		boolean useDirectReward = false;
		boolean useQLearning = true;

		ChannelDescription actionChannelConfig = new ChannelDescription(null, featuresSizes, fieldsClass,
				adaptiveVigilanceRaising, activeFields, temperatureOp, learningOp, learningRate, learningVigilances,
				performVigilances, gammas, alphas, fuzzyReadout, resonanceToPredict, useDirectReward, useQLearning);

		int[] featuresSizesC = new int[] { 5 + 8, 2 };
		boolean[] adaptiveVigilanceRaisingC = new boolean[] { true, true };
		boolean[] activeFieldsC = new boolean[] { true, true };
		int[] temperatureOpC = new int[] { 1, 2 };
		int[] learningOpC = new int[] { 1, 2 };
		int[] fieldsClassC = new int[] { AdaptiveNeuralSystem.STATE, AdaptiveNeuralSystem.REWARD };
		double[] learningRateC = new double[] { 1.0, 0.1 };
		double[] learningVigilancesC = new double[] { 0.2, 0.0 };
		double[] performVigilancesC = new double[] { 0.0, 0.0 };
		double[] gammasC = new double[] { 1.0, 0.0 };
		double[] alphasC = new double[] { 0.1, 0.1 };
		boolean fuzzyReadoutC = false;
		boolean resonanceToPredictC = true;
		boolean useDirectRewardC = true;
		boolean useQLearningC = true;

		ChannelDescription dirChannel = new ChannelDescription(null, featuresSizesC, fieldsClassC,
				adaptiveVigilanceRaisingC, activeFieldsC, temperatureOpC, learningOpC, learningRateC,
				learningVigilancesC, performVigilancesC, gammasC, alphasC, fuzzyReadoutC, resonanceToPredictC,
				useDirectRewardC, useQLearningC);

		BRAIN.insertChannel(actionChannelConfig);
		BRAIN.insertChannel(dirChannel);
		BRAIN.initialize();

		UAM_Agent agent = new UAM_Agent(5, true);
		agent.setBrain(BRAIN);

		double hits = 0.0;
		double cumulativeHits = 0.0;
		double counter = 0.0;
		int totalSims = trials;
		
		// variables to check the performance
		Integer[] iterations = new Integer[1];
		int iterCounter = 0;
		long elapsedTime = 0;
		double sizeCounter1 = 0;
		double sizeCounter2 = 0;
		double sizeCounter3 = 0;

		int currentEpoch = 0;
		for (int i = 1; i <= totalSims; i++) {
			UAMMineFieldSimulator mfs = new UAMMineFieldSimulator(baseStepSize, baseSize, baseSize, baseTotalMines);
			mfs.setAgent(agent);

			long startTime = System.currentTimeMillis();
			mfs.simulateAll(false, false, iterations);
			long stopTime = System.currentTimeMillis();
		    elapsedTime += stopTime - startTime;
			// mfs.simulateAll(i > totalSims - 20, false);
		    
		    iterCounter += iterations[0];

			HashMap<Integer,Pair>[] sizes = new HashMap[1];
			BRAIN.getAverageNeuronSize(sizes);
			sizeCounter1 += (double)sizes[0].get(0).first / (double)sizes[0].get(0).second;
			sizeCounter2 += (double)sizes[0].get(1).first / (double)sizes[0].get(1).second;
		    
			if (mfs.success()) {
				hits += 1.0;
				cumulativeHits += 1.0;
			}

			// always increment counter
			counter += 1.0;
			if (i > 0 && i % trialSize == 0) {
				// set variables
				performanceResults[currentEpoch] = hits / counter;
				
				// set iterations as a result
				iterationResults[currentEpoch] = (double)iterCounter / counter;
				
				// set network size
				networkSize1[currentEpoch] = (double)sizeCounter1 / counter;
				networkSize2[currentEpoch] = (double)sizeCounter2 / counter;
				
				// set execution time for this simulation
				executionTime[currentEpoch] = (double)elapsedTime / counter;
				
				System.out.println("Accuracy: " + performanceResults[currentEpoch]);
				currentEpoch++;
				counter = 0.0;
				hits = 0.0;
				iterCounter = 0;
				sizeCounter1 = 0;
				sizeCounter2 = 0;
				sizeCounter3 = 0;
				elapsedTime = 0;
			}

			BRAIN.getRoot().updateEpsilon();
		}
		
		System.out.println("Cumulative accuracy: " + cumulativeHits / totalSims);

		// set output parameters
 		successRate[0] = performanceResults;
		iter[0] = iterationResults;
		size1[0] = networkSize1;
		size2[0] = networkSize2;
		time[0] = executionTime;
	}


	// -------------------------------------------------------------------------------------------------------
	public void performUAMTests(int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines) {
		List<double[]> performance = new ArrayList<>();
		
		int total_tests = 10;

		// baseline tests
		for (int i = 0; i < total_tests; i++) {
			//double[] locPerf = FALCON_UAM(trials, trialSize, baseSize, baseStepSize, baseTotalMines);
			//performance.add(locPerf);
		}

		// write results
		new StatisticsHandler(performance,"UAM_TEST base.csv"); 
		
		// afterwards tests
		double minesPercentage = 0.1;
		baseTotalMines = (int) (baseSize * baseSize * minesPercentage);
		for (int i = 0; i < 4; i++) {
			performance.clear();

			int totalSteps = baseStepSize * baseTotalMines / 10;
			
			// baseline tests
			for (int j = 0; j < total_tests; j++) {
				//double[] locPerf = FALCON_UAM(trials, trialSize, baseSize, totalSteps, baseTotalMines);
				//performance.add(locPerf);
			}

			// write results
			new StatisticsHandler(performance,"UAM_TEST " + Integer.toString(i) + " Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(totalSteps) + ".csv");
			
			minesPercentage += 0.1;
			baseTotalMines = (int) (baseSize * baseSize * minesPercentage);
		}
	}
	
	public void performARAMSimple(String outputFilePrefix, int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines, int totalTests){
		List<double[]> performance = new ArrayList<>();
		List<double[]> iterations = new ArrayList<>();
		List<double[]> netSize = new ArrayList<>();
		List<double[]> time = new ArrayList<>();

		// baseline tests
		for (int i = 0; i < totalTests; i++) {
			double[][] locPerf = new double[1][];
			double[][] locIter = new double[1][];
			double[][] locSize = new double[1][];
			double[][] locTime = new double[1][];
			
			FALCON_ARAM(trials, trialSize, baseSize, baseStepSize, baseTotalMines, locPerf, locIter, locSize, locTime);
			
			performance.add(locPerf[0]);
			iterations.add(locIter[0]);
			netSize.add(locSize[0]);
			time.add(locTime[0]);
		}

		// write results
		new StatisticsHandler(performance, outputFilePrefix + " Performance Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv"); 
		new StatisticsHandler(iterations, outputFilePrefix + " Iterations Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv"); 
		new StatisticsHandler(netSize, outputFilePrefix + " Netsize Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv");
		new StatisticsHandler(time, outputFilePrefix + " Time Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv");
	}
	
	public void performUAMSimple(String outputPrefix, int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines, int totalTests, boolean twoWay){
		List<double[]> performance = new ArrayList<>();
		List<double[]> iterations = new ArrayList<>();
		List<double[]> netSize1 = new ArrayList<>();
		List<double[]> netSize2 = new ArrayList<>();
		List<double[]> netSize3 = new ArrayList<>();
		List<double[]> time = new ArrayList<>();

		// baseline tests
		for (int i = 0; i < totalTests; i++) {
			double[][] locPerf = new double[1][];
			double[][] locIter = new double[1][];
			double[][] locSize1 = new double[1][];
			double[][] locSize2 = new double[1][];
			double[][] locSize3 = new double[1][];
			double[][] locTime = new double[1][];
			
			if(twoWay) {
				FALCON_UAM_TWOWAY(trials, trialSize, baseSize, baseStepSize, baseTotalMines,locPerf,locIter,locSize1,locSize2,locTime);
			} else {
				FALCON_UAM(trials, trialSize, baseSize, baseStepSize, baseTotalMines,locPerf,locIter,locSize1,locSize2,locSize3,locTime);
				netSize3.add(locSize3[0]);
			}
			
			performance.add(locPerf[0]);
			iterations.add(locIter[0]);
			netSize1.add(locSize1[0]);
			netSize2.add(locSize2[0]);
			time.add(locTime[0]);
		}

		// write results
		new StatisticsHandler(performance, outputPrefix + " Performance Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv"); 
		new StatisticsHandler(iterations, outputPrefix + " Iterations Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv"); 
		new StatisticsHandler(netSize1, outputPrefix + " Netsize1 Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv");
		new StatisticsHandler(netSize2, outputPrefix + " Netsize2 Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv");
		
		if(!twoWay)
			new StatisticsHandler(netSize3, outputPrefix + " Netsize3 Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv");
		
		new StatisticsHandler(time, outputPrefix + " Time Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(baseStepSize) + ".csv");}

	// -------------------------------------------------------------------------------------------------------
	public void performanceFalconTests(int trials, int trialSize, int baseSize, int baseStepSize, int baseTotalMines, int totalTests) {
		List<double[]> performance = new ArrayList<>();
		
		int total_tests = totalTests;

		// baseline tests
		for (int i = 0; i < total_tests; i++) {
			//double[] locPerf = FALCON_ARAM(trials, trialSize, baseSize, baseStepSize, baseTotalMines);
			//performance.add(locPerf);
		}

		// write results
		new StatisticsHandler(performance,"ARAM_TEST base.csv"); 
		
		// afterwards tests
		double minesPercentage = 0.1;
		baseTotalMines = (int) (baseSize * baseSize * minesPercentage);
		for (int i = 0; i < 4; i++) {
			performance.clear();

			int totalSteps = baseStepSize * baseTotalMines / 10;
			
			// baseline tests
			for (int j = 0; j < total_tests; j++) {
				//double[] locPerf = FALCON_ARAM(trials, trialSize, baseSize, totalSteps, baseTotalMines);
				//performance.add(locPerf);
			}
			
			new StatisticsHandler(performance,"ARAM_TEST " + Integer.toString(i) + " Size " + Integer.toString(baseSize) + " Mines " + Integer.toString(baseTotalMines) + " Steps " + Integer.toString(totalSteps) + ".csv");

			minesPercentage += 0.1;
			baseTotalMines = (int) (baseSize * baseSize * minesPercentage);
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		if(args.length < 5)
		{
			System.out.println("Filename | maze size | steps | total mines | total tests");
			return;
		}
		
		String outputFileName = args[0];
		
		Program sim = new Program();
		
		int totalTrials = 3000;
		int epochSize = 200;
		int mazeSie = Integer.parseInt(args[1]);
		int stepsToComplete = Integer.parseInt(args[2]);
		int totalMinas = Integer.parseInt(args[3]);
		int totalTests = Integer.parseInt(args[4]);
		
		//sim.performARAMSimple(outputFileName, totalTrials, epochSize, mazeSie, stepsToComplete, totalMinas, totalTests);
		//sim.performUAMSimple(outputFileName, totalTrials, epochSize, mazeSie, stepsToComplete, totalMinas, totalTests, false);
		//sim.performUAMTests(totalTrials, epochSize, mazeSie, stepsToComplete, totalMinas);
		//sim.performanceFalconTests(totalTrials, epochSize, mazeSie, stepsToComplete, totalMinas);
	}
	// -------------------------------------------------------------------------------------------------------
}

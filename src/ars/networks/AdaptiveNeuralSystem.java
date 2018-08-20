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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ars.datastructures.ChannelDescription;
import ars.datastructures.Tuple;
import ars.datastructures.UAMANN;
import ars.datastructures.UAMChannel;

public class AdaptiveNeuralSystem {
	// -------------------------------------------------------------------------------------------------------
	// UAM area config link
	private UAMANN channelBaseConfig = null;

	// -------------------------------------------------------------------------------------------------------
	private int totalCampos = 3;

	public static int STATE = 0;
	public static int ACTION = 1;
	public static int REWARD = 2;

	private int[] featuresSizes;// = new int[3];
	private int[] temperatureOp;// = new int[] { 2, 2, 2 };
	private int[] learningOp;// = new int[] { 1, 1, 2 };
	private int[] fieldsClass;// = new int[] { STATE, ACTION, REWARD };
	private double[] learningRate;// = new double[] { 1.0, 1.0, 1.0 };
	private boolean[] activeFields;// = new double[] { 0.2, 0.2, 0.5 };
	private boolean fuzzyReadout;
	private double[] learnVigilances;// = new double[] { 0.2, 0.2, 0.5 };
	private double[] performVigilances;// = new double[] { 0.0, 0.0, 0.0 };
	private double[] gammas;// = new double[] { 1.0, 0.0, 0.0 };
	private double[] alphas;// = new double[] { 0.001, 0.001, 0.001 };
	private boolean resonanceToPredict;
	private boolean[] vigilancesRaising;

	private double[][] activity;
	private double[] activitySum;

	private double neuronErosionRate = 0.2;
	private double neuronReinforcementRate = 0.5;
	private double neuronDecayRate = 0.005;
	private double neuronConfidenceThreshold = 0.01;
	private int neuronPrunningThreshold = 100;
	private int lastSelectedNeuron = 0;
	private double stateVigilanceReinforcementRate = 0.001;
	private double minQEpsilon = 0.0;
	private double QEpsilonDecay = 0.005;
	private double QEpsilon = 0.5;
	private boolean useImediateReward = false;
	private double qDiscountParameter = 0.1;
	private double qLearningRate = 0.5;
	private boolean useQLearning = true;
	private double currentLearningAccuracyThreshold = 0.0;
	private double LearningAccuracyThreshold = 2.0;
	private boolean learningEnabled = true;

	public void setUAMChannelBase(UAMANN channelBaseConfig) {
		this.channelBaseConfig = channelBaseConfig;
	}

	// -------------------------------------------------------------------------------------------------------
	public void setCurrentLearningAccuracyThreshold(double threshold) {
		currentLearningAccuracyThreshold = threshold;
		if (currentLearningAccuracyThreshold >= LearningAccuracyThreshold && learningEnabled) {
			learningEnabled = false;
			System.out.println(
					"Threshold de aprendizado alcan�ado, parar aprendizado para evitar corrup��o de codigos cognitivos...");
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public boolean useQLearning() {
		return useQLearning;
	}

	// -------------------------------------------------------------------------------------------------------
	public boolean useImediateReward() {
		return useImediateReward;
	}

	// -------------------------------------------------------------------------------------------------------
	public double qDiscountParameter() {
		return qDiscountParameter;
	}

	// -------------------------------------------------------------------------------------------------------
	public double qLearningRate() {
		return qLearningRate;
	}

	// -------------------------------------------------------------------------------------------------------
	public double getCurrentEpsilon() {
		return QEpsilon;
	}

	// -------------------------------------------------------------------------------------------------------
	public void updateEpsilon() {
		QEpsilon = Math.max(QEpsilon - QEpsilonDecay, minQEpsilon);
	}

	// -------------------------------------------------------------------------------------------------------
	public void setCurrentEpsilon(double ep) {
		QEpsilon = ep;
	}

	// -------------------------------------------------------------------------------------------------------
	public void setEpsilonDecayParameters(double minQEpsilon, double currentQEpsilon, double epsilonDecay) {
		this.minQEpsilon = minQEpsilon;
		this.QEpsilon = currentQEpsilon;
		this.QEpsilonDecay = epsilonDecay;
	}

	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem(int[] featuresSizes, boolean[] vigilancesRaising, boolean[] activeFields, int[] temperatureOp,
			int[] learningOp, int[] fieldsClass, double[] learningRate, double[] learnVigilances,
			double[] performVigilances, double[] gammas, double[] alphas, boolean fuzzyReadout,
			boolean resonanceToPredict, boolean useImediateReward, boolean useQLearning) {
		totalCampos = featuresSizes.length;
		this.vigilancesRaising = vigilancesRaising;
		this.useQLearning = useQLearning;
		this.useImediateReward = useImediateReward;
		this.resonanceToPredict = resonanceToPredict;
		this.fuzzyReadout = fuzzyReadout;
		this.activeFields = activeFields;
		this.featuresSizes = featuresSizes;
		this.temperatureOp = temperatureOp;
		this.learningOp = learningOp;
		this.fieldsClass = fieldsClass;
		this.learningRate = learningRate;
		this.learnVigilances = learnVigilances;
		this.performVigilances = performVigilances;
		this.gammas = gammas;
		this.alphas = alphas;

		activity = new double[totalCampos][];
		for (int campo = 0; campo < totalCampos; campo++) {
			activity[campo] = new double[featuresSizes[campo]];
		}

		activitySum = new double[totalCampos];

		createNeuron(false);
	}
	
	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem(int[] featuresSizes, boolean[] vigilancesRaising, boolean[] activeFields, int[] temperatureOp,
			int[] learningOp, int[] fieldsClass, double[] learningRate, double[] learnVigilances,
			double[] performVigilances, double[] gammas, double[] alphas, boolean fuzzyReadout,
			boolean resonanceToPredict, boolean useImediateReward, boolean useQLearning, boolean useUAM) {
		totalCampos = featuresSizes.length;
		this.vigilancesRaising = vigilancesRaising;
		this.useQLearning = useQLearning;
		this.useImediateReward = useImediateReward;
		this.resonanceToPredict = resonanceToPredict;
		this.fuzzyReadout = fuzzyReadout;
		this.activeFields = activeFields;
		this.featuresSizes = featuresSizes;
		this.temperatureOp = temperatureOp;
		this.learningOp = learningOp;
		this.fieldsClass = fieldsClass;
		this.learningRate = learningRate;
		this.learnVigilances = learnVigilances;
		this.performVigilances = performVigilances;
		this.gammas = gammas;
		this.alphas = alphas;
		this.useUAM = useUAM;
		
		activity = new double[totalCampos][];
		for (int campo = 0; campo < totalCampos; campo++) {
			activity[campo] = new double[featuresSizes[campo]];
		}

		activitySum = new double[totalCampos];

		createNeuron(false);
	}

	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem(int[] featuresSizes, boolean[] vigilancesRaising, boolean[] activeFields, int[] temperatureOp,
			int[] learningOp, int[] fieldsClass, double[] learningRate, double[] learnVigilances,
			double[] performVigilances, double[] gammas, double[] alphas, boolean fuzzyReadout,
			boolean resonanceToPredict, boolean useImediateReward, boolean useQLearning, int bayesSize) {
		totalCampos = featuresSizes.length;
		this.vigilancesRaising = vigilancesRaising;
		this.useQLearning = useQLearning;
		this.useImediateReward = useImediateReward;
		this.resonanceToPredict = resonanceToPredict;
		this.fuzzyReadout = fuzzyReadout;
		this.activeFields = activeFields;
		this.featuresSizes = featuresSizes;
		this.temperatureOp = temperatureOp;
		this.learningOp = learningOp;
		this.fieldsClass = fieldsClass;
		this.learningRate = learningRate;
		this.learnVigilances = learnVigilances;
		this.performVigilances = performVigilances;
		this.gammas = gammas;
		this.alphas = alphas;

		activity = new double[totalCampos][];
		for (int campo = 0; campo < totalCampos; campo++) {
			activity[campo] = new double[featuresSizes[campo]];
		}

		activitySum = new double[totalCampos];

		createNeuron(false);
	}

	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem(ChannelDescription config) {
		totalCampos = config.featuresSizes.length;
		this.vigilancesRaising = config.adaptiveVigilanceRaising;
		this.useQLearning = config.useQLearning;
		this.useImediateReward = config.useDirectReward;
		this.resonanceToPredict = config.resonanceToPredict;
		this.fuzzyReadout = config.fuzzyReadout;
		this.activeFields = config.activeFields;
		this.featuresSizes = config.featuresSizes;
		this.temperatureOp = config.temperatureOp;
		this.learningOp = config.learningOp;
		this.fieldsClass = config.fieldsClass;
		this.learningRate = config.learningRate;
		this.learnVigilances = config.learnVigilances;
		this.performVigilances = config.performVigilances;
		this.gammas = config.gammas;
		this.alphas = config.alphas;

		activity = new double[totalCampos][];
		for (int campo = 0; campo < totalCampos; campo++) {
			activity[campo] = new double[featuresSizes[campo]];
		}

		activitySum = new double[totalCampos];

		createNeuron(false);
	}

	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem(ChannelDescription config, UAMANN channelBaseConfig, int channel) {
		this.channelBaseConfig = channelBaseConfig;
		this.totalCampos = config.featuresSizes.length;
		this.vigilancesRaising = config.adaptiveVigilanceRaising;
		this.useQLearning = config.useQLearning;
		this.useImediateReward = config.useDirectReward;
		this.resonanceToPredict = config.resonanceToPredict;
		this.fuzzyReadout = config.fuzzyReadout;
		this.activeFields = config.activeFields;
		this.featuresSizes = config.featuresSizes;
		this.temperatureOp = config.temperatureOp;
		this.learningOp = config.learningOp;
		this.fieldsClass = config.fieldsClass;
		this.learningRate = config.learningRate;
		this.learnVigilances = config.learnVigilances;
		this.performVigilances = config.performVigilances;
		this.gammas = config.gammas;
		this.alphas = config.alphas;

		activity = new double[totalCampos][];
		for (int campo = 0; campo < totalCampos; campo++) {
			activity[campo] = new double[featuresSizes[campo]];
		}

		activitySum = new double[totalCampos];

		channelId = channel;
		
		createNeuron(false);
	}

	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem(int stateCount, int actionCount, int rewardCount) {
		featuresSizes[STATE] = stateCount;
		featuresSizes[ACTION] = actionCount;
		featuresSizes[REWARD] = rewardCount;

		activity = new double[totalCampos][];
		for (int campo = 0; campo < totalCampos; campo++) {
			activity[campo] = new double[featuresSizes[campo]];
		}

		activitySum = new double[totalCampos];

		createNeuron(false);
	}

	// -------------------------------------------------------------------------------------------------------
	public void saveNetwork(String file) {
		try {
			File f = new File(file);
			BufferedWriter b = new BufferedWriter(new FileWriter(f));

			// total neurons
			b.write(neurons.size() + "\n");

			// weights
			for (int i = 0; i < neurons.size(); i++) {
				for (int field = 0; field < totalCampos; field++) {
					for (int j = 0; j < featuresSizes[field]; j++)
						b.write(String.format("%.17f", neurons.get(i)[field][j]) + " ");
				}
				b.write(neuronsAges.get(i) + "\n");
			}

			b.close();
		} catch (Exception E) {

		}
	}

	// -------------------------------------------------------------------------------------------------------
	private void createNeuron(boolean immutable) {
		double[][] neuron = new double[totalCampos][];

		for (int campo = 0; campo < totalCampos; campo++) {
			neuron[campo] = new double[featuresSizes[campo]];
			for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
				neuron[campo][elemento] = 1.0;
			}
		}

		neuronsAges.add(1);
		neuronsImmutable.add(immutable);
		neuronsSurprise.add(new SimpleSurprise());
		neurons.add(neuron);
		neuronsConfidence.add(1.0);

		// create new brain area
		if (useUAM) {
			// new channel
			int newChannel = channelId + 1;

			// subsequent channel
			UAMChannel channel = channelBaseConfig.getChannel(newChannel);

			if(channel == null)
				return;
			
			// creating a new AREA
			AdaptiveNeuralSystem newArea = new AdaptiveNeuralSystem(channel.channelConfiguration, channelBaseConfig, newChannel);

			// configure it to use the UAM
			newArea.useUAM(true);

			// configuring new area
			newArea.setChannel(newChannel);

			// configuring father channel
			newArea.setFatherChannel(channelId);

			// adding new area to channel newChannel
			areaLinks.add(newArea);
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void setActivity(double[][] stimulus) {
		for (int campo = 0; campo < totalCampos; campo++)
			System.arraycopy(stimulus[campo], 0, activity[campo], 0, featuresSizes[campo]);
	}

	// -------------------------------------------------------------------------------------------------------
	public double[] readAcitivity(int campo) {
		return activity[campo].clone();
	}

	// -------------------------------------------------------------------------------------------------------
	private void calculateActivitySum() {
		for (int campo = 0; campo < totalCampos; campo++) {
			activitySum[campo] = 0.0;
			for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
				activitySum[campo] += activity[campo][elemento];
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private double ARTI(int campo, double[] neuronField, double[] neuron_wAndxSum) {
		double xAndwSum = 0.0;
		double wSum = 0.0;
		for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
			xAndwSum += Math.min(neuronField[elemento], activity[campo][elemento]);
			wSum += neuronField[elemento];
		}

		neuron_wAndxSum[campo] = xAndwSum;

		double t = (xAndwSum / (alphas[campo] + wSum));

		if (t != t)
			t = 0.00001;

		return t * gammas[campo];
	}

	// -------------------------------------------------------------------------------------------------------
	private double ARTII(int campo, double[] neuronField, double[] neuron_wAndxSum) {
		double xAndwSum = 0.0;
		double xDotw = 0.0;
		double wLenght = 0.0;
		double xLenght = 0.0;

		for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
			xAndwSum += Math.min(neuronField[elemento], activity[campo][elemento]);
			xDotw += neuronField[elemento] * activity[campo][elemento];
			wLenght += Math.pow(neuronField[elemento], 2.0);
			xLenght += Math.pow(activity[campo][elemento], 2.0);
		}

		neuron_wAndxSum[campo] = xAndwSum;

		wLenght = Math.sqrt(wLenght);
		xLenght = Math.sqrt(xLenght);

		double t = xDotw / (alphas[campo] + (wLenght * xLenght));

		if (t != t)
			t = 0.00001;

		return t * gammas[campo];
	}

	// -------------------------------------------------------------------------------------------------------
	private double proximity(int campo, double[] neuronField, double[] neuron_wAndxSum) {
		double xAndwSum = 0.0;
		double dist = 0.0;

		for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
			xAndwSum += Math.min(neuronField[elemento], activity[campo][elemento]);
			dist += Math.abs(neuronField[elemento] - activity[campo][elemento]);
		}

		double t = 1.0 / (alphas[campo] + dist);
		neuron_wAndxSum[campo] = xAndwSum;
		// neuron_wAndxSum[campo] = 1.0 - (dist / (double)
		// featuresSizes[campo]);

		if (t != t)
			t = 0.000001;

		return t * gammas[campo];
	}

	// -------------------------------------------------------------------------------------------------------
	private double calculateTComposite(double[][] neuron, double[] neuron_wAndxSum) {
		double t = 0.0;
		for (int campo = 0; campo < totalCampos; campo++) {
			if (activeFields[campo]) {
				int op = temperatureOp[campo];
				switch (op) {
				case 1:
					t += ARTI(campo, neuron[campo], neuron_wAndxSum);
					break;
				case 2:
					t += ARTII(campo, neuron[campo], neuron_wAndxSum);
					break;
				case 3:
					t += proximity(campo, neuron[campo], neuron_wAndxSum);
					break;
				}
			}
		}

		return t;
	}

	// -------------------------------------------------------------------------------------------------------
	private double doMatch(int campo, double[] selectedNeuronField, double wAndxSum) {
		double m_j = wAndxSum / activitySum[campo];

		return m_j;
	}

	// -------------------------------------------------------------------------------------------------------
	private boolean perfectMissmatch(int campo, double[][] neuron) {
		boolean pmm = true;
		for(int i = 0; i < neuron[campo].length; i++){
			if(neuron[campo][i] != activity[campo][i])
				pmm = false;
		}
		
		return pmm;
	}

	// -------------------------------------------------------------------------------------------------------
	private void stampNeuronARTI(int campo, int selectedNeuron) {
		double[][] learningNeuron = neurons.get(selectedNeuron);
		for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
			double learnedValue = (1.0 - learningRate[campo]) * learningNeuron[campo][elemento]
					+ learningRate[campo] * Math.min(learningNeuron[campo][elemento], activity[campo][elemento]);
			learningNeuron[campo][elemento] = learnedValue;
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private void stampNeuronARTII(int campo, int selectedNeuron) {
		double[][] learningNeuron = neurons.get(selectedNeuron);
		for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
			double learnedValue = (1.0 - learningRate[campo]) * learningNeuron[campo][elemento]
					+ learningRate[campo] * activity[campo][elemento];
			learningNeuron[campo][elemento] = learnedValue;
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private void learnComposite(int selectedNeuron) {
		if (neurons.get(selectedNeuron)[totalCampos - 1][0] == 1.0
				&& neurons.get(selectedNeuron)[totalCampos - 1][1] == 0.0 && fieldsClass[totalCampos - 1] == REWARD)
			return;

		if (neurons.get(selectedNeuron)[totalCampos - 1][0] == 0.0
				&& neurons.get(selectedNeuron)[totalCampos - 1][1] == 1.0 && fieldsClass[totalCampos - 1] == REWARD)
			return;

		for (int campo = 0; campo < totalCampos; campo++) {
			int op = learningOp[campo];
			switch (op) {
			case 1:
				stampNeuronARTI(campo, selectedNeuron);
				break;
			case 2:
				stampNeuronARTII(campo, selectedNeuron);
				break;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void resetLastNeuronAction() {
		double[][] learningNeuron = neurons.get(lastSelectedNeuron);
		for (int campo = 0; campo < totalCampos; campo++) {
			if (fieldsClass[campo] == ACTION) {
				for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
					double learnedValue = activity[campo][elemento];
					learningNeuron[campo][elemento] = learnedValue;
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private void overwrite(int selectedNeuron) {
		double[][] learningNeuron = neurons.get(selectedNeuron);
		for (int campo = 0; campo < totalCampos; campo++) {
			for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
				double learnedValue = activity[campo][elemento];
				learningNeuron[campo][elemento] = learnedValue;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private void fuzzyReadout(int selectedNeuron) {
		double[][] readoutneuron = neurons.get(selectedNeuron);
		for (int campo = 0; campo < totalCampos; campo++) {
			for (int element = 0; element < featuresSizes[campo]; element++) {
				activity[campo][element] = Math.min(readoutneuron[campo][element], activity[campo][element]);
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private void readout(int selectedNeuron) {
		double[][] readoutneuron = neurons.get(selectedNeuron);
		for (int campo = 0; campo < totalCampos; campo++) {
			for (int element = 0; element < featuresSizes[campo]; element++) {
				activity[campo][element] = readoutneuron[campo][element];
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public int getNeuronsSize() {
		return neurons.size();
	}

	// -------------------------------------------------------------------------------------------------------
	public void neuronPrunning() {
		int neuronsToPrunne = 0;

		if (neurons.size() >= neuronPrunningThreshold) {
			for (int currentNeuron = 0; currentNeuron < neuronsConfidence.size(); currentNeuron++) {
				double confidence = neuronsConfidence.get(currentNeuron);
				if (confidence < neuronConfidenceThreshold) {
					neurons.set(currentNeuron, null);
					neuronsToPrunne++;
				}
			}
		}

		while (neuronsToPrunne > 0) {
			for (int currentNeuron = 0; currentNeuron < neurons.size() - 1; currentNeuron++) {
				if (neurons.get(currentNeuron) == null) {
					neurons.remove(currentNeuron);
					neuronsConfidence.remove(currentNeuron);
					neuronsSurprise.remove(currentNeuron);
					neuronsToPrunne--;
					break;
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void neuronReinforcement() {
		double oldConfidence = neuronsConfidence.get(lastSelectedNeuron);
		double newConfidence = oldConfidence + neuronReinforcementRate * (1.0 - oldConfidence);
		neuronsConfidence.set(lastSelectedNeuron, newConfidence);
	}

	// -------------------------------------------------------------------------------------------------------
	public void neuronErosion() {
		double oldConfidence = neuronsConfidence.get(lastSelectedNeuron);
		double newConfidence = oldConfidence - neuronErosionRate * oldConfidence;
		neuronsConfidence.set(lastSelectedNeuron, newConfidence);
	}

	// -------------------------------------------------------------------------------------------------------
	public void neuronDecay() {
		for (int currentNeuron = 0; currentNeuron < neuronsConfidence.size(); currentNeuron++) {
			double confidence = neuronsConfidence.get(currentNeuron);
			double newConfidence = confidence - neuronDecayRate * confidence;
			neuronsConfidence.set(currentNeuron, newConfidence);
		}
	}

	// -------------------------------------------------------------------------------------------------------
	private double[] calculateVigilances(boolean learning) {
		if (learning)
			return learnVigilances.clone();
		return performVigilances.clone();
	}

	// -------------------------------------------------------------------------------------------------------
	private static int max(double[] array) {
		int result = -1;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= max) {
				max = array[i];
				result = i;
			}
		}
		return result;
	}

	// -------------------------------------------------------------------------------------------------------
	public int selectAction() {
		prediction(false);

		if (lastSelectedNeuron == neurons.size() - 1) // Predi��o de neuronio
			return -1;

		int actionField = 0;
		for (int i = 0; i < totalCampos; i++) {
			if (fieldsClass[i] == ACTION) {
				actionField = i;
				break;
			}
		}

		int selectedAct = max(activity[actionField]);
		return selectedAct;
	}

	// -------------------------------------------------------------------------------------------------------
	private boolean isResonating(double[] vigilances, double[][] neuron, double[] neuronXandWSum) {
		boolean resonated = true;
		for (int campo = 0; campo < totalCampos; campo++) {
			if (activeFields[campo]) {
				double stateMatchFactor = doMatch(campo, neuron[campo], neuronXandWSum[campo]);

				if (stateMatchFactor < vigilances[campo]) {
					resonated = false;
					break;
				}
			}
		}
		return resonated;
	}

	// -------------------------------------------------------------------------------------------------------
	public void insert() {
		double[][] learningNeuron = neurons.get(lastSelectedNeuron);
		for (int campo = 0; campo < totalCampos; campo++) {
			for (int elemento = 0; elemento < featuresSizes[campo]; elemento++) {
				double learnedValue = activity[campo][elemento];
				learningNeuron[campo][elemento] = learnedValue;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void prediction(boolean learning) {
		calculateActivitySum();

		double[] vigilances = calculateVigilances(learning);

		double[][] wAndxSum = new double[neurons.size()][totalCampos];
		List<Tuple> neuronsTemperature = new ArrayList<>();
		for (int currentNeuron = 0; currentNeuron < neurons.size() - 1; currentNeuron++) {
			double t = calculateTComposite(neurons.get(currentNeuron), wAndxSum[currentNeuron]);
			neuronsTemperature.add(new Tuple(currentNeuron, t));
		}
		calculateTComposite(neurons.get(neurons.size() - 1), wAndxSum[neurons.size() - 1]);
		neuronsTemperature.add(new Tuple(neurons.size() - 1, 0.0));

		// try{
		Collections.sort(neuronsTemperature, new Comparator<Tuple>() {
			@Override
			public int compare(Tuple arg0, Tuple arg1) {
				double diff = arg0.t - arg1.t;
				if (diff < 0.0)
					return 1;
				if (diff > 0.0)
					return -1;
				return 0;
			}
		});
		/*
		 * }catch(Exception e){ for(int i = 0; i < neuronsTemperature.size();
		 * i++){ System.out.print(neuronsTemperature.get(i).neuronIndex + "\t");
		 * System.out.println(neuronsTemperature.get(i).t); }
		 * System.out.println(""); }
		 */

		int selectedNeuron = -1;
		boolean perfectMissmatch = false;
		for (int i = 0; i < neuronsTemperature.size(); i++) {
			int maxT = neuronsTemperature.get(i).neuronIndex;
			double[][] neuron = neurons.get(maxT);
			double[] neuronXandWSum = wAndxSum[maxT];
			selectedNeuron = maxT;

			boolean resonated = isResonating(vigilances, neuron, neuronXandWSum);

			if (resonated) {
				break;
			} else {
				boolean perfectError = true;
				for (int campo = 0; campo < totalCampos; campo++) {
					if (!perfectMissmatch(campo, neuron) && fieldsClass[campo] == STATE) {
						perfectError = false;
						break;
					}
				}

				if (perfectError) {
					perfectMissmatch = true;
					break;
				} else {
					for (int campo = 0; campo < totalCampos; campo++) {
						if (vigilancesRaising[campo]) {
							double stateMatchFactor = doMatch(campo, neuron[campo], neuronXandWSum[campo]);
							if (stateMatchFactor > vigilances[campo])
								vigilances[campo] = Math.min(stateMatchFactor + stateVigilanceReinforcementRate, 1.0);
						}
					}
				}
			}
		}

		if (learning) {
			if (learningEnabled) {
				 if (perfectMissmatch)
					 overwrite(selectedNeuron);
				 else
					 learnComposite(selectedNeuron);
				 
				neuronsAges.set(selectedNeuron, neuronsAges.get(selectedNeuron) + 1);
				if (selectedNeuron == neurons.size() - 1)
					createNeuron(false);
			}
		} else {
			if (fuzzyReadout)
				fuzzyReadout(selectedNeuron);
			else
				readout(selectedNeuron);
		}

		lastSelectedNeuron = selectedNeuron;
	}

	// -------------------------------------------------------------------------------------------------------
	public void prediction(boolean learning, boolean immutableLearning) {
		calculateActivitySum();

		double[] vigilances = calculateVigilances(learning);

		// if(immutableLearning)
		// for(int i = 0; i < vigilances.length; i++)
		// vigilances[i] = 1.0;

		double[][] wAndxSum = new double[neurons.size()][totalCampos];
		List<Tuple> neuronsTemperature = new ArrayList<>();
		for (int currentNeuron = 0; currentNeuron < neurons.size() - 1; currentNeuron++) {
			double t = calculateTComposite(neurons.get(currentNeuron), wAndxSum[currentNeuron]);
			neuronsTemperature.add(new Tuple(currentNeuron, t));
		}
		calculateTComposite(neurons.get(neurons.size() - 1), wAndxSum[neurons.size() - 1]);
		neuronsTemperature.add(new Tuple(neurons.size() - 1, 0.0));

		// try{
		Collections.sort(neuronsTemperature, new Comparator<Tuple>() {
			@Override
			public int compare(Tuple arg0, Tuple arg1) {
				double diff = arg0.t - arg1.t;
				if (diff < 0.0)
					return 1;
				if (diff > 0.0)
					return -1;
				return 0;
			}
		});
		/*
		 * }catch(Exception e){ for(int i = 0; i < neuronsTemperature.size();
		 * i++){ System.out.print(neuronsTemperature.get(i).neuronIndex + "\t");
		 * System.out.println(neuronsTemperature.get(i).t); }
		 * System.out.println(""); }
		 */

		int selectedNeuron = -1;
		boolean perfectMissmatch = false;
		for (int i = 0; i < neuronsTemperature.size(); i++) {
			int maxT = neuronsTemperature.get(i).neuronIndex;

			if (learningEnabled && neuronsImmutable.get(maxT) == true)
				continue;

			double[][] neuron = neurons.get(maxT);
			double[] neuronXandWSum = wAndxSum[maxT];
			selectedNeuron = maxT;

			boolean resonated = isResonating(vigilances, neuron, neuronXandWSum);

			if (resonated) {
				break;
			} else {
				if (perfectMissmatch(STATE, neuron)) {
					perfectMissmatch = true;
					break;
				} else {
					for (int campo = 0; campo < totalCampos; campo++) {
						if (vigilancesRaising[campo]) {
							double stateMatchFactor = doMatch(campo, neuron[campo], neuronXandWSum[campo]);
							if (stateMatchFactor > vigilances[campo])
								vigilances[campo] = Math.min(stateMatchFactor + stateVigilanceReinforcementRate, 1.0);
						}
					}
				}
			}
		}

		if (learning) {
			if (learningEnabled && neuronsImmutable.get(selectedNeuron) == false) {
				// if (perfectMissmatch)
				// overwrite(selectedNeuron);
				// else

				neuronsImmutable.set(selectedNeuron, immutableLearning);

				if (immutableLearning)
					overwrite(selectedNeuron);
				else
					learnComposite(selectedNeuron);

				if (selectedNeuron == neurons.size() - 1)
					createNeuron(false);
			}
		} else {
			if (!learningEnabled) {
				if (fuzzyReadout)
					fuzzyReadout(selectedNeuron);
				else
					readout(selectedNeuron);
			}
		}

		lastSelectedNeuron = selectedNeuron;
	}

	// -------------------------------------------------------------------------------------------------------
	public void Qprediction(boolean learning) {
		calculateActivitySum();

		double[] vigilances = calculateVigilances(learning);

		double[][] wAndxSum = new double[neurons.size()][totalCampos];
		List<Tuple> neuronsTemperature = new ArrayList<>();
		for (int currentNeuron = 0; currentNeuron < neurons.size() - 1; currentNeuron++) {
			double t = calculateTComposite(neurons.get(currentNeuron), wAndxSum[currentNeuron]);
			neuronsTemperature.add(new Tuple(currentNeuron, t));
		}
		calculateTComposite(neurons.get(neurons.size() - 1), wAndxSum[neurons.size() - 1]);
		neuronsTemperature.add(new Tuple(neurons.size() - 1, 0.0));

		// try{
		Collections.sort(neuronsTemperature, new Comparator<Tuple>() {
			@Override
			public int compare(Tuple arg0, Tuple arg1) {
				double diff = arg0.t - arg1.t;
				if (diff < 0.0)
					return 1;
				if (diff > 0.0)
					return -1;
				return 0;
			}
		});
		/*
		 * }catch(Exception e){ for(int i = 0; i < neuronsTemperature.size();
		 * i++){ System.out.print(neuronsTemperature.get(i).neuronIndex + "\t");
		 * System.out.println(neuronsTemperature.get(i).t); }
		 * System.out.println(""); }
		 */

		boolean perfectMissmatch = false;
		int selectedNeuron = -1;
		boolean findResonatingNeuron = learning;
		if (resonanceToPredict)
			findResonatingNeuron = true;

		for (int i = 0; i < neuronsTemperature.size(); i++) {
			int maxT = neuronsTemperature.get(i).neuronIndex;
			double[][] neuron = neurons.get(maxT);
			double[] neuronXandWSum = wAndxSum[maxT];
			selectedNeuron = maxT;

			boolean resonated = isResonating(vigilances, neuron, neuronXandWSum);

			if (findResonatingNeuron) {
				if (resonated) {
					break;
				} else { // RESET
					if (perfectMissmatch = perfectMissmatch(STATE, neuron))
						break;
					else {
						// Incrementa vigilancia do estado, apenas
						for (int campo = 0; campo < totalCampos; campo++) {
							if (vigilancesRaising[campo]) {
								double stateMatchFactor = doMatch(campo, neuron[campo], neuronXandWSum[campo]);
								if (stateMatchFactor > vigilances[campo])
									vigilances[campo] = Math.min(stateMatchFactor + stateVigilanceReinforcementRate,
											1.0);
							}
						}
					}
				}
			} else {
				break;
			}
		}

		if (learning) {
			if (learningEnabled && !neuronsImmutable.get(selectedNeuron)) {
				if (perfectMissmatch)
					overwrite(selectedNeuron);
				else
					learnComposite(selectedNeuron);

				if (selectedNeuron == neurons.size() - 1)
					createNeuron(false);
			}
		} else {
			if (fuzzyReadout)
				fuzzyReadout(selectedNeuron);
			else
				readout(selectedNeuron);
		}

		lastSelectedNeuron = selectedNeuron;
	}

	// -------------------------------------------------------------------------------------------------------
	public double[][] getLastActivatedPrediction() {
		return neurons.get(lastSelectedNeuron);
	}

	// -------------------------------------------------------------------------------------------------------
	public double[] getRewardField() {
		double[][] neuron = neurons.get(lastSelectedNeuron);
		return neuron[neuron.length - 1];
	}

	// -------------------------------------------------------------------------------------------------------
	public SimpleSurprise getBayesMemmory() {
		return neuronsSurprise.get(lastSelectedNeuron);
	}
	// -------------------------------------------------------------------------------------------------------

	public int getChannel() {
		return channelId;
	}
	
	public void setChannel(int channel) {
		channelId = channel;
	}

	// -------------------------------------------------------------------------------------------------------
	public void setFatherChannel(int channel) {
		fatherNeuronChannel = channel;
	}

	// -------------------------------------------------------------------------------------------------------
	public void useUAM(boolean useUAM) {
		this.useUAM = useUAM;
	}

	// -------------------------------------------------------------------------------------------------------
	public AdaptiveNeuralSystem getLastActivatedArea() {
		return areaLinks.get(lastSelectedNeuron);
	}

	// -------------------------------------------------------------------------------------------------------
	public void setAreaLinks(UAMANN areaLink) {
		this.channelBaseConfig = areaLink;
	}
	
	public List<AdaptiveNeuralSystem> getAreaLinks(){
		return areaLinks;
	}

	// -------------------------------------------------------------------------------------------------------
	private List<Double> neuronsConfidence = new ArrayList<>();
	private List<double[][]> neurons = new ArrayList<>();
	private List<AdaptiveNeuralSystem> areaLinks = new ArrayList<>();
	private List<Integer> neuronsAges = new ArrayList<>();
	private List<Boolean> neuronsImmutable = new ArrayList<>();
	private List<SimpleSurprise> neuronsSurprise = new ArrayList<>();
	private int fatherNeuronChannel = -1;
	private int channelId = 0;
	private boolean useUAM = true;
	// -------------------------------------------------------------------------------------------------------
}

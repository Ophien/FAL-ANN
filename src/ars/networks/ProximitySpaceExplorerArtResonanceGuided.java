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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ars.datastructures.Triple;

public class ProximitySpaceExplorerArtResonanceGuided {

	private double minQEpsilon = 0.0;
	private double QEpsilonDecay = 0.005;
	private double QEpsilon = 0.5;
	public boolean useImediateReward = false;
	private double qDiscountParameter = 0.1;
	private double qLearningRate = 0.1;

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
	public ProximitySpaceExplorerArtResonanceGuided() {
	}

	// -------------------------------------------------------------------------------------------------------
	public void saveNetwork(String file) {
		try {
			File f = new File(file);
			BufferedWriter b = new BufferedWriter(new FileWriter(f));

			// total neurons
			b.write(neurons.size() + "\n");

			// total fields
			b.write(totalFields + "\n");

			// vigilances
			for (int i = 0; i < totalFields - 1; i++)
				b.write(learnVigilances[i] + " ");
			b.write(learnVigilances[totalFields - 1] + "\n");

			// weights
			for (int i = 0; i < neurons.size() - 1; i++) {
				for (int j = 0; j < totalFields - 1; j++)
					b.write(String.format("%.17f", neurons.get(i)[j]) + " ");
				b.write(String.format("%.17f", neurons.get(i)[totalFields - 1]) + "\n");
			}

			for (int j = 0; j < totalFields - 1; j++)
				b.write(String.format("%.17f", neurons.get(neurons.size() - 1)[j]) + " ");
			b.write(String.format("%.17f", neurons.get(neurons.size() - 1)[totalFields - 1]));

			b.close();
		} catch (Exception E) {

		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void initialize(int inputLenght, double[] vigilances, double[] learningRates) {
		if (inputLenght != vigilances.length) {
			System.out.println("Impossivel criar rede, vetores de tamanhos diferentes...");
			return;
		}

		this.learningRates = learningRates;
		this.learnVigilances = vigilances;
		performVigilances = new double[inputLenght];

		totalFields = inputLenght;

		// Inicializa rede
		activity = new double[totalFields];
		prediction = new double[totalFields];
		fieldsActivity = new boolean[totalFields];
		fieldsToLearn = new boolean[totalFields];

		for (int field = 0; field < totalFields; field++) {
			fieldsActivity[field] = true;
			fieldsToLearn[field] = false;
		}

		neurons = new ArrayList<>();
		neuronsAges = new ArrayList<>();

		createNewNonCommitedNeuron();
	}
	
	// -------------------------------------------------------------------------------------------------------
	public void initialize(int inputLenght, double[] vigilances) {
		if (inputLenght != vigilances.length) {
			System.out.println("Impossivel criar rede, vetores de tamanhos diferentes...");
			return;
		}

		this.learningRates = new double[inputLenght];
		this.learnVigilances = vigilances;
		performVigilances = new double[inputLenght];

		totalFields = inputLenght;

		// Inicializa rede
		activity = new double[totalFields];
		prediction = new double[totalFields];
		fieldsActivity = new boolean[totalFields];
		fieldsToLearn = new boolean[totalFields];

		for (int field = 0; field < totalFields; field++) {
			fieldsActivity[field] = true;
			fieldsToLearn[field] = false;
		}

		neurons = new ArrayList<>();
		neuronsAges = new ArrayList<>();

		createNewNonCommitedNeuron();
	}

	// -------------------------------------------------------------------------------------------------------
	public void createNewNonCommitedNeuron() {
		double[] newNeuron = new double[totalFields];
		for (int field = 0; field < totalFields; field++) {
			newNeuron[field] = 1.0;
		}
		neurons.add(newNeuron);
		neuronsAges.add(1.0);
	}

	// -------------------------------------------------------------------------------------------------------
	public double calculateT(double[] input, double[] neuron, boolean[] resonance, double[] t_fields, double[] vigilances) {
		double t = 0.0;

		for (int field = 0; field < totalFields; field++) {
			resonance[field] = true;

			if (!fieldsActivity[field])
				continue;

			double fieldSum = Math.min(input[field], neuron[field]) / (neuron[field] + 0.1);

			t_fields[field] = Math.min(input[field], neuron[field]) / (input[field]);
			double resonate = fieldSum;
			if (resonate < vigilances[field])
				resonance[field] = false;

			t += fieldSum;
		}

		return t;
	}

	// -------------------------------------------------------------------------------------------------------
	public void learn(double[] input, double[] selectedNeuron) {
		for (int fieldElement = 0; fieldElement < totalFields; fieldElement++) {
			selectedNeuron[fieldElement] = (1.0 - learningRates[fieldElement]) * selectedNeuron[fieldElement] + learningRates[fieldElement] * input[fieldElement];
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void learnMask(double[] input, double[] selectedNeuron) {
		for (int fieldElement = 0; fieldElement < totalFields; fieldElement++) {
			if (fieldsToLearn[fieldElement]) {
				double learnedValue = input[fieldElement];
				selectedNeuron[fieldElement] = learnedValue;
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void readOut(double[] input, double[] selectedNeuron) {
		for (int fieldElement = 0; fieldElement < totalFields; fieldElement++)
			prediction[fieldElement] = selectedNeuron[fieldElement];
	}

	// -------------------------------------------------------------------------------------------------------
	private boolean isResonating(double[] vigilances, Triple neuron) {
		boolean resonated = true;
		for (int campo = 0; campo < totalFields; campo++) {
			if (fieldsActivity[campo]) {
				if (!neuron.resonated[campo]) {
					resonated = false;
					break;
				}
			}
		}
		return resonated;
	}

	// -------------------------------------------------------------------------------------------------------
	private double[] calculateVigilances(boolean learning) {
		if (learning)
			return learnVigilances.clone();
		return performVigilances.clone();
	}
	
	// -------------------------------------------------------------------------------------------------------
	public void prediction(double[] input, boolean learning) {
		double[] vigilances = calculateVigilances(learning);
		
		Triple[] neuronsResponse = new Triple[neurons.size()];
		for (int neuronIndex = 0; neuronIndex < neurons.size() - 1; neuronIndex++) {
			double[] neuron = neurons.get(neuronIndex);
			Triple neuronResponse = new Triple();
			boolean[] resonated = new boolean[totalFields];
			double[] t_fields = new double[totalFields];
			double t = calculateT(input, neuron, resonated, t_fields, vigilances);

			neuronResponse.T_value = t;
			neuronResponse.T_fields = t_fields;
			neuronResponse.resonated = resonated;
			neuronResponse.index = neuronIndex;
			neuronsResponse[neuronIndex] = neuronResponse;
		}

		// Assert non-commited neuron	
		Triple neuronResponse = new Triple();
		neuronResponse.T_value = 0.0;
		neuronResponse.T_fields = new double[totalFields];
		neuronResponse.resonated = new boolean[totalFields];
		neuronResponse.index = neurons.size() - 1;
		neuronsResponse[neurons.size() - 1] = neuronResponse;

		Arrays.sort(neuronsResponse, new Comparator<Triple>() {
			@Override
			public int compare(Triple arg0, Triple arg1) {
				double diff = arg0.T_value - arg1.T_value;
				if (diff < 0.0)
					return 1;
				if (diff > 0.0)
					return -1;
				return 0;
			}
		});

		int selectedNeuron = -1;
		for (int i = 0; i < neuronsResponse.length; i++) {
			int maxT = neuronsResponse[i].index;
			selectedNeuron = maxT;

			boolean resonated = isResonating(vigilances, neuronsResponse[i]);

			if (resonated) {
				break;
			} else { // RESET
						// Incrementa vigilancia do estado, apenas
				for (int campo = 0; campo < totalFields; campo++) {
					if (fieldsActivity[campo]) {
						double stateMatchFactor = neuronsResponse[i].T_fields[campo];
						if (stateMatchFactor > vigilances[campo])
							vigilances[campo] = Math.min(stateMatchFactor + stateVigilanceReinforcementRate, 1.0);
					}
				}
			}
		}

		double[] neuron = neurons.get(selectedNeuron);

		if (learning) {
			learn(input, neuron);
			
			if (selectedNeuron == neurons.size() - 1)
				createNewNonCommitedNeuron();
		} else {
			readOut(input, neuron);
		}
	}

	// -------------------------------------------------------------------------------------------------------
	public void activateField(int field) {
		fieldsActivity[field] = true;
	}

	// -------------------------------------------------------------------------------------------------------
	public void deactivateField(int field) {
		fieldsActivity[field] = false;
	}

	// -------------------------------------------------------------------------------------------------------
	public void activateLearnInField(int field) {
		fieldsToLearn[field] = true;
	}

	// -------------------------------------------------------------------------------------------------------
	public void deactivateLearnInField(int field) {
		fieldsToLearn[field] = false;
	}

	// -------------------------------------------------------------------------------------------------------
	private double stateVigilanceReinforcementRate = 0.001;
	public double[] learnVigilances;// = new double[] { 0.2, 0.2, 0.5 };
	public double[] performVigilances;// = new double[] { 0.0, 0.0, 0.0 };
	public int totalFields;
	public boolean[] fieldsActivity;
	public boolean[] fieldsToLearn;
	public double[] activity;
	public double[] prediction;
	public double[] learningRates;
	public List<Double> neuronsAges;
	public List<double[]> neurons;
	// -------------------------------------------------------------------------------------------------------
}

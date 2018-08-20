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

import java.util.Random;

import ars.datastructures.UAMANN;
import ars.networks.AdaptiveNeuralSystem;

public abstract class UAMFalconGenericAgent {
	// -------------------------------------------------------------------------------------------------------
	// RFalcon
	protected UAMANN BRAIN;
	protected int totalActions;
	private double[] environmentToLearn;
	private int actionToLearn;
	private double immediateReward;
	private double Q_old = -1;
	private double Q_new;
	private boolean twoWay = false;

	protected abstract double[] generateEnvironment(Object newEnvironment);

	protected abstract double calculateReward(Object newEnvironment);

	protected abstract int actionSelectionStucked();

	protected abstract double[][] generateActionVec(Object newEnvironment);

	protected abstract double simulateActionToGetFinalQValue(int selectedAction, Object environment);

	protected abstract Object executeAction(int selectedAction, Object action);

	protected abstract boolean isValidAction(int selectedAction);

	// -------------------------------------------------------------------------------------------------------
	protected void setTwoWay(boolean val) {
		twoWay = val;
	}

	// -------------------------------------------------------------------------------------------------------
	protected void enableBayes() {
	}

	// -------------------------------------------------------------------------------------------------------
	protected void disableBayes() {
	}

	// -------------------------------------------------------------------------------------------------------
	protected void setROld(double value) {
	}

	// -------------------------------------------------------------------------------------------------------
	public UAMFalconGenericAgent(int totalActions) {
		this.totalActions = totalActions;
	}

	// -------------------------------------------------------------------------------------------------------
	public void setBrain(UAMANN brain) {
		this.BRAIN = brain;
	}

	// -------------------------------------------------------------------------------------------------------
	public int update() {
		return qLearningWalk();
	}

	// -------------------------------------------------------------------------------------------------------
	protected double[] generateResetActions(int selectedActionToReset) {
		double[] result = generateDirectActions();
		result[selectedActionToReset] = 0;
		return result;
	}

	// -------------------------------------------------------------------------------------------------------
	protected double[] generateLearnActions(int selectedAction) {
		double[] result = new double[totalActions];
		result[selectedAction] = 1.0;
		return result;
	}

	// -------------------------------------------------------------------------------------------------------
	protected double[] generateDirectActions() {
		double[] directAccessAction = new double[totalActions];
		for (int i = 0; i < totalActions; i++)
			directAccessAction[i] = 1.0;
		return directAccessAction;
	}

	// -------------------------------------------------------------------------------------------------------
	protected double[] generateDirectActionsReward() {
		return new double[] { 1, 0 };
	}

	// -------------------------------------------------------------------------------------------------------
	protected double[] generatePredictionReward() {
		return new double[] { 1, 1 };
	}

	// -------------------------------------------------------------------------------------------------------
	public void setReward(Object newEnvironment) {
		calculateReward(newEnvironment);
	}

	// -------------------------------------------------------------------------------------------------------
	public void setOldReward() {
	}

	// -------------------------------------------------------------------------------------------------------
	public double calculateQValue(int selectedAction, double[] rewardField, Object environment) {
		double simulated_q_value = simulateActionToGetFinalQValue(selectedAction, environment);
		if (simulated_q_value != -1.0) {
			return simulated_q_value;
		}

		if (rewardField[0] == 1 && rewardField[1] == 1)
			return rewardField[0] / (rewardField[0] + rewardField[1]);

		return rewardField[0];
	}

	// -------------------------------------------------------------------------------------------------------
	private int qLearningWalk() {
		if (Q_old != -1.0) {
			qLearningLEARNING_STEP(prevEnvironmentObj, prevActions);
			Q_old = -1.0;
		}

		int selectedAction = -1;
		double[] prevEnv = null;

		double max_q = Double.NEGATIVE_INFINITY;
		int act = -1;
		int maxVA = 0;

		double[][] actions = generateActionVec(null);
		maxVA = actions.length;
		Random rand = new Random();
		double randomAction = rand.nextDouble();

		prevEnv = generateEnvironment(null);

		// Escolhe a��o com a rede
		if (randomAction >= BRAIN.getRoot().getCurrentEpsilon()) {
			// Selecionar a a��o com melhor q_valor aqui
			for (int action = 0; action < maxVA; action++) {
				act = action;// validActions[action];
				double[] actvec = actions[action];// generateLearnActions(act);
				double[] r = generatePredictionReward();

				double q_value = 0.0;

				if (twoWay) {
					BRAIN.setStimulus(0, actvec);
					BRAIN.setStimulus(1, new double[][] { prevEnv.clone(), r.clone() });
					BRAIN.predict(false);

					double[] predictedReward = BRAIN.getPrediction(1)[1];
					q_value = calculateQValue(act, predictedReward, null);
				} else {
					double[] sonar = new double[5];
					double[] objdir = new double[8];
					System.arraycopy(prevEnv, 0, sonar, 0, 5);
					System.arraycopy(prevEnv, 5, objdir, 0, 8);

					BRAIN.setStimulus(0, actvec);
					BRAIN.setStimulus(1, sonar);
					BRAIN.setStimulus(2, new double[][] { objdir.clone(), r.clone() });
					BRAIN.predict(false);

					double[] predictedReward = BRAIN.getPrediction(2)[1];
					q_value = calculateQValue(act, predictedReward, null);
				}

				if (q_value >= max_q) {
					max_q = q_value;
					selectedAction = act;
				}
			}
		}

		// Colocar surpresa aqui
		if (randomAction < BRAIN.getRoot().getCurrentEpsilon()) {
			act = rand.nextInt(maxVA);
			double[] actvec = actions[act];// generateLearnActions(act);
			double[] r = generatePredictionReward();
			double q_value = 0.0;

			if (twoWay) {
				BRAIN.setStimulus(0, actvec);
				BRAIN.setStimulus(1, new double[][] { prevEnv.clone(), r.clone() });
				BRAIN.predict(false);

				double[] predictedReward = BRAIN.getPrediction(1)[1];
				q_value = calculateQValue(act, predictedReward, null);
			} else {
				double[] sonar = new double[5];
				double[] objdir = new double[8];
				System.arraycopy(prevEnv, 0, sonar, 0, 5);
				System.arraycopy(prevEnv, 5, objdir, 0, 8);

				BRAIN.setStimulus(0, actvec);
				BRAIN.setStimulus(1, sonar);
				BRAIN.setStimulus(2, new double[][] { objdir.clone(), r.clone() });
				BRAIN.predict(false);

				double[] predictedReward = BRAIN.getPrediction(2)[1];
				q_value = calculateQValue(act, predictedReward, null);
			}

			max_q = q_value;
			selectedAction = act;
		}

		// Nesse momento o Q pertence a a��o selecionada
		environmentToLearn = prevEnv.clone();
		actionToLearn = selectedAction;
		Object newEnvironment = executeAction(selectedAction, actions[selectedAction]);
		immediateReward = calculateReward(newEnvironment);
		Q_old = max_q;
		prevActions = actions;
		prevEnvironmentObj = newEnvironment;

		if (Q_old == 1.0)
			updateFinalState(1.0, actions);

		return selectedAction;
	}

	// -------------------------------------------------------------------------------------------------------
	Object prevEnvironmentObj;
	double[][] prevActions;

	// -------------------------------------------------------------------------------------------------------
	private void updateFinalState(double value, double[][] oldActions) {
		double q_to_learn = value;
		double[] actvec = oldActions[actionToLearn];
		double[] r = new double[] { q_to_learn, 1.0 - q_to_learn };

		if (twoWay) {
			BRAIN.setStimulus(0, actvec);
			BRAIN.setStimulus(1, new double[][] { environmentToLearn.clone(), r.clone() });
			BRAIN.predict(true);
		} else {
			double[] sonar = new double[5];
			double[] objdir = new double[8];
			System.arraycopy(environmentToLearn, 0, sonar, 0, 5);
			System.arraycopy(environmentToLearn, 5, objdir, 0, 8);

			BRAIN.setStimulus(0, actvec);
			BRAIN.setStimulus(1, sonar);
			BRAIN.setStimulus(2, new double[][] { objdir.clone(), r.clone() });
			BRAIN.predict(true);
		}

	}

	// -------------------------------------------------------------------------------------------------------
	private void qLearningLEARNING_STEP(Object newEnvironment, double[][] oldActions) {
		double[] prevEnv = null;

		double max_q = Double.NEGATIVE_INFINITY;
		int act = -1;
		int maxVA = 0;

		double[][] actions = generateActionVec(newEnvironment);
		maxVA = actions.length;

		prevEnv = generateEnvironment(newEnvironment);

		if (maxVA == 0)
			max_q = 0.0;

		if (BRAIN.getRoot().useImediateReward()) {
			// Selecionar a a��o com melhor q_valor aqui
			if (immediateReward == 1.0 || immediateReward == 0.0) {
				Q_new = immediateReward;
			} else {
				for (int action = 0; action < maxVA; action++) {
					act = action;// validActions[action];
					double[] actvec = actions[action];// generateLearnActions(act);
					double[] r = generatePredictionReward();
					double q_value = 0.0;

					if (twoWay) {
						BRAIN.setStimulus(0, actvec);
						BRAIN.setStimulus(1, new double[][] { prevEnv.clone(), r.clone() });
						BRAIN.predict(false);

						double[] predictedReward = BRAIN.getPrediction(1)[1];
						q_value = calculateQValue(act, predictedReward, newEnvironment);
					} else {
						double[] sonar = new double[5];
						double[] objdir = new double[8];
						System.arraycopy(prevEnv, 0, sonar, 0, 5);
						System.arraycopy(prevEnv, 5, objdir, 0, 8);

						BRAIN.setStimulus(0, actvec);
						BRAIN.setStimulus(1, sonar);
						BRAIN.setStimulus(2, new double[][] { objdir.clone(), r.clone() });
						BRAIN.predict(false);

						double[] predictedReward = BRAIN.getPrediction(2)[1];
						q_value = calculateQValue(act, predictedReward, newEnvironment);
					}

					if (q_value >= max_q) {
						max_q = q_value;
					}

					Q_new = max_q;
				}
			}
		} else {
			Q_new = immediateReward;
		}

		double tdErr = 0.0;
		AdaptiveNeuralSystem brain = BRAIN.getRoot();
		tdErr = brain.qLearningRate() * (immediateReward + (brain.qDiscountParameter() * Q_new) - Q_old);

		tdErr = tdErr * (1.0 - Q_old);
		double q_to_learn = Q_old + tdErr;

		double[] actvec = oldActions[actionToLearn];
		double[] r = new double[] { q_to_learn, 1.0 - q_to_learn };

		if (twoWay) {
			BRAIN.setStimulus(0, actvec);
			BRAIN.setStimulus(1, new double[][] { environmentToLearn.clone(), r.clone() });
			BRAIN.predict(true);
		} else {
			double[] sonar = new double[5];
			double[] objdir = new double[8];
			System.arraycopy(environmentToLearn, 0, sonar, 0, 5);
			System.arraycopy(environmentToLearn, 5, objdir, 0, 8);

			BRAIN.setStimulus(0, actvec);
			BRAIN.setStimulus(1, sonar);
			BRAIN.setStimulus(2, new double[][] { objdir.clone(), r.clone() });
			BRAIN.predict(true);
		}
		double[][] neuron = BRAIN.getRoot().getLastActivatedPrediction();
	}
	// -------------------------------------------------------------------------------------------------------
}

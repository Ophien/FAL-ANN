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

import ars.networks.SimpleSurprise;
import ars.networks.AdaptiveNeuralSystem;
import ars.networks.BayesianSurprise;

public abstract class GenericAgent {
	// -------------------------------------------------------------------------------------------------------
	// RFalcon
	protected AdaptiveNeuralSystem brain;
	private double r_new;
	private double r_old;
	protected int totalActions;
	private boolean useSurprise;

	private double[] environmentToLearn;
	private int actionToLearn;
	private double imediate_reward;
	private double Q_old = -1;
	private double Q_new;

	protected abstract double[] generateEnvironment(Object newEnvironment);

	protected abstract double calculateReward(Object newEnvironment);

	protected abstract int actionSelectionStucked();

	protected abstract double[][] generateActionVec(Object newEnvironment);

	protected abstract double simulateActionToGetFinalQValue(int selectedAction, Object environment);

	protected abstract Object executeAction(int selectedAction, Object action);

	protected abstract boolean isValidAction(int selectedAction);
	// -------------------------------------------------------------------------------------------------------
	protected void enableBayes() {
		useSurprise = true;
	}
	// -------------------------------------------------------------------------------------------------------
	protected void disableBayes() {
		useSurprise = false;
	}
	// -------------------------------------------------------------------------------------------------------
	protected void setROld(double value) {
		r_old = value;
	}
	// -------------------------------------------------------------------------------------------------------
	public GenericAgent(int totalActions) {
		this.totalActions = totalActions;
		r_new = 0.0;
		r_old = 0.0;
	}
	// -------------------------------------------------------------------------------------------------------
	public void setBrain(AdaptiveNeuralSystem brain) {
		this.brain = brain;
	}
	// -------------------------------------------------------------------------------------------------------
	public int update() {
		if (brain.useQLearning())
			return qLearningWalk();
		else
			return falconWalk();
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
		r_new = calculateReward(newEnvironment);
	}
	// -------------------------------------------------------------------------------------------------------
	public void setOldReward() {
		r_old = r_new;
	}
	// -------------------------------------------------------------------------------------------------------
	private int falconWalk() {
		setOldReward();

		int selectedAction = -1;
		double[] prevEnv = null;
		do {
			prevEnv = generateEnvironment(null);
			double[] act = generateDirectActions();
			double[] r = generateDirectActionsReward();
			double[][] parameter = new double[][] { prevEnv.clone(), act.clone(), r.clone() };

			brain.setActivity(parameter);
			if (useSurprise)
				selectedAction = selecteActionBayes();
			else
				selectedAction = selectAction();

			if (selectedAction == -1) {
				selectedAction = actionSelectionStucked();
				if (selectedAction == -2)
					return -1;
			}
		} while (selectedAction == -1);

		Object newEnvironment = executeAction(selectedAction, null);

		setReward(newEnvironment);

		if (r_new > r_old) {
			double[] actionTaken = generateLearnActions(selectedAction); // action
																			// //
																			// selected
			double[] r = new double[] { r_new, 1.0 - r_new };
			double[][] parameter = new double[][] { prevEnv.clone(), actionTaken.clone(), r.clone() };
			brain.setActivity(parameter);
			brain.prediction(true);
			brain.neuronReinforcement();
		} else if (r_new <= 0.0 || r_new <= r_old) {
			double[] actionTaken = generateResetActions(selectedAction);
			double[] r = new double[] { 1.0 - r_new, r_new };
			double[][] parameter = new double[][] { prevEnv.clone(), actionTaken.clone(), r.clone() };
			brain.setActivity(parameter);
			brain.prediction(true);
			brain.neuronErosion();
		}

		brain.neuronDecay();
		brain.neuronPrunning();

		return selectedAction;
	}
	// -------------------------------------------------------------------------------------------------------
	private int selectAction() {
		Random rand = new Random();
		double randomAction = rand.nextDouble();

		int selectedAction = -1;

		// Escolhe a��o com a rede
		if (randomAction >= brain.getCurrentEpsilon()) {
			selectedAction = brain.selectAction();

			if (selectedAction != -1) {
				if (!isValidAction(selectedAction))
					selectedAction = -1;
			}
		}

		// Colocar surpresa aqui
		if (randomAction < brain.getCurrentEpsilon() || selectedAction == -1) {
			int[] validActions = new int[totalActions];
			int maxVA = 0;

			for (int i = 0; i < totalActions; i++) {
				if (isValidAction(i)) {
					validActions[maxVA] = i;
					maxVA++;
				}
			}

			if (maxVA > 0) {
				int randomIndex = (int) (rand.nextDouble() * maxVA);
				selectedAction = validActions[randomIndex];
			} else
				selectedAction = -1;
		}

		return selectedAction;
	}
	// -------------------------------------------------------------------------------------------------------
	private int selecteActionBayes() {
		Random rand = new Random();
		double randomAction = rand.nextDouble();

		int selectedAction = -1;

		// Escolhe a��o com a rede
		if (randomAction >= brain.getCurrentEpsilon()) {
			selectedAction = brain.selectAction();

			if (selectedAction != -1) {
				if (!isValidAction(selectedAction))
					selectedAction = -1;
			}
		}

		// Colocar surpresa aqui
		if (randomAction < brain.getCurrentEpsilon() || selectedAction == -1) {
			int[] validActions = new int[totalActions];
			int maxVA = 0;

			for (int i = 0; i < totalActions; i++) {
				if (isValidAction(i)) {
					validActions[maxVA] = i;
					maxVA++;
				}
			}

			// Usar o bayes aqui
			if (maxVA > 0) {
				// Setar neuronio da a��o para o contexto utilizado
				brain.prediction(false);

				SimpleSurprise surpriseMem = brain.getBayesMemmory();
				double maxSurprise = 0.0;
				int maxSurpriseAct = 0;

				for (int action = 0; action < maxVA; action++) {
					int act = validActions[action];
					double[] actToTest = generateLearnActions(act);
					// double actSurprise =
					// surpriseMem.CalculateSurprise(actToTest, 1, false);
					double actSurprise = surpriseMem.calculateSurprise(actToTest, false);

					if (actSurprise >= maxSurprise) {
						maxSurprise = actSurprise;
						maxSurpriseAct = act;
					}
				}

				// Atualiza surpresa
				double[] stimulus = generateLearnActions(maxSurpriseAct);
				surpriseMem.calculateSurprise(stimulus, true);

				selectedAction = maxSurpriseAct;
			} else
				selectedAction = -1;
		}

		return selectedAction;
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
		if(Q_old != -1.0){
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
		if (randomAction >= brain.getCurrentEpsilon()) {
			// Selecionar a a��o com melhor q_valor aqui
			for (int action = 0; action < maxVA; action++) {
				act = action;// validActions[action];
				double[] actvec = actions[action];// generateLearnActions(act);
				double[] r = generatePredictionReward();
				double[][] parameter = new double[][] { prevEnv.clone(), actvec.clone(), r.clone() };

				brain.setActivity(parameter);
				brain.prediction(false);
				double[] predictedReward = brain.getRewardField();
				double q_value = calculateQValue(act, predictedReward, null);

				if (q_value >= max_q) {
					max_q = q_value;
					selectedAction = act;
				}
			}
		}

		// Colocar surpresa aqui
		if (randomAction < brain.getCurrentEpsilon()) {
			act = rand.nextInt(maxVA);
			double[] actvec = actions[act];// generateLearnActions(act);
			double[] r = generatePredictionReward();
			double[][] parameter = new double[][] { prevEnv.clone(), actvec.clone(), r.clone() };

			brain.setActivity(parameter);
			brain.prediction(false);
			double[] predictedReward = brain.getRewardField();
			double q_value = calculateQValue(act, predictedReward, null);
			max_q = q_value;
			selectedAction = act;
		}

		// Nesse momento o Q pertence a a��o selecionada
		environmentToLearn = prevEnv.clone();
		actionToLearn = selectedAction;
		Object newEnvironment = executeAction(selectedAction, actions[selectedAction]);
		imediate_reward = calculateReward(newEnvironment);
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
		double[][] parameter = new double[][] { environmentToLearn.clone(), actvec.clone(), r.clone() };
		brain.setActivity(parameter);
		brain.prediction(true);
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

		// Selecionar a a��o com melhor q_valor aqui
		for (int action = 0; action < maxVA; action++) {
			act = action;// validActions[action];
			double[] actvec = actions[action];// generateLearnActions(act);
			double[] r = generatePredictionReward();
			double[][] parameter = new double[][] { prevEnv.clone(), actvec.clone(), r.clone() };

			brain.setActivity(parameter);
			brain.prediction(false);
			double[] predictedReward = brain.getRewardField();
			double q_value = calculateQValue(act, predictedReward, newEnvironment);

			if (q_value >= max_q) {
				max_q = q_value;
			}
		}

		// Calcular Q_valor final agora
		Q_new = max_q;
		double tdErr = 0.0;
		if (brain.useImediateReward())
			tdErr = brain.qLearningRate() * (imediate_reward + (brain.qDiscountParameter() * Q_new) - Q_old);
		else
			tdErr = brain.qLearningRate() * ((brain.qDiscountParameter() * Q_new) - Q_old);

		tdErr = tdErr * (1.0 - Q_old);
		double q_to_learn = Q_old + tdErr;
		double[] actvec = oldActions[actionToLearn];
		double[] r = new double[] { q_to_learn, 1.0 - q_to_learn };
		double[][] parameter = new double[][] { environmentToLearn.clone(), actvec.clone(), r.clone() };
		brain.setActivity(parameter);
		brain.prediction(true);
	}
	// -------------------------------------------------------------------------------------------------------
}

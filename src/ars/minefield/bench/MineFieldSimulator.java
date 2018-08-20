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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import ars.networks.AdaptiveNeuralSystem;

public class MineFieldSimulator implements MatrixTraverseListener {
	// -------------------------------------------------------------------------------------------------------
	private int iterationCount;
	private int maxIterationCount;
	private MineField mineField;
	//private Agent agent;
	private Agent agent;
	private boolean simulationEnded;
	private boolean success;
	// -------------------------------------------------------------------------------------------------------
	public boolean success() {
		return success;
	}
	public void setAgent(Agent agent){
		this.agent = agent;
		agent.init(this);
	}
	// -------------------------------------------------------------------------------------------------------
	public MineFieldSimulator(int maxIterationCountp, int w, int h, int mines) {
		maxIterationCount = maxIterationCountp;
		mineField = new MineField(w, h, mines);
	}
	// -------------------------------------------------------------------------------------------------------
	public MineField getMineField() {
		return mineField;
	}
	// -------------------------------------------------------------------------------------------------------
	public void simulateAll(boolean sleep, boolean freezeIteration, Integer[] spendedIterations) {

		InputStreamReader systemIn = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(systemIn);
		iterationCount = 0;
		agent.setReward(agent.getPos());
		while (!simulationEnded) {
			agent.update();
			
			iterationCount++;

			UpdateEndCondition(false);

			if (sleep) {
				System.out.println("\n\n\n\n\n\n");
				agent.printSonar();
				mineField.getMatrix().print(this);
				try {

					if (freezeIteration) {
						try {
							System.out.println("Pressione para continuar...");
							reader.readLine();
						} catch (Exception ex) {
						}
					} else
						Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		// set the expended iterations
		spendedIterations[0] = iterationCount;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	public void printValue(int v, vec2 pos) {
		if (agent.getPos().equals(pos)) {
			System.out.print("A");
		} else if (v == MineField.BLANK) {
			System.out.print(".");
		} else if (v == MineField.MINE) {
			System.out.print("O");
		} else if (v == MineField.START_POS) {
			System.out.print("S");
		} else if (v == MineField.END_POS) {
			System.out.print("E");
		}
	}
	// -------------------------------------------------------------------------------------------------------
	private void UpdateEndCondition(boolean debug) {
		if (iterationCount > maxIterationCount) {
			simulationEnded = true;
			if (debug)
				System.out.println("SIMULATION END -- MAX ITERATIONS");
		} else if (agent.isOverMine(agent.getPos())) {
			simulationEnded = true;
			if (debug)
				System.out.println("SIMULATION END -- COLLIDED WITH MINE");
		} else if (agent.isOverEnd(agent.getPos())) {
			simulationEnded = true;
			success = true;
			if (debug)
				System.out.println("SIMULATION END -- TARGET REACHED");
		}

	}
	// -------------------------------------------------------------------------------------------------------
}

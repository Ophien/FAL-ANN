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

public class UAM_Agent extends UAMFalconGenericAgent {
	// -------------------------------------------------------------------------------------------------------
	private vec2 dir = new vec2(0, -1);
	private vec2 targetDir = new vec2();
	private vec2 pos = new vec2();
	private UAMMineFieldSimulator simulator;
	// -------------------------------------------------------------------------------------------------------
	public UAM_Agent(int totalActions, boolean twoWay) {
		super(totalActions);
		setTwoWay(twoWay);
	}
	// -------------------------------------------------------------------------------------------------------
	public UAM_Agent(int totalActions) {
		super(totalActions);
	}
	// -------------------------------------------------------------------------------------------------------
	public void init(UAMMineFieldSimulator simulator)
	{
		this.simulator = simulator;
		pos = simulator.getMineField().getStartPos();
		computeTargetDir();
	}
	// -------------------------------------------------------------------------------------------------------
	public void printSonar() {
		float[] sonarValues = sonarDistance();

		for (float d : sonarValues)
			System.out.print(d + "\t");
		System.out.println();
		System.out.println(dir);
	}
	// -------------------------------------------------------------------------------------------------------
	public float[] sonarDistance() {
		Matrix matrix = simulator.getMineField().getMatrix();

		float[] result = new float[5];

		vec2[] sonarIterator = calcSonarConsultPos(dir);
		vec2 posIterator;
		for (int i = 0; i < 5; i++) {
			int count = 0;
			posIterator = new vec2(pos);
			posIterator.add(sonarIterator[i]);
			while (matrix.isInRange(posIterator)) {
				int posValue = matrix.get(posIterator);
				if (posValue == MineField.MINE) {
					break;
				}
				if (posValue == MineField.END_POS) {
					count = -1;
					break;
				}
				count++;
				posIterator.add(sonarIterator[i]);
			}

			if (count == -1)
				result[i] = 0;
			else
				result[i] = 1.0f / (float) (count + 1);
		}
		return result;
	}
	// -------------------------------------------------------------------------------------------------------
	public void setTargetDir(vec2 v) {
		targetDir = v;
	}
	// -------------------------------------------------------------------------------------------------------
	public vec2 getTargetDir() {
		return targetDir;
	}
	// -------------------------------------------------------------------------------------------------------
	public void setDir(vec2 v) {
		dir = v;
	}
	// -------------------------------------------------------------------------------------------------------
	public vec2 getDir() {
		return dir;
	}
	// -------------------------------------------------------------------------------------------------------
	public void setPos(vec2 v) {
		pos = v;
	}
	// -------------------------------------------------------------------------------------------------------
	public vec2 getPos() {
		return pos;
	}
	// -------------------------------------------------------------------------------------------------------
	public int[] getDirVec() {
		return computeDir(dir);
	}
	// -------------------------------------------------------------------------------------------------------
	public int[] getTargetDirVec() {
		return computeDir(targetDir);
	}
	// -------------------------------------------------------------------------------------------------------
	public static final vec2 forward = new vec2(-1, 0);
	// -------------------------------------------------------------------------------------------------------
	public static vec2 transformVec(vec2 agentDir, vec2 targetDir) {
		double lookAngle = -Math.atan2(agentDir.y, agentDir.x);
		lookAngle += Math.PI / 2.0;
		if (lookAngle < 0.0)
			lookAngle += 2 * Math.PI;

		double tAngle = -Math.atan2(targetDir.y, targetDir.x);
		tAngle += Math.PI / 2.0;
		if (tAngle < 0.0)
			tAngle += 2 * Math.PI;

		double res = (Math.PI / 2.0) - (tAngle - lookAngle);

		double x = -Math.sin(res);
		double y = Math.cos(res);
		targetDir = new vec2((int) (Math.round(x) + 0.5 * Math.signum(x)),
				(int) (Math.round(y) + 0.5 * Math.signum(y)));

		return targetDir;
	}
	// -------------------------------------------------------------------------------------------------------
	public void computeTargetDir() {
		vec2 endPos = simulator.getMineField().getEndPos();
		targetDir.x = sign(endPos.x - pos.x);
		targetDir.y = sign(endPos.y - pos.y);

		targetDir = transformVec(dir, targetDir);
	}
	// -------------------------------------------------------------------------------------------------------
	public boolean isOverEnd(vec2 intentPos) {
		return simulator.getMineField().getMatrix().get(intentPos) == MineField.END_POS;
	}
	// -------------------------------------------------------------------------------------------------------
	public boolean isOverMine(vec2 intentPos) {
		return simulator.getMineField().getMatrix().get(intentPos) == MineField.MINE;
	}
	// -------------------------------------------------------------------------------------------------------
	public boolean willStuck(vec2[] sonarIterator) {
		Matrix matrix = simulator.getMineField().getMatrix();

		for (int i = 0; i < 5; i++) {
			vec2 targetPos = new vec2(pos);
			targetPos.add(sonarIterator[i]);
			if (matrix.isInRange(targetPos)) {
				return false;
			}
		}

		return true;
	}
	// -------------------------------------------------------------------------------------------------------
	public static int[] computeDir(vec2 dir) {
		int[] result = new int[8];
		double angle = (-Math.atan2(dir.y, dir.x) / Math.PI + Math.PI) * 4.0;
		int iAngle = ((int) angle) % 8;
		for (int i = 0; i < 8; i++) {
			result[i] = (iAngle == i) ? 1 : 0;
		}
		return result;
	}
	// -------------------------------------------------------------------------------------------------------
	public static int sign(int a) {
		if (a > 0) {
			return 1;
		} else if (a < 0) {
			return -1;
		}
		return 0;
	}
	// -------------------------------------------------------------------------------------------------------
	public static vec2[] calcSonarConsultPos(vec2 dir) {
		vec2[] result = new vec2[5];

		double lookAngle = -Math.atan2(dir.y, dir.x);
		lookAngle += Math.PI / 2.0;
		double x, y;
		for (int i = 0; i < 5; i++) {
			x = Math.cos(lookAngle);
			y = -Math.sin(lookAngle);
			vec2 toConsult = new vec2((int) (Math.round(x) + 0.5 * Math.signum(x)),
					(int) (Math.round(y) + 0.5 * Math.signum(y)));
			result[i] = toConsult;
			lookAngle -= Math.PI / 4.0;

		}
		return result;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected double[] generateEnvironment(Object newEnvironment) {
		float[] sonarDst = sonarDistance();
		int[] targetDirVec = getTargetDirVec();

		double[] parameters = new double[5 + 8];
		for (int i = 0; i < 5; i++) {
			parameters[i] = (double) sonarDst[i];
		}

		for (int i = 0; i < 8; i++) {
			parameters[i + 5] = (double) targetDirVec[i];
		}

		return parameters;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected double calculateReward(Object newEnvironment) {
		vec2 intentPos = (vec2) newEnvironment;
		Matrix matrix = simulator.getMineField().getMatrix();

		if (!matrix.isInRange(intentPos))
			return 0.0;

		if (isOverEnd(intentPos))
			return 1.0;

		if (isOverMine(intentPos))
			return 0.0;

		if (BRAIN.getRoot().useImediateReward())
			return 1.0 / (1.0 + pos.EuclideanDist(simulator.getMineField().getEndPos()));
		return 0.0;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected int actionSelectionStucked() {
		dir.mult(-1);
		computeTargetDir();
		return -1;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected Object executeAction(int selectedAction, Object actionObj) {
		if (actionObj != null) {
			double[] act = (double[]) actionObj;
			int i = 0;
			for (; i < act.length; i++)
				if (act[i] == 1.0)
					break;
			selectedAction = i;
		}

		Matrix matrix = simulator.getMineField().getMatrix();
		vec2[] sonarIterator = calcSonarConsultPos(dir);

		vec2 targetPos = new vec2();
		targetPos.copy(pos);
		targetPos.add(sonarIterator[selectedAction]);
		if (matrix.isInRange(targetPos)) {
			// move
			dir.copy(sonarIterator[selectedAction]);
			pos.add(dir);
			computeTargetDir();
			return targetPos;
		}

		return null;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected boolean isValidAction(int selectedAction) {
		Matrix matrix = simulator.getMineField().getMatrix();
		vec2[] sonarIterator = calcSonarConsultPos(dir);

		vec2 targetPos = new vec2();
		targetPos.copy(pos);
		targetPos.add(sonarIterator[selectedAction]);
		return matrix.isInRange(targetPos);
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected double simulateActionToGetFinalQValue(int selectedAction, Object environment) {
		Matrix matrix = simulator.getMineField().getMatrix();
		vec2[] sonarIterator = calcSonarConsultPos(dir);

		vec2 targetPos = new vec2();
		targetPos.copy(pos);
		targetPos.add(sonarIterator[selectedAction]);
		double q_value = -1.0;

		if (matrix.isInRange(targetPos)) {
			// move
			if (isOverMine(targetPos))
				q_value = 0.0;
			if (isOverEnd(targetPos))
				q_value = 1.0;
		}

		return q_value;
	}
	// -------------------------------------------------------------------------------------------------------
	@Override
	protected double[][] generateActionVec(Object newEnvironment) {
		double[][] actions = null;
		int[] validActions = new int[totalActions];
		int maxVA = 0;

		do {
			validActions = new int[totalActions];

			for (int i = 0; i < totalActions; i++) {
				if (isValidAction(i)) {
					validActions[maxVA] = i;
					maxVA++;
				}
			}

			if (maxVA == 0) {
				actionSelectionStucked();
			}
		} while (maxVA == 0);

		actions = new double[maxVA][totalActions];
		for (int i = 0; i < maxVA; i++) {
			double[] act = generateLearnActions(validActions[i]);
			actions[i] = act;
		}

		return actions;
	}
	// -------------------------------------------------------------------------------------------------------
}

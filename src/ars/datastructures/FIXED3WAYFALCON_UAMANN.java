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

import java.util.HashMap;
import java.util.Map;

import ars.featureselection.EnvironmentBuilder;
import ars.networks.AdaptiveNeuralSystem;


public class FIXED3WAYFALCON_UAMANN {
	public Map<String, AdaptiveNeuralSystem> brain;
	
	private double minQEpsilon = 0.0;
	private double QEpsilonDecay = 0.0005;
	private double QEpsilon = 0.5;
	private boolean useImediateReward = false;
	private double qDiscountParameter = 0.1;
	private double qLearningRate = 0.5;
	private boolean useQLearning = false;
	
	public double getChannel2MeanNeuronSize(){
		double size = 0;
		
		for(AdaptiveNeuralSystem f : brain.values())
			size += f.getNeuronsSize();
		
		size /= (double)brain.size();
		
		return size;
	}
	
	public int getChannel1Size(){
		return brain.size();
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

	public FIXED3WAYFALCON_UAMANN() {	
		brain = new HashMap<>();
	}
	
	public AdaptiveNeuralSystem prediction(String key, ChannelDescription config, boolean overwrite, boolean learning){
		AdaptiveNeuralSystem net = getBrainArea(key, config, overwrite);
		
		net.setActivity(config.generateFullStimulus());
		net.prediction(learning);
		
		return net;
	}
	
	public AdaptiveNeuralSystem prediction(ChannelDescription config, boolean overwrite, boolean learning){
		AdaptiveNeuralSystem net = getBrainArea(config, overwrite);
		
		net.setActivity(config.generateFullStimulus());
		net.prediction(learning);
		
		return net;
	}
	
	public AdaptiveNeuralSystem resetLastNeuronActionField(ChannelDescription config, boolean overwrite, boolean learning){
		AdaptiveNeuralSystem net = getBrainArea(config, overwrite);
		
		net.setActivity(config.generateFullStimulus());
		net.resetLastNeuronAction();
		
		return net;
	}
	
	public void callDecayAndPrunning(ChannelDescription config, boolean overwrite){
		AdaptiveNeuralSystem net = getBrainArea(config, overwrite);
		
		net.neuronDecay();
		net.neuronPrunning();
	}
	
	public void callReinforcement(ChannelDescription config, boolean overwrite){
		AdaptiveNeuralSystem net = getBrainArea(config, overwrite);
		
		net.neuronReinforcement();
	}
	
	public void callErosion(ChannelDescription config, boolean overwrite){
		AdaptiveNeuralSystem net = getBrainArea(config, overwrite);
		
		net.neuronErosion();
	}
	
	public int reactiveSelecAction(ChannelDescription config, boolean overwrite, boolean learning){
		AdaptiveNeuralSystem net = getBrainArea(config, overwrite);
		
		net.setActivity(config.generateFullStimulus());

		int selectedAction = net.selectAction();
		
		return selectedAction;
	}

	public AdaptiveNeuralSystem getBrainArea(String key, ChannelDescription config, boolean overwrite) {
		AdaptiveNeuralSystem net = null;
		
		if(!brain.containsKey(key))
			net = createNewBrainArea(key, config);
		else if(overwrite)
			net = createNewBrainArea(key, config);
		else
			net = brain.get(key);
		
		return net;
	}
	
	public AdaptiveNeuralSystem getBrainArea(ChannelDescription config, boolean overwrite) {
		AdaptiveNeuralSystem net = null;
		
		String key = config.getStimulusKey();
		
		if(!brain.containsKey(key))
			net = createNewBrainArea(config);
		else if(overwrite)
			net = createNewBrainArea(config);
		else
			net = brain.get(key);
		
		return net;
	}

	public AdaptiveNeuralSystem createNewBrainArea(ChannelDescription config) {
		AdaptiveNeuralSystem net = new AdaptiveNeuralSystem(
				config.featuresSizes, config.adaptiveVigilanceRaising, config.activeFields, 
				config.temperatureOp, config.learningOp, config.fieldsClass, 
				config.learningRate, config.learnVigilances, config.performVigilances, 
				config.gammas, config.alphas, config.fuzzyReadout, config.resonanceToPredict, 
				config.useDirectReward, config.useQLearning, config.bayesSize);

		String key = config.getStimulusKey();
		
		brain.put(key, net);
		
		return net;
	}
	
	public AdaptiveNeuralSystem createNewBrainArea(String key, ChannelDescription config) {
		AdaptiveNeuralSystem net = new AdaptiveNeuralSystem(
				config.featuresSizes, config.adaptiveVigilanceRaising, config.activeFields, 
				config.temperatureOp, config.learningOp, config.fieldsClass, 
				config.learningRate, config.learnVigilances, config.performVigilances, 
				config.gammas, config.alphas, config.fuzzyReadout, config.resonanceToPredict, 
				config.useDirectReward, config.useQLearning, config.bayesSize);

		brain.put(key, net);
		
		return net;
	}
}

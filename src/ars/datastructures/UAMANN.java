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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ars.networks.AdaptiveNeuralSystem;

public class UAMANN {
	public UAMANN(){
	}
	
	public void initialize(){
		ChannelDescription rootConfig = channels.get(0).channelConfiguration;
		root = new AdaptiveNeuralSystem(rootConfig, this, 0);
		//root.setEpsilonDecayParameters(0.0, 1.0, 0.005);
		root.useUAM(true);
	}
	
	public void predict(boolean learn) {
		// clone last activation path
		oldActivationPath.clear();
		for(AdaptiveNeuralSystem area : activationPath)
			oldActivationPath.add(area);
		
		// clear old activation path
		activationPath.clear();
		
		// use root channel to configure first prediction
		UAMChannel currentChannel = channels.get(0);
		
		// configure root for operation
		AdaptiveNeuralSystem currentArea = root;
		
		// set experienced activity
		currentArea.setActivity(currentChannel.receivedStimulus);
		
		// perform prediction
		currentArea.prediction(learn);
		
		// set predicted expectation
		currentChannel.predictions = currentArea.getLastActivatedPrediction();
		
		// configure activation path
		activationPath.add(currentArea);
		
		// update opration area
		currentArea = currentArea.getLastActivatedArea();
		
		// visit all channels except the root
		int channelsToVisit = channels.size();
		for(int i = 1; i < channelsToVisit; i++){
			// configure channel for prediction
			currentChannel = channels.get(i);
			
			// set experienced activity
			currentArea.setActivity(currentChannel.receivedStimulus);
			
			// perform prediction
			currentArea.prediction(learn);
			
			// set predicted expectation
			currentChannel.predictions = currentArea.getLastActivatedPrediction();
			
			// configure activation path
			activationPath.add(currentArea);
			
			// update opration area
			if(i != channelsToVisit - 1)
				currentArea = currentArea.getLastActivatedArea();
		}
	}
	
	public int getTotalAmountOfChannels(){
		return channels.size();
	}

	public void insertChannel(ChannelDescription config){
		channels.add(new UAMChannel(config));
	}
	
	public void setStimulus(int channel, double[][] stimulus){
		if (channel < channels.size())
			channels.get(channel).receivedStimulus = stimulus;
	}
	
	public void setStimulus(int channel, double[] stimulus){
		double[][] receivedStimulus = new double[1][];
		receivedStimulus[0] = stimulus;
		if (channel < channels.size())
			channels.get(channel).receivedStimulus = receivedStimulus;
	}
	
	public double[][] getPrediction(int channel){
		if (channel < channels.size())
			return channels.get(channel).predictions;
		return null; 
	}

	public UAMChannel getChannel(int channel) {
		if (channel < channels.size())
			return channels.get(channel);
		return null;
	}
	
	public AdaptiveNeuralSystem getRoot(){
		return root;
	}
	
	public void getAverageNeuronSize(HashMap<Integer,Pair>[] sizesAveragesPerChannel) {
		sizesAveragesPerChannel[0] = new HashMap<Integer,Pair>();
		
		List<AdaptiveNeuralSystem> links = root.getAreaLinks();
		List<AdaptiveNeuralSystem> rootLinks = new ArrayList<>();
		rootLinks.addAll(links);
		
		sizesAveragesPerChannel[0].putIfAbsent(root.getChannel(), new Pair(rootLinks.size(), 1.0));
		
		while(rootLinks.size() > 0) {
			// set the current area to be processed
			AdaptiveNeuralSystem area = rootLinks.get(0);
			
			// the current channel to be processed
			int currentChannel = area.getChannel();
			
			// increment if element already exists in the base
			if(!sizesAveragesPerChannel[0].containsKey(currentChannel))
				sizesAveragesPerChannel[0].put(currentChannel, new Pair(area.getAreaLinks().size(), 1.0));
			else {
				Pair currentCount = sizesAveragesPerChannel[0].get(currentChannel);
				currentCount.first += area.getNeuronsSize();
				currentCount.second += 1.0;
				sizesAveragesPerChannel[0].put(currentChannel, currentCount);
			}

			// do a depth first search
			List<AdaptiveNeuralSystem> sonLinks = rootLinks.get(0).getAreaLinks();
			rootLinks.addAll(sonLinks);
			
			// remove the element
			rootLinks.remove(0);
		}
	}
	
	private List<AdaptiveNeuralSystem> oldActivationPath = new ArrayList<>();
	private List<AdaptiveNeuralSystem> activationPath = new ArrayList<>();
	private List<UAMChannel> channels = new ArrayList<>();
	private AdaptiveNeuralSystem root;
}

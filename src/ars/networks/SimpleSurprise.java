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

import java.util.HashMap;

public class SimpleSurprise {
	HashMap<String, Double> counter;
	
	public SimpleSurprise() {
		counter = new HashMap<>();
	}
	
	private String buildKey(double[] input){
		String key = "";
		for(int i = 0; i < input.length - 1; i++){
			key += Double.toString(input[i]) + "|";
		}
		key += Double.toString(input[input.length - 1]);
		
		return key;
	}
	
	public double calculateSurprise(double[] input, boolean insert){
		String key = buildKey(input);
		
		Double value = counter.get(key);
		Double surprise = 1.0;
		
		if(value != null)
			surprise = surprise / value;
		
		if(insert)
		{
			value = counter.putIfAbsent(key, 1.0);
			if(value != null)
				counter.replace(key, value + 1.0);
		}
		
		return surprise;
	}
	
	public double calculateSurpriseExp(double[] input, boolean insert){
		String key = buildKey(input);
		
		Double value = counter.get(key);
		Double surprise = 1.0;
		
		if(value != null)
			surprise = surprise / Math.pow(value, Math.E);
		
		if(insert)
		{
			value = counter.putIfAbsent(key, 1.0);
			if(value != null)
				counter.replace(key, value + 1.0);
		}
		
		return surprise;
	}
}

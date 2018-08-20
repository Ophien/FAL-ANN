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

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

package ars.minefield.bench;

import java.util.Random;
public class vec2{
	// -------------------------------------------------------------------------------------------------------
    public int x; 
    public int y;
	// -------------------------------------------------------------------------------------------------------
    public vec2(){
        
    }
	// -------------------------------------------------------------------------------------------------------
    public String toString(){
    	return Integer.toString(x) + " " + Integer.toString(y);
    }
	// -------------------------------------------------------------------------------------------------------
    public vec2(int x, int y){
        this.x = x;
        this.y = y;
    }
	// -------------------------------------------------------------------------------------------------------
    public vec2(vec2 v){
        this.x = v.x;
        this.y = v.y;
    }
	// -------------------------------------------------------------------------------------------------------
    public void add(vec2 v) {
        x += v.x;
        y += v.y;
    }
	// -------------------------------------------------------------------------------------------------------
    public void mult(int v) {
        x *= v;
        y *= v;
    }
	// -------------------------------------------------------------------------------------------------------
    public void copy(vec2 v) {
        x = v.x;
        y = v.y;
    }
	// -------------------------------------------------------------------------------------------------------
    public double ManhattanDist( vec2 b ) {
    	double result = Math.max(Math.abs(x-b.x),Math.abs(y-b.y));
        return result;
    }
	// -------------------------------------------------------------------------------------------------------
    public double EuclideanDist( vec2 b ) {
        int fx = x - b.x;
        int fy = y - b.y;
        fx *= fx;
        fy *= fy;
        double res = (double)(fx + fy);
        return res;
    }
	// -------------------------------------------------------------------------------------------------------
    public static vec2 Random(int xMax, int yMax) {
        Random random = new Random();
        return new vec2(random.nextInt(xMax),random.nextInt(yMax));
    }
	// -------------------------------------------------------------------------------------------------------
    public boolean equals(vec2 v){
        return x == v.x && y == v.y;
    }
	// -------------------------------------------------------------------------------------------------------
}

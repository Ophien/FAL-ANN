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
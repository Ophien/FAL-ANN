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

public class Matrix {
	// -------------------------------------------------------------------------------------------------------
    int [] matrix;
    int w;
    int h;
	// -------------------------------------------------------------------------------------------------------
    public Matrix(int w, int h){
        this.w = w;
        this.h = h;
        matrix = new int[w*h];
    }
	// -------------------------------------------------------------------------------------------------------
    public void print( MatrixTraverseListener listener ) {
        for(int y=0;y<h;y++){
            for(int x=0;x<h;x++){
                int v = matrix[x+y*w];
                listener.printValue(v, new vec2(x,y));
                System.out.print(" ");
            }
            System.out.println();
        }
    }
	// -------------------------------------------------------------------------------------------------------
    public boolean isInRange(vec2 p) {
        return p.x >= 0 && p.x < w && p.y >=0 && p.y < h;
    }
	// -------------------------------------------------------------------------------------------------------
    public int getWidth() {
        return w;
    }
	// -------------------------------------------------------------------------------------------------------
    public int getHeight() {
        return h;
    }
	// -------------------------------------------------------------------------------------------------------
    public int get(int x, int y) {
        return matrix[x+y*w];
    }
	// -------------------------------------------------------------------------------------------------------
    public int get(vec2 v) {
        return get(v.x,v.y);
    }
	// -------------------------------------------------------------------------------------------------------
    public void set(int x, int y, int v) {
        matrix[x+y*w] = v;
    }
	// -------------------------------------------------------------------------------------------------------
    public void set(vec2 vec,int v) {
        set(vec.x,vec.y,v);
    }
	// -------------------------------------------------------------------------------------------------------
}

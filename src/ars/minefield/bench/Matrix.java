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

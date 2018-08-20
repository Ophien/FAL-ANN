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

public class MineField {
	// -------------------------------------------------------------------------------------------------------
    public static final int BLANK = 0;
    public static final int MINE = 1;
    public static final int WALL = 2;
    public static final int END_POS = 3;
    public static final int START_POS = 4;
	// -------------------------------------------------------------------------------------------------------
    private vec2 StartPos;
    private vec2 EndPos;
	// -------------------------------------------------------------------------------------------------------
    public vec2 getStartPos() {
        return StartPos;
    }
	// -------------------------------------------------------------------------------------------------------
    public vec2 getEndPos() {
        return EndPos;
    }
	// -------------------------------------------------------------------------------------------------------
    
    public vec2 randOnBlankPos(){
        vec2 result = vec2.Random(matrix.getWidth(), matrix.getHeight());
        while (matrix.get(result) != BLANK)
            result = vec2.Random(matrix.getWidth(), matrix.getHeight());
        return result;
    }
	// -------------------------------------------------------------------------------------------------------
    private Matrix matrix;
    public MineField(int w, int h, int mineCount) {
        matrix = new Matrix(w,h);
        
       for(int i=0;i<mineCount;i++)
           matrix.set(randOnBlankPos(), MINE);
       
       EndPos = randOnBlankPos();
       matrix.set(EndPos, END_POS);
       
       StartPos = randOnBlankPos();
       matrix.set(StartPos, START_POS);
        
    }
	// -------------------------------------------------------------------------------------------------------
    public Matrix getMatrix() {
        return matrix;
    }
	// -------------------------------------------------------------------------------------------------------
}

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

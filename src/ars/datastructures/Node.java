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
import java.util.List;

public class Node<T>{
	// -------------------------------------------------------------------------------------------------------
	public Node(){
		parents = new ArrayList<>();
		children = new ArrayList<>();
	}
	// -------------------------------------------------------------------------------------------------------
	public void addChildren(Node<?> children){
		children.parents.add(this);
		this.children.add(children);
	}
	// -------------------------------------------------------------------------------------------------------
	public boolean visited;
	public T value;
	public List<Node<?>> parents;
	public List<Node<?>> children;
	// -------------------------------------------------------------------------------------------------------
}

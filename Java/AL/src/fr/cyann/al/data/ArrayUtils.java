/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.data;

import java.util.List;

/**
 <p>
 @author cyann
 */
public class ArrayUtils {

	public static float[] toArrayNumber(List<MutableVariant> list) {
		int size = list.size();
		float[] result = new float[size];

		for (int i = 0; i < size; i++) {
			result[i] = list.get(i).getNumber();
		}

		return result;
	}

}

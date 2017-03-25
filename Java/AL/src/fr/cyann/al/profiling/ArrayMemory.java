/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.profiling;

import fr.cyann.al.data.MutableVariant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author caronyn
 */
public class ArrayMemory {

	private static class MutableVariantProxy {

		private MutableVariant mv;

		public MutableVariantProxy(MutableVariant mv) {
			this.mv = mv;
		}
	}
	private static final int SIGNIFICANT_LOOP = 5000;
	private static Map<Integer, MutableVariant> javaMap;

	private static void initHashMap() {
		Map<Integer, MutableVariant> map = new HashMap<Integer, MutableVariant>(1000);
		map.put(4, new MutableVariant("Entry 1"));
		map.put(2, new MutableVariant("Entry 2"));
		map.put(9, new MutableVariant("Entry 3"));
		map.put(14, new MutableVariant("Entry 4"));
		map.put(44, new MutableVariant("Entry 5"));
		map.put(23, new MutableVariant("Entry 6"));
		map.put(7, new MutableVariant("Entry 7"));
		javaMap = map;
	}

	private static void initTreeMap() {
		Map<Integer, MutableVariant> map = new TreeMap<Integer, MutableVariant>();
		map.put(4, new MutableVariant("Entry 1"));
		map.put(2, new MutableVariant("Entry 2"));
		map.put(9, new MutableVariant("Entry 3"));
		map.put(14, new MutableVariant("Entry 4"));
		map.put(44, new MutableVariant("Entry 5"));
		map.put(23, new MutableVariant("Entry 6"));
		map.put(7, new MutableVariant("Entry 7"));
	}
		
	private static void initArray() {
		MutableVariant[] mvs1 = new MutableVariant[1000];
		for (int i = 0; i < mvs1.length; i++) {
			if (javaMap.get(i) != null) {
				mvs1[i] = new MutableVariant(javaMap.get(i));
			}
		}

		MutableVariant[] mvs2 = new MutableVariant[100];
		for (int i = 0; i < mvs2.length; i++) {
			if (javaMap.get(i) != null) {
				mvs2[i] = new MutableVariant(javaMap.get(i));
			}
		}

		MutableVariant[][] mvss = new MutableVariant[10][100];
		mvss[5] = mvs1;
		mvss[7] = mvs2;
	}

	private static void initArrayList() {
		ArrayList<MutableVariant> mvs = new ArrayList<MutableVariant>(10000);

		for (int i = 0; i < 100; i++) {
			if (javaMap.get(i) != null) {
				mvs.add(new MutableVariant(javaMap.get(i)));
			} else {
				mvs.add(null);
			}
		}

	}
	private static Iterator<MutableVariant> iterator;

	private static void initIndexArray() {
		char[] indexes = new char[10000];
		MutableVariant[] mvs = new MutableVariant[javaMap.size()];

		iterator = javaMap.values().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			mvs[i] = new MutableVariant(iterator.next());
			i++;
		}

	}

	/**
	 * Main methode, programm entry point.
	 *
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {

		initHashMap();
		for (int i = 0; i < 5000; i++) {
			//initHashMap();
			// ON GARDE TREE MAP
			initTreeMap();
			//initArray();
			//initArrayList();
			//initIndexArray();
			//initIntMutableVariantArray();
		}

	}
}

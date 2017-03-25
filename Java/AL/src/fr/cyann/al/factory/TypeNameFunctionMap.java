/*
 * Copyright (C) 2013 CyaNn
 * License modality not yet defined.
 */
package fr.cyann.al.factory;

import fr.cyann.al.data.FunctionInstance;
import fr.cyann.al.data.Identifiers;
import fr.cyann.al.data.Types;
import fr.cyann.al.visitor.AbstractRuntime;
import fr.cyann.al.visitor.RuntimeContext;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The TypeNameFunctionMap class. Creation date: 16 mars 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class TypeNameFunctionMap {

	private final Map<Types, Map<Integer, FunctionInstance>> map;
	private final AbstractRuntime runtime;
	private final RuntimeContext context;

	public TypeNameFunctionMap(AbstractRuntime runtime, RuntimeContext context) {
		map = new EnumMap<Types, Map<Integer, FunctionInstance>>(Types.class);
		this.runtime = runtime;
		this.context = context;
	}

	public void put(Types type, FunctionInstance value) {
		Map<Integer, FunctionInstance> fmap;
		if (map.containsKey(type)) {
			fmap = map.get(type);
		} else {
			fmap = new TreeMap<Integer, FunctionInstance>();
			map.put(type, fmap);
		}

		int id = Identifiers.getID(value.decl.getName());
		fmap.put(id, value);
		value.decl.injectVisitor(runtime);
		value.decl.visite(context);
	}

	public FunctionInstance get(Types type, Integer ident) {
		Map<Integer, FunctionInstance> typeMap = map.get(type);
		if (typeMap == null) {
			return null;
		}
		return typeMap.get(ident);
	}

	public Map<Types, Map<Integer, FunctionInstance>> getAll() {
		return map;
	}

}

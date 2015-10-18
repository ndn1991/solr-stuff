package com.ndn.solr.stuff.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

public class JsonMap extends HashMap<String, Object>implements com.ndn.solr.stuff.common.Loggable {
	private static final long serialVersionUID = -3811879513990560717L;

	public JsonMap() {
		super();
	}

	public JsonMap(Map<String, Object> map) {
		map.forEach((key, value) -> {
			String[] elements = key.split("\\.");
			if (elements.length == 1) {
				this.put(elements[0], value);
			} else {
				try {
					if (!this.containsKey(elements[0])) {
						this.put(elements[0], new JsonMap());
					}
					JsonMap parent = (JsonMap) this.get(elements[0]);
					int size = elements.length - 1;
					for (int i = 1; i < size; i++) {
						Object o = parent.get(elements[i]);
						if (o == null) {
							parent.put(elements[i], new JsonMap());
							parent = (JsonMap) parent.get(elements[i]);
						} else {
							parent = (JsonMap) o;
						}
					}
					parent.put(elements[size], value);
				} catch (ClassCastException e) {
					throw new RuntimeException("Sai cu phap", e);
				}
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NamedList toNamedList() {
		NamedList res = new NamedList();
		for (Entry<String, Object> e : this.entrySet()) {
			if (e.getValue() instanceof JsonMap) {
				res.add(e.getKey(), ((JsonMap) e.getValue()).toOrderMap());
			} else {
				res.add(e.getKey(), e.getValue());
			}
		}

		return res;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SimpleOrderedMap toOrderMap() {
		SimpleOrderedMap res = new SimpleOrderedMap();
		for (Entry<String, Object> e : this.entrySet()) {
			if (e.getValue() instanceof JsonMap) {
				res.add(e.getKey(), ((JsonMap) e.getValue()).toOrderMap());
			} else {
				res.add(e.getKey(), e.getValue());
			}
		}

		return res;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<SimpleOrderedMap> toOrderMap(NamedList nl) {
		List<SimpleOrderedMap> res = new ArrayList<SimpleOrderedMap>();
		for (int i = 0; i < nl.size(); i++) {
			SimpleOrderedMap som = new SimpleOrderedMap();
			som.add(nl.getName(i), nl.getVal(i));
			res.add(som);
		}

		return res;
	}
}

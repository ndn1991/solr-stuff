package com.ndn.solr.stuff.common.vo;

import java.util.HashMap;

public class SingleMap extends HashMap<String, Object> implements Comparable<Object> {
	private static final long serialVersionUID = 1L;

	public SingleMap(String s, Object o) {
		super();
		put(s, o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Object other) {
		try {
			if (other != null) {
				if (other instanceof Comparable<?>) {
					return ((Comparable<Object>) other).compareTo(values().iterator().next());
				}
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

}

package com.ndn.solr.stuff.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

public class CommonUltils {

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

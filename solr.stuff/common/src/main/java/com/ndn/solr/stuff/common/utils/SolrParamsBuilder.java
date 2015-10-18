package com.ndn.solr.stuff.common.utils;

import java.util.Arrays;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.ndn.solr.stuff.common.vo.Tupple2;

public class SolrParamsBuilder {
	private ModifiableSolrParams params = null;

	public SolrParamsBuilder() {
		params = new ModifiableSolrParams();
	}

	public SolrParamsBuilder(ModifiableSolrParams params) {
		this.params = params;
	}

	public SolrParams getParams() {
		return this.params;
	}

	public SolrParamsBuilder offset(Integer start) {
		if (start != null) {
			params.set(CommonParams.START, start.toString());
		}
		return this;
	}

	public SolrParamsBuilder limit(Integer limit) {
		if (limit != null) {
			params.set(CommonParams.ROWS, limit.toString());
		}
		return this;
	}

	public SolrParamsBuilder sort(String by, String order) {
		if (!Strings.isNullOrEmpty(by) && !Strings.isNullOrEmpty(order)) {
			params.set(CommonParams.SORT, by + " " + order);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public SolrParamsBuilder sortMultiField(Tupple2<String, String>... sorts) {
		if (sorts != null && sorts.length != 0) {
			String[] elements = new String[sorts.length];
			for (int i = 0; i < sorts.length; i++) {
				Tupple2<String, String> sort = sorts[i];
				elements[i] = sort._1() + " " + sort._2();
			}
			params.set(CommonParams.SORT, Joiner.on(',').join(elements));
		}
		return this;
	}

	public SolrParamsBuilder filter(String fieldName, String... fieldValues) {
		fieldValues = standardize(fieldValues);
		if (fieldValues.length != 0) {
			params.add(CommonParams.FQ, Joiner.on(" OR ").join(getCriterias(fieldName, fieldValues)));
		}
		return this;
	}

	public SolrParamsBuilder filterWithTag(String field, String tag, String... values) {
		values = standardize(values);
		if (values.length != 0) {
			params.add(CommonParams.FQ,
					"{!tag=" + tag + "}(" + Joiner.on(" OR ").join(getCriterias(field, values)) + ")");
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public SolrParamsBuilder filterRange(String fieldName, Tupple2<String, String>... values) {
		if (values != null && values.length != 0) {
			params.add(CommonParams.FQ, Joiner.on(" OR ").join(getRangeCriterias(fieldName, values)));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public SolrParamsBuilder filterRangeWithTag(String field, String tag, Tupple2<String, String>... values) {
		if (values != null && values.length != 0) {
			params.add(CommonParams.FQ,
					"{!tag=" + tag + "}(" + Joiner.on(" OR ").join(getRangeCriterias(field, values)) + ")");
		}
		return this;
	}

	public SolrParamsBuilder filterWithExistField(String... values) {
		values = standardize(values);
		if (values.length != 0) {
			params.add(CommonParams.FQ, Joiner.on(" OR ").join(values));
		}
		return this;
	}

	public SolrParamsBuilder facet(Integer minCount) {
		params.set("facet", true);
		if (minCount != null) {
			params.set("facet.mincount", minCount);
		}
		return this;
	}

	public SolrParamsBuilder keyword(String keyword) {
		if (!Strings.isNullOrEmpty(keyword)) {
			params.add(CommonParams.Q, keyword);
		} else {
			params.add(CommonParams.Q, "*:*");
		}
		return this;
	}

	public SolrParamsBuilder facetField(String field, String... excludes) {
		excludes = standardize(excludes);
		if (excludes.length == 0) {
			params.add("facet.field", field);
		} else {
			params.add("facet.field", "{!ex=" + Joiner.on(',').join(excludes) + "}" + field);
		}
		return this;
	}

	public SolrParamsBuilder facetRange(String field, int start, int end, int gap, String... excludes) {
		excludes = standardize(excludes);
		if (excludes.length == 0) {
			params.add("facet.range", field);
		} else {
			params.add("facet.range", "{!ex=" + Joiner.on(',').join(excludes) + "}" + field);
		}
		params.add("f." + field + ".facet.range.start", String.valueOf(start));
		params.add("f." + field + ".facet.range.end", String.valueOf(end));
		params.add("f." + field + ".facet.range.gap", String.valueOf(gap));
		return this;
	}

	public SolrParamsBuilder stats() {
		params.set("stats", true);
		return this;
	}

	public SolrParamsBuilder statsField(String field, String... excludes) {
		excludes = standardize(excludes);
		if (excludes.length != 0) {
			params.add("stats.field", "{!ex=" + Joiner.on(',').join(excludes) + "}" + field);
		} else {
			params.add("stats.field", field);
		}
		return this;
	}

	public SolrParamsBuilder collapseMax(String field, String maxFuction) {
		if (!Strings.isNullOrEmpty(maxFuction)) {
			params.add(CommonParams.FQ, "{!collapse field=" + field + " max=" + maxFuction + "}");
		} else {
			params.add(CommonParams.FQ, "{!collapse field=" + field + "}");
		}
		return this;
	}

	public SolrParamsBuilder add(String name, String value) {
		if (!Strings.isNullOrEmpty(value)) {
			params.add(name, value);
		}
		return this;
	}

	public SolrParamsBuilder addAdd(SolrParams params) {
		if (params != null) {
			this.params.add(params);
		}
		return this;
	}

	public SolrParamsBuilder edismax() {
		return this.defType("edismax");
	}

	public SolrParamsBuilder defType(String type) {
		params.set("defType", type);
		return this;
	}

	public static String[] getCriterias(String fieldName, String[] fieldValues) {
		String[] result = new String[fieldValues.length];
		for (int i = 0; i < fieldValues.length; i++) {
			result[i] = fieldName + ":" + fieldValues[i];
		}
		return result;
	}

	public static String[] standardize(String[] data) {
		if (data == null || data.length == 0) {
			return new String[] {};
		}
		String[] tmp = new String[data.length];
		int count = 0;
		for (int i = 0; i < data.length; i++) {
			String e = data[i].trim();
			if (!Strings.isNullOrEmpty(e)) {
				tmp[count] = e;
				count++;
			}
		}
		if (count == data.length) {
			return tmp;
		} else {
			return Arrays.copyOf(tmp, count);
		}
	}

	public static String[] getRangeCriterias(String fieldName, Tupple2<String, String>[] fieldValues) {
		String[] result = new String[fieldValues.length];
		for (int i = 0; i < fieldValues.length; i++) {
			Tupple2<String, String> value = fieldValues[i];
			result[i] = fieldName + ":" + "[" + value._1() + " TO " + value._2() + "]";
		}
		return result;
	}

	public static void main(String[] args) {

	}
}

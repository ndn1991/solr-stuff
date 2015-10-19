package com.ndn.solr.stuff.common.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
//<lst name="mappings">
//	<lst name="mapping">
//		<str name="source">productiemid</str>
//		<str mame="destination">product_item_id</str>
//		<boolean name="keepOriginal">false</boolean>
//	</lst>
//	<lst name="mapping">
//		<str name="source">keyword</str>
//		<str mame="destination">q</str>
//		<boolean name="keepOriginal">false</boolean>
//	</lst>
//</lst>

/**
 * Thực ra đây chỉ là một dạng super simple của việc mapping param, chúng ta hoàn toàn có thể làn việc này bằng localParams
 * {!edismax q=$keyword fq=category_path:$catid}
 * @author ndn
 *
 */
public class MappingParamsHandler extends BaseHandler {
	private Map<String, List<String>> mapping = null;
	private Map<String, Boolean> keepOriginals = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void inform(SolrResourceLoader resourceLoader) throws Exception {
		this.mapping = new HashMap<>();
		this.keepOriginals = new HashMap<>();
		List<NamedList> mappings = ((NamedList) getInitArgs().get("mappings")).getAll("mapping");
		for (NamedList mapping : mappings) {
			String source = (String) mapping.get("source");
			String destination = (String) mapping.get("destination");
			boolean keep = (boolean) mapping.get("keepOriginal");
			
			this.keepOriginals.put(source, keep);
			if (this.mapping.containsKey(source)) {
				this.mapping.get(source).add(destination);
			} else {
				List<String> values = new ArrayList<>();
				values.add(destination);
				this.mapping.put(source, values);
			}
		}
	}

	@Override
	protected void process(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		SolrParams _params = req.getParams();
		ModifiableSolrParams params = new ModifiableSolrParams();
		Iterator<String> it = _params.getParameterNamesIterator();
		while (it.hasNext()) {
			String name = it.next();
			if (this.mapping.containsKey(name)) {
				String value = _params.get(name);
				for (String des : this.mapping.get(name)) {
					params.add(des, value);
				}
				if (this.keepOriginals.get(name)) {
					params.add(name, value);
				}
			} else {
				params.add(name, _params.get(name));
			}
		}
		req.setParams(wrapDefaultsAndAppends(params));
		super.executeHandler("/select", req, rsp);
	}

	@Override
	protected void _preClose() {
		
	}

	@Override
	protected void _postClose() {
		
	}

}

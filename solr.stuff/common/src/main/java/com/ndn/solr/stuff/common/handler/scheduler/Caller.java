package com.ndn.solr.stuff.common.handler.scheduler;

import org.apache.solr.common.params.SolrParams;

public class Caller {
	private String name;
	private String core;
	private String handler;
	private SolrParams params;

	public Caller(String name, String core, String handler, SolrParams params) {
		super();
		this.name = name;
		this.core = core;
		this.handler = handler;
		this.params = params;
	}

	public String getCore() {
		return core;
	}

	public String getHandler() {
		return handler;
	}

	public SolrParams getParams() {
		return params;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public void setParams(SolrParams params) {
		this.params = params;
	}

}

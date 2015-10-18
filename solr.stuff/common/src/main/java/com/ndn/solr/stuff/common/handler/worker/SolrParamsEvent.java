package com.ndn.solr.stuff.common.handler.worker;

import org.apache.solr.common.params.SolrParams;

import com.ndn.solr.stuff.common.eventdriven.Callable;

public class SolrParamsEvent {
	private SolrParams params;
	private Callable callBack;
	private String core;
	private String handler;

	public String getCore() {
		return core;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public SolrParams getParams() {
		return params;
	}

	public void setParams(SolrParams params) {
		this.params = params;
	}

	public Callable getCallBack() {
		return callBack;
	}

	public void setCallBack(Callable callBack) {
		this.callBack = callBack;
	}

}

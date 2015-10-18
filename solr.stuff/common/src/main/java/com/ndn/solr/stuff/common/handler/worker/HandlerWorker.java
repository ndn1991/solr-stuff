package com.ndn.solr.stuff.common.handler.worker;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.response.SolrQueryResponse;

import com.lmax.disruptor.WorkHandler;
import com.ndn.solr.stuff.common.Loggable;
import com.ndn.solr.stuff.common.handler.BaseHandler;

public class HandlerWorker implements WorkHandler<SolrParamsEvent>, Loggable {
	private BaseHandler searchHandler;

	public HandlerWorker(BaseHandler searchHandler) {
		if (searchHandler == null) {
			throw new IllegalArgumentException("searchHandler can not be null");
		}
		this.searchHandler = searchHandler;
	}

	@Override
	public void onEvent(SolrParamsEvent evt) throws Exception {
		SolrQueryResponse rsp = null;
		try {
			rsp = searchHandler.executeHandler(evt.getCore(), evt.getHandler(), evt.getParams());
		} catch (Exception e) {
			getLogger().error("", e);
		}
		if (rsp != null) {
			evt.getCallBack().call(rsp.getValues());
		} else {
			evt.getCallBack().call(new NamedList<>());
		}
	}

}

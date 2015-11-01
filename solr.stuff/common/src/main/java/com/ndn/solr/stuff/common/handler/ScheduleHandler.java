package com.ndn.solr.stuff.common.handler;

import java.util.Map;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.SearchHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * Mục tiêu làm handler này là giúp thực hiện tất cả các công việc được lập lịch
 * và quản lí chúng. Các usecase cần có: satus, execute schedule, add + start +
 * stop + remove + overwrite a schedule
 * 
 * @author ndn
 *
 */
public class ScheduleHandler extends SearchHandler {
	public static final String CMD = "scd_cmd";
	public static final String CMD_RESUME = "resume";
	public static final String CMD_STOP = "stop";
	public static final String CMD_STATUS = "status";
	public static final String CMD_ADD = "add";
	public static final String CMD_REMOVE = "remove";

	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String INTERVAL = "interval";
	public static final String UNIT = "unit";
	public static final String EXPRESSION = "expression";

	private Map<String, V>
	
	@Override
	public final void inform(SolrCore core) {

	}

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		SolrParams params = req.getParams();

	}

}

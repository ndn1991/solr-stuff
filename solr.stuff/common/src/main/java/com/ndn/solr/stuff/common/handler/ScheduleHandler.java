package com.ndn.solr.stuff.common.handler;

import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
//<lst name="config">
//	<lst name="schedule">
//		<str name="name">deal-scheduler</str>
//		<str name="type">interval</str>
//		<int name="interval">10</int>
//		<str name="unit">MINUTES</str>
//		<boolean name="start">true</boolean>
//	</lst>
//	<lst name="schedule">
//		<str name="name">merchant-scheduler</str>
//		<str name="type">crond</str>
//		<str name="expression">******</str>
//		<boolean name="start">false</boolean>
//	</lst>
//</lst>

/**
 * Mục tiêu làm handler này là giúp thực hiện tất cả các công việc được lập lịch và quản lí chúng.
 * Các usecase cần có: satus, execute schedule, add + start + stop + remove + overwrite a schedule
 * @author ndn
 *
 */
public class ScheduleHandler extends BaseHandler {
	public static final String CMD = "cmd";
	public static final String CMD_START = "start";
	public static final String CMD_STOP = "stop";
	public static final String CMD_STATUS = "status";
	public static final String CMD_ADD = "add";
	public static final String CMD_REMOVE = "remove";

	public static final String NAME = "name";
	public static final String START = "start";
	public static final String INTERVAL = "interval";
	public static final String UNIT = "unit";
	
	@Override
	protected void _preClose() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void _postClose() {
	}

	@Override
	protected void inform(SolrResourceLoader resourceLoader) throws Exception {
		
	}

	@Override
	protected void process(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		// TODO Auto-generated method stub

	}

}

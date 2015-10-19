package com.ndn.solr.stuff.common.handler;

import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

//<lst name="config">
//	<lst name="schedule">
//		<!-- unique name -->
//		<str name="name"></str>
//		<!-- dd-mm-yyy hh:MM:ss.SSS -->
//		<str name="start">20-10-2015 01:00:00.000</str>
//		<int name="interval">10</int>
//		<str name="unit">MINUTES</str>
//	</lst>
//	<lst name="schedule">
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void process(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		// TODO Auto-generated method stub

	}

}

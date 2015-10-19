package com.ndn.solr.stuff.common.handler.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ndn.solr.stuff.common.handler.BaseHandler;

public class JobCaller implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Caller caller = (Caller) context.get("caller");
		if (caller == null) {
			throw new RuntimeException("Không có tham số thì gọi làm quái gì");
		}
		BaseHandler handler = (BaseHandler) context.get("handler");
		if (handler == null) {
			throw new RuntimeException("Không truyền vào handler thì execute thế nào được");
		}
		handler.executeHandler(caller.getCore(), caller.getHandler(), caller.getParams());
	}

}

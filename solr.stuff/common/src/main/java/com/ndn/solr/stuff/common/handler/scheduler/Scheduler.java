package com.ndn.solr.stuff.common.handler.scheduler;

import org.quartz.SchedulerException;

public interface Scheduler {
	void schedulerAtFixRate(long delay, String unit, Caller caller) throws SchedulerException;
	void schedulerCrondtab(String expression, Caller caller) throws SchedulerException;
	void close() throws SchedulerException;
	void pauseAll() throws SchedulerException;
	void resumeAll() throws SchedulerException;
	void pause(String name) throws SchedulerException;
	void resume(String name) throws SchedulerException;
}

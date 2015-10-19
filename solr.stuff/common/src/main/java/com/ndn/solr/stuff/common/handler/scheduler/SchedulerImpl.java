package com.ndn.solr.stuff.common.handler.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.core.jmx.JobDataMapSupport;

import com.ndn.solr.stuff.common.Loggable;
import com.ndn.solr.stuff.common.handler.BaseHandler;

public class SchedulerImpl implements Scheduler, Loggable {
	private SchedulerFactory schedFact;
	private org.quartz.Scheduler sched;
	private BaseHandler handler;

	public SchedulerImpl(BaseHandler handler) throws SchedulerException {
		schedFact = new org.quartz.impl.StdSchedulerFactory();
		sched = schedFact.getScheduler();
		this.handler = handler;
	}

	@Override
	public void schedulerAtFixRate(long delay, String unit, Caller caller) throws SchedulerException {
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever();
		if (MILLISECOND.equalsIgnoreCase(unit)) {
			scheduleBuilder = scheduleBuilder.withIntervalInMilliseconds(delay);
		} else if (SECOND.equalsIgnoreCase(unit)) {
			scheduleBuilder = scheduleBuilder.withIntervalInSeconds(Long.valueOf(delay).intValue());
		} else if (MINUTE.equalsIgnoreCase(unit)) {
			scheduleBuilder = scheduleBuilder.withIntervalInMinutes(Long.valueOf(delay).intValue());
		} else if (HOUR.equalsIgnoreCase(unit)) {
			scheduleBuilder = scheduleBuilder.withIntervalInHours(Long.valueOf(delay).intValue());
		} else {
			throw new IllegalArgumentException("Chỉ support MILLISECOND, SECOND, MINUTE, HOUR");
		}
		Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(scheduleBuilder).build();
		sched.scheduleJob(getJobDetail(caller), trigger);
	}

	@Override
	public void schedulerCrondtab(String expression, Caller caller) throws SchedulerException {
		Trigger trigger = TriggerBuilder.newTrigger().startNow()
				.withSchedule(CronScheduleBuilder.cronSchedule(expression)).build();

		sched.scheduleJob(getJobDetail(caller), trigger);
	}

	private JobDetail getJobDetail(Caller caller) {
		Map<String, Object> jobData = new HashMap<>();
		jobData.put("handler", this.handler);
		jobData.put("caller", caller);
		JobDetail job = JobBuilder.newJob(JobCaller.class).setJobData(JobDataMapSupport.newJobDataMap(jobData))
				.withIdentity(JobKey.jobKey(caller.getName(), "com.adr.bigdata")).build();
		return job;
	}

	public static final String MILLISECOND = "millisecond";
	public static final String SECOND = "second";
	public static final String MINUTE = "minute";
	public static final String HOUR = "hour";

	@Override
	public void close() throws SchedulerException {
		if (sched != null && !sched.isShutdown()) {
			sched.shutdown();
		}
	}

	@Override
	public void pauseAll() throws SchedulerException {
		if (sched != null && !sched.isShutdown()) {
			sched.pauseAll();
		}
	}

	@Override
	public void resumeAll() throws SchedulerException {
		if (sched != null && !sched.isShutdown()) {
			sched.resumeAll();
		}
	}

	@Override
	public void pause(String name) throws SchedulerException {
		if (sched != null && !sched.isShutdown()) {
			sched.pauseJob(JobKey.jobKey(name, "com.adr.bigdata"));
		}
	}

	@Override
	public void resume(String name) throws SchedulerException {
		if (sched != null && !sched.isShutdown()) {
			sched.resumeJob(JobKey.jobKey(name, "com.adr.bigdata"));
		}
	}
}

package com.ndn.solr.stuff.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggable {
	public default Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}
}

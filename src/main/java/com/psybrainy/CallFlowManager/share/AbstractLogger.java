package com.psybrainy.CallFlowManager.share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLogger {

    protected final Logger log;

    public AbstractLogger() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }
}

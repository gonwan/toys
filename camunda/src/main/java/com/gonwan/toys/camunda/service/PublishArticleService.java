package com.gonwan.toys.camunda.service;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishArticleService implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);

    @Override
    public void execute(DelegateExecution execution) {
        logger.info("Publishing the approved article.");
    }

}

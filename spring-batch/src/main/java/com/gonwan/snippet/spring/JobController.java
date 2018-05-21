package com.gonwan.snippet.spring;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job job;

    @GetMapping(value = "start", produces = "application/json")
    public void start() throws JobExecutionException {
        /* See: JobLauncherCommandLineRunner#execute() */
        JobParameters nextParameters = new JobParametersBuilder(
                new JobParameters(), this.jobExplorer).getNextJobParameters(job).toJobParameters();
        JobExecution jobExecution = jobLauncher.run(job, nextParameters);
        logger.info("Started job execution: " + jobExecution.getId());
    }

    @GetMapping(value = "stop", produces = "application/json")
    public void stop() throws JobExecutionException {
        Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
        JobExecution jobExecution = jobExecutions.stream().max(Comparator.comparing(JobExecution::getCreateTime)).orElse(null);
        if (jobExecution != null) {
            jobOperator.stop(jobExecution.getId());
            logger.info("Stopped job execution: " + jobExecution.getId());
        }
    }

    /* force restart */
    @GetMapping(value = "restart", produces = "application/json")
    public void restart() throws JobExecutionException {
        Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
        JobExecution jobExecution = jobExecutions.stream().max(Comparator.comparing(JobExecution::getCreateTime)).orElse(null);
        if (jobExecution != null) {
            jobExecution.setStatus(BatchStatus.FAILED);
            jobExecution.setExitStatus(ExitStatus.FAILED);
            jobExecution.setEndTime(new Date());
            jobRepository.update(jobExecution);
            jobOperator.restart(jobExecution.getId());
        }
    }

}

package com.gonwan.snippet.spring;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
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

    /* resume */
    @GetMapping(value = "resume", produces = "application/json")
    public void resume() throws JobExecutionException {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), 0, 1);
        if (!jobInstances.isEmpty()) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstances.get(0));
            if (!jobExecutions.isEmpty()) {
                JobExecution jobExecution = jobExecutions.get(0);
                if (jobExecution.getStatus() == BatchStatus.STOPPED) {
                    jobOperator.restart(jobExecution.getId());
                    logger.info("Resumed job execution: " + jobExecution.getId());
                }
            }
        }
    }

    /* HACK: force resume when the process exits abnormally */
    @GetMapping(value = "resume2", produces = "application/json")
    public void restart() throws JobExecutionException {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), 0, 1);
        if (!jobInstances.isEmpty()) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstances.get(0));
            if (!jobExecutions.isEmpty()) {
                JobExecution jobExecution = jobExecutions.get(0);
                if (jobExecution.getStatus() == BatchStatus.STARTED) {
                    jobExecution.setStatus(BatchStatus.FAILED);
                    jobExecution.setExitStatus(ExitStatus.FAILED);
                    jobExecution.setEndTime(new Date());
                    Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
                    for (StepExecution stepExecution : stepExecutions) {
                        stepExecution.setStatus(BatchStatus.FAILED);
                        stepExecution.setExitStatus(ExitStatus.FAILED);
                        stepExecution.setEndTime(new Date());
                        jobRepository.update(stepExecution);
                    }
                    jobRepository.update(jobExecution);
                    jobOperator.restart(jobExecution.getId());
                    logger.info("Force resumed job execution: " + jobExecution.getId());
                }
            }
        }
    }

}

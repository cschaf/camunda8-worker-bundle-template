package com.schaflabs.c8.workers.jobhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.schaflabs.c8.common.exceptions.UseCaseException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJobWorker {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  protected void executeWithErrorHandling(
      JobClient client, ActivatedJob job, JobExecutor executor) {
    try {
      executor.execute();
    } catch (Exception e) {
      if (e.getCause() instanceof JsonProcessingException je) {
        logger.error("Deserialization failed for job {}: {}", job.getKey(), je.getMessage());
        failJob(client, job, "Deserialization failed: " + je.getMessage());
      } else if (e instanceof UseCaseException ue) {
        logger.error("Use case error in job {}: {}", job.getKey(), ue.getUserMessage());
        failJob(client, job, ue.getUserMessage());
      } else {
        logger.error("Internal error in job {}: {}", job.getKey(), e.getMessage(), e);
        failJob(client, job, "Internal error: " + e.getClass().getName());
      }
    }
  }

  private void failJob(JobClient client, ActivatedJob job, String errorMessage) {
    client.newFailCommand(job.getKey()).retries(0).errorMessage(errorMessage).send().join();
  }
}

package com.schaflabs.c8.workers.jobhandlers;

@FunctionalInterface
public interface JobExecutor {
  void execute() throws Exception;
}

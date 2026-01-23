package com.schaflabs.c8.workers.jobhandlers;

import com.schaflabs.c8.common.domain.UseCaseResponse;
import com.schaflabs.c8.common.utils.JobWorkerUtils;
import com.schaflabs.c8.workers.JobWorkerTypes;
import com.schaflabs.c8.workers.definitions.CreateExampleObjectProcessVariables;
import com.schaflabs.c8.workers.usecases.CreateExampleObjectUseCase;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class ExampleObjectHandler {
  private final CreateExampleObjectUseCase createExampleObjectUseCase;

  @JobWorker(
      name = "ExampleObjectCreateObjectWorker",
      type = JobWorkerTypes.CREATE_EXAMPLE_OBJECT,
      autoComplete = false)
  public void createExampleObject(JobClient client, final ActivatedJob job) {
    log.info("Handling create example object for process instance {}", job.getProcessInstanceKey());
    CreateExampleObjectProcessVariables processVariables =
        job.getVariablesAsType(CreateExampleObjectProcessVariables.class);

    // Map process variables to  CreateExampleObjectUseCase.Request
    CreateExampleObjectUseCase.ObjectDataDto objectDataDto =
        CreateExampleObjectUseCase.ObjectDataDto.builder()
            .price(processVariables.getPrice())
            .color(processVariables.getColor())
            .year(processVariables.getYear())
            .hardDiskSize(processVariables.getHardDiskSize())
            .cpuModel(processVariables.getCpuModel())
            .build();
    CreateExampleObjectUseCase.Request useCaseRequest =
        CreateExampleObjectUseCase.Request.builder().build();
    useCaseRequest.setName(processVariables.getName());
    useCaseRequest.setData(objectDataDto);

    UseCaseResponse<CreateExampleObjectUseCase.Response> useCaseResponse =
        createExampleObjectUseCase.execute(useCaseRequest);

    if (useCaseResponse.isSuccessful) {
      log.info("A example object was successfully created: {}", useCaseResponse.getData().getId());
      Map<String, Object> variables = new HashMap<>();
      Map<String, String> customHeaders = job.getCustomHeaders();
      String resultKey = customHeaders.get("resultVariable");
      if (resultKey != null) {
        variables.put(resultKey, useCaseResponse.getData());
      }
      String resultExpression = customHeaders.get("resultExpression");
      if (resultExpression != null) {
        Map<String, Object> headerResultVariables =
            JobWorkerUtils.evaluateResultExpression(resultExpression, useCaseResponse.getData());
        variables.putAll(headerResultVariables);
      }
      client.newCompleteCommand(job.getKey()).variables(variables).send();
    } else {
      client
          .newFailCommand(job.getKey())
          .retries(0)
          .errorMessage(useCaseResponse.getError().getUserMessage())
          .send()
          .join();
    }
  }
}

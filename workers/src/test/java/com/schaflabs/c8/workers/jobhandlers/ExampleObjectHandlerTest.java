package com.schaflabs.c8.workers.jobhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.schaflabs.c8.common.domain.UseCaseResponse;
import com.schaflabs.c8.common.exceptions.UseCaseException;
import com.schaflabs.c8.workers.definitions.CreateExampleObjectProcessVariables;
import com.schaflabs.c8.workers.usecases.CreateExampleObjectUseCase;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.CompleteJobCommandStep1;
import io.camunda.zeebe.client.api.command.FailJobCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExampleObjectHandlerTest {

  @Mock private CreateExampleObjectUseCase createExampleObjectUseCase;

  @Mock private JobClient client;

  @Mock private ActivatedJob job;

  @InjectMocks private ExampleObjectHandler exampleObjectHandler;

  private void setupJob() {
    when(job.getKey()).thenReturn(1L);
    when(job.getVariablesAsType(CreateExampleObjectProcessVariables.class))
        .thenReturn(new CreateExampleObjectProcessVariables());
  }

  private void setupSuccess() {
    setupJob();
    CompleteJobCommandStep1 completeCommand = mock(CompleteJobCommandStep1.class);
    when(client.newCompleteCommand(job.getKey())).thenReturn(completeCommand);
    when(completeCommand.variables(any(Map.class))).thenReturn(completeCommand);
    when(completeCommand.send()).thenReturn(mock(ZeebeFuture.class));
  }

  private void setupFailure() {
    setupJob();
    FailJobCommandStep1 failCommand = mock(FailJobCommandStep1.class);
    FailJobCommandStep1.FailJobCommandStep2 failStep2 =
        mock(FailJobCommandStep1.FailJobCommandStep2.class);
    when(client.newFailCommand(job.getKey())).thenReturn(failCommand);
    when(failCommand.retries(0)).thenReturn(failStep2);
    when(failStep2.errorMessage(anyString())).thenReturn(failStep2);
    when(failStep2.send()).thenReturn(mock(ZeebeFuture.class));
  }

  @Test
  @DisplayName("Should complete the job when the use case is successful")
  void shouldCompleteJobOnSuccess() {
    // given
    setupSuccess();
    CreateExampleObjectUseCase.Response useCaseResponseData =
        CreateExampleObjectUseCase.Response.builder().id("123").build();
    UseCaseResponse<CreateExampleObjectUseCase.Response> useCaseResponse =
        UseCaseResponse.<CreateExampleObjectUseCase.Response>builder()
            .setData(useCaseResponseData)
            .success();
    when(createExampleObjectUseCase.execute(any())).thenReturn(useCaseResponse);

    // when
    exampleObjectHandler.createExampleObject(client, job);

    // then
    verify(client).newCompleteCommand(job.getKey());
  }

  @Test
  @DisplayName("Should fail the job with a deserialization error message")
  void shouldFailJobOnJsonProcessingException() {
    // given
    setupFailure();
    when(job.getVariablesAsType(CreateExampleObjectProcessVariables.class))
        .thenThrow(new RuntimeException(new JsonProcessingException("Deserialization failed") {}));

    // when
    exampleObjectHandler.createExampleObject(client, job);

    // then
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(client.newFailCommand(job.getKey()).retries(0)).errorMessage(captor.capture());
    assertEquals("Deserialization failed: Deserialization failed", captor.getValue());
  }

  @Test
  @DisplayName("Should fail the job with the use case error message")
  void shouldFailJobOnUseCaseException() {
    // given
    setupFailure();
    when(createExampleObjectUseCase.execute(any()))
        .thenThrow(new UseCaseException("The input is invalid", "Invalid input"));

    // when
    exampleObjectHandler.createExampleObject(client, job);

    // then
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(client.newFailCommand(job.getKey()).retries(0)).errorMessage(captor.capture());
    assertEquals("The input is invalid", captor.getValue());
  }

  @Test
  @DisplayName("Should fail the job with an internal error message")
  void shouldFailJobOnRuntimeException() {
    // given
    setupFailure();
    when(createExampleObjectUseCase.execute(any()))
        .thenThrow(new RuntimeException("Something went wrong"));

    // when
    exampleObjectHandler.createExampleObject(client, job);

    // then
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(client.newFailCommand(job.getKey()).retries(0)).errorMessage(captor.capture());
    assertEquals("Internal error: java.lang.RuntimeException", captor.getValue());
  }
}

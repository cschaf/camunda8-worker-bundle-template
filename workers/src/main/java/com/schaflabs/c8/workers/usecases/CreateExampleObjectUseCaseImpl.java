package com.schaflabs.c8.workers.usecases;

import static com.schaflabs.c8.common.utils.ErrorMessageBuilder.buildDeveloperErrorMessage;

import com.schaflabs.c8.common.domain.ServiceAdapterResponse;
import com.schaflabs.c8.common.domain.UseCaseException;
import com.schaflabs.c8.common.domain.UseCaseResponse;
import com.schaflabs.c8.workers.definitions.CreateExampleRequestDto;
import com.schaflabs.c8.workers.definitions.ExceptionType;
import com.schaflabs.c8.workers.io.ServiceAdapter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class CreateExampleObjectUseCaseImpl implements CreateExampleObjectUseCase {
  private static final String INVALID_INPUT_DATA_FOUND =
      "Failed to create example object! Check given input data!";
  @Autowired private ServiceAdapter serviceAdapter;

  @Override
  public UseCaseResponse<Response> onExecute(CreateExampleRequestDto request)
      throws UseCaseException {
    if (request == null) {
      log.error(INVALID_INPUT_DATA_FOUND);
      final String developerMessage =
          buildDeveloperErrorMessage(
              INVALID_INPUT_DATA_FOUND,
              ExceptionType.CREATE_EXAMPLE_OBJECT_FAILED_EXCEPTION.getException());
      throw new UseCaseException(
          INVALID_INPUT_DATA_FOUND, developerMessage, CreateExampleObjectUseCase.class);
    }
    ServiceAdapterResponse<Response> response =
        serviceAdapter.createExampleObject(request, Response.class);
    if (!response.isSuccess()) {
      log.error(response.getDeveloperErrorMessage());
      final String developerMessage =
          buildDeveloperErrorMessage(
              response.getDeveloperErrorMessage(),
              ExceptionType.CREATE_EXAMPLE_OBJECT_FAILED_EXCEPTION.getException());

      throw new UseCaseException(
          response.getUserErrorMessage(),
          developerMessage,
          response.getStatusCode(),
          CreateExampleObjectUseCase.class);
    }

    return UseCaseResponse.<Response>builder().setData(response.getBody()).success();
  }
}

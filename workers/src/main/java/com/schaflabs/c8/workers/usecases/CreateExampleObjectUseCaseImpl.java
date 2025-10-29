package com.schaflabs.c8.workers.usecases;

import static com.schaflabs.c8.common.utils.ErrorMessageBuilder.buildDeveloperErrorMessage;

import com.schaflabs.c8.common.domain.ServiceAdapterResponse;
import com.schaflabs.c8.common.domain.UseCaseException;
import com.schaflabs.c8.common.domain.UseCaseResponse;
import com.schaflabs.c8.workers.definitions.ExceptionType;
import com.schaflabs.c8.workers.io.dtos.CreateExampleRequestDto;
import com.schaflabs.c8.workers.io.dtos.CreateExampleResponseDto;
import com.schaflabs.c8.workers.io.dtos.ExampleObjectDataDto;
import com.schaflabs.c8.workers.io.services.ServiceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CreateExampleObjectUseCaseImpl implements CreateExampleObjectUseCase {
  private static final String INVALID_INPUT_DATA_FOUND =
      "Failed to create example object! Check given input data!";
  private final ServiceAdapter serviceAdapter;

  @Override
  public UseCaseResponse<Response> onExecute(Request request) throws UseCaseException {
    if (request == null) {
      log.error(INVALID_INPUT_DATA_FOUND);
      final String developerMessage =
          buildDeveloperErrorMessage(
              INVALID_INPUT_DATA_FOUND,
              ExceptionType.CREATE_EXAMPLE_OBJECT_FAILED_EXCEPTION.getException());
      throw new UseCaseException(
          INVALID_INPUT_DATA_FOUND, developerMessage, CreateExampleObjectUseCase.class);
    }
    // Map Request to CreateExampleRequestDto
    ExampleObjectDataDto exampleObjectDataDto =
        ExampleObjectDataDto.builder()
            .year(request.getData().getYear())
            .price(request.getData().getPrice())
            .hardDiskSize(request.getData().getHardDiskSize())
            .cpuModel(request.getData().getCpuModel())
            .color(request.getData().getColor())
            .build();

    CreateExampleRequestDto createExampleRequestDto =
        CreateExampleRequestDto.builder()
            .name(request.getName())
            .data(exampleObjectDataDto)
            .build();

    ServiceAdapterResponse<CreateExampleResponseDto> response =
        serviceAdapter.createExampleObject(createExampleRequestDto);
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
    // Map CreateExampleResponseDto to CreateExampleObjectUseCase.Response
    ObjectDataDto objectDataDto =
        ObjectDataDto.builder()
            .year(request.getData().getYear())
            .price(request.getData().getPrice())
            .color(request.getData().getColor())
            .hardDiskSize(request.getData().getHardDiskSize())
            .cpuModel(request.getData().getCpuModel())
            .build();

    Response responseDto =
        Response.builder()
            .id(response.getBody().getId())
            .name(response.getBody().getName())
            .createdAt(response.getBody().getCreatedAt())
            .data(objectDataDto)
            .build();

    return UseCaseResponse.<Response>builder().setData(responseDto).success();
  }
}

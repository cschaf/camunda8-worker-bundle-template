package com.schaflabs.c8.workers.io.services;

import com.schaflabs.c8.common.domain.ServiceAdapterResponse;
import com.schaflabs.c8.workers.io.dtos.CreateExampleRequestDto;
import com.schaflabs.c8.workers.io.dtos.CreateExampleResponseDto;
import java.util.List;

public interface ServiceAdapter {
  ServiceAdapterResponse<CreateExampleResponseDto> createExampleObject(
      CreateExampleRequestDto request);

  <T> ServiceAdapterResponse<T> getExampleObjectById(long id, Class<T> clazz);

  <T> ServiceAdapterResponse<List<T>> getAllExampleObjects(Class<T> clazz);

  <T> ServiceAdapterResponse<T> deleteExampleObjectById(long id, Class<T> clazz);

  <T> ServiceAdapterResponse<T> updateExampleObjectById(long id, Class<T> clazz);
}

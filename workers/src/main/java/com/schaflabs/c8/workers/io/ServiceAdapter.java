package com.schaflabs.c8.workers.io;

import com.schaflabs.c8.common.domain.ServiceAdapterResponse;
import com.schaflabs.c8.workers.definitions.CreateExampleRequestDto;
import java.util.List;

public interface ServiceAdapter {
  <T> ServiceAdapterResponse<T> createExampleObject(
      CreateExampleRequestDto request, Class<T> clazz);

  <T> ServiceAdapterResponse<T> getExampleObjectById(long id, Class<T> clazz);

  <T> ServiceAdapterResponse<List<T>> getAllExampleObjects(Class<T> clazz);

  <T> ServiceAdapterResponse<T> deleteExampleObjectById(long id, Class<T> clazz);

  <T> ServiceAdapterResponse<T> updateExampleObjectById(long id, Class<T> clazz);
}

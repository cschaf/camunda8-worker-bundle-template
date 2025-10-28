package com.schaflabs.c8.common.io;

import com.schaflabs.c8.common.domain.UseCase;
import com.schaflabs.c8.common.domain.UseCaseException;
import com.schaflabs.c8.common.domain.UseCaseResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.function.Function;

/**
 * Die default Implementation von {@link UseCaseHelper}
 *
 * @see UseCaseHelper
 */
public final class DefaultUseCaseHelper implements UseCaseHelper {
  /** Ein default Failure-Response Mapper. */
  private final Function<UseCaseException, ResponseWrapper> defaultResponseFailureMapper =
      error ->
          ResponseWrapper.builder()
              .statusCode(500)
              .developerMessage(error.getDeveloperMessage())
              .isSuccessful(false)
              .internalCode(error.getStatusCode())
              .userMessage(error.getUserMessage())
              .timestamp(new Date())
              .build();

  /**
   * Erstellt einen Default Success-Response Mapper.
   *
   * @param <T> Der Typ der Use Case Response.
   * @return Eine {@link Function}, die den Parameter aufnimmt {@param <T>} und einen {@link
   *     ResponseWrapper} zurückgibt.
   */
  private <T> Function<T, ResponseWrapper> createDefaultResponseSuccessMapper() {
    return data ->
        ResponseWrapper.<T>builder()
            .isSuccessful(true)
            .data(data)
            .statusCode(200)
            .timestamp(new Date())
            .build();
  }

  /**
   * Erstellt einen Default Success-Response Mapper.
   *
   * @param <T> Der Typ der Use Case Response.
   * @return Eine {@link Function}, die den Parameter aufnimmt {@param <T>} und einen {@link
   *     ResponseWrapper} zurückgibt.
   */
  private <T> Function<T, ResponseWrapper> createNoDataResponseSuccessMapper() {
    return data ->
        ResponseWrapper.<T>builder()
            .isSuccessful(true)
            .statusCode(204)
            .timestamp(new Date())
            .build();
  }

  /**
   * Löst einen bestimmten {@link UseCase} auf.
   *
   * @param useCase Der {@link UseCase}, der gelöst werden soll.
   * @param param Die Daten, die den UseCase triggern und in der {@link UseCase#execute(Object)}
   *     Methode bereitgestellt werden.
   * @param successMapper Eine {@link Function} die einen {@link ResponseWrapper} zurückgeben muss
   *     und ausgeführt wird wenn der UseCase erfolgreich ausgeführt wurde.
   * @param failureMapper Eine {@link Function} die einen {@link ResponseWrapper} zurückgeben muss
   *     ausgeführt wird wenn der UseCase nicht erfolgreich ausgeführt wurde.
   * @param <R> Der Typ der Daten der UseCase Response {@link UseCaseResponse}
   * @param <T> Der Typ der Daten der UseCase Request
   * @return Ein {@link ResponseEntity}.
   */
  @Override
  public <R, T> ResponseEntity<ResponseWrapper<T>> resolve(
      @NotNull UseCase<R, T> useCase,
      T param,
      Function<R, ResponseWrapper> successMapper,
      Function<UseCaseException, ResponseWrapper> failureMapper) {
    Assert.notNull(useCase, "Parameter 'useCase' must not be null!");
    UseCaseResponse<R> useCaseResponse = useCase.execute(param);
    if (useCaseResponse.isSuccessful) {
      if (useCaseResponse.data != null) {
        if (successMapper == null) {
          successMapper = createDefaultResponseSuccessMapper();
        }
      } else {
        if (successMapper == null) {
          successMapper = createNoDataResponseSuccessMapper();
        }
      }
      return successMapper.apply(useCaseResponse.data).toResponseEntity();
    } else {
      if (failureMapper == null) {
        failureMapper = defaultResponseFailureMapper;
      }
      return failureMapper.apply(useCaseResponse.error).toResponseEntity();
    }
  }

  @Override
  public <T> ResponseWrapper executeDefaultMapper(T result) {
    return createDefaultResponseSuccessMapper().apply(result);
  }
}

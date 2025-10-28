package com.schaflabs.c8.common.io;

import com.schaflabs.c8.common.domain.UseCase;
import com.schaflabs.c8.common.domain.UseCaseException;
import com.schaflabs.c8.common.domain.UseCaseResponse;
import jakarta.validation.constraints.NotNull;
import java.util.function.Function;
import org.springframework.http.ResponseEntity;

public interface UseCaseHelper {
  /**
   * Löst einen spezifischen {@link UseCase}.
   *
   * @param useCase Der {@link UseCase} welcher gelöst werden soll.
   * @param param Die Daten, die den UseCase triggern und in der {@link UseCase#execute(Object)}
   *     Methode bereitgestellt werden.
   * @param <R> Der Typ der Daten der UseCase Response {@link UseCaseResponse}
   * @param <T> Der Typ der Daten der UseCase Request
   * @return A {@link ResponseEntity}.
   */
  @NotNull
  default <R, T> ResponseEntity resolve(@NotNull UseCase<R, T> useCase, T param) {
    return resolve(useCase, param, null, null);
  }

  /**
   * Löst einen bestimmten {@link UseCase} auf.
   *
   * @param useCase Der {@link UseCase}, der gelöst werden soll.
   * @param param Die Daten, die den UseCase triggern und in der {@link UseCase#execute(Object)}
   *     Methode bereitgestellt werden.
   * @param successMapper Eine {@link Function} die einen {@link ResponseWrapper} zurückgeben muss
   *     und ausgeführt wird wenn der UseCase erfolgreich ausgeführt wurde.
   * @param <R> Der Typ der Daten der UseCase Response {@link UseCaseResponse}
   * @param <T> Der Typ der Daten der UseCase Request
   * @return Ein {@link ResponseEntity}.
   */
  @NotNull
  default <R, T> ResponseEntity resolve(
      @NotNull UseCase<R, T> useCase, T param, Function<R, ResponseWrapper> successMapper) {
    return resolve(useCase, param, successMapper, null);
  }

  /**
   * Löst einen bestimmten {@link UseCase} auf.
   *
   * @param useCase Der {@link UseCase}, der gelöst werden soll.
   * @param param Die Daten, die den UseCase triggern und in der {@link UseCase#execute(Object)}
   *     Methode bereitgestellt werden.
   * @param failureMapper Eine {@link Function} die einen {@link ResponseWrapper} zurückgeben muss
   *     ausgeführt wird wenn der UseCase nicht erfolgreich ausgeführt wurde.
   * @param <R> Der Typ der Daten der UseCase Response {@link UseCaseResponse}
   * @param <T> Der Typ der Daten der UseCase Request
   * @return Ein {@link ResponseEntity}.
   */
  @NotNull
  default <R, T> ResponseEntity resolveWithDefaultMapper(
      @NotNull UseCase<R, T> useCase,
      T param,
      Function<UseCaseException, ResponseWrapper> failureMapper) {
    return resolve(useCase, param, this::executeDefaultMapper, failureMapper);
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
  @NotNull
  <R, T> ResponseEntity resolve(
      @NotNull UseCase<R, T> useCase,
      T param,
      Function<R, ResponseWrapper> successMapper,
      Function<UseCaseException, ResponseWrapper> failureMapper);

  @NotNull
  <T> ResponseWrapper executeDefaultMapper(T result);
}

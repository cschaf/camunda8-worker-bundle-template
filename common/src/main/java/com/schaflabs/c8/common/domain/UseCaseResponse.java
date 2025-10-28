package com.schaflabs.c8.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * Die Representation einer {@link UseCase}'s Response.
 *
 * @param <T> Ein Objekt vom Typ {@link UseCaseResponse#data}. Dieses Feld hält die individuellen
 *     Daten, welche der Response mitgegeben werden soll.
 */
@Data
public final class UseCaseResponse<T> {
  public final boolean isSuccessful;
  public final UseCaseException error;
  public final T data;

  private UseCaseResponse(boolean isSuccessful, UseCaseException error, T data) {
    this.isSuccessful = isSuccessful;
    this.error = error;
    this.data = data;
  }

  /**
   * Eine Factory-Methode um eine neue Instanz von {@link Builder} zu erhalten.
   *
   * @param <T> Ein Objekt vom Typ {@link UseCaseResponse#data}.
   * @return Eine neue {@link Builder} Instanz.
   */
  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  /**
   * Eine Builder-Klasse für {@link UseCaseResponse}
   *
   * @param <T> Ein Objekt vom Typ {@link UseCaseResponse#data}.
   */
  public static final class Builder<T> {
    private T data;

    private Builder() {}

    /**
     * Setzt die Daten der Response
     *
     * @param value Die Daten, welche gesetzt werden sollen. Null ist möglich.
     * @return Die Builder-Instanz.
     */
    public Builder<T> setData(@NotNull T value) {
      this.data = value;
      return this;
    }

    /**
     * Die build-Methode des Builders, welche eine {@link UseCaseResponse} mit {@link
     * UseCaseResponse#isSuccessful} als true zurückgibt.
     *
     * @return Ein {@link UseCaseResponse}-Objekt.
     */
    public UseCaseResponse<T> success() {
      return new UseCaseResponse<>(true, null, data);
    }

    /**
     * Die build-Methode des Builders, welche eine {@link UseCaseResponse} mit {@link
     * UseCaseResponse#isSuccessful} als false und dazu eine aufgetretene Exception zurückgibt.
     *
     * @param error Ein {@link Throwable} was aufzeigt, warum die {@link UseCaseResponse}
     *     fehlgeschlagen ist.
     * @return Ein {@link UseCaseResponse}-Objekt.
     */
    public UseCaseResponse<T> failure(@NotNull UseCaseException error) {
      Assert.notNull(error, "Parameter 'error' must not be null!");
      return new UseCaseResponse<>(false, error, data);
    }
  }
}

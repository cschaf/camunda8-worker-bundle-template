package com.schaflabs.c8.common.domain;

/**
 * Ein Interface, dessen Implementierungen die Geschäftsregeln repräsentieren sollen.
 *
 * @param <R> Der Typ der Response-Daten des Use Case.
 * @param <T> Der Typ der Request-Daten des Use Case.
 */
public interface UseCase<R, T> {
  /**
   * Die Methode löst die Ausführung des Use Case aus.
   *
   * @param request Das Objekt, was alle Daten, welche für die Ausführung des Use Case benötigt
   *     werden, beinhaltet.
   * @return Eine {@link UseCaseResponse}, welche das Ergebnis des Use Case repräsentiert.
   */
  default UseCaseResponse<R> execute(T request) {
    try {
      return onExecute(request);
    } catch (UseCaseException throwable) {
      return UseCaseResponse.<R>builder().failure(throwable);
    }
  }

  /**
   * Die Methode sollte von jedem Use Case implementiert werden und die eigentliche Geschäftslogik
   * beinhalten
   *
   * @param request Das Objekt, was alle Daten, welche für die Ausführung des Use Case benötigt
   *     werden, beinhaltet.
   * @return Eine {@link UseCaseResponse}, welche das Ergebnis des Use Case repräsentiert.
   * @throws UseCaseException Jeder mögliche Use Case Fehler.
   */
  UseCaseResponse<R> onExecute(T request) throws UseCaseException;
}

package com.schaflabs.c8.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobWorkerUtils {
  /**
   * Wertet eine komplexe FEEL-ResultExpression aus und extrahiert Werte aus dem Response-Objekt
   *
   * @param resultExpression Die Expression im Format "{variableName1: response.Property1,
   *     variableName2: response.Property2}"
   * @param responseObject Das Response-Objekt, aus dem Werte extrahiert werden sollen
   * @return Eine Map mit den extrahierten Werten als Prozessvariablen
   */
  public static Map<String, Object> evaluateResultExpression(
      String resultExpression, Object responseObject) {
    Map<String, Object> resultVariables = new HashMap<>();

    if (resultExpression == null) {
      return resultVariables;
    }

    try {
      // Entfernen Sie '=' am Anfang, falls vorhanden
      String cleanExpression = resultExpression;
      if (cleanExpression.startsWith("=")) {
        cleanExpression = cleanExpression.substring(1).trim();
      }

      // Entfernen Sie die äußeren Klammern
      cleanExpression = removeOuterBraces(cleanExpression);

      // Aufteilen der Expression in einzelne Zuordnungen
      List<String> assignments = splitExpressionPreservingBraces(cleanExpression);

      for (String assignment : assignments) {
        // Jetzt verarbeiten wir jede Zuordnung einzeln
        String[] parts = assignment.split(":", 2);

        if (parts.length == 2) {
          String variableName = parts[0].trim();
          String valuePath = parts[1].trim();

          // Entfernen der geschweiften Klammern, falls vorhanden
          variableName = removeAllBraces(variableName);
          valuePath = removeAllBraces(valuePath);

          // Extrahieren des Property-Namens aus dem Pfad
          String propertyName = extractPropertyNameFromPath(valuePath);

          // Wert aus dem Response-Objekt mittels Reflection extrahieren
          Object value = extractValueFromObject(responseObject, propertyName);

          if (value != null) {
            resultVariables.put(variableName, value);
          }
        }
      }
    } catch (Exception e) {
      // Fehlerbehandlung
      System.err.println("Fehler bei der Auswertung der ResultExpression: " + e.getMessage());
      e.printStackTrace();
    }

    return resultVariables;
  }

  /** Entfernt die äußeren geschweiften Klammern, falls vorhanden */
  private static String removeOuterBraces(String input) {
    String trimmed = input.trim();
    if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
      return trimmed.substring(1, trimmed.length() - 1).trim();
    }
    return trimmed;
  }

  /** Entfernt alle geschweiften Klammern aus einem String */
  private static String removeAllBraces(String input) {
    return input.replaceAll("[{}]", "").trim();
  }

  /**
   * Teilt die Expression in einzelne Zuordnungen auf, wobei die Struktur von geschachtelten
   * Klammern beachtet wird
   */
  private static List<String> splitExpressionPreservingBraces(String expression) {
    List<String> result = new ArrayList<>();
    int braceLevel = 0;
    int lastSplitPos = 0;

    for (int i = 0; i < expression.length(); i++) {
      char c = expression.charAt(i);

      if (c == '{') {
        braceLevel++;
      } else if (c == '}') {
        braceLevel--;
      } else if (c == ',' && braceLevel == 0) {
        // Nur trennen, wenn wir nicht innerhalb einer geschweiften Klammer sind
        result.add(expression.substring(lastSplitPos, i).trim());
        lastSplitPos = i + 1;
      }
    }

    // Den letzten Teil hinzufügen
    if (lastSplitPos < expression.length()) {
      result.add(expression.substring(lastSplitPos).trim());
    }

    return result;
  }

  /** Extrahiert den Property-Namen aus einem Pfad wie "response.Id" */
  private static String extractPropertyNameFromPath(String path) {
    String[] segments = path.split("\\.");

    // Den letzten Teil des Pfads zurückgeben, der den eigentlichen Property-Namen enthält
    if (segments.length > 0) {
      return segments[segments.length - 1];
    }

    return path; // Fallback, falls keine Segmente vorhanden sind
  }

  /** Extrahiert einen Wert aus einem Objekt anhand des Property-Namens mittels Reflection */
  private static Object extractValueFromObject(Object obj, String propertyName) throws Exception {
    if (obj == null) {
      return null;
    }

    // Bei "Id" suchen wir nach "id" (Kleinbuchstaben für Java-Konventionen)
    String javaPropertyName =
        propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

    try {
      // Versuchen, das Feld direkt zu finden
      Field field = obj.getClass().getDeclaredField(javaPropertyName);
      field.setAccessible(true);
      return field.get(obj);
    } catch (NoSuchFieldException e) {
      // Falls das nicht klappt, versuchen wir es mit dem Original-Namen
      try {
        Field field = obj.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        return field.get(obj);
      } catch (NoSuchFieldException e2) {
        // Als letzte Möglichkeit versuchen wir über die Getter-Methode
        String getterName =
            "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        try {
          return obj.getClass().getMethod(getterName).invoke(obj);
        } catch (Exception e3) {
          throw new Exception("Property " + propertyName + " konnte nicht gefunden werden.");
        }
      }
    }
  }
}

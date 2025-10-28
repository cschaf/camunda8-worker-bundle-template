package com.schaflabs.c8.common.utils;

import com.schaflabs.c8.common.exceptions.DateConversionException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Eine Utils-Klasse die Funktionalitäten für das Konvertieren und Manipulieren von Zeitangaben
 * bereitstellt
 */
public class DateUtils {
  /**
   * Konvertiert ein Datum mittels Quell- und Zielformat in das gewünschte Format.
   *
   * @param dateString Datum als String
   * @param sourceFormat Quellformat des Datums {@link DateTimeFormatter}
   * @param destinationFormat Zielformat des Datums {@link DateTimeFormatter}
   * @return liefert das Datum in gewünschten Format zurück
   */
  public static String convertDateByFormat(
      String dateString, String sourceFormat, String destinationFormat)
      throws DateConversionException {
    try {
      // Erstellen eines DateTimeFormatters für das Eingabeformat
      DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(sourceFormat);
      // Konvertieren des Strings zu LocalDate
      LocalDate date = LocalDate.parse(dateString, inputFormatter);
      // Erstellen eines DateTimeFormatters für das gewünschte Ausgabeformat
      DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(destinationFormat);
      // Konvertieren von LocalDate zu String im gewünschten Format
      return date.format(outputFormatter);
    } catch (DateTimeParseException dateTimeParseException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_FORMATS_ARE_NOT_VALID);
    }
  }

  /**
   * Konvertiert ein Datum mittels Quell- und Zielformat in das gewünschte Format. Hier wird davan
   * ausgegangen dass das sourceFormat="yyyy-MM-dd" und das destinationFormat="dd.MM.yyyy" ist
   *
   * @param dateString Datum als String
   * @return liefert das Datum in gewünschten Format zurück
   */
  public static String convertDateByFormat(String dateString) throws DateConversionException {
    return convertDateByFormat(dateString, "yyyy-MM-dd", "dd.MM.yyyy");
  }

  /**
   * Ermittelt ob das Zeitintervall der übergebenen Daten mehr als einen Tag beträgt
   *
   * @param startDateStr Datum für den Interval-Start
   * @param endDateStr Datum für das Interval-Ende
   * @param sourceFormat Datum-Format in dem die Daten angegeben sind
   * @return liefert true, wenn das Interval mehr als einen Tag lang ist ansonsten false
   */
  public static Boolean isMoreThenOneDay(
      String startDateStr, String endDateStr, String sourceFormat) throws DateConversionException {
    try {
      // Erstellen eines DateTimeFormatters für das Eingabeformat
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(sourceFormat);
      // Konvertieren der Strings zu LocalDate
      LocalDate startDate = LocalDate.parse(startDateStr, formatter);
      LocalDate endDate = LocalDate.parse(endDateStr, formatter);
      // Berechnen der Tage zwischen den beiden Datumswerten
      long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

      return daysBetween > 0;
    } catch (DateTimeParseException dateTimeParseException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    }
  }

  /**
   * Ermittelt ob das Zeitintervall der übergebenen Daten mehr als einen Tag beträgt. Hier wird
   * davon ausgegenagen, dass das Daten-Format "yyyy-MM-dd" ist.
   *
   * @param startDateStr Datum für den Interval-Start
   * @param endDateStr Datum für das Interval-Ende
   * @return liefert true, wenn das Interval mehr als einen Tag lang ist ansonsten false
   */
  public static Boolean isMoreThenOneDay(String startDateStr, String endDateStr)
      throws DateConversionException {
    return isMoreThenOneDay(startDateStr, endDateStr, "yyyy-MM-dd");
  }

  /**
   * Ermittelt das Datum des nächstmöglichen Arbeitstag. Dabei werden lediglich die deutschen
   * Werktage als mögliche Arbeitstage angesehen.
   *
   * @param dateString Datum, von dem aus der nächste Arbeitstag berechnet wird
   * @return neues Datum, welches den nächsten Arbeitstag symbolisiert
   */
  public static String calculateNextWorkingDay(String dateString) throws DateConversionException {
    try {
      // Erstellen eines DateTimeFormatters für das Eingabeformat
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      // Konvertieren des Strings zu LocalDate
      LocalDate date = LocalDate.parse(dateString, formatter);
      // Finden des nächsten Werktags ohne Feiertagsüberprüfung
      LocalDate nextWorkday = findNextWorkday(date);
      return nextWorkday.format(formatter);
    } catch (DateTimeParseException exception) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    }
  }

  /**
   * Ermittelt das Datum des nächstmöglichen Arbeitstag. Dabei werden lediglich die deutschen
   * Werktage als mögliche Arbeitstage angesehen.
   *
   * @param date Datum als LocalDate
   * @return Datum welches den nächsten Arbeitstag symbolisiert.
   */
  private static LocalDate findNextWorkday(LocalDate date) {
    LocalDate nextWorkday = date.plusDays(1);

    while (isWeekend(nextWorkday)) {
      nextWorkday = nextWorkday.plusDays(1);
    }

    return nextWorkday;
  }

  /**
   * Prüft ein Datum, ob dies Samstag oder Sonntag ist.
   *
   * @param date Datum, was geprüft werden soll
   * @return liefert true, wenn das Datum am Wochenende ist, sonst false
   */
  private static boolean isWeekend(LocalDate date) {
    return date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
        || date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
  }

  /**
   * Setzt die Uhrzeit eines Datums auf 23:59, also auf das Ende eines Tages
   *
   * @param dateString Datum als String
   * @param formatter Format des Datums
   * @return liefert das neue Datum zurück
   */
  public static String setDateEndOfDay(String dateString, DateTimeFormatter formatter)
      throws DateConversionException {
    try {
      // Konvertieren des Strings zu LocalDate
      LocalDateTime date = LocalDateTime.parse(dateString, formatter);
      // Setzen der Zeit auf 23:59
      LocalDateTime dateTime =
          LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);
      // Optional: Formatieren als String im Format "yyyy-MM-dd HH:mm"
      return dateTime.format(formatter);
    } catch (DateTimeParseException dateTimeParseException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_FORMATS_ARE_NOT_VALID);
    }
  }

  /**
   * Setzt die Uhrzeit eines Datums auf 23:59, also auf das Ende eines Tages. Hier wird angenommen,
   * dass das Format des Datum "yyyy-MM-dd" entspricht
   *
   * @param dateString Datum als String
   * @return liefert das neue Datum zurück
   */
  public static String setDateEndOfDay(String dateString) throws DateConversionException {
    return setDateEndOfDay(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  /**
   * Addiert einen Datg zu einem Datum hinzu
   *
   * @param dateString Datum als String
   * @param sourceFormat Format des Datums
   * @return liefert das neue Datum zurück
   */
  public static String addOneDay(String dateString, String sourceFormat)
      throws DateConversionException {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(sourceFormat);
      // Konvertieren des Strings zu LocalDate
      LocalDate date = LocalDate.parse(dateString, formatter);
      // Einen Tag hinzufügen
      LocalDate newDate = date.plusDays(1);
      // Konvertieren des neuen Datums zu einem String
      return newDate.format(formatter);
    } catch (DateTimeParseException dateTimeParseException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_FORMATS_ARE_NOT_VALID);
    }
  }

  public static String addOneDay(String dateString, DateTimeFormatter formatter)
      throws DateConversionException {
    try {
      // Konvertieren des Strings zu LocalDate
      LocalDateTime date = LocalDateTime.parse(dateString, formatter);
      // Einen Tag hinzufügen
      LocalDateTime newDate = date.plusDays(1);
      // Konvertieren des neuen Datums zu einem String
      return newDate.format(formatter);
    } catch (DateTimeParseException dateTimeParseException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_FORMATS_ARE_NOT_VALID);
    }
  }

  /**
   * Addiert einen Tag zum angegebenen Datum hinzu. Hier wird angenommen, dass das Format des Datum
   * "yyyy-MM-dd" entspricht
   *
   * @param dateString Datum als String
   * @return liefert das neue Datum zurück
   */
  public static String addOneDay(String dateString) throws DateConversionException {
    return addOneDay(dateString, "yyyy-MM-dd");
  }

  /**
   * Prüft, ob die beiden zu prüfenden Daten als selbes Datum gelten
   *
   * @param date1 Datum 1 was mit Datum 2 verglichen werden soll
   * @param date2 Datum 2 was mit Datum 1 verglichen werden soll
   * @param dateFormat Datum-Format welches beide Daten haben
   * @return liefert true, wenn beide Daten identisch sind ansonsten false
   */
  public static boolean isSameDay(String date1, String date2, String dateFormat)
      throws DateConversionException {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
      LocalDate startDate = LocalDate.parse(date1, formatter);
      LocalDate endDate = LocalDate.parse(date2, formatter);

      return startDate.isEqual(endDate);
    } catch (DateTimeParseException ex) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_COULD_NOT_BE_PARSED);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new DateConversionException(DateConversionException.GIVEN_DATE_FORMATS_ARE_NOT_VALID);
    }
  }

  /**
   * Prüft, ob die beiden zu prüfenden Daten als selbes Datum gelten. Hier wird angenommen, dass das
   * Format beider Daten "yyyy-MM-dd" entspricht
   *
   * @param date1 Datum 1 was mit Datum 2 verglichen werden soll
   * @param date2 Datum 2 was mit Datum 1 verglichen werden soll
   * @return liefert true, wenn beide Daten identisch sind ansonsten false
   */
  public static boolean isSameDay(String date1, String date2) throws DateConversionException {
    return isSameDay(date1, date2, "yyyy-MM-dd");
  }
}

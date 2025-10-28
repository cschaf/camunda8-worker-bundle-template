package com.schaflabs.c8.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Utility-Klasse zum Parsen und Formatieren von FEEL DateTime-Ausdrücken */
public class FeelDateUtils {

  // Regex-Patterns für die verschiedenen DateTime-Formate
  private static final Pattern LOCAL_PATTERN =
      Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?)");
  private static final Pattern OFFSET_PATTERN =
      Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?)([-+]\\d{2}:\\d{2})");
  private static final Pattern ZONE_PATTERN =
      Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?)@([\\w/]+)");

  // Neues Pattern für Offset+Zone Format: 2025-02-21T10:32:05.131+01:00[Europe/Berlin]
  private static final Pattern OFFSET_ZONE_PATTERN =
      Pattern.compile(
          "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?)([-+]\\d{2}:\\d{2})\\[([\\w/]+)\\]");

  /**
   * Parst einen FEEL DateTime-String in ein ZonedDateTime - Unterstützt lokale Zeit (ohne Zeitzone)
   * - Unterstützt Offset-Zeit (+/-HH:mm) - Unterstützt benannte Zeitzonen (@Europe/Paris) -
   * Unterstützt Offset+Zone Format (2025-02-21T10:32:05.131+01:00[Europe/Berlin])
   *
   * @param feelDateTime Der zu parsende FEEL DateTime-String
   * @return ZonedDateTime-Objekt
   * @throws DateTimeParseException Bei ungültigem Format
   */
  public static ZonedDateTime parseFeelDateTime(String feelDateTime) {
    // @ am Anfang entfernen, falls vorhanden
    if (feelDateTime.startsWith("@")) {
      feelDateTime = feelDateTime.substring(1);
    }

    // Versuch, mit Offset+Zone zu parsen (2025-02-21T10:32:05.131+01:00[Europe/Berlin])
    Matcher offsetZoneMatcher = OFFSET_ZONE_PATTERN.matcher(feelDateTime);
    if (offsetZoneMatcher.matches()) {
      String dateTimePart = offsetZoneMatcher.group(1);
      String offsetPart = offsetZoneMatcher.group(2);
      String zonePart = offsetZoneMatcher.group(3);

      // Erst parsen mit dem Offset
      LocalDateTime localDateTime =
          LocalDateTime.parse(dateTimePart, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      ZoneOffset offset = ZoneOffset.of(offsetPart);

      // Dann in die korrekte Zeitzone umwandeln
      ZoneId zoneId = ZoneId.of(zonePart);
      return ZonedDateTime.ofInstant(localDateTime.toInstant(offset), zoneId);
    }

    // Versuch, mit Offset zu parsen (+/-HH:mm)
    Matcher offsetMatcher = OFFSET_PATTERN.matcher(feelDateTime);
    if (offsetMatcher.matches()) {
      String dateTimePart = offsetMatcher.group(1);
      String offsetPart = offsetMatcher.group(2);
      LocalDateTime localDateTime =
          LocalDateTime.parse(dateTimePart, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      ZoneOffset offset = ZoneOffset.of(offsetPart);
      return ZonedDateTime.of(localDateTime, offset);
    }

    // Versuch, mit benannter Zeitzone zu parsen (@ZoneId)
    Matcher zoneMatcher = ZONE_PATTERN.matcher(feelDateTime);
    if (zoneMatcher.matches()) {
      String dateTimePart = zoneMatcher.group(1);
      String zonePart = zoneMatcher.group(2);
      LocalDateTime localDateTime =
          LocalDateTime.parse(dateTimePart, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      ZoneId zoneId = ZoneId.of(zonePart);
      return ZonedDateTime.of(localDateTime, zoneId);
    }

    // Versuch, lokale Zeit zu parsen (ohne Zeitzone)
    Matcher localMatcher = LOCAL_PATTERN.matcher(feelDateTime);
    if (localMatcher.matches()) {
      String dateTimePart = localMatcher.group(1);
      LocalDateTime localDateTime =
          LocalDateTime.parse(dateTimePart, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    }

    // Als letzten Ausweg versuchen wir die direkte Parsing mit ZonedDateTime
    try {
      return ZonedDateTime.parse(feelDateTime);
    } catch (DateTimeParseException e) {
      throw new DateTimeParseException(
          "Ungültiges FEEL DateTime-Format: " + e.getMessage(), feelDateTime, 0);
    }
  }

  /**
   * Formatiert ein ZonedDateTime zu einem ISO-8601 konformen String ohne Zeitzoneninformation
   * Format: 2025-02-20T18:50:38.536Z
   *
   * @param dateTime Das zu formatierende ZonedDateTime
   * @return Formatierter String ohne Zeitzoneninformation
   */
  public static String formatWithoutZone(ZonedDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
  }

  /**
   * Fügt eine Dauer zu einem FEEL DateTime-String hinzu und gibt das Ergebnis ohne
   * Zeitzoneninformation zurück
   *
   * @param feelDateTime Der FEEL DateTime-String
   * @param hours Anzahl der hinzuzufügenden Stunden
   * @return Neuer DateTime-String ohne Zeitzoneninformation
   */
  public static String addHoursWithoutZone(String feelDateTime, int hours) {
    ZonedDateTime dateTime = parseFeelDateTime(feelDateTime);
    ZonedDateTime newDateTime = dateTime.plusHours(hours);
    return formatWithoutZone(newDateTime);
  }

  /**
   * Konvertiert den FEEL-Ausdruck now() + duration("PTxH") in einen ISO-String ohne Zeitzone
   *
   * @param hours Anzahl der zum aktuellen Zeitpunkt hinzuzufügenden Stunden
   * @return ISO-DateTime-String ohne Zeitzoneninformation
   */
  public static String nowPlusHoursWithoutZone(int hours) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime newDateTime = now.plusHours(hours);
    return formatWithoutZone(newDateTime);
  }

  /**
   * Setzt die Uhrzeit eines ZonedDateTime auf das Ende des Tages (23:59:59.999)
   *
   * @param dateTime Das Ausgangsdatum
   * @return ZonedDateTime mit Uhrzeit am Ende des Tages
   */
  public static ZonedDateTime setToEndOfDay(ZonedDateTime dateTime) {
    return dateTime
        .withHour(23)
        .withMinute(59)
        .withSecond(59)
        .withNano(999_000_000); // 999 Millisekunden
  }

  /**
   * Formatiert das übergebene Datum mit Uhrzeit am Ende des Tages ohne Zeitzone
   *
   * @param feelDateTime FEEL DateTime-String
   * @return Formatierter String für das Ende des Tages
   */
  public static String setToEndOfDayFormatted(String feelDateTime) {
    ZonedDateTime dateTime = parseFeelDateTime(feelDateTime);
    ZonedDateTime endOfDay = setToEndOfDay(dateTime);
    return formatWithoutZone(endOfDay);
  }

  /**
   * Gibt das Datum des letzten Tages der aktuellen Woche (Sonntag) für das übergebene Datum zurück
   *
   * @param dateTime Das Ausgangsdatum
   * @return ZonedDateTime des letzten Tages der Woche (Sonntag)
   */
  public static ZonedDateTime getLastDayOfWeek(ZonedDateTime dateTime) {
    // In Java ist DayOfWeek.SUNDAY = 7, DayOfWeek.MONDAY = 1
    return dateTime.with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
  }

  /**
   * Formatiert das Datum des letzten Tages der Woche ohne Zeitzone
   *
   * @param feelDateTime FEEL DateTime-String
   * @return Formatierter String für den letzten Tag der Woche
   */
  public static String getLastDayOfWeekFormatted(String feelDateTime) {
    ZonedDateTime dateTime = parseFeelDateTime(feelDateTime);
    ZonedDateTime lastDay = getLastDayOfWeek(dateTime);
    return formatWithoutZone(lastDay);
  }

  /**
   * Gibt das Datum des nächsten Arbeitstages (Montag-Freitag) für das übergebene Datum zurück Falls
   * das übergebene Datum bereits ein Arbeitstag ist, wird das gleiche Datum zurückgegeben
   *
   * @param dateTime Das Ausgangsdatum
   * @return ZonedDateTime des nächsten Arbeitstages
   */
  public static ZonedDateTime getNextWorkday(ZonedDateTime dateTime) {
    DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

    // Wenn Freitag, gehe zu Montag (+3 Tage)
    if (dayOfWeek == DayOfWeek.FRIDAY) {
      return dateTime.plusDays(3);
    }
    // Wenn Samstag, gehe zu Montag (+2 Tage)
    else if (dayOfWeek == DayOfWeek.SATURDAY) {
      return dateTime.plusDays(2);
    }
    // Wenn Sonntag, gehe zu Montag (+1 Tag)
    else if (dayOfWeek == DayOfWeek.SUNDAY) {
      return dateTime.plusDays(1);
    }
    // Wenn bereits Arbeitstag (Montag-Donnerstag), gebe das gleiche Datum zurück
    else {
      return dateTime.plusDays(1);
    }
  }

  /**
   * Formatiert das Datum des nächsten Arbeitstages ohne Zeitzone
   *
   * @param feelDateTime FEEL DateTime-String
   * @return Formatierter String für den nächsten Arbeitstag
   */
  public static String getNextWorkdayFormatted(String feelDateTime) {
    ZonedDateTime dateTime = parseFeelDateTime(feelDateTime);
    ZonedDateTime nextWorkday = getNextWorkday(dateTime);
    return formatWithoutZone(nextWorkday);
  }

  /**
   * Formatiert ein ZonedDateTime zu einem ISO-8601 konformen String mit Offset aber ohne
   * Zeitzoneninformation Format: 2025-02-20T17:00:00.804+01:00
   *
   * @param dateTime Das zu formatierende ZonedDateTime
   * @return Formatierter String mit Offset aber ohne Zeitzoneninformation
   */
  public static String formatWithOffset(ZonedDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
  }

  /**
   * Parst einen FEEL DateTime-String und formatiert ihn mit Offset aber ohne Zeitzoneninformation
   * Format: 2025-02-20T17:00:00.804+01:00
   *
   * @param feelDateTime Der zu parsende FEEL DateTime-String
   * @return Formatierter String mit Offset aber ohne Zeitzoneninformation
   */
  public static String formatWithOffsetFromString(String feelDateTime) {
    ZonedDateTime dateTime = parseFeelDateTime(feelDateTime);
    return formatWithOffset(dateTime);
  }

  /** Beispiel-Verwendung */
  public static void main(String[] args) {
    // Beispiel-Strings
    String localTime = "2025-02-20T18:50:38.536";
    String offsetTime = "2025-02-20T18:50:38.536+01:00";
    String zoneTime = "2025-02-20T18:50:38.536@Europe/Paris";
    String atPrefixTime = "@2025-02-20T18:50:38.536@Europe/Berlin";
    String offsetZoneTime = "2025-02-21T10:32:05.131+01:00[Europe/Berlin]";

    // Alle parsen und ohne Zeitzone formatieren
    System.out.println(formatWithoutZone(parseFeelDateTime(localTime)));
    System.out.println(formatWithoutZone(parseFeelDateTime(offsetTime)));
    System.out.println(formatWithoutZone(parseFeelDateTime(zoneTime)));
    System.out.println(formatWithoutZone(parseFeelDateTime(atPrefixTime)));
    System.out.println(formatWithoutZone(parseFeelDateTime(offsetZoneTime)));

    // Stunden hinzufügen
    System.out.println(addHoursWithoutZone(offsetTime, 1));

    // Aktueller Zeitpunkt + 1 Stunde (entspricht now() + duration("PT1H"))
    System.out.println(nowPlusHoursWithoutZone(1));

    // Test für letzten Tag der Woche
    System.out.println("\n--- Letzter Tag der Woche Beispiele ---");
    System.out.println(
        "Für Montag (16.09.2024): " + getLastDayOfWeekFormatted("2024-09-16T10:00:00"));
    System.out.println(
        "Für Mittwoch (18.09.2024): " + getLastDayOfWeekFormatted("2024-09-18T10:00:00"));
    System.out.println(
        "Für Sonntag (22.09.2024): " + getLastDayOfWeekFormatted("2024-09-22T10:00:00"));

    // Test für nächsten Arbeitstag
    System.out.println("\n--- Nächster Arbeitstag Beispiele ---");
    System.out.println(
        "Für Mittwoch (18.09.2024): " + getNextWorkdayFormatted("2024-09-18T10:00:00"));
    System.out.println(
        "Für Freitag (20.09.2024): " + getNextWorkdayFormatted("2024-09-20T10:00:00"));
    System.out.println(
        "Für Samstag (21.09.2024): " + getNextWorkdayFormatted("2024-09-21T10:00:00"));
    System.out.println(
        "Für Sonntag (22.09.2024): " + getNextWorkdayFormatted("2024-09-22T10:00:00"));

    // Test für Ende des Tages
    System.out.println("\n--- Ende des Tages Beispiele ---");
    System.out.println(
        "Vormittag zu Ende des Tages: " + setToEndOfDayFormatted("2024-09-18T10:30:45"));
    System.out.println(
        "Mitternacht zu Ende des Tages: " + setToEndOfDayFormatted("2024-09-18T00:00:00"));
    System.out.println(
        "Mit Zeitzone zu Ende des Tages: " + setToEndOfDayFormatted("2024-09-18T15:42:23+02:00"));
    System.out.println(
        "Mit Offset+Zone zu Ende des Tages: "
            + setToEndOfDayFormatted("2025-02-21T10:32:05.131+01:00[Europe/Berlin]"));
  }
}

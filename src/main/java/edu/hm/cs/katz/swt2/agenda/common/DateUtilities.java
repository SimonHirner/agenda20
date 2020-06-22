package edu.hm.cs.katz.swt2.agenda.common;

import java.util.Calendar;
import java.util.Date;

/**
 * Hilfsklasse für die Rückgabe von passenden Datums-Objekten.
 * 
 * @author Simon Hirner (mailto: simon.hirner@hm.edu)
 */
public final class DateUtilities {
  
  private DateUtilities() {
  }
  
  /**
   * Gibt das heutige Datum zurück.
   */
  public static Date getCurrentDate() {
    Calendar currentDate = Calendar.getInstance();
    currentDate.set(Calendar.HOUR_OF_DAY, 0);
    currentDate.set(Calendar.MINUTE, 0);
    currentDate.set(Calendar.SECOND, 0);
    currentDate.set(Calendar.MILLISECOND, 0);
    return currentDate.getTime();
  }
  
  /**
   * Gibt ein Datum von dem entsprechenden Tag, Monat und Jahr zurück.
   * 
   * @param day Tag
   * @param month Monat
   * @param year Jahr
   */
  public static Date getDate(int day, int month, int year) {
    Calendar currentDate = Calendar.getInstance();
    currentDate.setTime(getCurrentDate());
    currentDate.set(Calendar.DAY_OF_MONTH, day);
    currentDate.set(Calendar.MONTH, month - 1);
    currentDate.set(Calendar.YEAR, year);
    return currentDate.getTime();
  }
}

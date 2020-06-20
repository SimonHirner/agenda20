package edu.hm.cs.katz.swt2.agenda.common;

public enum VisibilityEnum {
  PRIVATE("Privat"),
  PUBLIC("Öffentlich");

  public final String displayValue;

  private VisibilityEnum(String displayValue) {
    this.displayValue = displayValue;
  }

  public String getDisplayValue() {
    return displayValue;
  }
}

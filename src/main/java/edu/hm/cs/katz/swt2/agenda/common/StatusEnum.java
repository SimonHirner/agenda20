package edu.hm.cs.katz.swt2.agenda.common;

public enum StatusEnum {
  NEU("Noch nicht erledigt"),
  OFFEN("Noch nicht erledigt"),
  FERTIG("Erledigt"),
  ABGELAUFEN("Nicht rechtzeitig erledigt");
  
  public final String displayValue;

  private StatusEnum(String displayValue) {
    this.displayValue = displayValue;
  }

  public String getDisplayValue() {
    return displayValue;
  }
}
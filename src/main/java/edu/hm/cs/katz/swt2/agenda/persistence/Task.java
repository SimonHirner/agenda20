package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * Modellklasse für die Speicherung der Aufgaben. Enthält die Abbildung auf eine Datenbanktabelle in
 * Form von JPA-Annotation.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Entity
public class Task {
  
  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  
  @NotNull
  @Length(min = 8, max = 32)
  @Column(length = 32)
  private String title;
  
  @NotNull
  @Length(min = 8, max = 120)
  @Column(length = 120)
  private String shortInfo;
  
  @NotNull
  @Length(min = 0, max = 500)
  @Column(length = 500)
  private String longInfo;
  
  @NotNull
  @ManyToOne
  private Topic topic;
  
  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public Task() {
    // JPA benötigt einen Default-Konstruktor!
  }
  
  /**
   * Konstruktor zum Erstellen eines neuen Tasks.
   *
   * @param topic Topic, darf nicht null sein.
   * @param title Titel, darf nicht null sein.
   * @param shortInfo Kurzbeschreibung, darf nicht null sein.
   * @param longInfo Ausführliche Beschreibung, darf nicht null sein.
   */
  public Task(final Topic topic, final String title, final String shortInfo, final String longInfo) {
    this.topic = topic;
    topic.addTask(this);
    this.title = title;
    this.shortInfo = shortInfo;
    this.longInfo = longInfo;
  }
  
  @Override
  public String toString() {
    return "Task \"" + title + "\"";
  }
  
  public Long getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public String getshortInfo() {
    return shortInfo;
  }
  
  public String getlongInfo() {
    return longInfo;
  }
  
  public Topic getTopic() {
    return topic;
  }
  
  /*
   * Standard-Methoden. Es ist sinnvoll, hier auf die Auswertung der Assoziationen zu verzichten,
   * nur die Primärschlüssel zu vergleichen und insbesonderen Getter zu verwenden, um auch mit den
   * generierten Hibernate-Proxys kompatibel zu bleiben.
   */
  
  @Override
  public int hashCode() {
    return Objects.hash(id, topic);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Task)) {
      return false;
    }
    Task other = (Task) obj;
    return Objects.equals(getId(), other.getId());
  }
}

package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

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
  private String shortDescription;
  
  @Length(min = 0, max = 1000)
  @Column(length = 1000)
  private String longDescription;
  
  @NotNull
  @ManyToOne
  private Topic topic;
  
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Temporal(TemporalType.DATE)
  private Date deadline;
  
  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  private Collection<Status> statuses;
  
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
   * @param title Titel, zwischen 8 und 32 Zeichen.
   * @param shortDescription Kurzbeschreibung, zwischen 8 und 120 Zeichen.
   * @param longDescription Langbeschreibung, mit maximal 1000 Zeichen.
   * @param deadline Deadline in Form eines Datums.
   */
  public Task(final Topic topic, final String title, final String shortDescription,
      final String longDescription, Date deadline) {
    this.topic = topic;
    topic.addTask(this);
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.deadline = deadline;
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
  
  public String getShortDescription() {
    return shortDescription;
  }
  
  public String getLongDescription() {
    return longDescription;
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

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public Collection<Status> getStatuses() {
    return statuses;
  }

  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }

  public Date getDeadline() {
    return deadline;
  }
}

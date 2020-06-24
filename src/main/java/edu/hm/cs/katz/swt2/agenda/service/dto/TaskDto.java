package edu.hm.cs.katz.swt2.agenda.service.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Tasks. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @see OwnerTaskDto
 * @see SubscriberTaskDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class TaskDto {
  
  private Long id;
  
  private String title;
  
  private SubscriberTopicDto topic;
  
  private String shortDescription;
  
  private String longDescription;
  
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date deadline;
  
  /**
   * Konstruktor.
   */
  public TaskDto(Long id, String title, String shortDescription, String longDescription,
      Date deadline, SubscriberTopicDto topicDto) {
    this.id = id;
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.topic = topicDto;
    this.deadline = deadline;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getShortDescription() {
    return shortDescription;
  }
  
  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }
  
  public String getLongDescription() {
    return longDescription;
  }
  
  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }
  
  public SubscriberTopicDto getTopic() {
    return topic;
  }

  public Date getDeadline() {
    return deadline;
  }

  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }
}

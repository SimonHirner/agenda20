package edu.hm.cs.katz.swt2.agenda.service.dto;

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
  Long id;
  String title;
  SubscriberTopicDto topic;
  String shortDescription;
  String longDescription;
  

  /**
   * Konstruktor.
   */
  public TaskDto(Long id, String title, String shortInfo, String longInfo,
      SubscriberTopicDto topicDto) {
    this.id = id;
    this.title = title;
    this.shortDescription = shortInfo;
    this.longDescription = longInfo;
    this.topic = topicDto;
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

  public void setTopic(SubscriberTopicDto topic) {
    this.topic = topic;
  }


}

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
  String shortInfo;
  String longInfo;
  

  /**
   * Konstruktor.
   */
  public TaskDto(Long id, String title, String shortInfo, String longInfo,
      SubscriberTopicDto topicDto) {
    this.id = id;
    this.title = title;
    this.shortInfo = shortInfo;
    this.longInfo = longInfo;
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
  
  public String getShortInfo() {
    return shortInfo;
  }
  
  public void setShortInfo(String shortInfo) {
    this.shortInfo = shortInfo;
  }
  
  public String getLongInfo() {
    return longInfo;
  }
  
  public void setLongInfo(String longInfo) {
    this.longInfo = longInfo;
  }
  
  public SubscriberTopicDto getTopic() {
    return topic;
  }

  public void setTopic(SubscriberTopicDto topic) {
    this.topic = topic;
  }


}

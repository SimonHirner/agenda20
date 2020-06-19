package edu.hm.cs.katz.swt2.agenda.service.dto;

import edu.hm.cs.katz.swt2.agenda.common.VisibilityEnum;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Topics. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @see OwnerTopicDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class  SubscriberTopicDto {
  private String uuid;
  private UserDisplayDto creator;
  private String title;
  private VisibilityEnum visibility;
  private String shortDescription;
  private String longDescription;
  private int subscriberCount;

  /**
   * Konstruktor.
   */
  public SubscriberTopicDto(String uuid, UserDisplayDto creator, String title, VisibilityEnum visibility,
      String shortDescription, String longDescription) {
    this.uuid = uuid;
    this.creator = creator;
    this.title = title;
    this.visibility = visibility;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
  }

  public String getUuid() {
    return uuid;
  }

  public UserDisplayDto getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public VisibilityEnum getVisibility() {
    return visibility;
  }

  public void setVisibility(VisibilityEnum visibility) {
    this.visibility = visibility;
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

  public int getSubscriberCount() {
    return subscriberCount;
  }

  public void setSubscriberCount(int subscriberCount) {
    this.subscriberCount = subscriberCount;
  }
  
  public String getKey() {
    return getUuid().substring(28);
  }
}

package edu.hm.cs.katz.swt2.agenda.service.dto;

import edu.hm.cs.katz.swt2.agenda.common.VisibilityEnum;
import java.util.List;

/**
 * Transferobjekt für Topics mit Metadaten, die nur für Verwalter eines Topics (d.h. Eigentümer des
 * Topics) sichtbar sind. Transferobjekte sind Schnittstellenobjekte der Geschäftslogik; Sie sind
 * nicht Teil des Modells, so dass Änderungen an den Transferobjekten die Überprüfungen der
 * Geschäftslogik nicht umgehen können.
 * 
 * @see SubscriberTopicDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class OwnerTopicDto extends SubscriberTopicDto {
  
  private List<UserDisplayDto> subscribers;
  
  public OwnerTopicDto(String uuid, UserDisplayDto user, String title, VisibilityEnum visibility,
      String shortDescription, String longDescription) {
    super(uuid, user, title, visibility, shortDescription, longDescription);
  }
  
  public void setSubscribers(List<UserDisplayDto> subscribers) {
    this.subscribers = subscribers;
  }

  public List<UserDisplayDto> getSubscribers() {
    return subscribers;
  }
  
}

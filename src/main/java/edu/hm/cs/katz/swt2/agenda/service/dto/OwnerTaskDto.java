package edu.hm.cs.katz.swt2.agenda.service.dto;

/**
 * Transferobjekt für Tasks mit Metadaten, die nur für Verwalter eines Tasks (d.h. Eigentümer des
 * Topics) sichtbar sind. Transferobjekte sind Schnittstellenobjekte der Geschäftslogik; Sie sind
 * nicht Teil des Modells, so dass Änderungen an den Transferobjekten die Überprüfungen der
 * Geschäftslogik nicht umgehen können.
 *
 * @see TaskDto
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class OwnerTaskDto extends TaskDto {
  
  public OwnerTaskDto(Long id, String title, String shortInfo, String longInfo, 
      SubscriberTopicDto topicDto) {
    super(id, title, shortInfo, longInfo, topicDto);
  }
}

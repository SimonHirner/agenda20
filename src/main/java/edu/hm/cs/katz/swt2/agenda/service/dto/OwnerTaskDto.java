package edu.hm.cs.katz.swt2.agenda.service.dto;

import java.util.Date;
import java.util.List;

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
  
  private int doneStatusesCount;
  
  private List<StatusDto> statusesWithComment;
  
  public OwnerTaskDto(Long id, String title, String shortDescription, String longDescription,
      Date deadline, SubscriberTopicDto topicDto) {
    super(id, title, shortDescription, longDescription, deadline, topicDto);
  }

  public int getDoneStatusesCount() {
    return doneStatusesCount;
  }

  public void setDoneStatusesCount(int doneStatusCount) {
    this.doneStatusesCount = doneStatusCount;
  }

  public List<StatusDto> getStatusesWithComment() {
    return statusesWithComment;
  }

  public void setStatusesWithComment(List<StatusDto> statusesWithComment) {
    this.statusesWithComment = statusesWithComment;
  }
}

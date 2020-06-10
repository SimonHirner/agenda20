package edu.hm.cs.katz.swt2.agenda.service.dto;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;

/**
 * Transferobjekt für Statusinformationen zu Tasks, die spezifisch für Abonnenten des Topics sind.
 * Transferobjekte sind Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells,
 * so dass Änderungen an den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen
 * können.
 * 
 * @see TaskDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class StatusDto {

  private StatusEnum status;
  
  private UserDisplayDto user;
  
  private String comment;

  /**
   * Konstruktor.
   */
  public StatusDto(StatusEnum status, UserDisplayDto user, String comment) {
    this.status = status;
    this.user = user;
    this.comment = comment;
  }

  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public UserDisplayDto getUser() {
    return user;
  }

  public void setUser(UserDisplayDto user) {
    this.user = user;
  }
}

package edu.hm.cs.katz.swt2.agenda.service.dto;

import java.util.Map;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Anwendern. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class UserDisplayDto {
  
  private String login = "";
  
  private String name;
  
  private int topicCount;
  
  private int subscriptionCount;
  
  private Map<String, Integer> doneTasksCountForTopicUuid;
  
  public String getDisplayName() {
    return name;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public int getTopicCount() {
    return topicCount;
  }

  public void setTopicCount(int topicCount) {
    this.topicCount = topicCount;
  }

  public int getSubscriptionCount() {
    return subscriptionCount;
  }

  public void setSubscriptionCount(int subscriptionCount) {
    this.subscriptionCount = subscriptionCount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Integer> getDoneTasksCountForTopicUuid() {
    return doneTasksCountForTopicUuid;
  }

  public void setDoneTasksCountForTopicUuid(Map<String, Integer> doneTasksCountForTopicUuid) {
    this.doneTasksCountForTopicUuid = doneTasksCountForTopicUuid;
  }
}

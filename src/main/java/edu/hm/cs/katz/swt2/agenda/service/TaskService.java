package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Serviceklasse für Verarbeitung von Tasks.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public interface TaskService {

  /**
   * Erstellt einen neuen Task.
   */
  Long createTask(String topicUuid, String title, String shortInfo, String longInfo, String login,
      Date deadline, Date creationDate);
  
  /**
   * Lösche einen Task.
   */
  void deleteTask(Long id, String login);

  /**
   * Zugriff auf einen Task (priviligierte Sicht für Ersteller des Topics).
   */
  OwnerTaskDto getManagedTask(Long taskId, String login);

  /**
   * Zugriff auf alle Tasks eines eigenen Topics.
   */
  List<OwnerTaskDto> getManagedTasks(String topicUuid, String login);

  /**
   * Zugriff auf einen Task (Abonnentensicht).
   */
  SubscriberTaskDto getTask(Long taskId, String login);

  /**
   * Zugriff auf alle Tasks abonnierter Topics.
   */
  List<SubscriberTaskDto> getAllTasksOfSubscribedTopics(String login);
  
  /**
   * Zugriff auf alle Tasks abonnierter Topics mit Status als Filter.
   */
  List<SubscriberTaskDto> getAllTasksForStatus(String login, StatusEnum status, String search);
  
  /**
   * Zugriff auf alle Tasks eines abonnierten Topics.
   */
  List<SubscriberTaskDto> getTasksOfTopic(String topicUuid, String login);
  
  /**
   * Zugriff auf alle Tasks eines abonnierten Topics mit Status als Filter.
   */
  List<SubscriberTaskDto> getTasksOfTopicWithStatus(String topicUuid, String login,
      StatusEnum status);
  
  /**
   * Markiert einen Task für einen Abonnenten als "done".
   */
  void checkTask(Long taskId, String login);

  /**
   * Aktualisierung der Beschreibungen.
   */
  void updateTask(Long id, String login, String shortDescription, String longDescription,
      Date deadline, Date updateDate);
  
  /**
   * Zurücksetzen des Status eines Tasks.
   */
  void resetTask(Long id, String name);
  
  /**
   * Zurücksetzen der Status aller Tasks.
   */
  void resetAllTasks(String uuid, String name);
  
  /**
   * Aktualisierung eines Kommentars.
   */
  void updateComment(Long taskId, String login, String comment);

  /**
   * Zugriff auf einen Status.
   */
  StatusDto getStatus(Long taskId, String login);
  
  /**
   * Aktualisierung der Bewertung.
   */
  void updateRating(Long taskId, String loginTopicOwner, String loginSubscriber, String rating);
  
  /**
   * Zugriff auf alle Status eines Tasks.
   */
  List<StatusDto> getStatuses(Long taskId, String login);
  
  /**
   * Zugriff auf alle erledigten Status eines Topics für alle Abonnenten.
   */
  Map<String, Integer> getDoneStatusesCountForUser(String uuid, String login);
}
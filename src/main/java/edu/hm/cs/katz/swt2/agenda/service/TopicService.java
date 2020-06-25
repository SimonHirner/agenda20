package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.VisibilityEnum;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import java.util.List;

/**
 * Serviceklasse für Verarbeitung von Topics.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public interface TopicService {

  /**
   * Erstellt ein neues Topic.
   */
  String createTopic(String title, VisibilityEnum visibility, String shortDescription,
      String longDescription, String login);
  
  /**
   * Löscht ein Topic.
   * 
   * @param topicUuid UUID des Tasks
   * @param login Login des Anwenders
   */
  void deleteTopic(String topicUuid, String login);

  /**
   * Zugriff auf ein eigenes Topic.
   */
  OwnerTopicDto getManagedTopic(String topicUuid, String login);

  /**
   * Zugriff auf alle eigenen Topics.
   */
  List<OwnerTopicDto> getManagedTopics(String login, String search);

  /**
   * Zugriff auf ein abonniertes Topic.
   */
  SubscriberTopicDto getTopic(String topicUuid, String login);

  /**
   * Zugriff auf alle abonnierten Topics.
   */
  List<SubscriberTopicDto> getSubscriptions(String login, String search);

  /**
   * Abonnieren eines Topics.
   */
  void subscribe(String topicUuid, String login);

  /**
   * Aktualisierung der Beschreibungen.
   */
  void updateTopic(String uuid, String login, VisibilityEnum visibility,
      String shortDescription, String longDescription);
  
  /**
   * Liefert zu einem gegebenen Registrierungsschlüssel die UUID des Topics zurück.
   */
  String getTopicUuid(String key, String login);
  
  void unsubscribe(String topicUuid, String login);

  /**
   * Suchzugriff auf alle öffentlichen Topics.
   */
  List<SubscriberTopicDto> getPublicTopics(String login, String search);
}

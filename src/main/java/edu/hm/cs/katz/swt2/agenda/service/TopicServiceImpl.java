package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.UuidProviderImpl;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;

@Component
@Transactional(rollbackFor = Exception.class)
public class TopicServiceImpl implements TopicService {

  private static final Logger LOG = LoggerFactory.getLogger(TopicServiceImpl.class);

  @Autowired
  private UuidProviderImpl uuidProvider;

  @Autowired
  private UserRepository anwenderRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private DtoMapper mapper;
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void deleteTopic(String topicUuid, String login) {
    LOG.info("Lösche Topic {}.", topicUuid);
    LOG.debug("Topic wird gelöscht von {}.", login);
    
    Topic topic = topicRepository.getOne(topicUuid);
    
    User createdBy = topic.getCreator();
    if (!login.equals(createdBy.getLogin())) {
      LOG.warn("Anwender {} ist nicht berechtigt Topic {} zu löschen!", login, topicUuid);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    if (topic.getSubscriber().size() != 0) {
      LOG.debug("Topic {} wurde bereits abonniert und kann nicht gelöscht werden!", topicUuid);
      throw new ValidationException("Das Topic kann nicht gelöscht werden, da es bereits "
          + "abonniert wurde!");
    }
    
    topicRepository.delete(topic);
  }
  
  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public void updateTopic(String uuid, String login, String shortDescription,
      String longDescription) {
    LOG.info("Aktualisiere Topic {}.", uuid);
    LOG.debug("Topic wird von {} aktualisiert.", login);
    
    Topic topic = topicRepository.getOne(uuid);
    User user = anwenderRepository.getOne(login);
    if (!user.equals(topic.getCreator())) {
      LOG.warn("Anwender {} ist nicht berechtigt Topic {} zu aktualisieren!", login, uuid);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    // Validierung der Kurzbeschreibung
    if (shortDescription.length() < 1) {
      LOG.debug("Die Kurzbeschreibung {} ist leer, Topic kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Bitte gib eine Kurzbeschreibung für das Topic an!");
    }
    
    if (shortDescription.length() < 10) {
      LOG.debug("Die Kurzbeschreibung {} ist zu kurz, Topic kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Die Kurzbeschreibung des Topics muss mindestens 20 Zeichen "
          + "lang sein!");
    }
    
    if (shortDescription.length() > 120) {
      LOG.debug("Die Kurzbeschreibung {} ist zu lang, Topic kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Die Kurzbeschreibung des Topics darf höchstens 120 Zeichen "
          + "lang sein!");
    }
    
    // Validierung der Langbeschreibung  
    if (longDescription.length() > 1000) {
      LOG.debug("Die Langbeschreibung {} ist zu lang, Topic kann nicht angelegt werden.", 
          longDescription);
      throw new ValidationException("Die Langbeschreibung des Topics darf höchstens 1500 Zeichen "
          + "lang sein!");
    }    
    
    topic.setLongDescription(longDescription);
    topic.setShortDescription(shortDescription);
  }
  

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public String createTopic(String title, String shortDescription, String longDescription,
      String login) {
    LOG.info("Erstelle neues Topic {}.", title);
    LOG.debug("Topic wird erstellt von {}.", login);
    
    // Validierung des Topic Namens
    if (title.length() < 1) {
      LOG.debug("Der Name {} ist leer, Topic kann nicht angelegt werden.", title);
      throw new ValidationException("Bitte gib einen Namen für das Topic an!");
    }
    
    if (title.length() < 10) {
      LOG.debug("Der Name {} ist zu kurz, Topic kann nicht angelegt werden.", title);
      throw new ValidationException("Der Name des Topics muss mindestens 10 Zeichen lang sein!");
    }
    
    if (title.length() > 60) {
      LOG.debug("Der Name {} ist zu lang, Topic kann nicht angelegt werden.", title);
      throw new ValidationException("Der Name des Topics darf höchstens 60 Zeichen lang sein!");
    }
    
    // Validierung der Kurzbeschreibung
    if (shortDescription.length() < 1) {
      LOG.debug("Die Kurzbeschreibung {} ist leer, Topic kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Bitte gib eine Kurzbeschreibung für das Topic an!");
    }
    
    if (shortDescription.length() < 10) {
      LOG.debug("Die Kurzbeschreibung {} ist zu kurz, Topic kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Die Kurzbeschreibung des Topics muss mindestens 20 Zeichen "
          + "lang sein!");
    }
    
    if (shortDescription.length() > 120) {
      LOG.debug("Die Kurzbeschreibung {} ist zu lang, Topic kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Die Kurzbeschreibung des Topics darf höchstens 120 Zeichen "
          + "lang sein!");
    }
    
    // Validierung der Langbeschreibung  
    if (longDescription.length() > 1000) {
      LOG.debug("Die Langbeschreibung {} ist zu lang, Topic kann nicht angelegt werden.", 
          longDescription);
      throw new ValidationException("Die Langbeschreibung des Topics darf höchstens 1500 Zeichen "
          + "lang sein!");
    }    
    
    String uuid = uuidProvider.getRandomUuid();
    User creator = anwenderRepository.findById(login).get();
    Topic topic = new Topic(uuid, title, shortDescription, longDescription, creator);
    topicRepository.save(topic);
    return uuid;
  }

  @Override
  @PreAuthorize("#login==authentication.name")
  public List<OwnerTopicDto> getManagedTopics(String login) {
    LOG.info("Rufe verwaltete Topics von {} auf.", login);
  
    User creator = anwenderRepository.findById(login).get();
    List<Topic> managedTopics = topicRepository.findByCreatorOrderByTitleAsc(creator);
    List<OwnerTopicDto> result = new ArrayList<>();
    for (Topic topic : managedTopics) {
      result.add(mapper.createManagedDto(topic));
    }
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTopicDto getManagedTopic(String topicUuid, String login) {
    LOG.info("Rufe Verwaltung für Topic {} auf.", topicUuid);
    LOG.debug("Topicverwaltung wird aufgerufen von {}.", login);
    
    Topic topic = topicRepository.getOne(topicUuid);
    return mapper.createManagedDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTopicDto getTopic(String uuid, String login) {
    LOG.info("Rufe Topic {} auf.", uuid);
    LOG.debug("Topic wird aufgerufen von {}.", login);
    
    Topic topic = topicRepository.getOne(uuid);
    return mapper.createDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void subscribe(String topicUuid, String login) {
    LOG.info("Abonniere Topic {}.", topicUuid);
    LOG.debug("Topic wird von {} abonniert.", login);
    
    Topic topic = topicRepository.getOne(topicUuid);
    User anwender = anwenderRepository.getOne(login);
    topic.register(anwender);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTopicDto> getSubscriptions(String login) {
    LOG.info("Rufe abonnierte Topics von {} auf.", login);
    
    User subscriber = anwenderRepository.findById(login).get();
    Collection<Topic> subscriptions = topicRepository.findBySubscriberOrderByTitleAsc(subscriber);
    List<SubscriberTopicDto> result = new ArrayList<>();
    for (Topic topic : subscriptions) {
      result.add(mapper.createDto(topic));
    }
    
    return result;
  }
}

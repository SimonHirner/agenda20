package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.UuidProviderImpl;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class TopicServiceImpl implements TopicService {

  private static final Logger LOG = LoggerFactory.getLogger(TopicServiceImpl.class);

  @Autowired
  private UuidProviderImpl uuidProvider;

  @Autowired
  private UserRepository userRepository;

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
    
    if (topic.getSubscribers().size() != 0) {
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
    User user = userRepository.getOne(login);
    if (!user.equals(topic.getCreator())) {
      LOG.warn("Anwender {} ist nicht berechtigt Topic {} zu aktualisieren!", login, uuid);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    validateTopicShortDescription(shortDescription);
    validateTopicLongDescription(longDescription);    
    
    topic.setLongDescription(longDescription);
    topic.setShortDescription(shortDescription);
  }

  private void validateTopicShortDescription(String shortDescription) {
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
  }

  private void validateTopicLongDescription(String longDescription) {
    if (longDescription.length() > 1000) {
      LOG.debug("Die Langbeschreibung {} ist zu lang, Topic kann nicht angelegt werden.", 
          longDescription);
      throw new ValidationException("Die Langbeschreibung des Topics darf höchstens 1500 Zeichen "
          + "lang sein!");
    }
  }
  

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public String createTopic(String title, String shortDescription, String longDescription,
      String login) {
    LOG.info("Erstelle neues Topic {}.", title);
    LOG.debug("Topic wird erstellt von {}.", login);
    
    validateTopicName(title);
    validateTopicShortDescription(shortDescription);
    validateTopicLongDescription(longDescription); 
    
    String uuid = uuidProvider.getRandomUuid();
    User creator = userRepository.findById(login).orElse(null);
    Topic topic = new Topic(uuid, title, shortDescription, longDescription, creator);
    topicRepository.save(topic);
    return uuid;
  }

  private void validateTopicName(String title) {
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
  }

  @Override
  @PreAuthorize("#login==authentication.name")
  public List<OwnerTopicDto> getManagedTopics(String login, String search) {
    LOG.info("Rufe verwaltete Topics von {} auf.", login);
  
    User creator = userRepository.findById(login).orElse(null);
    Collection<Topic> managedTopics = topicRepository.findAllByCreator(creator,
        Sort.by(Sort.Order.asc("title").ignoreCase()));
    List<OwnerTopicDto> result = new ArrayList<>();
    for (Topic topic : managedTopics) {
      result.add(mapper.createManagedDto(topic));
    }
    result.removeIf(t -> !t.getTitle().toLowerCase().contains(search.toLowerCase())
            && !t.getShortDescription().toLowerCase().contains(search.toLowerCase()));
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTopicDto getManagedTopic(String topicUuid, String login) {
    LOG.info("Rufe verwaltetes Topic {} auf.", topicUuid);
    LOG.debug("Verwaltetes Topic wird aufgerufen von {}.", login);
    
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
    User anwender = userRepository.getOne(login);
    if (topic.getSubscribers().contains(anwender)) {
      throw new ValidationException("Sia haben das Topic bereits abonniert!");
    } else {
      topic.register(anwender);
    }
  }
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void unsubscribe(String topicUuid, String login) {
    LOG.info("Entfolge Topic {}.", topicUuid);
    LOG.debug("Topic wird von {} deabonniert.", login);
    
    Topic topic = topicRepository.getOne(topicUuid);
    User anwender = userRepository.getOne(login);
    topic.unregister(anwender);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTopicDto> getSubscriptions(String login, String search) {
    LOG.info("Rufe abonnierte Topics von {} auf.", login);
    
    User subscriber = userRepository.findById(login).orElse(null);
    Collection<Topic> subscriptions = topicRepository.findAllBySubscribers(subscriber,
        Sort.by(Sort.Order.asc("title").ignoreCase()));
    List<SubscriberTopicDto> result = new ArrayList<>();
    for (Topic topic : subscriptions) {
      result.add(mapper.createDto(topic));
    }
    result.removeIf(t -> !t.getTitle().toLowerCase().contains(search.toLowerCase())
            && !t.getShortDescription().toLowerCase().contains(search.toLowerCase()));
    return result;
  }
  
  @Override
  public String getTopicUuid(String key, String login) {
    LOG.info("Uuid auflösen für Key{}", key);
    LOG.debug("Key wird von {} eingereicht.", login);
    if (key.length() < 8) {
      throw new ValidationException("Der eingegebene Schlüssel ist zu kurz!");
    }
    Topic topic = topicRepository.findByUuidEndingWith(key);
    if (topic == null) {
      throw new ValidationException("Der eingegebene Schlüssel ist ungültig!");
    }
    return topic.getUuid();
  }
}

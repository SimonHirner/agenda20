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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;
import javax.validation.ValidationException;

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
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public String createTopic(String title, String login) {
    LOG.debug("Erstelle Topic {}.", title);
    
    //Validierung des Topic Namens
    if (title.length() < 1){
      throw new ValidationException("Bitte geben sie einen Namen für das Topic an!");
    }
    if (title.length() > 60){
      throw new ValidationException("Der Name des Topics darf höchstens 60 Zeichen lang sein!");
    }
    
    String uuid = uuidProvider.getRandomUuid();
    User creator = anwenderRepository.findById(login).get();
    Topic topic = new Topic(uuid, title, creator);
    topicRepository.save(topic);
    return uuid;
  }

  @Override
  @PreAuthorize("#login==authentication.name")
  public List<OwnerTopicDto> getManagedTopics(String login) {
    User creator = anwenderRepository.findById(login).get();
    List<Topic> managedTopics = topicRepository.findByCreator(creator);
    List<OwnerTopicDto> result = new ArrayList<>();
    for (Topic topic : managedTopics) {
      result.add(mapper.createManagedDto(topic));
    }
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTopicDto getManagedTopic(String topicUuid, String login) {
    Topic topic = topicRepository.getOne(topicUuid);
    return mapper.createManagedDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTopicDto getTopic(String uuid, String login) {
    Topic topic = topicRepository.getOne(uuid);
    return mapper.createDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void subscribe(String topicUuid, String login) {
    Topic topic = topicRepository.getOne(topicUuid);
    User anwender = anwenderRepository.getOne(login);
    topic.register(anwender);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTopicDto> getSubscriptions(String login) {
    User creator = anwenderRepository.findById(login).get();
    Collection<Topic> subscriptions = creator.getSubscriptions();
    List<SubscriberTopicDto> result = new ArrayList<>();
    for (Topic topic : subscriptions) {
      result.add(mapper.createDto(topic));
    }
    return result;
  }

}

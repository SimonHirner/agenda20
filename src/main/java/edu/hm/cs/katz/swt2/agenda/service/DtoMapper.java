package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.persistence.Status;
import edu.hm.cs.katz.swt2.agenda.persistence.Task;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hilfskomponente zum Erstellen der Transferobjekte aus den Entities. Für diese Aufgabe gibt es
 * viele Frameworks, die aber zum Teil recht schwer zu verstehen sind. Da das Mapping sonst zu viel
 * redundantem Code führt, ist die Zusammenführung aber notwendig.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
public class DtoMapper {

  @Autowired
  private ModelMapper mapper;
  
  @Autowired
  private TopicRepository topicRepository;

  /**
   * Erstellt ein {@link UserDisplayDto} aus einem {@link User}.
   */
  public UserDisplayDto createDto(User user) {
    UserDisplayDto dto = mapper.map(user, UserDisplayDto.class);
    dto.setTopicCount(topicRepository.countByCreator(user));
    dto.setSubscriptionCount(user.getSubscriptions().size());  
    return dto;
  }

  /**
   * Erstellt ein {@link SubscriberTopicDto} aus einem {@link Topic}.
   */
  public SubscriberTopicDto createDto(Topic topic) {
    UserDisplayDto creatorDto = mapper.map(topic.getCreator(), UserDisplayDto.class);
    SubscriberTopicDto topicDto =
        new SubscriberTopicDto(topic.getUuid(), creatorDto, topic.getTitle(), topic.getVisibility(),
            topic.getShortDescription(), topic.getLongDescription());
    topicDto.setSubscriberCount(topic.getSubscribers().size());       
    return topicDto;
  }

  /**
   * Erstellt ein {@link StatusDto} aus einem {@link Status}.
   */
  public StatusDto createDto(Status status) {
    return new StatusDto(status.getStatus(), createDto(status.getUser()), status.getComment(),
        status.getRating());
  }

  /**
   * Erstellt ein {@link SubscriberTaskDto} aus einem {@link Task} und einem {@link Status}.
   */
  public SubscriberTaskDto createReadDto(Task task, Status status) {
    Topic topic = task.getTopic();
    SubscriberTopicDto topicDto = createDto(topic);
    return new SubscriberTaskDto(task.getId(), task.getTitle(), task.getShortDescription(),
        task.getLongDescription(), topicDto, task.getDeadline(), createDto(status));
  }

  /**
   * Erstellt ein {@link OwnerTopicDto} aus einem {@link Topic}.
   */
  public OwnerTopicDto createManagedDto(Topic topic) {
    OwnerTopicDto topicDto = 
        new OwnerTopicDto(topic.getUuid(), createDto(topic.getCreator()), topic.getTitle(), 
            topic.getVisibility(), topic.getShortDescription(), topic.getLongDescription());
    topicDto.setSubscriberCount(topic.getSubscribers().size());   
    
    List<UserDisplayDto> subscribers = new ArrayList<UserDisplayDto>();
    for (User user : topic.getSubscribers()) {
      subscribers.add(createDto(user));
    }
    topicDto.setSubscribers(subscribers);
    
    return topicDto;
  }

  /**
   * Erstellt ein {@link OwnerTaskDto} aus einem {@link Task}.
   */
  public OwnerTaskDto createManagedDto(Task task) {
    OwnerTaskDto ownerTaskDto = new OwnerTaskDto(task.getId(), task.getTitle(),
        task.getShortDescription(), task.getLongDescription(), task.getDeadline(), 
        createDto(task.getTopic()));
       
    List<StatusDto> statusesWithComment = new ArrayList<StatusDto>();
    List<StatusDto> doneStatuses = new ArrayList<StatusDto>();
    List<StatusDto> statuses = new ArrayList<StatusDto>();
    for (Status status : task.getStatuses()) {
      statuses.add(createDto(status));
      if (!status.getComment().equals("")) {
        statusesWithComment.add(createDto(status));
      }
      if (status.getStatus().equals(StatusEnum.FERTIG)) {
        doneStatuses.add(createDto(status));
      }
    }
    ownerTaskDto.setDoneStatuses(doneStatuses);
    ownerTaskDto.setStatusesWithComment(statusesWithComment);
    
    return ownerTaskDto;
  }
}
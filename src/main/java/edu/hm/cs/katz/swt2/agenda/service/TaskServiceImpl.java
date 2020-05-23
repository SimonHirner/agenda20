package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.persistence.Status;
import edu.hm.cs.katz.swt2.agenda.persistence.StatusRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.Task;
import edu.hm.cs.katz.swt2.agenda.persistence.TaskRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.ValidationException;
import org.apache.commons.collections4.SetUtils;
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
public class TaskServiceImpl implements TaskService {

  private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StatusRepository statusRepository;

  @Autowired
  private DtoMapper mapper;
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void deleteTask(Long id, String login) {
    LOG.info("Lösche Task {}.", id);
    LOG.debug("Task wird gelöscht von {}.", login);
    Task task = taskRepository.getOne(id);
    
    User createdBy = task.getTopic().getCreator();
    if (!login.equals(createdBy.getLogin())) {
      LOG.warn("Anwender {} ist nicht berechtigt Task {} zu löschen!", login, id);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    taskRepository.delete(task);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public Long createTask(String uuid, String title, String shortInfo, String longInfo,
      String login) {
    LOG.info("Erstelle neuen Task in Topic {}.", uuid);
    LOG.debug("Task mit Titel {} wird erstellt von {}.", title, login);
    
    Topic t = topicRepository.findById(uuid).get();
    User user = userRepository.getOne(login);
    
    if (!user.equals(t.getCreator())) {
      LOG.warn("Anwender {} ist nicht berechtigt einen Task in Topic {} zu erstellen!", 
          login, t.getTitle());
      throw new AccessDeniedException("Kein Zugriff auf dieses Topic möglich.");
    }
  
    // Validierung des Task Namens
    if (title.length() < 1) {
      LOG.debug("Der Name ist leer, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Bitte gib einen Namen für den Task an.");
    }
    if (title.length() < 8) {
      LOG.debug("Der Name {} ist zu kurz, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Der Name des Tasks muss mindestens 8 Zeichen lang sein.");
    }
    if (title.length() > 32) {
      LOG.debug("Der Name {} ist zu lang, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Der Name des Tasks darf höchstens 32 Zeichen lang sein.");
    }
  
    // Validierung der Task Beschreibungen
    if (shortInfo.length() < 1) {
      LOG.debug("Die Kurzbeschreibung ist leer, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Bitte gib eine kurze Beshreibung für den Task an.");
    }
    if (shortInfo.length() < 8) {
      LOG.debug("Der Kurzbeschreibung {} ist zu kurz, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Die Beschreibung muss mindestens 8 Zeichen lang sein.");
    }
    if (shortInfo.length() > 120) {
      LOG.debug("Die Beschhreibung {} ist zu lang, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Die Beschreibung des Tasks darf höchstens 120 Zeichen "
          + "lang sein.");
    }
    if (longInfo.length() > 500) {
      LOG.debug("Die Langbeschhreibung {} ist zu lang, Task kann nicht angelegt werden.", title);
      throw new ValidationException("Die Langbeschreibung des Tasks darf höchstens 500 Zeichen "
          + "lang sein.");
    }
    
    Task task = new Task(t, title, shortInfo, longInfo);
    taskRepository.save(task);
    return task.getId();
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTaskDto getTask(Long taskId, String login) {
    LOG.info("Rufe Task {} auf.", taskId);
    LOG.debug("Task von {} wird aufgerufen.", login);
    
    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User user = userRepository.getOne(login);
    if (!(topic.getCreator().equals(user) || topic.getSubscriber().contains(user))) {
      LOG.warn("Anwender {} ist nicht berechtigt Task {} einzusehen!", login, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    Status status = getOrCreateStatus(taskId, login);
    return mapper.createReadDto(task, status);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTaskDto getManagedTask(Long taskId, String login) {
    LOG.info("Rufe Verwaltung für Task {} auf.", taskId);
    LOG.debug("Taskverwaltung wird von {} aufgerufen.", login);
    
    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User createdBy = topic.getCreator();
    if (!login.equals(createdBy.getLogin())) {
      LOG.warn("Anwender {} ist nicht berechtigt Task {} zu verwalten!", login, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    return mapper.createManagedDto(task);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getSubscribedTasks(String login) {
    LOG.info("Rufe abonnierte Tasks von {} auf.", login);
    
    User user = userRepository.getOne(login);
    Collection<Topic> topics = user.getSubscriptions();
    return extracted(user, topics);
  }

  private List<SubscriberTaskDto> extracted(User user, Collection<Topic> topics) {
    Collection<Status> status = user.getStatus();
    Map<Task, Status> statusForTask = new HashMap<>();
    for (Status currentStatus : status) {
      statusForTask.put(currentStatus.getTask(), currentStatus);
    }

    List<SubscriberTaskDto> result = new ArrayList<>();

    for (Task task : taskRepository.findAllByTopicIn(topics,
        Sort.by(Sort.Order.asc("title").ignoreCase()))) {
      if (statusForTask.get(task) == null) {
        Status createdStatus = getOrCreateStatus(task.getId(), user.getLogin());
        statusForTask.put(task, createdStatus);
      }
     
    }
    
    for (Task task1 : taskRepository.findAllByTopicIn(topics,
        Sort.by(Sort.Order.asc("title").ignoreCase()))) {
      if (statusForTask.get(task1).getStatus().equals(StatusEnum.OFFEN)
          || statusForTask.get(task1).getStatus().equals(StatusEnum.NEU)) {
        result.add(mapper.createReadDto(task1, statusForTask.get(task1)));
      }
    }
    
    for (Task task1 : taskRepository.findAllByTopicIn(topics,
        Sort.by(Sort.Order.asc("title").ignoreCase()))) {
      if (statusForTask.get(task1).getStatus().equals(StatusEnum.FERTIG)) {
        result.add(mapper.createReadDto(task1, statusForTask.get(task1)));
      }
    }
      
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getTasksForTopic(String uuid, String login) {
    LOG.info("Rufe Tasks für Topic {} auf.", uuid);
    LOG.debug("Tasks von {} werden aufgerufen.", login);
    
    User user = userRepository.getOne(login);
    Topic topic = topicRepository.getOne(uuid);

    return extracted(user, SetUtils.hashSet(topic));
  }


  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void checkTask(Long taskId, String login) {
    LOG.info("Ändere Status von Task {}.", taskId);
    LOG.debug("Status wird von {} geändert.", login);
    
    Status status = getOrCreateStatus(taskId, login);
    status.setStatus(StatusEnum.FERTIG);
    LOG.debug("Status von Task {} und Anwender {} gesetzt auf {}", status.getTask(),
        status.getUser(), status.getStatus());
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<OwnerTaskDto> getManagedTasks(String uuid, String login) {
    LOG.info("Rufe verwaltete Tasks von Topic {} auf.", uuid);
    LOG.debug("Tasks werden von {} aufgerufen.", login);
    
    List<OwnerTaskDto> result = new ArrayList<>();
    Topic topic = topicRepository.getOne(uuid);
    for (Task task : taskRepository.findAllByTopic(topic,
        Sort.by(Sort.Order.asc("title").ignoreCase()))) {
      result.add(mapper.createManagedDto(task));
    }
    return result;
  }

  private Status getOrCreateStatus(Long taskId, String login) {
    User user = userRepository.getOne(login);
    Task task = taskRepository.getOne(taskId);
    Status status = statusRepository.findByUserAndTask(user, task);
    if (status == null) {
      status = new Status(task, user);
      statusRepository.save(status);
    }
    return status;
  }
}

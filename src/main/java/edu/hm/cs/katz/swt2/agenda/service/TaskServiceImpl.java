package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.DateUtilities;
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
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
  public Long createTask(String uuid, String title, String shortDescription, String longDescription,
      String login, Date deadline, Date creationDate) {
    LOG.info("Erstelle neuen Task in Topic {}.", uuid);
    LOG.debug("Task mit Titel {} wird erstellt von {}.", title, login);
    
    Topic t = topicRepository.findById(uuid).orElse(null);
    if (t == null) {
      LOG.warn("Topic {} konnte nicht gefunden werden!", uuid);
      throw new NoSuchElementException("Topic konnte nicht gefunden werden.");
    }
    User user = userRepository.getOne(login);
    
    if (!user.equals(t.getCreator())) {
      LOG.warn("Anwender {} ist nicht berechtigt einen Task in Topic {} zu erstellen!", 
          login, t.getTitle());
      throw new AccessDeniedException("Kein Zugriff auf dieses Topic möglich.");
    }
  
    validateTaskName(title);
    validateTaskShortDescription(shortDescription);
    validateTaskLongDescription(longDescription);
    validateDeadline(deadline, creationDate);
    
    Task task = new Task(t, title, shortDescription, longDescription, deadline);
    taskRepository.save(task);
    
    return task.getId();
  }

  private void validateDeadline(Date deadline, Date creationDate) {    
    if (deadline != null && deadline.before(creationDate)) {
      LOG.debug("Der Abgabetermin {} liegt in der Vergangenheit, Task kann nicht angelegt werden.",
          deadline);
      throw new ValidationException("Der Abgabetermin darf nicht in der Vergangenheit liegen.");
    }
  }

  private void validateTaskName(String title) {
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
  }
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void updateTask(Long id, String login, String shortDescription, String longDescription,
      Date deadline, Date updateDate) {
    LOG.info("Aktualisiere Task {}.", id);
    LOG.debug("Task wird von {} aktualisiert.", login);
    
    Task task = taskRepository.getOne(id);
    User user = userRepository.getOne(login);
    if (!user.equals(task.getTopic().getCreator())) {
      LOG.warn("Anwender {} ist nicht berechtigt Task {} zu aktualisieren!", login, id);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    validateTaskShortDescription(shortDescription);
    validateTaskLongDescription(longDescription);
    validateDeadline(deadline, updateDate);
    
    task.setLongDescription(longDescription);
    task.setShortDescription(shortDescription);
    task.setDeadline(deadline);
  }

  private void validateTaskShortDescription(String shortDescription) {
    if (shortDescription.length() < 1) {
      LOG.debug("Die Kurzbeschreibung ist leer, Task kann nicht angelegt werden.");
      throw new ValidationException("Bitte gib eine Kurzbeschreibung für das Topic an!");
    }
    
    if (shortDescription.length() < 8) {
      LOG.debug("Der Kurzbeschreibung {} ist zu kurz, Task kann nicht angelegt werden.",
          shortDescription);
      throw new ValidationException("Die Kurzbeschreibung muss mindestens 8 Zeichen lang sein!");
    }
    
    if (shortDescription.length() > 120) {
      LOG.debug("Die Kurzbeschhreibung ist zu lang, Task kann nicht angelegt werden.");
      throw new ValidationException("Die Kurzbeschreibung des Tasks darf höchstens 120 Zeichen "
          + "lang sein!");
    }
  }

  private void validateTaskLongDescription(String longDescription) {
    if (longDescription.length() > 1000) {
      LOG.debug("Die Langbeschhreibung ist zu lang, Task kann nicht angelegt werden.");
      throw new ValidationException("Die Langbeschreibung des Tasks darf höchstens 500 Zeichen "
          + "lang sein!");
    }
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTaskDto getTask(Long taskId, String login) {
    LOG.info("Rufe Task {} auf.", taskId);
    LOG.debug("Task von {} wird aufgerufen.", login);
    
    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User user = userRepository.getOne(login);
    if (!(topic.getCreator().equals(user) || topic.getSubscribers().contains(user))) {
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
  public List<SubscriberTaskDto> getAllTasksOfSubscribedTopics(String login) {
    LOG.info("Rufe abonnierte Tasks von {} auf.", login);
    
    User user = userRepository.getOne(login);
    Collection<Topic> topics = user.getSubscriptions();
    
    return createTaskDtosWithStatusForTopics(user, topics);
  }
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getAllTasksForStatus(String login, StatusEnum statusEnum,
      String search) {
    LOG.info("Rufe abonnierte Tasks von {} mit Status {} auf.", login, statusEnum);
    
    User user = userRepository.getOne(login);
    
    for (Status status : user.getStatuses()) {
      checkIfDeadlineExpiredOrChanged(status);
    }
    
    Collection<Topic> topics = user.getSubscriptions();
    List<SubscriberTaskDto> result = createTaskDtosForStatusForTopics(user, topics, statusEnum);    
    result.removeIf(t -> !t.getTitle().toLowerCase().contains(search.toLowerCase())
            && !t.getShortDescription().toLowerCase().contains(search.toLowerCase()));
    
    return result;
  }
  
  private List<SubscriberTaskDto> createTaskDtosWithStatusForTopics(User user,
      Collection<Topic> topics) {
    Map<Task, Status> statusForTask = createTaskToStatusMapForUsersTasks(user);
    
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
      if (statusForTask.get(task1).getStatus().equals(StatusEnum.FERTIG)
          || statusForTask.get(task1).getStatus().equals(StatusEnum.ABGELAUFEN)) {
        result.add(mapper.createReadDto(task1, statusForTask.get(task1)));
      }
    }
    
    return result;
  }
  
  private List<SubscriberTaskDto> createTaskDtosForStatusForTopics(User user, 
      Collection<Topic> topics, StatusEnum status) {
    Map<Task, Status> statusForTask = createTaskToStatusMapForUsersTasks(user);
    
    List<SubscriberTaskDto> result = new ArrayList<>();
    
    for (Task task : taskRepository.findAllByTopicIn(topics,
            Sort.by(Sort.Order.asc("title").ignoreCase()))) {
      if (statusForTask.get(task) == null) {
        Status createdStatus = getOrCreateStatus(task.getId(), user.getLogin());
        statusForTask.put(task, createdStatus);
      }
    }
    
    for (Task task : taskRepository.findAllByTopicIn(topics,
            Sort.by(Sort.Order.asc("title").ignoreCase()))) {
      if (statusForTask.get(task).getStatus().equals(status)) {
        result.add(mapper.createReadDto(task, statusForTask.get(task)));
      }
    }
    return result;
  }
  
  private Map<Task, Status> createTaskToStatusMapForUsersTasks(User user) {
    Collection<Status> statuses = user.getStatuses();
    Map<Task, Status> statusForTask = new HashMap<>();
    for (Status currentStatus : statuses) {
      statusForTask.put(currentStatus.getTask(), currentStatus);
    }
    return statusForTask;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getTasksOfTopic(String topicUuid, String login) {
    LOG.info("Rufe Tasks für Topic {} auf.", topicUuid);
    LOG.debug("Tasks von {} werden aufgerufen.", login);

    User user = userRepository.getOne(login);
    Topic topic = topicRepository.getOne(topicUuid);
    
    for (Status status : user.getStatuses()) {
      checkIfDeadlineExpiredOrChanged(status);
    }
    
    return createTaskDtosWithStatusForTopics(user, SetUtils.hashSet(topic));
  }
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getTasksOfTopicWithStatus(String topicUuid, String login,
      StatusEnum statusEnum) {
    LOG.info("Rufe Tasks für Topic {} mit Status {} auf.", topicUuid, statusEnum);
    LOG.debug("Tasks von {} werden aufgerufen.", login);
    
    User user = userRepository.getOne(login);
    Topic topic = topicRepository.getOne(topicUuid);
    
    for (Status status : user.getStatuses()) {
      checkIfDeadlineExpiredOrChanged(status);
    }
    
    return createTaskDtosForStatusForTopics(user, SetUtils.hashSet(topic), statusEnum);
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
  public void resetTask(Long taskId, String login) {
    LOG.info("Setze Status von Task {} zurück.", taskId);
    LOG.debug("Status wird von {} zurückgesetzt.", login);
  
    Status status = getOrCreateStatus(taskId, login);
    
    if (!status.getStatus().equals(StatusEnum.FERTIG)) {
      LOG.debug("Task {} mit dem Status {} kann nicht zurückgesetzt werden!",
          taskId, status.getStatus());
      throw new ValidationException("Dieser Task kann nicht zurückgesetzt werden.");
    }
    
    status.setStatus(StatusEnum.OFFEN);
    LOG.debug("Status von Task {} und Anwender {} gesetzt auf {}", status.getTask(),
            status.getUser(), status.getStatus());
  }
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void resetAllTasks(String uuid, String login) {
    LOG.info("Setze Status von allen Tasks des Topic {} zurück.", uuid);
    LOG.debug("Status der Tasks wird für ein Topic von {} zurückgesetzt.", login);
    
    List<SubscriberTaskDto> tasks = getTasksOfTopic(uuid, login);
    for (SubscriberTaskDto task : tasks) {
      Status status = getOrCreateStatus(task.getId(), login);
      status.setStatus(StatusEnum.OFFEN);
    }
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
    
    checkIfDeadlineExpiredOrChanged(status);
    
    return status;
  }

  private void checkIfDeadlineExpiredOrChanged(Status status) {
    Date currentDate = DateUtilities.getCurrentDate();
    
    if (status.getTask().getDeadline() != null 
        && status.getTask().getDeadline().before(currentDate)
        && (status.getStatus().equals(StatusEnum.NEU)
            || status.getStatus().equals(StatusEnum.OFFEN))) {
      status.setStatus(StatusEnum.ABGELAUFEN);
    }
    
    if (status.getTask().getDeadline() != null 
        && !status.getTask().getDeadline().before(currentDate)
        && (status.getStatus().equals(StatusEnum.ABGELAUFEN))) {
      status.setStatus(StatusEnum.OFFEN);
    }
  }
  
  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void updateComment(Long taskId, String login, String comment) {
    LOG.info("Aktualisiere Kommentar von Task {}.", taskId);
    LOG.debug("Kommentar wird von {} aktualisiert.", login);
    
    Task task = taskRepository.getOne(taskId);
    User user = userRepository.getOne(login);
    Status status = statusRepository.findByUserAndTask(user, task);
    
    if (!user.equals(status.getUser())) {
      LOG.warn("Anwender {} ist nicht berechtigt Task {} zu kommentieren!", login, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    status.setComment(comment);
  }
  
  @Override
  public void updateRating(Long taskId, String loginTopicOwner, String loginSubscriber,
      String rating) {
    LOG.info("Aktualisiere Bewertung von Task {}.", taskId);
    LOG.debug("Bewertung wird von {} aktualisiert.", loginTopicOwner);
    
    Task task = taskRepository.getOne(taskId);
    User userTopicOwner = userRepository.getOne(loginTopicOwner);
    User userSubscriber = userRepository.getOne(loginSubscriber);
    Status status = statusRepository.findByUserAndTask(userSubscriber, task);
    
    if (!userTopicOwner.equals(status.getTask().getTopic().getCreator())) {
      LOG.warn("Anwender {} ist nicht berechtigt Task {} zu bewerten!", loginTopicOwner, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    status.setRating(rating);
  }
  
  @Override
  public StatusDto getStatus(Long taskId, String login) {
    LOG.info("Rufe Status von Task {} auf.", taskId);
    LOG.debug("Status wird von {} aufgerufen.", login);
    
    Task task = taskRepository.getOne(taskId);
    Status status = getOrCreateStatus(taskId, login);
    
    if (!login.equals(status.getUser().getLogin())
        && !login.equals(task.getTopic().getCreator().getLogin())) {
      LOG.warn("Anwender {} ist nicht berechtigt den Status von Task {} einzusehen!", login,
          taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    return mapper.createDto(status);
  }
  
  @Override
  public List<StatusDto> getStatuses(Long taskId, String login) {
    LOG.info("Rufe Status von Task {} auf.", taskId);
    LOG.debug("Status wird von {} aufgerufen.", login);
    
    Task task = taskRepository.getOne(taskId);
    List<StatusDto> statuses = new ArrayList<StatusDto>();
    
    for (User user : task.getTopic().getSubscribers()) {
      Status status = getOrCreateStatus(taskId, user.getLogin());
      statuses.add(mapper.createDto(status));
    }
    
    if (!login.equals(task.getTopic().getCreator().getLogin())) {
      LOG.warn("Anwender {} ist nicht berechtigt die Status von Task {} einzusehen!", login,
          taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    
    return statuses;
  }
  
  @Override
  public Map<String, Integer> getDoneStatusesCountForUser(String uuid, String login) {
    LOG.info("Rufe erledigte Status von Topic {} auf.", uuid);
    LOG.debug("Erledigte Status werden von {} aufgerufen.", login);
    
    Topic topic = topicRepository.getOne(uuid);
    Map<String, Integer> doneStatusesCountForUser = new LinkedHashMap<String, Integer>();
    
    for (Task task : topic.getTasks()) {
      for (StatusDto status : getStatuses(task.getId(), login)) {
        if (doneStatusesCountForUser.get(status.getUser().getLogin()) == null) {
          doneStatusesCountForUser.put(status.getUser().getLogin(), 0);
        }
        if (status.getStatus().equals(StatusEnum.FERTIG)) {
          doneStatusesCountForUser.put(status.getUser().getLogin(),
              doneStatusesCountForUser.get(status.getUser().getLogin()) + 1);
        }
      }
    }
    
    if (!login.equals(topic.getCreator().getLogin())) {
      LOG.warn("Anwender {} ist nicht berechtigt die erledigten Status von Topic {} einzusehen!",
          login, uuid);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
        
    return doneStatusesCountForUser;
  }
}